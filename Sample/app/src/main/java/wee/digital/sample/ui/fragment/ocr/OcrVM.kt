package wee.digital.sample.ui.fragment.ocr

import android.annotation.SuppressLint
import android.util.Log
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import vplib.ResponseVPFaceSearch
import wee.digital.camera.toStringBase64
import wee.digital.sample.app.lib
import wee.digital.sample.repository.model.CardRespVP
import wee.digital.sample.repository.model.CheckingResultResp
import wee.digital.sample.repository.model.OcrReq
import wee.digital.sample.repository.model.SearchReq
import wee.digital.sample.shared.Configs
import wee.digital.sample.shared.Utils
import wee.digital.sample.ui.base.BaseViewModel
import wee.digital.sample.ui.base.EventLiveData
import java.util.regex.Matcher
import java.util.regex.Pattern

class OcrVM : BaseViewModel() {

    val statusExtractFrontVP = EventLiveData<CardRespVP>()

    val statusExtractBackVP = EventLiveData<CardRespVP>()

    val statusSearchVP = EventLiveData<ResponseVPFaceSearch>()

    @SuppressLint("CheckResult")
    fun scanOCRFrontVP(type: Int, sessionId: String, image: ByteArray) {
        Single.fromCallable {
            val body = OcrReq(
                    kioskId = Configs.KIOSK_ID,
                    type = type,
                    sessionId = sessionId,
                    image = image.toStringBase64()
            )
            Log.d("scanOCRFront", body.toString())
            lib?.kioskService!!.vpOCR(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.d("scanOCRFront", "$it")
                    if (it.responseCode.code != 0L) {
                        statusExtractFrontVP.postValue(CardRespVP(code = -1))
                    } else {
                        val checkingResult = CheckingResultResp(
                                recapturedResult = it.result.data.checkingResult.recapturedResult,
                                checkPhotocopiedResult = it.result.data.checkingResult.checkPhotocopiedResult,
                                editedResult = it.result.data.checkingResult.editedResult,
                                cornerCutResult = it.result.data.checkingResult.cornerCutResult,
                                editedProb = it.result.data.checkingResult.editedProb,
                                recapturedProb = it.result.data.checkingResult.recapturedProb,
                                checkPhotocopiedProb = it.result.data.checkingResult.checkPhotocopiedProb,
                                checkTitleResult = it.result.data.checkingResult.checkTitleResult,
                                checkEmblemResult = it.result.data.checkingResult.checkEmblemResult,
                                checkEmblemProb = it.result.data.checkingResult.checkEmblemProb,
                                checkFingerprintResult = it.result.data.checkingResult.checkFingerprintResult,
                                checkStampResult = it.result.data.checkingResult.checkStampResult,
                                checkEmbossedStampResult = it.result.data.checkingResult.checkEmbossedStampResult,
                                checkEmbossedStampProb = it.result.data.checkingResult.checkEmbossedStampProb,
                                checkBorderResult = it.result.data.checkingResult.checkBorderResult,
                                checkBorderProb = it.result.data.checkingResult.checkBorderProb
                        )
                        val data = CardRespVP(
                                code = 0,
                                id = it.result.data.id,
                                idProb = it.result.data.idProb,
                                name = it.result.data.name,
                                nameProb = it.result.data.nameProb,
                                dob = it.result.data.dob,
                                dobProb = it.result.data.dobProb,
                                sex = it.result.data.sex,
                                sexProb = it.result.data.sexProb,
                                nationality = it.result.data.nationality,
                                nationalityProb = it.result.data.nationalityProb,
                                home = it.result.data.home,
                                homeProb = it.result.data.homeProb,
                                address = it.result.data.address,
                                addressProb = it.result.data.addressProb,
                                typeNew = it.result.data.typeNew,
                                doe = it.result.data.doe,
                                doeProb = it.result.data.doeProb,
                                type = it.result.data.typeNew,
                                ward = it.result.data.ward,
                                district = it.result.data.district,
                                province = it.result.data.province,
                                street = it.result.data.street,
                                ethnicityProb = it.result.data.ethnicityProb,
                                religion = it.result.data.religion,
                                religionProb = it.result.data.religionProb,
                                features = it.result.data.features,
                                featuresProb = it.result.data.featuresProb,
                                issueDate = it.result.data.issueDate,
                                issueDateProb = it.result.data.issueDateProb,
                                issueLoc = it.result.data.issueLoc,
                                issueLocProb = it.result.data.issueLocProb,
                                passportNumber = it.result.data.passportNumber,
                                passportNumberProb = it.result.data.passportNumberProb,
                                idNumber = it.result.data.idNumber,
                                idNumberProb = it.result.data.idNumberProb,
                                doi = it.result.data.doi,
                                doiProb = it.result.data.doiProb,
                                checkingResult = checkingResult
                        )
                        statusExtractFrontVP.postValue(data)
                    }
                }, {
                    Log.d("scanOCRFront", "${it.message}")
                    statusExtractFrontVP.postValue(CardRespVP(code = -1))
                })
    }

    @SuppressLint("CheckResult")
    fun scanOCRBackVP(type: Int, sessionId: String, image: ByteArray) {
        Single.fromCallable {
            val body = OcrReq(
                    kioskId = Configs.KIOSK_ID,
                    type = type,
                    sessionId = sessionId,
                    image = image.toStringBase64()
            )
            Log.d("scanOCRBack", body.toString())
            lib?.kioskService!!.vpOCR(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.d("scanOCRBack", "$it")
                    if (it.responseCode.code != 0L) {
                        statusExtractBackVP.postValue(CardRespVP(code = -1))
                    } else {
                        val data = CardRespVP(
                                code = 0,
                                issueDate = it.result.data.issueDate,
                                issueLoc = it.result.data.issueLoc
                        )
                        statusExtractBackVP.postValue(data)
                    }
                }, {
                    Log.d("scanOCRBack", "$it")
                    statusExtractBackVP.postValue(CardRespVP(code = -1))
                })
    }

    @SuppressLint("CheckResult")
    fun searchCustomer(idCardPhoto: String) {
        Single.fromCallable {
            val body = SearchReq(
                    kioskId = Configs.KIOSK_ID,
                    sessionId = Utils.getUUIDRandom(),
                    idCardPhoto = idCardPhoto
            )
            lib?.kioskService!!.faceSearchVP(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    statusSearchVP.postValue(it)
                }, {
                    statusSearchVP.postValue(null)
                })
    }

    fun isEmailValid(email: String?): Boolean {
        email ?: return true
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(email)
        return matcher.matches()
    }

}