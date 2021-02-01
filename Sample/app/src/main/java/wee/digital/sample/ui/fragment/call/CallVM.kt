package wee.digital.sample.ui.fragment.call

import android.annotation.SuppressLint
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import vplib.ResponseTellerContact
import vplib.ResponseVideoCallCreateSession
import wee.digital.library.extension.put
import wee.digital.sample.app.lib
import wee.digital.sample.shared.Configs
import wee.digital.sample.ui.base.BaseViewModel
import wee.digital.sample.ui.base.EventLiveData

class CallVM : BaseViewModel(){

    val statusContacts = EventLiveData<ResponseTellerContact>()

    val statusCreateNewSession = EventLiveData<ResponseVideoCallCreateSession>()

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
    fun getContacts() {
        Single.fromCallable {
            lib?.kioskService!!.tellerContact(Configs.KIOSK_ID)
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.e("getContacts", "$it")
                    statusContacts.postValue(it)
                }, {
                    Log.e("getContacts", "${it.message}")
                    statusContacts.postValue(null)
                })
    }

}