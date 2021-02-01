package wee.digital.sample.ui.main

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
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

    val statusCreateNewSession = EventLiveData<ResponseVideoCallCreateSession>()

    val dialogTag = mutableListOf<String>()

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
    fun createNewSession(kioskId : String){
        Single.fromCallable {
            val body = JsonObject().put("kioskId", kioskId)
            lib?.kioskService!!.videoCallCreateSession(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.e("createNewSession", "$it")
                    statusCreateNewSession.postValue(it)
                }, {
                    Log.e("createNewSession", "${it.message}")
                    statusCreateNewSession.postValue(null)
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
                        val videoCallId = Shared.sessionVideo.value?.result?.videoCallID ?: ""
                        val body = RecordSendData(videoCallId = videoCallId, Ekycid = data.sizeDataStr, body = data.repairedData.toStringBase64())
                        val a = lib?.kioskService!!.videoCallRecord(Gson().toJson(body).toByteArray())
                        Log.e("recordVideo", "$a")
                    }
                }, {
                    Log.e("recordVideo", "${it.message}")
                })
    }

}