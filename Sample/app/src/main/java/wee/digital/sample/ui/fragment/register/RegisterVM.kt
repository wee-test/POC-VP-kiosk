package wee.digital.sample.ui.fragment.register

import android.util.Base64
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vplib.ResponseCustomerRegister
import vplib.ResponseFaceVerifyToIDCard
import wee.digital.sample.app.lib
import wee.digital.sample.repository.model.CustomerInfoRegister
import wee.digital.sample.repository.model.CustomerRegisterReq
import wee.digital.sample.repository.model.VerifyIdCardReq
import wee.digital.sample.ui.base.BaseViewModel
import wee.digital.sample.ui.base.EventLiveData

class RegisterVM : BaseViewModel(){

    val statusVerifyCard = EventLiveData<Boolean>()

    val statusRegisterCard = EventLiveData<ResponseCustomerRegister>()

    fun verifyIdCard(cardImage: String, faceImage: ByteArray) {
        Single.fromCallable {
            val body = VerifyIdCardReq(
                    cardImage = cardImage,
                    faceImage = Base64.encodeToString(faceImage, Base64.NO_WRAP)
            )
            lib?.kioskService!!.faceVerifyToIDCard(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(object : SingleObserver<ResponseFaceVerifyToIDCard> {

                    override fun onSubscribe(d: Disposable) {}

                    override fun onSuccess(t: ResponseFaceVerifyToIDCard) {
                        if (t.responseCode.code == 0L) {
                            statusVerifyCard.postValue(true)
                        } else {
                            statusVerifyCard.postValue(false)
                        }
                    }

                    override fun onError(e: Throwable) {
                        statusVerifyCard.postValue(false)
                    }

                })
    }

    fun registerCard(body : CustomerRegisterReq){
        Single.fromCallable {
            lib?.kioskService!!.customerCardRegister(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(object : SingleObserver<ResponseCustomerRegister>{

                    override fun onSubscribe(d: Disposable) {}

                    override fun onSuccess(t: ResponseCustomerRegister) {
                        statusRegisterCard.postValue(t)
                    }

                    override fun onError(e: Throwable) {
                        statusRegisterCard.postValue(null)
                    }

                })
    }

}