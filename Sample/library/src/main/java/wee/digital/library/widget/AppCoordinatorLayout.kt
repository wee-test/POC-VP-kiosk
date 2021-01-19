package wee.digital.library.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.coordinatorlayout.widget.CoordinatorLayout

class AppCoordinatorLayout(context: Context, attrs: AttributeSet) : CoordinatorLayout(context, attrs) {

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

}
