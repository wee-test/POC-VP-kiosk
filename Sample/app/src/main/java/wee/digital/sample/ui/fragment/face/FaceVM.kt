package wee.digital.sample.ui.fragment.face

import android.annotation.SuppressLint
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import wee.digital.library.extension.put
import wee.digital.sample.app.lib
import wee.digital.sample.shared.Configs
import wee.digital.sample.ui.base.BaseViewModel
import wee.digital.sample.ui.base.EventLiveData

class FaceVM : BaseViewModel() {

    val statusIdentify = EventLiveData<Boolean>()

    @SuppressLint("CheckResult")
    fun identifyFace(face: ByteArray) {
        Observable.fromCallable {
            val body = JsonObject()
                    .put("kioskID", Configs.KIOSK_ID)
                    .put("photo", Base64.encodeToString(face, Base64.NO_WRAP))
            lib?.kioskService!!.faceIdentity(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    if (it.responseCode.code == 0L) {
                        statusIdentify.postValue(true)
                    } else {
                        statusIdentify.postValue(false)
                    }
                }, {
                    statusIdentify.postValue(false)
                })
    }

}