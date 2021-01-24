package wee.digital.sample.ui.fragment.register

import android.annotation.SuppressLint
import android.util.Base64
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vplib.ResponseFaceIdentity
import wee.digital.sample.app.lib
import wee.digital.sample.model.IdentifyFaceReq
import wee.digital.sample.ui.base.BaseViewModel
import wee.digital.sample.ui.base.EventLiveData

class RegisterVM : BaseViewModel(){

    val statusIdentify = EventLiveData<Boolean>()

    @SuppressLint("CheckResult")
    fun identifyFace(face: ByteArray) {
        Single.fromCallable {
            val body = IdentifyFaceReq(faceImage = Base64.encodeToString(face, Base64.NO_WRAP))
            lib?.kioskService!!.faceIdentity(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(object : SingleObserver<ResponseFaceIdentity> {

                    override fun onSubscribe(d: Disposable) {}

                    override fun onSuccess(resp: ResponseFaceIdentity) {
                        if(resp.responseCode.code != 0L){
                            statusIdentify.postValue(false)
                            return
                        }
                        statusIdentify.postValue(true)
                    }

                    override fun onError(e: Throwable) {
                        statusIdentify.postValue(false)
                    }

                })
    }

}