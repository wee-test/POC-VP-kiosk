package wee.digital.sample.ui.fragment.card

import android.view.View
import com.google.gson.annotations.SerializedName
import kotlinx.android.synthetic.main.card_receive_method.*
import wee.digital.library.extension.toast
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.model.HomeInfo
import wee.digital.sample.model.MethodOfReceiving
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
                navigate(MainDirections.actionGlobalReviewFragment())
            }
        }
    }

    override fun onLiveDataObserve() {}

    private fun checkValidData(): Boolean {
        if (!receiveRadioDirectly.isChecked || !receiveRadioHome.isChecked || !receiveRadioBranch.isChecked) {
            toast("Vui lòng chọn hình thức nhận thẻ")
            return false
        }
        if (receiveRadioDirectly.isChecked) {
            val body = MethodOfReceiving(type = 0)
            Shared.methodReceiveCard.postValue(body)
            return true
        }
        if (receiveRadioBranch.isChecked) {
            val body = MethodOfReceiving(type = 3)
            Shared.methodReceiveCard.postValue(body)
            return true
        }
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
        val data = MethodOfReceiving(type = 1, homeInfo = home)
        Shared.methodReceiveCard.postValue(data)
        return true
    }

}