package wee.digital.sample.ui.fragment.call

import android.annotation.SuppressLint
import android.util.Log
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import vplib.ResponseTellerContact
import wee.digital.sample.app.lib
import wee.digital.sample.shared.Configs
import wee.digital.sample.ui.base.BaseViewModel
import wee.digital.sample.ui.base.EventLiveData

class CallVM : BaseViewModel(){

    val statusContacts = EventLiveData<ResponseTellerContact>()

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