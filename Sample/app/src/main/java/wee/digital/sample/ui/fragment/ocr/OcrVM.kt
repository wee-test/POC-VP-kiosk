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
import wee.digital.sample.model.ExtractCardReq
import wee.digital.sample.model.VerifyIdCardReq
import wee.digital.sample.ui.base.BaseViewModel
import wee.digital.sample.widget.TextInputView

class OcrVM : BaseViewModel() {

    val statusExtractFront = MutableLiveData<Boolean>()

    val statusExtractBack = MutableLiveData<Boolean>()

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
                        if (t.responseCode.code == 0L) {
                            statusExtractFront.postValue(true)
                        } else {
                            statusExtractFront.postValue(false)
                        }
                    }


                    override fun onError(e: Throwable) {
                        statusExtractFront.postValue(false)
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
                        if (t.responseCode.code == 0L) {
                            statusExtractBack.postValue(true)
                        } else {
                            statusExtractBack.postValue(false)
                        }
                    }


                    override fun onError(e: Throwable) {
                        statusExtractBack.postValue(false)
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
                        if (t.responseCode.code == 0L) {
                            statusExtractFront.postValue(true)
                        } else {
                            statusExtractFront.postValue(false)
                        }
                    }


                    override fun onError(e: Throwable) {
                        statusExtractFront.postValue(false)
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
                        if (t.responseCode.code == 0L) {
                            statusExtractBack.postValue(true)
                        } else {
                            statusExtractBack.postValue(false)
                        }
                    }


                    override fun onError(e: Throwable) {
                        statusExtractBack.postValue(false)
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
                        if (t.responseCode.code == 0L) {
                            statusExtractFront.postValue(true)
                        } else {
                            statusExtractFront.postValue(false)
                        }
                    }

                    override fun onError(e: Throwable) {
                        statusExtractFront.postValue(false)
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
                        if (t.responseCode.code == 0L) {
                            statusExtractBack.postValue(true)
                        } else {
                            statusExtractBack.postValue(false)
                        }
                    }


                    override fun onError(e: Throwable) {
                        statusExtractBack.postValue(false)
                    }

                })
    }

}