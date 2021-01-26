package wee.digital.sample.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vplib.ResponseLogin
import wee.digital.sample.app.lib
import wee.digital.sample.repository.model.LoginKioskReq
import wee.digital.sample.shared.Configs
import wee.digital.sample.ui.base.EventLiveData
import wee.digital.sample.ui.fragment.dialog.alert.Alert
import wee.digital.sample.ui.fragment.dialog.selectable.SelectableAdapter
import wee.digital.sample.ui.fragment.dialog.web.WebArg

open class MainVM : ViewModel() {

    val statusLoginKiosk = EventLiveData<ResponseLogin>()

    val dialogTag = mutableListOf<String>()

    val dialogLiveData = EventLiveData<NavDirections>()

    val selectableLiveData = MutableLiveData<SelectableAdapter<*>>()

    val selectableTitle = MutableLiveData<String>()

    val alertLiveData = MutableLiveData<Alert.Arg?>()

    val webLiveData = MutableLiveData<WebArg>()

    fun loginKiosk() {
        Single.fromCallable {
            val body = LoginKioskReq(Configs.KIOSK_CODE)
            lib?.kioskService!!.login(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(object : SingleObserver<ResponseLogin>{

                    override fun onSubscribe(d: Disposable) {}

                    override fun onSuccess(t: ResponseLogin) {
                        statusLoginKiosk.postValue(t)
                    }

                    override fun onError(e: Throwable) {
                        statusLoginKiosk.postValue(null)
                    }

                })
    }

}