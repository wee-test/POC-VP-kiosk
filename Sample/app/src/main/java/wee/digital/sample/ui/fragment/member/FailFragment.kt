package wee.digital.sample.ui.fragment.member

import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.fail.*
import wee.dev.weewebrtc.WeeCaller
import wee.digital.library.extension.toArray
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.repository.model.UpdateInfoReq
import wee.digital.sample.shared.Configs
import wee.digital.sample.shared.Shared
import wee.digital.sample.ui.main.MainActivity
import wee.digital.sample.ui.main.MainFragment
import wee.digital.sample.util.extention.Voice
import java.text.SimpleDateFormat

class FailFragment : MainFragment() {

    override fun layoutResource(): Int = R.layout.fail

    override fun onViewCreated() {
        addClickListener(failActionAgain)
        updateInfo()
    }

    private fun updateInfo() {
        if (Shared.dataCallLog == null) return
        MainActivity.weeCaller?.callHangUp()
        val tellerData = Shared.socketStatusConnect.value?.listTellersIDString
        val tellersId = tellerData?.toArray()?.get(0)?.asString ?: ""
        val dataCall = Shared.dataCallLog
        val timeWaiting = ((dataCall?.ConnectedTimeIn!! - dataCall.TimeCallIn) / 1000).toInt()
        val timeReceived = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(dataCall.TimeCallIn)
        val processTime = ((dataCall.ConnectedTimeOut - dataCall.ConnectedTimeIn) / 1000).toInt()
        val status = when (dataCall.StatusCall) {
            WeeCaller.CALL_STATUS_CONNECTED -> 1
            WeeCaller.CALL_STATUS_REJECT -> 2
            WeeCaller.CALL_STATUS_MISSING -> 3
            else -> 3
        }
        val body = UpdateInfoReq(
                kioskId = Configs.KIOSK_ID,
                videoId = Shared.sessionVideo.value?.result?.videoCallID ?: "",
                customerId = "",
                transType = 1,
                counterId = tellersId,
                videoCallStatus = status,
                timeReceived = timeReceived,
                waitingTime = timeWaiting,
                processingTime = processTime,
                createAt = System.currentTimeMillis().toString()
        )
        Log.e("updateInfo", "$body")
        mainVM.updateInfo(body)
    }

    override fun onLiveDataObserve() {
        Shared.messageFail.observe {
            failTitle.text = it.title
            failContent.text = it.message
            Voice.ins?.request(it.message)
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            failActionAgain -> {
                navigate(MainDirections.actionGlobalAdvFragment()) { setLaunchSingleTop() }
            }
        }
    }

}