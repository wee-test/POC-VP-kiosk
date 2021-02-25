package wee.digital.sample.ui.fragment.adv

import android.view.View
import kotlinx.android.synthetic.main.adv.*
import wee.dev.weewebrtc.utils.extension.setFastClickListener
import wee.digital.library.extension.gone
import wee.digital.library.extension.show
import wee.digital.library.extension.toast
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.data.local.SharedHelper
import wee.digital.sample.shared.Configs
import wee.digital.sample.shared.Shared
import wee.digital.sample.ui.base.viewModel
import wee.digital.sample.ui.fragment.call.CallVM
import wee.digital.sample.ui.main.MainFragment

class AdvFragment : MainFragment() {

    private val advVM: AdvVM by lazy { viewModel(AdvVM::class) }

    private val callVM: CallVM by lazy { viewModel(CallVM::class) }

    override fun layoutResource(): Int = R.layout.adv

    override fun onViewCreated() {
        advVM.getListAdv()
        addClickListener(advActionStart)
        Shared.resetData()
        configOnOff()
    }

    private fun configOnOff() {
        advLabelStatus.text = "${getStatusApi()}"
        advActionOnOff.setFastClickListener(7) {
            SharedHelper.instance.put(SharedHelper.STATUS_CALL_API, !getStatusApi())
            advLabelStatus.text = "${getStatusApi()}"
        }
    }

    override fun onLiveDataObserve() {
        advVM.advLiveData.observe { advSlide.listItem = it }
        callVM.statusCreateNewSession.observe {
            if (it == null || it.responseCode.code != 0L) {
                advActionStart.show()
                advProgress.gone()
                toast("Không thể tạo được mã đăng ký, vui lòng thử lại")
            } else {
                Shared.sessionVideo.postValue(it)
                navigate(MainDirections.actionGlobalVerifyFaceFragment())
            }
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            advActionStart -> {
                advActionStart.gone()
                advProgress.show()
                callVM.createNewSession(Configs.KIOSK_ID)
            }
        }
    }

}