package wee.digital.sample.ui.main

import androidx.navigation.NavController
import androidx.navigation.findNavController
import wee.digital.sample.R
import wee.digital.sample.ui.base.BaseActivity

class MainActivity : BaseActivity() {

    override fun layoutResource(): Int {
        return R.layout.activity_main
    }


    override fun navController(): NavController {
        return findNavController(R.id.mainFragmentHost)
    }

    override fun onViewCreated() {
    }

    override fun onLiveDataObserve() {

    }

}