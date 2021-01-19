package wee.digital.sample.ui.fragment.dialog.web

import android.view.View
import kotlinx.android.synthetic.main.web.*
import wee.digital.sample.R
import wee.digital.sample.ui.main.MainDialog

class WebFragment : MainDialog() {

    override fun layoutResource(): Int {
        return R.layout.web
    }

    override fun onViewCreated() {
        addClickListener(dialogViewDismiss, dialogView)
    }

    override fun onLiveDataObserve() {
        mainVM.webLiveData.observe(this::onBindArg)
    }

    override fun onViewClick(v: View?) {
        dismissAllowingStateLoss()
    }

    private fun onBindArg(it: WebArg?) {
        it ?: return
        dialogTextViewTitle.text = it.title
        dialogWebView.loadUrl(it.url)
    }

}

