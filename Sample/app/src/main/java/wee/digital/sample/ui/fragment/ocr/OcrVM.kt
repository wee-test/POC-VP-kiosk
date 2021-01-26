package wee.digital.sample.ui.fragment.ocr

import android.annotation.SuppressLint
import android.util.Base64
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vplib.*
import wee.digital.sample.app.lib
import wee.digital.sample.repository.model.BackCardResp
import wee.digital.sample.repository.model.ExtractCardReq
import wee.digital.sample.repository.model.FrontCardResp
import wee.digital.sample.ui.base.BaseViewModel

class OcrVM : BaseViewModel() {

    val statusExtractFront = MutableLiveData<FrontCardResp>()

    val statusExtractBack = MutableLiveData<BackCardResp>()

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
//                        statusExtractFront.postValue(t)
                        print("")
                    }


                    override fun onError(e: Throwable) {
                        print("")
                        /*statusExtractFront.postValue(null)*/
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
                        /*statusExtractBack.postValue(t)*/
                        print("")
                    }


                    override fun onError(e: Throwable) {
                        /*statusExtractBack.postValue(false)*/
                        print("")
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
                        /*statusExtractFront.postValue(true)*/
                    }


                    override fun onError(e: Throwable) {
                        /*statusExtractFront.postValue(null)*/
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
                        /*statusExtractBack.postValue(t)*/
                    }


                    override fun onError(e: Throwable) {
                        /*statusExtractBack.postValue(null)*/
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
                        /*statusExtractFront.postValue(t)*/
                    }

                    override fun onError(e: Throwable) {
                        /*statusExtractFront.postValue(null)*/
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

                    override fun onSuccess(t: ResponseExtractCCCDBack) {
                        /*statusExtractBack.postValue(t)*/
                    }


                    override fun onError(e: Throwable) {
                        /*statusExtractBack.postValue(null)*/
                    }

                })
    }

}