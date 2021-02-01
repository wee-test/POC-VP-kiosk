package wee.digital.sample.ui.fragment.rating

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.rating_fragment.*
import wee.dev.weewebrtc.WeeCaller
import wee.digital.library.extension.toArray
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.repository.model.ServiceReviewReq
import wee.digital.sample.repository.model.SocketReq
import wee.digital.sample.repository.model.UpdateInfoReq
import wee.digital.sample.repository.socket.Socket
import wee.digital.sample.shared.Configs
import wee.digital.sample.shared.Shared
import wee.digital.sample.shared.VoiceData
import wee.digital.sample.ui.base.viewModel
import wee.digital.sample.ui.main.MainActivity
import wee.digital.sample.ui.main.MainFragment
import wee.digital.sample.util.extention.Voice
import java.text.SimpleDateFormat

class RatingFragment : MainFragment() {

    private val ratingVM : RatingVM by lazy { viewModel(RatingVM::class) }

    private val adapter = RatingAdapter()

    private var ratingModel = RatingItem()

    override fun layoutResource(): Int {
        return R.layout.rating_fragment
    }

    override fun onViewCreated() {
        bindRatingList()
        addClickListener(frgRatingActionNext)
        sendSocket()
        Voice.ins?.request(VoiceData.SUCCESS_SCREEN)
    }

    private fun bindRatingList() {
        adapter.also { it.bind(recyclerViewReact, 4) }
        adapter.set(RatingItem.defaultList)
        adapter.onItemClick = { model, position ->
            ratingModel = model
            for (model in RatingItem.defaultList) if (model.isSelected) model.isSelected = false
            RatingItem.defaultList[position].isSelected = true
            adapter.bind(recyclerViewReact, 4)
            adapter.notifyDataSetChanged()
            sendSocket()
        }
    }

    override fun onLiveDataObserve() {
        Shared.customerInfoRegisterSuccess.observe {
            it ?: return@observe
            fragRatingCustomerId.text = it.result.customerID
            frgRatingCustomerCode.text = it.result.transID
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            frgRatingActionNext -> {
                val customerRegister = Shared.customerInfoRegisterSuccess.value
                val body = ServiceReviewReq(
                        customerId = customerRegister?.result?.customerID.toString(),
                        transId = customerRegister?.result?.transID.toString(),
                        reviewType = ratingModel.type
                )
                updateInfo()
                ratingVM.serviceReview(body)
                Socket.action.sendData(SocketReq(cmd = Configs.END_STEP))
                Voice.ins?.request(VoiceData.THANKS_RATE) {
                    activity?.runOnUiThread {
                        navigate(MainDirections.actionGlobalAdvFragment()) { setLaunchSingleTop() }
                    }
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun updateInfo(){
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
        val customerId = Shared.customerInfoRegisterSuccess.value?.result?.customerID
        val body = UpdateInfoReq(
                kioskId = Configs.KIOSK_ID,
                videoId = Shared.sessionVideo.value?.result?.videoCallID ?: "",
                customerId = if(customerId.isNullOrEmpty()) "" else customerId,
                transType = 1,
                counterId = tellersId,
                videoCallStatus = status,
                timeReceived = timeReceived,
                waitingTime = timeWaiting,
                processingTime = if(processTime < 0) 1 else processTime,
                createAt = System.currentTimeMillis().toString()
        )
        Log.e("updateInfo", "$body")
        mainVM.updateInfo(body)
    }

    private fun sendSocket(){
        val req = Shared.socketReqData.value
        req?.cmd = Configs.FORM_STEP_8
        req?.data?.reviewType = ratingModel.type
        Socket.action.sendData(req)
    }

}