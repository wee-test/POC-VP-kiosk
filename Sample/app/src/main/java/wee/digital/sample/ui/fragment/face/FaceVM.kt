package wee.digital.sample.ui.fragment.face

import android.annotation.SuppressLint
import android.content.Context
import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vplib.ResponseFaceIdentity
import vplib.ResponseFaceVerify
import vplib.ResponseGetCustomerInfo
import wee.digital.camera.job.FaceCaptureJob
import wee.digital.camera.utils.RecordVideo
import wee.digital.sample.app.lib
import wee.digital.sample.repository.model.CustomerInfoReq
import wee.digital.sample.repository.model.IdentifyFaceReq
import wee.digital.sample.repository.model.VerifyFaceReq
import wee.digital.sample.ui.base.BaseViewModel
import wee.digital.sample.ui.base.EventLiveData

class FaceVM : BaseViewModel() {

    val statusIdentify = EventLiveData<ResponseFaceIdentity>()

    val statusVerify = EventLiveData<Boolean>()

    val statusInfoCustomer = EventLiveData<ResponseGetCustomerInfo>()

    @SuppressLint("CheckResult")
    fun verifyFace(face: ByteArray, customerId: String) {
        Single.fromCallable {
            val body = VerifyFaceReq(
                    customerID = customerId,
                    faceImage = Base64.encodeToString(face, Base64.NO_WRAP)
            )
            lib?.kioskService!!.faceVerify(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.e("verifyFace", "$it")
                    if (it.responseCode.code == 0L) {
                        statusVerify.postValue(true)
                    } else {
                        statusVerify.postValue(false)
                    }
                }, {
                    Log.e("verifyFace", "${it.message}")
                    statusVerify.postValue(false)
                })
    }

    @SuppressLint("CheckResult")
    fun identifyFace(face: ByteArray) {
        Single.fromCallable {
            val body = IdentifyFaceReq(faceImage = Base64.encodeToString(face, Base64.NO_WRAP))
            lib?.kioskService!!.faceIdentity(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.e("identifyFace", "$it")
                    statusIdentify.postValue(it)
                }, {
                    Log.e("identifyFace", "${it.message}")
                    statusIdentify.postValue(null)
                })
    }

    @SuppressLint("CheckResult")
    fun getInfoCustomer(customerId: String) {
        Single.fromCallable {
            val body = CustomerInfoReq(
                    customerID = customerId
            )
            lib?.kioskService!!.getCustomerInfo(Gson().toJson(body).toByteArray())
        }.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe({
                    Log.e("getInfoCustomer", "$it")
                    statusInfoCustomer.postValue(it)
                },{
                    Log.e("getInfoCustomer", "${it.message}")
                    statusInfoCustomer.postValue(null)
                })
    }

    fun createVideo(context : Context){
        val recordVideo = RecordVideo(context)
        recordVideo.startVideo()
        recordVideo.createVideo(object : RecordVideo.MyVideoCallBack{
            override fun onResult(path: String) {
                Log.e("videoPath", "$path")
            }
        })
    }

}