package wee.digital.sample.ui.fragment.ocr

import android.annotation.SuppressLint
import android.util.Base64
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vplib.*
import wee.digital.sample.app.lib
import wee.digital.sample.model.ExtractCardReq
import wee.digital.sample.model.VerifyIdCardReq
import wee.digital.sample.ui.base.BaseViewModel

class OcrVM : BaseViewModel() {

    fun verifyIdCard(cardImage: ByteArray, faceImage: ByteArray) {
        Single.fromCallable {
            val body = VerifyIdCardReq(
                    cardImage = Base64.encodeToString(cardImage, Base64.NO_WRAP),
                    faceImage = Base64.encodeToString(faceImage, Base64.NO_WRAP)
            )
            lib?.kioskService!!.faceVerifyToIDCard(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(object : SingleObserver<ResponseFaceVerifyToIDCard> {

                    override fun onSubscribe(d: Disposable) {}

                    override fun onSuccess(t: ResponseFaceVerifyToIDCard) {

                    }

                    override fun onError(e: Throwable) {

                    }

                })
    }

    @SuppressLint("CheckResult")
    fun extractNidFront(image: ByteArray) {
        Single.fromCallable {
            val body = ExtractCardReq(
                    cardImage = Base64.encodeToString(image, Base64.NO_WRAP)
            )
            lib?.kioskService!!.extractCMNDFrontInfo(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(object : SingleObserver<ResponseExtractCMNDFront> {

                    override fun onSubscribe(d: Disposable) {}

                    override fun onSuccess(t: ResponseExtractCMNDFront) {

                    }


                    override fun onError(e: Throwable) {

                    }

                })
    }

    @SuppressLint("CheckResult")
    fun extractNidBack(image: ByteArray) {
        Single.fromCallable {
            val body = ExtractCardReq(
                    cardImage = Base64.encodeToString(image, Base64.NO_WRAP)
            )
            lib?.kioskService!!.extractCMNDBackInfo(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(object : SingleObserver<ResponseExtractCMNDBack> {

                    override fun onSubscribe(d: Disposable) {}

                    override fun onSuccess(t: ResponseExtractCMNDBack) {

                    }


                    override fun onError(e: Throwable) {

                    }

                })
    }

    @SuppressLint("CheckResult")
    fun extractNid12Front(image: ByteArray) {
        Single.fromCallable {
            val body = ExtractCardReq(
                    cardImage = Base64.encodeToString(image, Base64.NO_WRAP)
            )
            lib?.kioskService!!.extractCMND12FrontInfo(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(object : SingleObserver<ResponseExtractCMND12Front> {

                    override fun onSubscribe(d: Disposable) {}

                    override fun onSuccess(t: ResponseExtractCMND12Front) {

                    }


                    override fun onError(e: Throwable) {

                    }

                })
    }

    @SuppressLint("CheckResult")
    fun extractNid12Back(image: ByteArray) {
        Single.fromCallable {
            val body = ExtractCardReq(
                    cardImage = Base64.encodeToString(image, Base64.NO_WRAP)
            )
            lib?.kioskService!!.extractCMND12BackInfo(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(object : SingleObserver<ResponseExtractCMND12Back> {

                    override fun onSubscribe(d: Disposable) {}

                    override fun onSuccess(t: ResponseExtractCMND12Back) {

                    }


                    override fun onError(e: Throwable) {

                    }

                })
    }

    @SuppressLint("CheckResult")
    fun extractCccdFront(image: ByteArray) {
        Single.fromCallable {
            val body = ExtractCardReq(
                    cardImage = Base64.encodeToString(image, Base64.NO_WRAP)
            )
            lib?.kioskService!!.extractCCCDFrontInfo(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(object : SingleObserver<ResponseExtractCCCDFront> {

                    override fun onSubscribe(d: Disposable) {}

                    override fun onSuccess(t: ResponseExtractCCCDFront) {

                    }


                    override fun onError(e: Throwable) {

                    }

                })
    }

    @SuppressLint("CheckResult")
    fun extractCccdBack(image: ByteArray) {
        Single.fromCallable {
            val body = ExtractCardReq(
                    cardImage = Base64.encodeToString(image, Base64.NO_WRAP)
            )
            lib?.kioskService!!.extractCCCDBackInfo(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(object : SingleObserver<ResponseExtractCCCDBack> {

                    override fun onSubscribe(d: Disposable) {}

                    override fun onSuccess(t: ResponseExtractCCCDBack) {}


                    override fun onError(e: Throwable) {

                    }

                })
    }

}