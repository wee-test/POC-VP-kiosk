package wee.digital.sample.ui.fragment.ocr

import android.view.View
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.ocr_confirm_content.*
import wee.digital.library.extension.addViewClickListener
import wee.digital.library.extension.gone
import wee.digital.library.extension.show
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.repository.model.IdentifyCardInfo
import wee.digital.sample.repository.model.SocketReq
import wee.digital.sample.repository.socket.Socket.Companion.action
import wee.digital.sample.shared.Configs
import wee.digital.sample.shared.Shared
import wee.digital.sample.shared.Utils
import wee.digital.sample.shared.VoiceData
import wee.digital.sample.ui.base.viewModel
import wee.digital.sample.ui.main.MainFragment
import wee.digital.sample.util.extention.Voice
import wee.digital.sample.widget.TextInputView
import java.net.Socket
import java.util.concurrent.TimeUnit


class OcrConfirmFragment : MainFragment(), TextInputView.TextInputListener {

    private var disposableSendSocket: Disposable? = null

    var errorMessage = ""

    override fun layoutResource(): Int {
        return R.layout.ocr_confirm
    }

    override fun onViewCreated() {
        addClickListener(ocrConfirmActionNext)
        ocrInputIssuePlace.showIconDrop()
        ocrInputGender.showIconDrop()
        ocrInputIssuePlace.addViewClickListener {
            ocrInputIssuePlace.buildSelectable(mainVM, Shared.provinceList)
        }
        ocrInputGender.addViewClickListener {
            ocrInputGender.buildSelectable(mainVM, Shared.genderList)
        }
        ocrInputBirth.addViewClickListener {
            ocrInputBirth.buildDatePicker(this)
        }
        ocrInputIssueDate.addViewClickListener {
            ocrInputIssueDate.buildDatePicker(this)
        }
        ocrInputExDate.addViewClickListener{
            ocrInputExDate.buildDatePicker(this)
        }
        initListenerInput()
        Voice.ins?.request(VoiceData.OCR_OKE)
    }

    private fun uiPassport(){

    }

    private fun uiCard(type : String){

    }

    private fun initListenerInput() {
        ocrInputFullName.initListener(this)
        ocrInputNumber.initListener(this)
        ocrInputIssueDate.initListener(this)
        ocrInputIssuePlace.initListener(this)
        ocrInputBirth.initListener(this)
        ocrInputGender.initListener(this)
        ocrInputHometown.initListener(this)
        ocrInputAddress.initListener(this)
        ocrInputPhone.initListener(this)
        ocrInputEmail.initListener(this)
        ocrInputExDate.initListener(this)
    }

    override fun onViewClick(v: View?) {
        when (v) {
            ocrConfirmActionNext -> {
                if (!checkValidData()) {
                    if(errorMessage.isNotEmpty()) {
                        Voice.ins?.request(errorMessage)
                    }
                    return
                }
                sendSocket()
                action.sendData(SocketReq(cmd = Configs.END_STEP))
                navigate(MainDirections.actionGlobalRegisterFragment())
            }
        }
    }

    override fun onLiveDataObserve() {
        if (Shared.typeCardOcr.value == Configs.TYPE_NID) {
            ocrInputExDate.gone()
        } else {
            ocrInputExDate.show()
        }
        if (Shared.typeCardOcr.value == Configs.TYPE_PASSPORT) {
            Shared.passportData.observe {
                ocrInputFullName.text = "${it.firstName} ${it.lassName}"
                ocrInputNumber.text = it.idNumber
                ocrInputIssueDate.text = Utils.getIssueDatePassport(it.exDate)
                ocrInputExDate.text = it.exDate
                ocrInputIssuePlace.hint = "Loại"
                ocrInputIssuePlace.text = it.type
                ocrInputBirth.text = it.birthDay
                ocrInputGender.text = when (it.gender) {
                    "M" -> "Nam"
                    "F" -> "Nữ"
                    else -> "Nam"
                }
                ocrInputHometown.hint = "Quốc gia"
                ocrInputHometown.text = it.nationCode
            }
        } else {
            Shared.ocrCardFront.observe {
                ocrInputFullName.text = it.fullName
                ocrInputNumber.text = it.number
                ocrInputBirth.text = it.birthday.replace("-", "/")
                ocrInputGender.text = it.sex
                ocrInputExDate.text = it.expiryDate
                ocrInputHometown.text = it.homeTown
                ocrInputAddress.text = it.address
            }
            Shared.ocrCardBack.observe {
                ocrInputIssueDate.text = it.issueDate.replace(" ", "/")
                ocrInputIssuePlace.text = it.issueBy
            }
        }
    }

