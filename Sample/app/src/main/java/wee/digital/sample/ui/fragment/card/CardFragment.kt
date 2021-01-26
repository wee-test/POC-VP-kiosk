package wee.digital.sample.ui.fragment.card

import kotlinx.android.synthetic.main.card.*
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.repository.model.SocketData
import wee.digital.sample.repository.model.SocketReq
import wee.digital.sample.repository.socket.Socket
import wee.digital.sample.shared.Configs
import wee.digital.sample.shared.Shared
import wee.digital.sample.ui.main.MainFragment

class CardFragment : MainFragment() {

    private val adapter1 = CardAdapter()

    private val adapter2 = CardAdapter()

    override fun layoutResource(): Int {
        return R.layout.card
    }

    override fun onViewCreated() {
        adapter1.set(listCard1)
        adapter1.bindHorizontal(cardRecyclerView1)
        adapter1.onItemClick = {model, _ ->
            sendSocket(model)
            navigate(MainDirections.actionGlobalCardReceiveFragment())
        }

        adapter2.set(listCard2)
        adapter2.bindHorizontal(cardRecyclerView2)
        adapter2.onItemClick = {model, _ ->
            sendSocket(model)
            navigate(MainDirections.actionGlobalCardReceiveFragment())
        }
    }

    private fun sendSocket(data : CardItem){
        val req = Shared.socketReqData.value
        req?.cmd = Configs.FORM_STEP_5
        req?.data?.cardType = data.type
        Socket.action.sendData(req)
    }

    override fun onLiveDataObserve() {
    }
}