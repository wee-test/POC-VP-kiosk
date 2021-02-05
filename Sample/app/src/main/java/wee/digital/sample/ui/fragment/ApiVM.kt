package wee.digital.sample.ui.fragment

import android.annotation.SuppressLint
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import wee.digital.sample.app.lib
import wee.digital.sample.repository.model.LivenessReq
import wee.digital.sample.repository.model.MatchingReq
import wee.digital.sample.repository.model.OcrReq
import wee.digital.sample.repository.model.SearchReq
import wee.digital.sample.shared.Configs
import wee.digital.sample.ui.base.BaseViewModel

class ApiVM : BaseViewModel() {

    @SuppressLint("CheckResult")
    fun scanOCR(type: Int, sessionId: String, image: String) {
        Single.fromCallable {
            val body = OcrReq(
                    kioskId = Configs.KIOSK_ID,
                    type = type,
                    sessionId = sessionId,
                    image = image
            )
            lib?.kioskService!!.vpOCR(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe({

                }, {

                })
    }

    @SuppressLint("CheckResult")
    fun matchingFrame(type: Int, sessionId: String, idCardPhoto: String, face: String) {
        Single.fromCallable {
            val body = MatchingReq(
                    kioskId = Configs.KIOSK_ID,
                    type = type,
                    sessionId = sessionId,
                    idCardPhoto = idCardPhoto,
                    facePhoto = face
            )
            lib?.kioskService!!.faceMatchingVP(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe({

                }, {

                })
    }

    @SuppressLint("CheckResult")
    fun livenessFace(type: Int, sessionId: String, idCardPhoto: String, video: String) {
        Single.fromCallable {
            val body = LivenessReq(
                    kioskId = Configs.KIOSK_ID,
                    type = type,
                    sessionId = sessionId,
                    idCardPhoto = idCardPhoto,
                    livenessVideo = video
            )
            lib?.kioskService!!.faceLivenessVP(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe({

                }, {

                })
    }

    @SuppressLint("CheckResult")
    fun searchCustomer(type: Int, sessionId: String, idCardPhoto: String) {
        Single.fromCallable {
            val body = SearchReq(
                    kioskId = Configs.KIOSK_ID,
                    type = type,
                    sessionId = sessionId,
                    idCardPhoto = idCardPhoto
            )
            lib?.kioskService!!.faceSearchVP(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe({

                }, {

                })
    }

}