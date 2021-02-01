package wee.digital.sample.ui.fragment.review

import kotlinx.android.synthetic.main.review_info.*
import wee.digital.library.extension.gone
import wee.digital.library.extension.hide
import wee.digital.sample.R
import wee.digital.sample.shared.Configs
import wee.digital.sample.shared.Shared
import wee.digital.sample.ui.main.MainFragment

class ReviewInfoFragment : MainFragment() {

    override fun layoutResource(): Int = R.layout.review_info

    override fun onViewCreated() {}

    override fun onLiveDataObserve() {
        Shared.ocrConfirmData.observe {
            when(Shared.typeCardOcr.value){
                Configs.TYPE_NID, Configs.TYPE_NID_12, Configs.TYPE_CCCD -> {
                    reviewInfoDateBirth.text = it.dateOfBirth
                    reviewInfoGender.text = when(it.gender){
                        1 -> "Nam"
                        2 -> "Nữ"
                        else -> "Khác"
                    }
                    reviewInfoNumberNid.text = it.number
                    reviewInfoDateRange.text = it.issuedDate
                    if(it.expiredDate.isEmpty()){
                        reviewInfoDateExpiration.hide()
                    }else{
                        reviewInfoDateExpiration.text = it.expiredDate
                    }
                    reviewInfoIssued.text = it.issuedPlace
                    reviewInfoDomicile.text = it.hometown
                }
                else -> {
                    reviewInfoDateBirth.text = it.dateOfBirth
                    reviewInfoGender.text = when(it.gender){
                        1 -> "Nam"
                        2 -> "Nữ"
                        else -> "Khác"
                    }
                    reviewInfoNumberNid.text = it.number
                    reviewInfoDateRange.text = it.issuedDate
                    reviewInfoDateExpiration.text = it.expiredDate
                    reviewInfoIssued.title = "Số Passport"
                    reviewInfoIssued.text = it.passportNumber
                    reviewInfoDomicile.title = "Type Passport"
                    reviewInfoDomicile.text = it.passportType
                }
            }

        }
    }

}