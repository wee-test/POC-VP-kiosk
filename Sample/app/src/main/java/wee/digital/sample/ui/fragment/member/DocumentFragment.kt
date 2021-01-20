package wee.digital.sample.ui.fragment.member

import wee.digital.sample.R
import wee.digital.sample.ui.main.MainFragment

class DocumentFragment : MainFragment() {

    override fun layoutResource(): Int = R.layout.document

    override fun onViewCreated() {
    }

    override fun onLiveDataObserve() {
    }
    /*start video
    val urlPath = "android.resource://" + activity?.packageName + "/" + R.raw.card
    tipVideo.setVideoURI(Uri.parse(urlPath))
    tipVideo.start()
    tipVideo.setOnCompletionListener { it.start() }*/
}