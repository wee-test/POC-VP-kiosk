package wee.digital.sample.ui.fragment.adv

import kotlinx.android.synthetic.main.adv.*
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.ui.base.viewModel
import wee.digital.sample.ui.main.MainFragment

class AdvFragment : MainFragment() {

    private val advVM : AdvVM by lazy { viewModel(AdvVM::class) }

    override fun layoutResource(): Int = R.layout.adv

    override fun onViewCreated() {
        advVM.getListAdv()
        advActionStart.setOnClickListener {
            navigate(MainDirections.actionGlobalVerifyFaceFragment())
        }
    }

    override fun onLiveDataObserve() {
        advVM.advLiveData.observe { advSlide.listItem = it }
    }

}