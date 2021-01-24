package wee.digital.sample.ui.fragment.face

import android.annotation.SuppressLint
import android.util.Base64
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vplib.ResponseFaceIdentity
import vplib.ResponseFaceVerify
import vplib.ResponseGetCustomerInfo
import wee.digital.sample.app.lib
import wee.digital.sample.model.CustomerInfoReq
import wee.digital.sample.model.IdentifyFaceReq
import wee.digital.sample.model.VerifyFaceReq
import wee.digital.sample.ui.base.BaseViewModel
import wee.digital.sample.ui.base.EventLiveData

class FaceVM : BaseViewModel() {

    val statusIdentify = EventLiveData<Boolean>()

    val statusVerify = EventLiveData<Boolean>()

    @SuppressLint("CheckResult")
    fun identifyFace(face: ByteArray) {
        Single.fromCallable {
            val body = IdentifyFaceReq(faceImage = Base64.encodeToString(face, Base64.NO_WRAP))
            lib?.kioskService!!.faceIdentity(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(object : SingleObserver<ResponseFaceIdentity>{

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

    fun verifyFace(face : ByteArray, customerId : String){
        Single.fromCallable {
            val body = VerifyFaceReq(
                    customerID = customerId,
                    faceImage = Base64.encodeToString(face, Base64.NO_WRAP)
            )
            lib?.kioskService!!.faceVerify(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(object : SingleObserver<ResponseFaceVerify>{

                    override fun onSubscribe(d: Disposable) {}

                    override fun onSuccess(resp: ResponseFaceVerify) {
                        if(resp.responseCode.code == 0L){
                            statusVerify.postValue(true)
                        }else{
                            statusVerify.postValue(false)
                        }
                    }

                    override fun onError(e: Throwable) {
                        statusVerify.postValue(false)
                    }

                })
    }

    fun getInfoCustomer(customerId : String){
        Single.fromCallable {
            val body = CustomerInfoReq(
                    customerID = customerId
            )
            lib?.kioskService!!.getCustomerInfo(Gson().toJson(body).toByteArray())
        }.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(object : SingleObserver<ResponseGetCustomerInfo>{

                    override fun onSubscribe(d: Disposable) {}

                    override fun onSuccess(t: ResponseGetCustomerInfo) {

                    }

                    override fun onError(e: Throwable) {

                    }

                })
    }

}