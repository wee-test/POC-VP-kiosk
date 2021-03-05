package wee.digital.sample.ui.fragment.com

import android.view.View
import kotlinx.android.synthetic.main.com.*
import kotlinx.android.synthetic.main.com_item.view.*
import wee.digital.library.adapter.BaseRecyclerAdapter
import wee.digital.library.extension.load
import wee.digital.sample.R
import wee.digital.sample.data.local.SharedHelper
import wee.digital.sample.shared.Shared
import wee.digital.sample.ui.main.MainActivity
import wee.digital.sample.ui.main.MainFragment

class ComFragment : MainFragment() {

    private val adapter = ComAdapter()

    override fun layoutResource(): Int {
        return R.layout.com
    }

    override fun onViewCreated() {
        setupRecycler()
        addClickListener(comViewConnect,comViewClose,comViewNext)

        val socketUrl = SharedHelper.instance.str(SharedHelper.URL_SOCKET_PRINTER, "") ?: ""
        editTextPrinterSocket.setText(socketUrl)
    }

    private fun setupRecycler() {
        adapter.set(listCom)
        adapter.bind(comRecycler, 3)
        adapter.onItemClick = { model, _ ->
            Shared.dataCallLog = null
            navigate(model.navigate)
        }
    }

    override fun onLiveDataObserve() {}

    override fun onViewClick(v: View?) {
        val mainActivity = activity as? MainActivity ?: return
        when (v) {
            comViewConnect -> {
                SharedHelper.instance.put(SharedHelper.URL_SOCKET_PRINTER, editTextPrinterSocket?.text.toString())
                mainActivity.printerSocket.open(editTextPrinterSocket.text?.toString())
            }
            comViewClose -> mainActivity.printerSocket.close()
            comViewNext -> {
                mainActivity.printCard("9234 4567 8910 8684",
                        "TRAN LUU ANH THI",
                        "21/22")
            }
        }
    }

    inner class ComAdapter : BaseRecyclerAdapter<ComItem>() {

        override fun layoutResource(model: ComItem, position: Int): Int = R.layout.com_item

        override fun View.onBindModel(model: ComItem, position: Int, layout: Int) {
            comItemImage.load("${URL_COM}${model.image}")
            comItemLabel.text = model.name
        }

    }

}