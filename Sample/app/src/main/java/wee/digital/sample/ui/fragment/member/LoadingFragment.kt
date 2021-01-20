package wee.digital.sample.ui.fragment.member

import kotlinx.android.synthetic.main.view_header.*
import wee.digital.library.extension.gone
import wee.digital.sample.R
import wee.digital.sample.ui.main.MainFragment

class LoadingFragment : MainFragment() {

    override fun layoutResource(): Int = R.layout.loading

    override fun onViewCreated() {
        headerAction.gone()
    }

    override fun onLiveDataObserve() {}

}