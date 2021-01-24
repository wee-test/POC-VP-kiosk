package wee.digital.sample.ui.fragment.member

import android.net.Uri
import android.view.View
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.document.*
import kotlinx.android.synthetic.main.document_select.*
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.shared.Configs
import wee.digital.sample.shared.Shared
import wee.digital.sample.ui.main.MainFragment
import java.util.concurrent.TimeUnit

class DocumentFragment : MainFragment() {

    private var disposable: Disposable? = null

    override fun layoutResource(): Int = R.layout.document

    override fun onViewCreated() {

    }

    override fun onLiveDataObserve() {
    }

    override fun onViewClick(v: View?) {
        when(v){
            documentRootNid -> {
                Shared.typeCardOcr.postValue(Configs.TYPE_NID)
                navigate(MainDirections.actionGlobalOcrFragment())
            }
            documentRootNid12 -> {
                Shared.typeCardOcr.postValue(Configs.TYPE_NID_12)
                navigate(MainDirections.actionGlobalOcrFragment())
            }
            documentRootCccd -> {
                Shared.typeCardOcr.postValue(Configs.TYPE_CCCD)
                navigate(MainDirections.actionGlobalOcrFragment())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        disposable = Observable.timer(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val urlPath = "android.resource://" + activity?.packageName + "/" + R.raw.card
                    documentTipGif.setVideoURI(Uri.parse(urlPath))
                    documentTipGif.start()
                    documentTipGif.setOnCompletionListener { it.start() }
                    addClickListener(documentRootNid, documentRootNid12, documentRootCccd, documentRootPassport)
                }, {})
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

}