package wee.digital.sample.ui.fragment.com

import android.view.View
import kotlinx.android.synthetic.main.com.*
import kotlinx.android.synthetic.main.com_item.view.*
import wee.digital.library.adapter.BaseRecyclerAdapter
import wee.digital.library.extension.load
import wee.digital.sample.R
import wee.digital.sample.shared.Shared
import wee.digital.sample.ui.main.MainFragment

class ComFragment : MainFragment() {

    private val adapter = ComAdapter()

    override fun layoutResource(): Int {
        return R.layout.com
    }

    override fun onViewCreated() {
        setupRecycler()
        comInputDate.addDateWatcher()
        comInputList.buildSelectable(mainVM, Shared.provinceList)
    }

    private fun setupRecycler() {
        adapter.set(listCom)
        adapter.bind(comRecycler, 3)
        adapter.onItemClick = { model, _ ->
            navigate(model.navigate)
        }
    }

    override fun onLiveDataObserve() {}

    inner class ComAdapter : BaseRecyclerAdapter<ComItem>() {

        override fun layoutResource(model: ComItem, position: Int): Int = R.layout.com_item

        override fun View.onBindModel(model: ComItem, position: Int, layout: Int) {
            comItemImage.load("${URL_COM}${model.image}")
            comItemLabel.text = model.name
        }

    }

}