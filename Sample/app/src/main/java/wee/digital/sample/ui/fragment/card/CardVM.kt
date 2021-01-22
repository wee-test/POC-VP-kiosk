package wee.digital.sample.ui.fragment.card

import android.util.Base64
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vplib.ResponseFaceVerifyToIDCard
import wee.digital.library.extension.put
import wee.digital.sample.app.lib
import wee.digital.sample.shared.Configs
import wee.digital.sample.ui.base.BaseViewModel
import wee.digital.sample.ui.base.EventLiveData

class CardVM : BaseViewModel() {

    val statusCardId = EventLiveData<Boolean>()

    fun verifyToIdCard(idCard: String, face: ByteArray) {
        Single.fromCallable {
            val body = JsonObject()
                    .put("kioskID", Configs.KIOSK_ID)
                    .put("idCardPhoto", idCard)
                    .put("facePhoto", Base64.encodeToString(face, Base64.NO_WRAP))
            lib?.kioskService!!.faceVerifyToIDCard(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(object : SingleObserver<ResponseFaceVerifyToIDCard> {

                    override fun onSubscribe(d: Disposable) {}

                    override fun onSuccess(resp: ResponseFaceVerifyToIDCard) {
                        if (resp.responseCode.code == 0L) {
                            statusCardId.postValue(true)
                        } else {
                            statusCardId.postValue(false)
                        }
                    }

                    override fun onError(e: Throwable) {
                        statusCardId.postValue(false)
                    }

                })
    }

}