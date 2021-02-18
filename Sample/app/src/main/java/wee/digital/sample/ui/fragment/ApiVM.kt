package wee.digital.sample.ui.fragment

import android.annotation.SuppressLint
import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import wee.digital.camera.toStringBase64
import wee.digital.sample.app.lib
import wee.digital.sample.repository.model.*
import wee.digital.sample.shared.Configs
import wee.digital.sample.shared.Utils
import wee.digital.sample.ui.base.BaseViewModel
import wee.digital.sample.ui.base.EventLiveData

class ApiVM : BaseViewModel() {

    val statusExtractFront = EventLiveData<FrontCardResp>()

    val statusExtractBack = EventLiveData<BackCardResp>()

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
                    Log.e("extractNidFront", "$it")
                    if(it.responseCode.code != 0L){
                        statusExtractFront.postValue(FrontCardResp(code = -1))
                    }else{
                        val resp = FrontCardResp(
                                code = it.responseCode.code,
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
                    Log.e("extractNidFront", "$it")
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
                    Log.e("extractNidBack", "$it")
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
                    Log.e("extractNidBack", "${it.message}")
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
                    Log.e("extractNid12Front", "$it")
                    if(it.responseCode.code != 0L){
                        statusExtractFront.postValue(FrontCardResp(code = -1))
                    }else{
                        val resp = FrontCardResp(
                                code = it.responseCode.code,
                                image = it.frontInfo.image,
                                address = it.frontInfo.address,
                                number = it.frontInfo.address,
                                fullName = it.frontInfo.fullName,
                                birthday = it.frontInfo.birthday,
                                homeTown = it.frontInfo.homeTown,
                                expiryDate = it.frontInfo.expiryDate,
                                nationality = it.frontInfo.nationality,
                                sex = it.frontInfo.sex,
                                correctAddress = it.frontInfo.correctAddress,
                                correctHomeTown = it.frontInfo.correctHomeTown,
                                correctName = it.frontInfo.correctName
                        )
                        statusExtractFront.postValue(resp)
                    }
                }, {
                    Log.e("extractNid12Front", "${it.message}")
                    statusExtractFront.postValue(FrontCardResp(code = -1))
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
                    if(it.responseCode.code != 0L){
                        statusExtractBack.postValue(BackCardResp(code = -1))
                    }else{
                        val resp = BackCardResp(
                                code = it.responseCode.code,
                                image = it.backInfo.image,
                                issueDate = it.backInfo.issueDate
                        )
                        statusExtractBack.postValue(resp)
                    }
                }, {
                    statusExtractBack.postValue(BackCardResp(code = -1))
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
                    Log.e("extractCccdFront", "$it")
                    if(it.responseCode.code != 0L){
                        statusExtractFront.postValue(FrontCardResp(code = -1))
                    }else{
                        val resp = FrontCardResp(
                                code = it.responseCode.code,
                                image = it.frontInfo.image,
                                address = it.frontInfo.address,
                                fullName = it.frontInfo.fullName,
                                number = it.frontInfo.number,
                                birthday = it.frontInfo.birthday,
                                expiryDate = it.frontInfo.expiryDate,
                                sex = it.frontInfo.sex,
                                homeTown = it.frontInfo.homeTown
                        )
                        statusExtractFront.postValue(resp)
                    }
                }, {
                    Log.e("extractCccdFront", "${it.message}")
                    statusExtractFront.postValue(FrontCardResp(code = -1))
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
                    Log.e("extractCccdBack", "$it")
                    if(it.responseCode.code != 0L){
                        statusExtractBack.postValue(BackCardResp(code = -1))
                    }else{
                        val resp = BackCardResp(
                                code = it.responseCode.code,
                                image = it.backInfo.image,
                                issueDate = it.backInfo.issueDate
                        )
                        statusExtractBack.postValue(resp)
                    }
                }, {
                    Log.e("extractCccdBack", "${it.message}")
                    statusExtractBack.postValue(BackCardResp(code = -1))
                })
    }

}