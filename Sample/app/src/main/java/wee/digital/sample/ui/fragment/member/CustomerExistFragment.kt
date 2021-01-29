package wee.digital.sample.ui.fragment.member

import android.view.View
import kotlinx.android.synthetic.main.customer_exist.*
import kotlinx.android.synthetic.main.review_info.*
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.shared.Shared
import wee.digital.sample.shared.VoiceData
import wee.digital.sample.ui.main.MainFragment
import wee.digital.sample.util.extention.Voice

class CustomerExistFragment : MainFragment() {

    override fun layoutResource(): Int = R.layout.customer_exist

    override fun onViewCreated() {
        addClickListener(customerAction)
        bindData()
    }

    private fun bindData() {

    }

    override fun onLiveDataObserve() {
        Shared.customerInfoExist.observe {
            customerInfoImageFace.setImageBitmap(Shared.faceCapture.value)
            customerInfoLabelName.text = it.customerInfo.identityCardInfo.fullName
            reviewInfoDateBirth.text = it.customerInfo.identityCardInfo.dateOfBirth
            reviewInfoGender.text = when (it.customerInfo.identityCardInfo.gender) {
                1L -> "Nam"
                2L -> "Nữ"
                3L -> "Khác"
                else -> "Nam"
            }
            reviewInfoNumberNid.text = it.customerInfo.identityCardInfo.idCardNumber
            reviewInfoDateRange.text = it.customerInfo.identityCardInfo.issuedDate
            reviewInfoDateExpiration.text = it.customerInfo.identityCardInfo.expiredDate
            reviewInfoIssued.text = it.customerInfo.identityCardInfo.issuedPlace
            reviewInfoDomicile.text = it.customerInfo.identityCardInfo.hometown
            Voice.ins?.request("${VoiceData.HI_USER}${it.customerInfo.identityCardInfo.fullName}")
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            customerAction -> navigate(MainDirections.actionGlobalAdvFragment())
        }
    }

}