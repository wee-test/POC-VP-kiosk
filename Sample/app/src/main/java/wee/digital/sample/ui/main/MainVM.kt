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
import wee.digital.library.extension.put
import wee.digital.library.extension.toast
import wee.digital.sample.app.lib
import wee.digital.sample.repository.model.LoginKioskReq
import wee.digital.sample.repository.model.UpdateInfoReq
import wee.digital.sample.shared.Configs
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
                    statusCreateNewSession.postValue(it)
                }, {
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
                    toast("${it.responseCode.code}")
                    Log.e("updateInfo", "$it")
                    statusUpdateInfo.postValue(it)
                }, {
                    toast("${it.message}")
                    Log.e("updateInfo", "$it")
                    statusUpdateInfo.postValue(null)
                })
    }

}