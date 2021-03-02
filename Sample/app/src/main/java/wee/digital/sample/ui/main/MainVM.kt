package wee.digital.sample.ui.main

import android.annotation.SuppressLint
import android.os.Environment
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import vplib.ResponseLogin
import vplib.ResponseVideoCallUpdateInfo
import wee.dev.weewebrtc.WeeCaller
import wee.dev.weewebrtc.repository.model.RecordData
import wee.digital.library.extension.toArray
import wee.digital.sample.app.lib
import wee.digital.sample.repository.model.LoginKioskReq
import wee.digital.sample.repository.model.UpdateInfoReq
import wee.digital.sample.repository.network.VPDatabase
import wee.digital.sample.shared.Configs
import wee.digital.sample.shared.Shared
import wee.digital.sample.shared.Utils
import wee.digital.sample.ui.base.EventLiveData
import wee.digital.sample.ui.fragment.dialog.alert.Alert
import wee.digital.sample.ui.fragment.dialog.date.DateArg
import wee.digital.sample.ui.fragment.dialog.selectable.SelectableAdapter
import wee.digital.sample.ui.fragment.dialog.web.WebArg
import java.io.File
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit


open class MainVM : ViewModel() {

    val dateLiveData = MutableLiveData<DateArg>()

    val statusLoginKiosk = EventLiveData<ResponseLogin>()

    val dialogLiveData = EventLiveData<NavDirections>()

    val selectableLiveData = MutableLiveData<SelectableAdapter<*>>()

    val selectableTitle = MutableLiveData<String>()

    val alertLiveData = MutableLiveData<Alert.Arg?>()

    val webLiveData = MutableLiveData<WebArg>()

    val statusUpdateInfo = EventLiveData<ResponseVideoCallUpdateInfo>()

    val statusUpdateKiosk = EventLiveData<Boolean>()

    @SuppressLint("CheckResult")
    fun loginKiosk() {
        val body = LoginKioskReq(Configs.KIOSK_CODE)
        Single.fromCallable {
            lib?.kioskService!!.login(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    statusLoginKiosk.postValue(it)
                }, {
                    statusLoginKiosk.postValue(null)
                })
    }

    @SuppressLint("CheckResult")
    fun updateInfo(data: UpdateInfoReq) {
        Single.fromCallable {
            lib?.kioskService!!.videoCallUpdateInfo(Gson().toJson(data).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.e("updateInfo", "$it")
                    statusUpdateInfo.postValue(it)
                }, {
                    Log.e("updateInfo", "$it")
                    statusUpdateInfo.postValue(null)
                })
    }

    @SuppressLint("CheckResult")
    fun recordVideo(data: RecordData) {
        Single.fromCallable {
            sendVideoPart(Shared.sessionVideo.value?.result?.videoCallID ?: "", data)
        }
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.e("recordVideo", "${it}")
                }, {
                    Log.e("recordVideo", "${it.message}")
                })

    }

    private fun sendVideoPart(videoId: String/*, sizeDataStr: String*/, data: RecordData) {
        val client = OkHttpClient().newBuilder().writeTimeout(280, TimeUnit.SECONDS).readTimeout(280, TimeUnit.SECONDS)
                .build()
        val body: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("localVideo", data.localVideo!!.path.substringAfterLast("/"),
                        data.localVideo!!.asRequestBody("application/octet-stream".toMediaTypeOrNull()))

                .addFormDataPart("remoteVideo", data.remoteVideo!!.path.substringAfterLast("/"),
                        data.remoteVideo!!.asRequestBody("application/octet-stream".toMediaTypeOrNull()))

                .addFormDataPart("localAudio", data.localAudio!!.path.substringAfterLast("/"),
                        data.localAudio!!.asRequestBody("application/octet-stream".toMediaTypeOrNull()))

                .addFormDataPart("remoteAudio", data.remoteAudio!!.path.substringAfterLast("/"),
                        data.remoteAudio!!.asRequestBody("application/octet-stream".toMediaTypeOrNull()))

                .build()
        val request: Request = Request.Builder()
                .url("https://vpbank.wee.vn/api/kiosk/videoCall/record")
                .addHeader("videoCallId", videoId)
                .addHeader("Ekycid", videoId)
                .method("POST", body)
                .build()
        val response: Response = client.newCall(request).execute()
        Log.e("recordVideo", "${response}")
        Utils.deleteDirectory(File("${Environment.getExternalStoragePublicDirectory("WeeVideoCall")}"))
    }

    fun listenerUpdateKiosk() {
        VPDatabase.ins.insertKiosk()
        val database = Firebase.database
        val myRef = database.reference.child("vpKiosk").child("kiosk").child(Configs.KIOSK_CODE)
        myRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val dataResp = snapshot.child("check").value ?: return
                    dataResp as Boolean
                    if (!dataResp) return
                    statusUpdateKiosk.postValue(true)
                } catch (e: Exception) {
                }
            }

            override fun onCancelled(error: DatabaseError) {
                print("")
            }

        })
    }

}