    private fun checkValidData(): Boolean {
        val name = ocrInputFullName.text.toString()
        val nameSplit = name.split("")
        if (name.isEmpty() || name.length < 3 || nameSplit.size < 2) {
            errorMessage = "Họ và tên không hợp lệ"
            ocrInputFullName.error = errorMessage
            return false
        }
        val numberCard = ocrInputNumber.text.toString()
        if (numberCard.isEmpty() || numberCard.length != Utils.lengthNumberCard(Shared.typeCardOcr.value)) {
            errorMessage = "số giấy tờ không đúng"
            ocrInputNumber.error = errorMessage
            return false
        }
        val issueDate = ocrInputIssueDate.text.toString()
        val listIssueData = issueDate.split("/")
        if (issueDate.isEmpty() || listIssueData.size < 3) {
            errorMessage = "vui lòng nhập ngày cấp"
            ocrInputIssueDate.error = errorMessage
            return false
        }
        val exDate = ocrInputExDate.text.toString()
        val listExData = exDate.split("/")
        if ((exDate.isEmpty() || listExData.size < 3) && Shared.typeCardOcr.value != Configs.TYPE_NID) {
            errorMessage = "vui lòng nhập ngày cấp"
            ocrInputExDate.error = errorMessage
            return false
        }
        val issuePlace = ocrInputIssuePlace.text.toString()
        if (issuePlace.isEmpty()) {
            errorMessage = "Vui lòng nhập nguyên quán nơi cấp"
            ocrInputIssuePlace.error = errorMessage
            return false
        }
        val birth = ocrInputBirth.text.toString()
        val listBirth = birth.split("/")
        if (birth.isNullOrEmpty() || listBirth.size < 3) {
            errorMessage = "Vui lòng chọn ngày sinh"
            ocrInputBirth.error = errorMessage
            return false
        }
        if (ocrInputGender.text.toString().isEmpty()) {
            errorMessage = "Vui lòng chọn giới tính"
            ocrInputGender.error = errorMessage
            return false
        }
        val hometown = ocrInputHometown.text.toString()
        if (hometown.isEmpty()) {
            errorMessage = "Vui lòng nhập nguyên quán"
            ocrInputHometown.error = errorMessage
            return false
        }
        val address = ocrInputAddress.text.toString()
        if (address.isEmpty() || address.length < 10) {
            errorMessage = "Vui lòng nhập địa chỉ thường chú"
            ocrInputAddress.error = errorMessage
            return false
        }
        if (Utils.checkPhoneInvalid(ocrInputPhone.text.toString())) {
            errorMessage = "Số điện thoại không đúng"
            ocrInputPhone.error = errorMessage
            return false
        }
        if (ocrInputEmail.text.isNullOrEmpty()) {
            errorMessage = "Email không hợp lệ"
            ocrInputEmail.error = errorMessage
            return false
        }
        val gender = when (ocrInputGender.text) {
            "Nam" -> 1
            "Nữ" -> 2
            else -> 3
        }
        errorMessage = ""
        val data = IdentifyCardInfo(
                type = Shared.typeCardOcr.value ?: "",
                photo = Shared.frameCardData.value,
                number = numberCard,
                fullName = name,
                dateOfBirth = birth,
                gender = gender,
                hometown = hometown,
                permanentAddress = address,
                issuedDate = issueDate,
                issuedPlace = issuePlace,
                expiredDate = exDate,
                phone = ocrInputPhone.text.toString(),
                nationality = Shared.ocrCardFront.value?.nationality ?: "Việt Nam",
        )
        Shared.ocrConfirmData.postValue(data)
        return true
    }

    private fun sendSocket() {
        val resp = Shared.socketReqData.value
        resp?.cmd = Configs.FORM_STEP_3
        resp?.data?.fullName = ocrInputFullName.text.toString()
        resp?.data?.idCardNumber = ocrInputNumber.text.toString()
        resp?.data?.issuedDate = ocrInputIssueDate.text.toString()
        resp?.data?.issuedPlace = ocrInputIssuePlace.text.toString()
        resp?.data?.dateOfBirth = ocrInputBirth.text.toString()
        resp?.data?.expiredDate = if (ocrInputExDate.text.toString() == "null") "" else ocrInputExDate.text.toString()
        resp?.data?.gender = when (ocrInputGender.text) {
            "Nam" -> 1
            "Nữ" -> 2
            else -> 3
        }
        resp?.data?.hometown = ocrInputHometown.text.toString()
        resp?.data?.permanentAddress = ocrInputAddress.text.toString()
        resp?.data?.phoneNumber = ocrInputPhone.text.toString()
        resp?.data?.email = ocrInputEmail.text.toString()
        action.sendData(resp)
    }

    /**
     * implement text change
     */
    override fun onChange() {
        disposableSendSocket?.dispose()
        disposableSendSocket = Observable.timer(3, TimeUnit.SECONDS)
                .subscribe { sendSocket() }
    }

    override fun onPause() {
        super.onPause()
        disposableSendSocket?.dispose()
    }

}