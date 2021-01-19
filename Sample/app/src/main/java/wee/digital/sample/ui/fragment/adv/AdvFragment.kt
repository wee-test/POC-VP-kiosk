package wee.digital.sample.ui.fragment.adv

import android.view.View
import kotlinx.android.synthetic.main.fragment_adv.*
import wee.digital.sample.R
import wee.digital.sample.ui.base.viewModel
import wee.digital.sample.ui.main.MainFragment

class AdvFragment : MainFragment() {

    private val advVM : AdvVM by lazy { viewModel(AdvVM::class) }

    override fun layoutResource(): Int = R.layout.fragment_adv

    override fun onViewCreated() {
        advVM.getListAdv()
        addClickListener(advActionStart)
    }

    override fun onLiveDataObserve() {
        advVM.advLiveData.observe { advSlide.listItem = it }
    }

    override fun addClickListener(vararg views: View?) {
        when(view){
            advActionStart -> ""
        }
    }

}