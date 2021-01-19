package wee.digital.sample.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import wee.digital.sample.ui.fragment.alert.Alert

open class MainVM : ViewModel() {

    val alertLiveData = MutableLiveData<Alert.Arg?>()
}