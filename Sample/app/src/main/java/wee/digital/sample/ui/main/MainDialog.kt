package wee.digital.sample.ui.main

import wee.digital.sample.ui.base.BaseDialog
import wee.digital.sample.ui.base.activityVM

abstract class MainDialog : BaseDialog() {

    val mainVM: MainVM by lazy { activityVM(MainVM::class) }

}