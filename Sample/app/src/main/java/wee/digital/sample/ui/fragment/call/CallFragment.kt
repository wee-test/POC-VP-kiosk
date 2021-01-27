package wee.digital.sample.ui.fragment.call

import wee.digital.library.extension.toast
import wee.digital.sample.R
import wee.digital.sample.repository.model.SocketData
import wee.digital.sample.repository.model.SocketReq
import wee.digital.sample.shared.Configs
import wee.digital.sample.shared.Shared
import wee.digital.sample.ui.base.viewModel
import wee.digital.sample.ui.main.MainFragment

class CallFragment : MainFragment() {

    private val callVM : CallVM by lazy { viewModel(CallVM::class) }

    override fun layoutResource(): Int = R.layout.call

    override fun onViewCreated() {
        callVM.getContacts()
        Shared.videoCall.postValue(true)
    }

    override fun onLiveDataObserve() {
        callVM.statusContacts.observe {
            if (it == null || it.responseCode.code != 0L) {
                toast("hệ thống đang cập nhật bạn vui lòng thử lại")
            } else {
                Shared.socketReqData.postValue(SocketReq(cmd = Configs.FORM_STEP_1, data = SocketData()))
                Shared.socketStatusConnect.postValue(it)
            }
        }
    }

}