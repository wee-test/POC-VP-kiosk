package wee.digital.sample.ui.fragment.rating

import kotlinx.android.synthetic.main.rating_fragment.*
import wee.digital.sample.R
import wee.digital.sample.ui.main.MainFragment

class RatingFragment : MainFragment() {
    private val adapter = RatingAdapter()
    override fun onViewCreated() {
        bindRatingList()
    }

    private fun bindRatingList() {
        adapter.also { it.bind(recyclerViewReact, 4) }
        adapter.set(RatingItem.defaultList)
        adapter.onItemClick = { _, position ->
            for (model in RatingItem.defaultList) if (model.isSelected) model.isSelected = false
            RatingItem.defaultList[position].isSelected = true
            adapter.bind(recyclerViewReact, 4)
            adapter.notifyDataSetChanged()
        }
    }

    override fun layoutResource(): Int {
        return R.layout.rating_fragment
    }

    override fun onLiveDataObserve() {

    }
}