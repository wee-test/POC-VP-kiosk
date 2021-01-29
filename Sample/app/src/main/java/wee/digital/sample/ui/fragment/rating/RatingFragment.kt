package wee.digital.sample.ui.fragment.rating

import android.view.View
import kotlinx.android.synthetic.main.rating_fragment.*
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.repository.model.ServiceReviewReq
import wee.digital.sample.repository.socket.Socket
import wee.digital.sample.shared.Configs
import wee.digital.sample.shared.Shared
import wee.digital.sample.shared.VoiceData
import wee.digital.sample.ui.base.viewModel
import wee.digital.sample.ui.main.MainFragment
import wee.digital.sample.util.extention.Voice

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
        }
    }

    override fun onLiveDataObserve() {}

    override fun onViewClick(v: View?) {
        when (v) {
            frgRatingActionNext -> {
                val customerRegister = Shared.customerInfoRegisterSuccess.value
                val body = ServiceReviewReq(
                        customerId = customerRegister?.result?.customerID.toString(),
                        transId = customerRegister?.result?.transID.toString(),
                        reviewType = ratingModel.type
                )
                ratingVM.serviceReview(body)
                sendSocket()
                Voice.ins?.request(VoiceData.THANKS_RATE){
                    activity?.runOnUiThread {
                        navigate(MainDirections.actionGlobalAdvFragment()) { setLaunchSingleTop() }
                    }
                }

            }
        }
    }

    private fun sendSocket(){
        val req = Shared.socketReqData.value
        req?.cmd = Configs.FORM_STEP_8
        req?.data?.reviewType = ratingModel.type
        Socket.action.sendData(req)
    }

}