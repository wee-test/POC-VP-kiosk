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
import wee.digital.sample.ui.base.EventLiveData

class OcrVM : BaseViewModel() {

    val statusExtractFront = EventLiveData<FrontCardResp>()

    val statusExtractBack = EventLiveData<BackCardResp>()

    @SuppressLint("CheckResult")
    fun extractNidFront(image: ByteArray) {
        Single.fromCallable {
            val body = ExtractCardReq(
                    cardImage = Base64.encodeToString(image, Base64.NO_WRAP)
            )
            lib?.kioskService!!.extractCMNDFrontInfo(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    if(it.responseCode.code != 0L){
                        statusExtractFront.postValue(FrontCardResp(code = -1))
                    }else{
                        val resp = FrontCardResp(
                                code = 0,
                                image = it.frontInfo.image,
                                address = it.frontInfo.address,
                                number = it.frontInfo.number,
                                birthday = it.frontInfo.birthday,
                                fullName = it.frontInfo.fullName,
                                homeTown = it.frontInfo.homeTown
                        )
                        statusExtractFront.postValue(resp)
                    }
                },{
                    statusExtractFront.postValue(FrontCardResp(code = -1))
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
                .subscribe({
                    if(it.responseCode.code != 0L){
                        statusExtractBack.postValue(BackCardResp(code = -1))
                    }else{
                        val resp = BackCardResp(
                                code = it.responseCode.code,
                                image = it.backInfo.image,
                                issueDate = it.backInfo.issueDate,
                                issueBy = it.backInfo.issueBy,
                        )
                        statusExtractBack.postValue(resp)
                    }
                },{
                    statusExtractBack.postValue(BackCardResp(code = -1))
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
                .subscribe({
                    print("")
                },{
                    print("")
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
                .subscribe({
                    print("")
                },{
                    print("")
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
                .subscribe({
                    print("")
                },{
                    print("")
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
                .subscribe({
                    print("")
                },{
                    print("")
                })
    }

}