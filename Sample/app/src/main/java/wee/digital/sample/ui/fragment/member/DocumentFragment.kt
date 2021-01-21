package wee.digital.sample.ui.fragment.member

import android.net.Uri
import android.view.View
import kotlinx.android.synthetic.main.document.*
import kotlinx.android.synthetic.main.document_select.*
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.ui.main.MainFragment

class DocumentFragment : MainFragment() {

    override fun layoutResource(): Int = R.layout.document

    override fun onViewCreated() {
        val urlPath = "android.resource://" + activity?.packageName + "/" + R.raw.card
        documentTipGif.setVideoURI(Uri.parse(urlPath))
        documentTipGif.start()
        documentTipGif.setOnCompletionListener { it.start() }
        addClickListener(documentRootNid, documentRootNid12, documentRootCccd, documentRootPassport)
    }

    override fun onLiveDataObserve() {
    }

    override fun onViewClick(v: View?) {
        when(v){
            documentRootNid -> {
                navigate(MainDirections.actionGlobalOcrFragment())
            }
            documentRootNid12 -> {
                navigate(MainDirections.actionGlobalOcrFragment())
            }
            documentRootCccd -> {
                navigate(MainDirections.actionGlobalOcrFragment())
            }
        }
    }

}