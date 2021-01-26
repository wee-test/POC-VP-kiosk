package wee.digital.sample.ui.fragment.member

import kotlinx.android.synthetic.main.view_header.*
import wee.digital.library.extension.gone
import wee.digital.library.extension.toast
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.ui.base.activityVM
import wee.digital.sample.ui.fragment.register.RegisterVM
import wee.digital.sample.ui.main.MainFragment

class LoadingFragment : MainFragment() {

    private val registerVM: RegisterVM by lazy { activityVM(RegisterVM::class) }

    override fun layoutResource(): Int = R.layout.loading

    override fun onViewCreated() {
        headerAction.gone()
//        registerVM.registerCard()
    }

    override fun onLiveDataObserve() {
        registerVM.statusRegisterCard.observe {
            toast("$it")
        }
    }

}