package wee.digital.sample.ui.fragment.dialog.selectable

import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.selectable.*
import wee.digital.library.extension.string
import wee.digital.library.util.Utils.getHeightScreen
import wee.digital.sample.R
import wee.digital.sample.ui.main.MainDialog

class SelectableFragment : MainDialog() {

    override fun layoutResource(): Int {
        return R.layout.selectable
    }

    override fun onViewCreated() {
        addClickListener(dialogView, dialogViewDismiss)
    }

    override fun onLiveDataObserve() {
        mainVM.selectableLiveData.observe(this::onBindArg)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainVM.selectableLiveData.value?.also {
            it.onDismiss()
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            dialogView, dialogViewDismiss -> dismissAllowingStateLoss()
        }
    }

    private fun onBindArg(it: SelectableAdapter<*>?) {
        it ?: return
        dialogTextViewTitle.text = string(it.title)
        it.bind(selectableRecyclerView)
        it.addOnItemClick { _, _ ->
            dismissAllowingStateLoss()
        }
        val list = it.listItem.size
        if(list > 8){
            val params: ViewGroup.LayoutParams = selectableRecyclerView.layoutParams
            params.height = (getHeightScreen(requireActivity()) / 1.5).toInt()
            selectableRecyclerView.layoutParams = params
        }
    }

}