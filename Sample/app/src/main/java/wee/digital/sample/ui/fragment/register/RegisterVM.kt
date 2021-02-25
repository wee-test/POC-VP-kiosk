package wee.digital.sample.ui.fragment.register

import android.annotation.SuppressLint
import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vplib.*
import wee.digital.camera.toStringBase64
import wee.digital.camera.utils.RecordVideo
import wee.digital.sample.app.lib
import wee.digital.sample.repository.model.*
import wee.digital.sample.shared.Configs
import wee.digital.sample.shared.Utils
import wee.digital.sample.ui.base.BaseViewModel
import wee.digital.sample.ui.base.EventLiveData
import java.io.File

class RegisterVM : BaseViewModel(){

    val statusVerifyCard = EventLiveData<ResponseFaceVerifyToIDCard>()

    val statusRegisterCard = EventLiveData<ResponseCustomerRegister>()

    val statusMatching = EventLiveData<ResponseVPFaceMatching>()

    val statusLivess = EventLiveData<ResponseVPFaceLiveness>()

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
                    Log.d("verifyIdCard", "$it")
                    statusVerifyCard.postValue(it)
                }, {
                    Log.d("verifyIdCard", "${it.message}")
                    statusVerifyCard.postValue(null)
                })
    }

    @SuppressLint("CheckResult")
    fun matchingFrame(idCardPhoto: String, face: ByteArray) {
        Single.fromCallable {
            val body = MatchingReq(
                    kioskId = Configs.KIOSK_ID,
                    sessionId = Utils.getUUIDRandom(),
                    idCardPhoto = idCardPhoto,
                    facePhoto = face.toStringBase64()
            )
            lib?.kioskService!!.faceMatchingVP(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.d("matchingFrame","$it")
                    statusMatching.postValue(it)
                }, {
                    Log.d("matchingFrame","${it.message}")
                    statusMatching.postValue(null)
                })
    }

    @SuppressLint("CheckResult")
    fun registerCard(body : CustomerRegisterReq){
        Single.fromCallable {
            Log.d("registerCard", "$body")
            lib?.kioskService!!.customerCardRegister(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.d("registerCard", "$it")
                    statusRegisterCard.postValue(it)
                }, {
                    Log.d("registerCard", "${it.message}")
                    statusRegisterCard.postValue(null)
                })
    }

    fun createVideo(context : Context, face : String){
        val recordVideo = RecordVideo(context)
        recordVideo.startVideo()
        recordVideo.createVideo(object : RecordVideo.MyVideoCallBack{
            override fun onResult(path: String) {
                val video = File(path).readBytes()
                livenessFace(face, video.toStringBase64())
            }
        })
    }

    @SuppressLint("CheckResult")
    private fun livenessFace(face: String, video: String) {
        Single.fromCallable {
            val body = LivenessReq(
                    kioskId = Configs.KIOSK_ID,
                    sessionId = Utils.getUUIDRandom(),
                    face = face,
                    livenessVideo = video
            )
            lib?.kioskService!!.faceLivenessVP(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.d("livenessFace","$it")
                    statusLivess.postValue(it)
                }, {
                    Log.d("livenessFace","${it.message}")
                    statusLivess.postValue(null)
                })
    }

}