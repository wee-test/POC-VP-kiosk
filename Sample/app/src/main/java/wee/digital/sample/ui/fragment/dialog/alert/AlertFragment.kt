package wee.digital.sample.ui.fragment.dialog.alert

import android.widget.TextView
import kotlinx.android.synthetic.main.alert.*
import wee.digital.library.extension.*
import wee.digital.sample.R
import wee.digital.sample.ui.main.MainDialog

class AlertFragment : MainDialog() {

    /**
     * [MainDialog] override
     */
    override fun layoutResource(): Int {
        return R.layout.alert
    }

    override fun onViewCreated() {

    }

    override fun onLiveDataObserve() {
        mainVM.alertLiveData.observe(this::onBindArg)
    }

    /**
     * [AlertFragment] properties
     */
    private fun onBindArg(arg: Alert.Arg?) {
        arg ?: return
        alertImageViewIcon.setImageResource(arg.icon)
        alertImageViewIcon.backgroundTintRes(arg.iconBackgroundTint)
        alertTextViewTitle.text = arg.title
        alertTextViewMessage.setHyperText(arg.message)
        alertViewAccept.setBackgroundResource(arg.acceptBackground)
        alertViewAccept.onBindButton(arg.acceptLabel, arg.acceptOnClick)
        alertViewCancel.onBindButton(arg.cancelLabel, arg.cancelOnClick)
        if (arg.hideCancel) alertViewCancel.gone()
    }

    private fun TextView.onBindButton(label: String?, onClick: (AlertFragment) -> Unit) {
        this.isGone(label.isNullOrEmpty())
        this.text = label
        this.addViewClickListener {
            dismissAllowingStateLoss()
            onClick(this@AlertFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainVM.alertLiveData.value?.onDismiss?.also {
            it()
        }
    }

}