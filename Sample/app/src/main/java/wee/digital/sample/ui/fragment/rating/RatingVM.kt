package wee.digital.sample.ui.fragment.rating

import android.annotation.SuppressLint
import android.util.Log
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import vplib.ResponseCustomerServiceReview
import wee.digital.sample.app.lib
import wee.digital.sample.repository.model.ServiceReviewReq
import wee.digital.sample.ui.base.BaseViewModel
import wee.digital.sample.ui.base.EventLiveData

class RatingVM: BaseViewModel() {

    val statusCustomerService = EventLiveData<ResponseCustomerServiceReview>()

    @SuppressLint("CheckResult")
    fun serviceReview(body : ServiceReviewReq){
        Single.fromCallable {
            Log.e("serviceReview", "$body")
            lib?.kioskService!!.customerServiceReview(Gson().toJson(body).toByteArray())
        }.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    Log.e("serviceReview", "$it")
                    statusCustomerService.postValue(it)
                },{
                    Log.e("serviceReview", "${it.message}")
                    statusCustomerService.postValue(null)
                })
    }

}