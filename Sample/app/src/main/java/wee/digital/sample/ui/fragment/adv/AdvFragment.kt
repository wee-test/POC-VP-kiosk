package wee.digital.sample.ui.fragment.adv

import android.view.View
import kotlinx.android.synthetic.main.adv.*
import wee.digital.library.extension.post
import wee.digital.library.extension.toast
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.repository.model.SocketData
import wee.digital.sample.repository.model.SocketReq
import wee.digital.sample.shared.Configs
import wee.digital.sample.shared.Shared
import wee.digital.sample.ui.base.viewModel
import wee.digital.sample.ui.main.MainFragment

class AdvFragment : MainFragment() {

    private val advVM : AdvVM by lazy { viewModel(AdvVM::class) }

    override fun layoutResource(): Int = R.layout.adv

    override fun onViewCreated() {
        advVM.getListAdv()
        Shared.socketStatusConnect.postValue(null)
        Shared.socketReqData.postValue(null)
        addClickListener(advActionStart)

    }

    override fun onLiveDataObserve() {
        advVM.advLiveData.observe { advSlide.listItem = it }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            advActionStart -> {
                navigate(MainDirections.actionGlobalVerifyFaceFragment())
            }
        }
    }

}