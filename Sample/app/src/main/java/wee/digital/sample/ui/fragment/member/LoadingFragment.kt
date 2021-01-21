package wee.digital.sample.ui.fragment.member

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.view_header.*
import wee.digital.library.extension.gone
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.ui.main.MainFragment
import java.util.concurrent.TimeUnit

class LoadingFragment : MainFragment() {

    private var disposable : Disposable? = null

    override fun layoutResource(): Int = R.layout.loading

    override fun onViewCreated() {
        headerAction.gone()
    }

    override fun onLiveDataObserve() {}

    private fun startTime(){
        disposable = Observable.timer(3, TimeUnit.SECONDS)
                .subscribe { navigate(MainDirections.actionGlobalRatingFragment()) }
    }

    override fun onResume() {
        super.onResume()
        startTime()
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

}