package wee.digital.sample.ui.fragment.card

import kotlinx.android.synthetic.main.card.*
import wee.digital.sample.MainDirections
import wee.digital.sample.R
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
            Shared.cardSelected.postValue(model)
            navigate(MainDirections.actionGlobalCardReceiveFragment())
        }

        adapter2.set(listCard2)
        adapter2.bindHorizontal(cardRecyclerView2)
        adapter2.onItemClick = {model, _ ->
            Shared.cardSelected.postValue(model)
            navigate(MainDirections.actionGlobalCardReceiveFragment())
        }
    }

    override fun onLiveDataObserve() {
    }
}