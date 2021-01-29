package wee.digital.sample.util.extention

import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * -------------------------------------------------------------------------------------------------
 * @Project: FEKiosk
 * @Created: Huy 2020/10/12
 * @Organize: Wee Digital
 * @Description: ...
 * All Right Reserved
 * -------------------------------------------------------------------------------------------------
 */

interface SimpleSingleObserver<T> : SingleObserver<T> {
    override fun onSubscribe(d: Disposable) {
    }

    override fun onError(e: Throwable) {
        onCompleted(null, e)
    }

    override fun onSuccess(t: T) {
        onCompleted(t, null)
    }

    fun onCompleted(t: T?, e: Throwable?) {

    }

}

fun <T> Single<T>.subscribeOnIo(): Single<T> {
    return this.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}