package wee.digital.sample.ui.fragment.passport

import android.view.View
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.ocr_confirm_content.*
import kotlinx.android.synthetic.main.passport_confirm_content.*
import wee.digital.camera.toBytes
import wee.digital.camera.toStringBase64
import wee.digital.library.extension.addViewClickListener
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.repository.model.IdentifyCardInfo
import wee.digital.sample.repository.model.PhotoCardInfo
import wee.digital.sample.repository.model.SocketReq
import wee.digital.sample.repository.socket.Socket
import wee.digital.sample.shared.Configs
import wee.digital.sample.shared.Shared
import wee.digital.sample.shared.Utils
import wee.digital.sample.ui.main.MainFragment
import wee.digital.sample.util.extention.Voice
import wee.digital.sample.widget.TextInputView
import java.util.concurrent.TimeUnit

class PassportConfirmFragment : MainFragment(), TextInputView.TextInputListener {

    private var disposable: Disposable? = null

    var errorMessage = ""

    override fun layoutResource(): Int = R.layout.passport_confirm

    override fun onViewCreated() {
        initListener()
        addClickListener(passportConfirmActionNext)
        passportInputExDate.addViewClickListener {
            passportInputExDate.buildDatePicker(this) {}
        }
        passportInputBirths.addViewClickListener {
            passportInputBirths.buildDatePicker(this) {}
        }
        passportInputGender.addViewClickListener {
            passportInputGender.buildSelectable(mainVM, Shared.genderList)
        }
    }

    private fun initListener() {
        passportInputFullName.initListener(this)
        passportInputNumberNid.initListener(this)
        passportInputInputNationality.initListener(this)
        passportInputNumberPassport.initListener(this)
        passportInputExDate.initListener(this)
        passportInputBirths.initListener(this)
        passportInputGender.initListener(this)
        passportInputType.initListener(this)
        passportInputCode.initListener(this)
        passportInputPhone.initListener(this)
        passportInputEmail.initListener(this)
    }

    override fun onLiveDataObserve() {
        Shared.passportData.observe {
            passportInputFullName.text = "${it.firstName} ${it.lassName}"
            passportInputNumberNid.text = "${it.idNumber}"
            passportInputInputNationality.text = Utils.getNameNational(it.nationCode)
            passportInputNumberPassport.text = "${it.documentNumber}"
            passportInputExDate.text = "${it.exDate}"
            passportInputBirths.text = "${it.birthDay}"
            passportInputGender.text = when (it.gender) {
                "P" -> "Nam"
                "F" -> "Nữ"
                else -> "Nam"
            }
            passportInputType.text = "${it.type}"
            passportInputCode.text = "${it.nationCode}"
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            passportConfirmActionNext -> {
                if (!checkValid()) {
                    if (errorMessage.isNotEmpty()) {
                        Voice.ins?.request(errorMessage)
                    }
                    return
                }
                sendSocket()
                Socket.action.sendData(SocketReq(cmd = Configs.END_STEP))
                navigate(MainDirections.actionGlobalRegisterFragment())
            }
        }
    }

    override fun onChange() {
        disposable?.dispose()
        disposable = Observable.timer(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    sendSocket()
                }, {})
    }

    private fun checkValid(): Boolean {
        val name = passportInputFullName.text.toString()
        val nameSplit = name.split(" ")
        if (name.isEmpty() || name.length < 3 || nameSplit.size < 2) {
            errorMessage = "Họ và tên không hợp lệ"
            passportInputFullName.error = errorMessage
            return false
        }
        val numberNid = passportInputNumberNid.text.toString()
        if (numberNid.isEmpty() || numberNid.length != Utils.lengthNumberCard(Shared.typeCardOcr.value)) {
            errorMessage = "số giấy tờ không đúng"
            passportInputNumberNid.error = errorMessage
            return false
        }
        if (passportInputInputNationality.text.isNullOrEmpty()) {
            errorMessage = "Vui lòng nhập quốc tịch"
            passportInputNumberNid.error = errorMessage
            return false
        }
        if (passportInputNumberPassport.text.isNullOrEmpty()) {
            errorMessage = "Vui lòng nhập số hộ chiếu"
            passportInputNumberNid.error = errorMessage
            return false
        }
        val exDate = passportInputExDate.text.toString()
        val listExData = exDate.split("/")
        if (exDate.isEmpty() || listExData.size < 3) {
            errorMessage = "vui lòng nhập ngày hết hạn"
            passportInputExDate.error = errorMessage
            return false
        }
        val birth = passportInputBirths.text.toString()
        val listBirth = birth.split("/")
        if (birth.isNullOrEmpty() || listBirth.size < 3) {
            errorMessage = "Vui lòng chọn ngày sinh"
            passportInputBirths.error = errorMessage
            return false
        }
        if (passportInputGender.text.isNullOrEmpty()) {
            errorMessage = "Vui lòng chọn giới tính"
            passportInputBirths.error = errorMessage
            return false
        }
        if (passportInputType.text.isNullOrEmpty()) {
            errorMessage = "Vui lòng nhập type passport"
            passportInputType.error = errorMessage
            return false
        }
        if (Utils.checkPhoneInvalid(passportInputPhone.text.toString())) {
            errorMessage = "Số điện thoại không đúng"
            passportInputPhone.error = errorMessage
            return false
        }
        if (passportInputEmail.text.isNullOrEmpty()) {
            errorMessage = "Email không hợp lệ"
            passportInputEmail.error = errorMessage
            return false
        }
        val gender = when (passportInputGender.text) {
            "Nam" -> 1
            "Nữ" -> 2
            else -> 3
        }
        errorMessage = ""
        val photo = PhotoCardInfo(
                cardFront = Shared.passportData.value?.frame.toBytes().toStringBase64(),
                cardBack = Shared.passportData.value?.frame.toBytes().toStringBase64()
        )
        val data = IdentifyCardInfo(
                type = Configs.TYPE_PASSPORT,
                photo = photo,
                number = numberNid,
                passportCode = passportInputCode.text.toString(),
                passportType = passportInputType.text.toString(),
                passportNumber = passportInputNumberPassport.text.toString(),
                fullName = name,
                dateOfBirth = birth,
                gender = gender,
                hometown = "",
                permanentAddress = "",
                issuedDate = Utils.getIssueDatePassport(exDate),
                issuedPlace = "",
                expiredDate = exDate,
                phone = passportInputPhone.text.toString(),
                email = passportInputEmail.text.toString(),
                nationality = passportInputInputNationality.text.toString(),
        )
        Shared.ocrConfirmData.postValue(data)
        return true
    }

    private fun sendSocket() {
        val resp = Shared.socketReqData.value
        resp?.cmd = Configs.FORM_STEP_3
        resp?.data?.fullName = passportInputFullName.text.toString()
        resp?.data?.idCardNumber = passportInputNumberNid.text.toString()
        resp?.data?.passportNumber = passportInputNumberPassport.text.toString()
        resp?.data?.passportCode = passportInputCode.text.toString()
        resp?.data?.passportType = passportInputType.text.toString()
        resp?.data?.issuedDate = Utils.getIssueDatePassport(passportInputExDate.text.toString())
        resp?.data?.dateOfBirth = passportInputBirths.text.toString()
        resp?.data?.expiredDate = passportInputExDate.text.toString()
        resp?.data?.gender = when (passportInputGender.text) {
            "Nam" -> 1
            "Nữ" -> 2
            else -> 3
        }
        resp?.data?.phoneNumber = passportInputPhone.text.toString()
        resp?.data?.email = passportInputEmail.text.toString()
        Socket.action.sendData(resp)
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

}