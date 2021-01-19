package wee.digital.sample.ui.base

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import wee.digital.sample.R
import wee.digital.library.util.Logger

abstract class BaseDialog : DialogFragment(), BaseView {

    /**
     * [DialogFragment] override
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, style())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.App_DialogAnim
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutResource(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log.d("onViewCreated")
        onViewCreated()
        onLiveDataObserve()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        view?.clearAnimation()
        log.d("onViewDestroy")
        dialog?.window?.attributes?.windowAnimations = 0
    }

    override fun onStart() {
        super.onStart()
        when (style()) {
            R.style.App_Dialog_FullScreen,
            R.style.App_Dialog_FullScreen_Transparent -> dialog?.window?.apply {
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        log.d("onCancel")
    }

    override fun onResume() {
        super.onResume()
        log.d("onResume")
    }

    override fun onPause() {
        super.onPause()
        view?.clearAnimation()
        log.d("onPause")
    }

    /**
     * [BaseDialog] Required implements
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

    /**
     * [BaseDialog] properties
     */
    protected open fun style(): Int {
        return R.style.App_Dialog_FullScreen
    }

}