package wee.digital.sample.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import wee.digital.library.util.Logger

abstract class BaseFragment : Fragment(), BaseView {

    /**
     * [Fragment] override
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutResource(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewCreated()
        onLiveDataObserve()
    }

    override fun onPause() {
        super.onPause()
        view?.clearAnimation()
    }

    /**
     * [BaseFragment] required implements
     */
    abstract fun layoutResource(): Int

    abstract fun onViewCreated()

    abstract fun onLiveDataObserve()

    /**
     * [BaseView] implement
     */
    final override val log by lazy { Logger(this::class) }

    final override val fragmentActivity: FragmentActivity get() = requireActivity()

    final override val lifecycleOwner: LifecycleOwner get() = viewLifecycleOwner

    override fun navController(): NavController? {
        return findNavController()
    }

}