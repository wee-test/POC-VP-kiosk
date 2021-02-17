package wee.digital.sample.ui.fragment

import android.annotation.SuppressLint
import android.util.Log
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import wee.digital.camera.toStringBase64
import wee.digital.sample.app.lib
import wee.digital.sample.repository.model.LivenessReq
import wee.digital.sample.repository.model.MatchingReq
import wee.digital.sample.repository.model.OcrReq
import wee.digital.sample.repository.model.SearchReq
import wee.digital.sample.shared.Configs
import wee.digital.sample.shared.Utils
import wee.digital.sample.ui.base.BaseViewModel

class ApiVM : BaseViewModel() {

    @SuppressLint("CheckResult")
    fun scanOCR(type: Int, sessionId: String, image: ByteArray) {
        Single.fromCallable {
            val body = OcrReq(
                    kioskId = Configs.KIOSK_ID,
                    type = type,
                    sessionId = sessionId,
                    image = image.toStringBase64()
            )
            lib?.kioskService!!.vpOCR(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.d("scanOCR","$it")
                }, {
                    Log.d("scanOCR","${it.message}")
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
                }, {
                    Log.d("matchingFrame","${it.message}")
                })
    }

    @SuppressLint("CheckResult")
    fun livenessFace(type: Int, idCardPhoto: String, video: String) {
        Single.fromCallable {
            val body = LivenessReq(
                    kioskId = Configs.KIOSK_ID,
                    type = type,
                    sessionId = Utils.getUUIDRandom(),
                    idCardPhoto = idCardPhoto,
                    livenessVideo = video
            )
            lib?.kioskService!!.faceLivenessVP(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.d("livenessFace","$it")
                }, {
                    Log.d("livenessFace","${it.message}")
                })
    }

    @SuppressLint("CheckResult")
    fun searchCustomer(type: Int, idCardPhoto: ByteArray) {
        Single.fromCallable {
            val body = SearchReq(
                    kioskId = Configs.KIOSK_ID,
                    type = type,
                    sessionId = Utils.getUUIDRandom(),
                    idCardPhoto = idCardPhoto.toStringBase64()
            )
            lib?.kioskService!!.faceSearchVP(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.d("searchCustomer","$it")
                }, {
                    Log.d("searchCustomer","${it.message}")
                })
    }
}