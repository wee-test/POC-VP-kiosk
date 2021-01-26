package wee.digital.sample.ui.fragment.card

import android.view.View
import com.google.gson.annotations.SerializedName
import kotlinx.android.synthetic.main.card_receive_method.*
import wee.digital.library.extension.toast
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.repository.model.BranchInfo
import wee.digital.sample.repository.model.HomeInfo
import wee.digital.sample.repository.model.MethodOfReceiving
import wee.digital.sample.repository.socket.Socket
import wee.digital.sample.shared.Configs
import wee.digital.sample.shared.Shared
import wee.digital.sample.shared.Utils
import wee.digital.sample.ui.main.MainFragment

class CardReceiveFragment : MainFragment() {

    override fun layoutResource(): Int {
        return R.layout.card_receive_method
    }

    override fun onViewCreated() {
        addClickListener(cardReceiveActionNext)
        receiveInputProvince.buildSelectable(mainVM, Shared.provinceList)
        receiveInputDistrict.buildSelectable(mainVM, Shared.provinceList)
        receiveInputWard.buildSelectable(mainVM, Shared.provinceList)
    }

    override fun onViewClick(v: View?) {
        when (v) {
            cardReceiveActionNext -> {
                if (!checkValidData()) return
                val codeReceiver = when {
                    receiveRadioDirectly.isChecked -> 1
                    receiveRadioHome.isChecked -> 2
                    receiveRadioBranch.isChecked -> 3
                    else -> 1
                }
                sendSocket(codeReceiver)
                navigate(MainDirections.actionGlobalReviewFragment())
            }
        }
    }

    override fun onLiveDataObserve() {}

    private fun checkValidData(): Boolean {
        if (!receiveRadioDirectly.isChecked && !receiveRadioHome.isChecked && !receiveRadioBranch.isChecked) {
            toast("Vui lòng chọn hình thức nhận thẻ")
            return false
        }
        if (receiveRadioDirectly.isChecked) {
            sendSocket(1)
            val body = MethodOfReceiving(type = 1)
            Shared.methodReceiveCard.postValue(body)
            return true
        }
        if (receiveRadioBranch.isChecked) {
            sendSocket(3)
            val body = MethodOfReceiving(type = 3)
            Shared.methodReceiveCard.postValue(body)
            return true
        }
        sendSocket(2)
        val name = receiveInputFullName.text.toString()
        val listName = name.split(" ")
        if (name.isEmpty() || listName.size < 2) {
            receiveInputFullName.error = "Họ và tên không hợp lệ"
            return false
        }
        if (Utils.checkPhoneInvalid(receiveInputPhone.text.toString())) {
            receiveInputPhone.error = "Số điện thoại không đúng"
            return false
        }
        if (receiveInputProvince.text.isNullOrEmpty()) {
            receiveInputProvince.error = "Vui lòng chọn tỉnh thành"
            return false
        }
        if (receiveInputDistrict.text.isNullOrEmpty()) {
            receiveInputDistrict.error = "Vui lòng chọn quận huyện"
            return false
        }
        if (receiveInputWard.text.isNullOrEmpty()) {
            receiveInputWard.error = "Vui lòng chọn phường xã"
            return false
        }
        if (receiveInputAddress.text.isNullOrEmpty()) {
            receiveInputWard.error = "Vui lòng nhập số nhà"
            return false
        }
        val home = HomeInfo(
                fullName = name,
                phoneNumber = receiveInputPhone.text.toString(),
                province = receiveInputProvince.text.toString(),
                district = receiveInputDistrict.text.toString(),
                wards = receiveInputWard.text.toString(),
                apartmentNumber = receiveInputAddress.text.toString()
        )
        val data = MethodOfReceiving(type = 2, homeInfo = home)
        Shared.methodReceiveCard.postValue(data)
        return true
    }

    private fun sendSocket(typeReceiver: Int) {
        val req = Shared.socketReqData.value
        req?.cmd = Configs.FORM_STEP_6
        when (typeReceiver) {
            1 -> {
                Socket.action.sendData(req)
            }
            2 -> {
                val home = HomeInfo(
                        fullName = receiveInputFullName.text.toString(),
                        phoneNumber = receiveInputPhone.text.toString(),
                        province = receiveInputProvince.text.toString(),
                        district = receiveInputDistrict.text.toString(),
                        wards = receiveInputWard.text.toString(),
                        apartmentNumber = receiveInputAddress.text.toString()
                )
                req?.data?.homeInfo = home
                req?.data?.branchInfo = null
                Socket.action.sendData(req)
            }
            3 -> {
                val branch = BranchInfo(
                        id = "1",
                        code = "12",
                        name = "Ho Chi Minh",
                        address = "B20 Bach Dang, Phuong 6, Quan Go vap, TPHCM"
                )
                req?.data?.homeInfo = null
                req?.data?.branchInfo = branch
                Socket.action.sendData(req)
            }
        }
    }

}