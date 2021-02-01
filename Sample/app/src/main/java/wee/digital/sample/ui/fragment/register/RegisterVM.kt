package wee.digital.sample.ui.fragment.register

import android.annotation.SuppressLint
import android.util.Base64
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vplib.*
import wee.digital.sample.app.lib
import wee.digital.sample.repository.model.*
import wee.digital.sample.ui.base.BaseViewModel
import wee.digital.sample.ui.base.EventLiveData

class RegisterVM : BaseViewModel(){

    val statusVerifyCard = EventLiveData<ResponseFaceVerifyToIDCard>()

    val statusRegisterCard = EventLiveData<ResponseCustomerRegister>()

    @SuppressLint("CheckResult")
    fun verifyIdCard(cardImage: String, faceImage: ByteArray) {
        Single.fromCallable {
            val body = VerifyIdCardReq(
                    cardImage = cardImage,
                    faceImage = Base64.encodeToString(faceImage, Base64.NO_WRAP)
            )
            lib?.kioskService!!.faceVerifyToIDCard(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.e("verifyIdCard", "$it")
                    statusVerifyCard.postValue(it)
                }, {
                    Log.e("verifyIdCard", "${it.message}")
                    statusVerifyCard.postValue(null)
                })
    }

    @SuppressLint("CheckResult")
    fun registerCard(body : CustomerRegisterReq){
        Single.fromCallable {
            Log.e("registerCard", "$body")
            lib?.kioskService!!.customerCardRegister(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.e("registerCard", "$it")
                    statusRegisterCard.postValue(it)
                }, {
                    Log.e("registerCard", "${it.message}")
                    statusRegisterCard.postValue(null)
                })
    }

}