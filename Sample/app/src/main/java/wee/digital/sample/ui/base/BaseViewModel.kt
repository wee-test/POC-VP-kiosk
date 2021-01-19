package wee.digital.sample.ui.base

import androidx.lifecycle.ViewModel
import wee.digital.library.util.Logger

abstract class BaseViewModel : ViewModel() {

    val log by lazy { Logger(this::class) }

}
