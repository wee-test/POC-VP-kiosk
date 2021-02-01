package wee.digital.sample.ui.main

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vplib.ResponseLogin
import vplib.ResponseTellerContact
import vplib.ResponseVideoCallCreateSession
import vplib.ResponseVideoCallUpdateInfo
import wee.dev.weewebrtc.repository.model.RecordData
import wee.digital.camera.toStringBase64
import wee.digital.library.extension.put
import wee.digital.library.extension.toast
import wee.digital.sample.app.lib
import wee.digital.sample.repository.model.LoginKioskReq
import wee.digital.sample.repository.model.RecordSendData
import wee.digital.sample.repository.model.UpdateInfoReq
import wee.digital.sample.shared.Configs
import wee.digital.sample.shared.Shared
import wee.digital.sample.ui.base.EventLiveData
import wee.digital.sample.ui.fragment.dialog.alert.Alert
import wee.digital.sample.ui.fragment.dialog.date.DateArg
import wee.digital.sample.ui.fragment.dialog.selectable.SelectableAdapter
import wee.digital.sample.ui.fragment.dialog.web.WebArg

open class MainVM : ViewModel() {

    val dateLiveData = MutableLiveData<DateArg>()

    val statusLoginKiosk = EventLiveData<ResponseLogin>()

    val dialogLiveData = EventLiveData<NavDirections>()

    val selectableLiveData = MutableLiveData<SelectableAdapter<*>>()

    val selectableTitle = MutableLiveData<String>()

    val alertLiveData = MutableLiveData<Alert.Arg?>()

    val webLiveData = MutableLiveData<WebArg>()

    val statusUpdateInfo = EventLiveData<ResponseVideoCallUpdateInfo>()

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
        Single.fromCallable { data.repair() }
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe({
                if (it) {
                    /*val videoCallId = Shared.sessionVideo.value?.result?.videoCallID ?: ""
                    val body = RecordSendData(videoCallId = videoCallId, Ekycid = data.sizeDataStr, body = data.repairedData)
                    val dataB = Gson().toJson(body).toByteArray()
                    Log.e("recordVideo","Size Data: ${dataB.size}")
                    val a = lib?.kioskService!!.videoCallRecord(dataB)
                    Log.e("recordVideo", "$a")*/
                    sendVideoRecord(Shared.sessionVideo.value?.result?.videoCallID
                            ?: "", data.sizeDataStr, data.repairedData!!)
                }
            }, {
                Log.e("recordVideo", "${it.message}")
            })
    }

    private fun sendVideoRecord(videoId: String, sizeDataStr: String, data: ByteArray) {
        val regUrl = "http://weezi.biz:7080/kiosk/videoCall/record"
        try {

            Log.e("recordVideo", "Size: ${data.size}")
            val timeIn = System.currentTimeMillis()
            regUrl.httpPost().timeout(30000).header(Pair("Content-Type", "application/json"),
                    Pair("videoCallId", videoId),
                    Pair("Ekycid", videoId),
                    Pair("seg",sizeDataStr)
            ).body(data).responseString { _, _, result ->
                when (result) {
                    is Result.Failure -> {
                        Log.e("recordVideo", "Send Fail [${System.currentTimeMillis() - timeIn}]: ${result.error}")
                    }
                    is Result.Success -> {
                        Log.e("recordVideo", "Send Success [${System.currentTimeMillis() - timeIn}]: ${result.value}")
                    }
                }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            Log.e("recordVideo", "Failed: ${t.message}")
        }
    }

}