package wee.digital.sample.ui.fragment.review

import kotlinx.android.synthetic.main.review_form.*
import kotlinx.android.synthetic.main.review_form_home.*
import wee.digital.library.extension.show
import wee.digital.sample.R
import wee.digital.sample.shared.Shared
import wee.digital.sample.ui.main.MainFragment

class ReviewFormFragment : MainFragment() {

    override fun layoutResource(): Int = R.layout.review_form

    override fun onViewCreated() {
    }

    override fun onLiveDataObserve() {
        Shared.methodReceiveCard.observe {
            when (it.type) {
                1 -> {
                    reviewForm1.show()
                }
                2 -> {
                    reviewForm2.show()
                    formHomeName.text = it.homeInfo?.fullName
                    formHomePhone.text = it.homeInfo?.phoneNumber
                    formHomeProvince.text = it.homeInfo?.province
                    formHomeDistrict.text = it.homeInfo?.district
                    formHomeWard.text = it.homeInfo?.wards
                }
                3 -> {
                    reviewForm3.show()
                }
            }
        }
    }

}