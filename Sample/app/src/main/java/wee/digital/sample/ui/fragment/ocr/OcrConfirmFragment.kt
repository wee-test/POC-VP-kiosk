package wee.digital.sample.ui.fragment.ocr

import android.view.View
import kotlinx.android.synthetic.main.ocr_confirm_content.*
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.repository.model.IdentifyCardInfo
import wee.digital.sample.shared.Shared
import wee.digital.sample.shared.Utils
import wee.digital.sample.ui.base.viewModel
import wee.digital.sample.ui.main.MainFragment


class OcrConfirmFragment : MainFragment() {

    private val ocrVM: OcrVM by lazy { viewModel(OcrVM::class) }

    override fun layoutResource(): Int {
        return R.layout.ocr_confirm
    }

    override fun onViewCreated() {
        ocrInputBirth.addDateWatcher()
        ocrInputIssueDate.addDateWatcher()
        ocrInputIssuePlace.buildSelectable(mainVM, Shared.provinceList)
        ocrInputGender.buildSelectable(mainVM, Shared.genderList)
        addClickListener(ocrConfirmActionNext)
    }

    override fun onViewClick(v: View?) {
        when (v) {
            ocrConfirmActionNext -> {
                if (!checkValidData()) return
                navigate(MainDirections.actionGlobalRegisterFragment())
            }
        }
    }

    override fun onLiveDataObserve() {
        Shared.ocrCardFront.observe {
            ocrInputFullName.text = it.fullName
            ocrInputNumber.text = it.number
            ocrInputBirth.text = it.birthday.replace(" ", "/")
            ocrInputGender.text = it.sex
            ocrInputHometown.text = it.homeTown
            ocrInputAddress.text = it.address
        }
        Shared.ocrCardBack.observe {
            ocrInputIssueDate.text = it.issueDate
            ocrInputIssuePlace.text = it.issueBy
        }
    }


    private fun checkValidData(): Boolean {
        val name = ocrInputFullName.text.toString()
        val nameSplit = name.split("")
        if (name.isEmpty() || name.length < 3 || nameSplit.size < 2) {
            ocrInputFullName.error = "Họ và tên không hợp lệ"
            return false
        }
        val numberCard = ocrInputNumber.text.toString()
        if (numberCard.isEmpty() || numberCard.length != Utils.lengthNumberCard(Shared.typeCardOcr.value)) {
            ocrInputNumber.error = "số giấy tờ không đúng"
            return false
        }
        val issueDate = ocrInputIssueDate.text.toString()
        val listIssueData = issueDate.split("/")
        if (issueDate.isEmpty() || listIssueData.size < 3) {
            ocrInputIssueDate.error = "vui lòng nhập ngày cấp"
            return false
        }
        val issuePlace = ocrInputIssuePlace.text.toString()
        if (issuePlace.isEmpty() || issuePlace.length < 10) {
            ocrInputIssuePlace.error = "nơi cấp phải từ 10 đến 50 ký tự"
            return false
        }
        val birth = ocrInputBirth.text.toString()
        val listBirth = birth.split("/")
        if (birth.isNullOrEmpty() || listBirth.size < 3) {
            ocrInputBirth.error = "Vui lòng chọn ngày sinh"
            return false
        }
        if (ocrInputGender.text.toString().isEmpty()) {
            ocrInputGender.error = "Vui lòng chọn giới tính"
            return false
        }
        val hometown = ocrInputHometown.text.toString()
        if (hometown.isEmpty() || hometown.length < 10) {
            ocrInputHometown.error = "Nguyên quán phải từ 10 đến 50 ký tự"
            return false
        }
        val address = ocrInputAddress.text.toString()
        if (address.isEmpty() || address.length < 10) {
            ocrInputAddress.error = "Địa chỉ thường chú phải từ 10 đến 50 ký tự"
            return false
        }
        if (Utils.checkPhoneInvalid(ocrInputPhone.text.toString())) {
            ocrInputPhone.error = "Số điện thoại không đúng"
            return false
        }
        if (ocrInputEmail.text.isNullOrEmpty()) {
            ocrInputEmail.error = "Email không hợp lệ"
            return false
        }
        val gender = when (ocrInputGender.text) {
            "Nam" -> 1
            "Nữ" -> 2
            else -> 3
        }
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
                expiredDate = "",
                nationality = ""
        )
        Shared.ocrConfirmData.postValue(data)
        return true
    }

}