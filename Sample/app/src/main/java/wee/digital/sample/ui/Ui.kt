package wee.digital.sample.ui

import android.app.Activity
import android.view.View
import wee.digital.library.extension.addViewClickListener
import wee.digital.library.extension.hideKeyboard
import wee.digital.sample.R
import wee.digital.sample.ui.fragment.dialog.selectable.Selectable
import wee.digital.sample.ui.fragment.dialog.selectable.SelectableAdapter
import wee.digital.sample.ui.main.Main
import wee.digital.sample.ui.main.MainVM
import wee.digital.sample.widget.TextInputView

fun <T : Selectable> View.buildSelectable(
        mainVM: MainVM,
        data: List<T>?,
        adaptive: SelectableAdapter<T>.() -> Unit = {}
) {

    data ?: return
    val showDialog = {
        (context as? Activity)?.hideKeyboard()
        val adapter = SelectableAdapter<T>().also {
            it.set(data)
            @Suppress("UNCHECKED_CAST")
            it.selectedItem = mainVM.selectableMap[id] as? T
            it.onDismiss = {
                when (this) {
                    is TextInputView -> {
                        text = mainVM.selectableMap[id]?.text
                        onFocusChange(null, this.hasFocus())
                    }
                }
            }
            it.addOnItemClick { model, _ ->
                mainVM.selectableMap[id] = model
            }
        }
        mainVM.selectableMap[id] = adapter.selectedItem ?: data.firstOrNull()
        mainVM.selectableLiveData.value = adapter
        mainVM.dialogLiveData.value = Main.selectable
        adapter.adaptive()
    }
    addViewClickListener {
        when (this) {
            is TextInputView -> drawBorder(R.color.colorInputFocused)
        }
        showDialog()
    }
}



