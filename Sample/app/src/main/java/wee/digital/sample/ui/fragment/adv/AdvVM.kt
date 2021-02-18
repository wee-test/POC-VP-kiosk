package wee.digital.sample.ui.fragment.adv

import androidx.lifecycle.MutableLiveData
import wee.digital.sample.R
import wee.digital.sample.ui.base.BaseViewModel

class AdvVM : BaseViewModel() {

    val advLiveData = MutableLiveData<List<Int>>()

    fun getListAdv(){
        advLiveData.postValue(listOf(R.mipmap.adv1, R.mipmap.adv2))
    }

}