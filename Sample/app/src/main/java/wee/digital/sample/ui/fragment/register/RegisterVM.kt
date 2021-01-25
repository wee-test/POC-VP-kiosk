package wee.digital.sample.ui.fragment.register

import android.util.Base64
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vplib.ResponseFaceVerifyToIDCard
import wee.digital.sample.app.lib
import wee.digital.sample.model.VerifyIdCardReq
import wee.digital.sample.ui.base.BaseViewModel

class RegisterVM : BaseViewModel(){

    val statusVerifyCard = MutableLiveData<Boolean>()

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

}