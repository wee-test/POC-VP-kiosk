package wee.digital.sample.ui.fragment.ocr

import kotlinx.android.synthetic.main.ocr_confirm_content.*
import wee.digital.sample.R
import wee.digital.sample.shared.Shared
import wee.digital.sample.ui.main.MainFragment


class OcrConfirmFragment  : MainFragment(){

    override fun layoutResource(): Int {
        return R.layout.ocr_confirm
    }


    override fun onViewCreated() {
        ocrInputBirth.addDateWatcher()
        ocrInputIssueDate.addDateWatcher()
        ocrInputIssuePlace.buildSelectable(mainVM, Shared.provinceList)
        ocrInputFullName.text = "CON CAC V1P"
        ocrInputFullName.error = "con cac v1p"
    }


    override fun onLiveDataObserve() {
    }

}