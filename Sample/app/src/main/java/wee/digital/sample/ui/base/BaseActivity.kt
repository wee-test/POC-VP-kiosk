package wee.digital.sample.ui.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.tbruyelle.rxpermissions2.RxPermissions
import wee.digital.library.extension.hideSystemUI
import wee.digital.library.util.Logger
import wee.digital.sample.BuildConfig


abstract class BaseActivity : AppCompatActivity(), BaseView {

    /**
     * [AppCompatActivity] override
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResource())
        onViewCreated()
        onLiveDataObserve()
    }

    /**
     * [BaseActivity] abstract implements
     */
    abstract fun layoutResource(): Int

    abstract fun onViewCreated()

    abstract fun onLiveDataObserve()

    /**
     * [BaseView] implement
     */
    override fun navController(): NavController? {
        return null
    }

    final override val log by lazy { Logger(this::class) }

    final override val fragmentActivity: FragmentActivity get() = this

    final override val lifecycleOwner: LifecycleOwner get() = this

    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    @SuppressLint("CheckResult")
    open fun permissionRequest(vararg permission: String, block: () -> Unit) {
        RxPermissions(this).requestEach(*permission)
                .subscribe {
                    when {
                        it.granted -> {
                            block()
                        }
                        it.shouldShowRequestPermissionRationale -> {
                        }
                        else -> {
                            val i = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID))
                            startActivity(i)
                        }
                    }
                }
    }

    /**
     * [BaseActivity] properties
     */
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

}
