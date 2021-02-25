package wee.digital.sample.ui.fragment.review

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.review.*
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.shared.Shared
import wee.digital.sample.shared.VoiceData
import wee.digital.sample.ui.main.MainActivity
import wee.digital.sample.ui.main.MainFragment
import wee.digital.sample.util.extention.Voice

class ReviewFragment : MainFragment() {

    override fun layoutResource(): Int = R.layout.review

    override fun onViewCreated() {
        reviewTab.setUpViewPager(this, ReviewInfoFragment(), ReviewCardFragment(), ReviewFormFragment(), reviewPager)
        (reviewPager.getChildAt(0) as RecyclerView).overScrollMode = View.OVER_SCROLL_NEVER
        addClickListener(reviewActionAccept)
        Voice.ins?.request(VoiceData.CONFIRM_INFO)
    }

    override fun onViewClick(v: View?) {
        val mainActivity = activity as? MainActivity ?:return
        when(v){
            reviewActionAccept -> {
                if (true || Shared.methodReceiveCard.value?.type == 1) {
                    mainActivity.printCard(
                            "1234 4567 8910 1112",
                            Shared.ocrConfirmData.value?.fullName!!,
                            "39/79"
                    )
                }
                navigate(MainDirections.actionGlobalLoadingFragment())
            }
        }
    }

    override fun onLiveDataObserve() {
        Shared.faceCapture.observe {
            reviewImageFace.setImageBitmap(it)
        }
    }

}