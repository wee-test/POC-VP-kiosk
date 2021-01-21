package wee.digital.sample.ui.main

import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import wee.digital.sample.R
import wee.digital.sample.ui.base.BaseActivity
import wee.digital.sample.ui.base.activityVM

class MainActivity : BaseActivity() {

    val mainVM: MainVM by lazy { activityVM(MainVM::class) }

    override fun layoutResource(): Int {
        return R.layout.activity_main
    }


    override fun navController(): NavController {
        return findNavController(R.id.mainFragmentHost)
    }

    override fun onViewCreated() {
    }

    override fun onLiveDataObserve() {
        mainVM.dialogLiveData.observe(this::onShowDialog)
    }

    private fun onShowDialog(directions: NavDirections?) {
        directions ?: return
        navigate(directions) {
            setVerticalAnim()
        }
    }

}
