package wee.digital.sample.ui.fragment.adv

import androidx.lifecycle.MutableLiveData
import wee.digital.sample.shared.Shared
import wee.digital.sample.ui.base.BaseViewModel

class AdvVM : BaseViewModel() {

    val advLiveData = MutableLiveData<List<Int>>()

    fun getListAdv(){
        advLiveData.postValue(Shared.listAdv)
    }

}