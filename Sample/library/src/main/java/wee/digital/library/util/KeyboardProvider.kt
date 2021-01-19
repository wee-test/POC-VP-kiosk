package wee.digital.library.util

import android.app.Activity
import android.content.res.Configuration
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import wee.digital.library.R
import wee.digital.library.extension.screenHeight

/**
 * -------------------------------------------------------------------------------------------------
 * @Project: Kotlin
 * @Created: Huy QV 2018/09/01
 * @Description:
 *
 * Manifest:
 * <activity android:name=".ui.main.MainActivity"
 * android:windowSoftInputMode="adjustNothing"/>
 *
 * Fragment:
 * keyboardProvider = KeyboardProvider(activity!!)
 * view?.post { keyboardProvider!!.start() }
 *
 * Activity:
 * keyboardProvider = KeyboardProvider(this)
 * post { keyboardProvider!!.start() }
 * None Right Reserved
 * -------------------------------------------------------------------------------------------------
 */
class KeyboardProvider constructor(private val activity: Activity) : PopupWindow(activity) {

    interface KeyboardListener {
        fun onKeyboardShow(height: Int, orientation: Int)

        fun onKeyboardHide(orientation: Int)
    }

    var keyboardListener: KeyboardListener? = null

    /** The cached landscape height of the keyboard  */
    private var landscapeHeight: Int = 0

    /** The cached portrait height of the keyboard  */
    private var portraitHeight: Int = 0

    private val popupView: View?

    private val parentView: View

    /**
     * Get the screen orientation
     *
     * @return the screen orientation
     */
    private val screenOrientation: Int
        get() = activity.resources.configuration.orientation

    init {

        val inflater = activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.popupView = inflater.inflate(R.layout.view_popup, null, false)
        contentView = popupView

        softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
        inputMethodMode = INPUT_METHOD_NEEDED

        parentView = activity.findViewById(android.R.id.content)

        width = 0
        height = WindowManager.LayoutParams.MATCH_PARENT

        popupView!!.viewTreeObserver.addOnGlobalLayoutListener {
            handleOnGlobalLayout()
        }
    }

    /**
     * Start the KeyboardProvider, this must be called after the onResume of the Activity.
     * PopupWindows are not allowed to be registered before the onResume has finished
     * of the Activity.
     */
    fun observer(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {

            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun onStart() {
                Handler(Looper.getMainLooper())
                        .postDelayed({
                            start()
                        }, 1000)
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onPause() {
                close()
            }
        })
    }

    fun start() {
        if (!isShowing && parentView.windowToken != null) {
            setBackgroundDrawable(ColorDrawable(0))
            showAtLocation(parentView, Gravity.NO_GRAVITY, 0, 0)
        }
    }

    fun close() {
        this.keyboardListener = null
        dismiss()
    }

    /**
     * Popup window itself is as big as the window of the Activity.
     * The keyboard can then be calculated by extracting the popup view bottom
     * from the activity window height.
     */
    private fun handleOnGlobalLayout() {
        val keyboardHeight = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val rect = activity.windowManager.currentWindowMetrics.bounds
            popupView!!.getWindowVisibleDisplayFrame(rect)
            activity.screenHeight - rect.bottom
        } else {
            val rect = Rect()
            val screenSize = Point()
            @Suppress("DEPRECATION")
            activity.windowManager.defaultDisplay.getSize(screenSize)
            popupView!!.getWindowVisibleDisplayFrame(rect)
            screenSize.y - rect.bottom
        }

        when {

            keyboardHeight == 0 -> {
                keyboardListener?.onKeyboardHide(screenOrientation)
            }
            screenOrientation == Configuration.ORIENTATION_PORTRAIT -> {
                if (portraitHeight == keyboardHeight) return
                portraitHeight = keyboardHeight
                keyboardListener?.onKeyboardShow(keyboardHeight, screenOrientation)
            }
            else -> {
                if (landscapeHeight == keyboardHeight) return
                landscapeHeight = keyboardHeight
                keyboardListener?.onKeyboardShow(keyboardHeight, screenOrientation)
            }
        }

    }

}