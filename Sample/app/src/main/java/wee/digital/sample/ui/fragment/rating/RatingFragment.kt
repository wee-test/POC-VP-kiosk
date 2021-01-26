package wee.digital.sample.ui.fragment.rating

import android.view.View
import kotlinx.android.synthetic.main.rating_fragment.*
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.ui.main.MainFragment

class RatingFragment : MainFragment() {

    private val adapter = RatingAdapter()

    override fun layoutResource(): Int {
        return R.layout.rating_fragment
    }

    override fun onViewCreated() {
        bindRatingList()
        addClickListener(frgRatingActionNext)
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

    override fun onLiveDataObserve() {}

    override fun onViewClick(v: View?) {
        when (v) {
            frgRatingActionNext -> {
                navigate(MainDirections.actionGlobalAdvFragment()) { setLaunchSingleTop() }
            }
        }
    }

}