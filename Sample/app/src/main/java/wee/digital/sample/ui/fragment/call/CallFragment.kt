package wee.digital.sample.ui.fragment.call

import android.view.View
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.call.*
import wee.digital.library.extension.post
import wee.digital.library.extension.toast
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.repository.model.MessageData
import wee.digital.sample.repository.model.SocketData
import wee.digital.sample.repository.model.SocketReq
import wee.digital.sample.shared.Configs
import wee.digital.sample.shared.Shared
import wee.digital.sample.shared.VoiceData
import wee.digital.sample.ui.base.viewModel
import wee.digital.sample.ui.main.MainFragment
import wee.digital.sample.util.extention.Voice

class CallFragment : MainFragment() {

    private val callVM : CallVM by lazy { viewModel(CallVM::class) }

    override fun layoutResource(): Int = R.layout.call

    override fun onViewCreated() {
        post(700) { callVM.getContacts() }
        addClickListener(callActionCancel)
        Voice.ins?.request(VoiceData.CALLING_SCREEN)
    }

    override fun onLiveDataObserve() {
        callVM.statusContacts.observe {
            if (it == null || it.responseCode.code != 0L) {
                Shared.messageFail.postValue(
                        MessageData("Không thể kết nối được với hệ thống", "Hệ thống đang xảy ra lỗi, bạn vui lòng thử lại")
                )
                navigate(MainDirections.actionGlobalFailFragment())
                return@observe
            } else {
                Shared.socketReqData.postValue(SocketReq(cmd = Configs.FORM_STEP_1, data = SocketData()))
                Shared.socketStatusConnect.postValue(it)
            }
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            callActionCancel -> {
                Shared.callVideo.postValue("")
                navigate(MainDirections.actionGlobalAdvFragment())
            }
        }
    }

}