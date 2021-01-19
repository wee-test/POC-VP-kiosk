package wee.digital.library.widget

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.widget.AppCompatImageView

/**
 * -------------------------------------------------------------------------------------------------
 *
 * @Project: Kotlin
 * @Created: Huy QV 2019/02/18
 * @Description: ...
 * None Right Reserved
 * -------------------------------------------------------------------------------------------------
 */
class AnimateImageView : AppCompatImageView {

    private val defaultAnim: ObjectAnimator
        get() {
            return ObjectAnimator.ofFloat(this, "rotation", 0f, 36000f).apply {
                duration = 200000
                interpolator = DecelerateInterpolator()
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.RESTART
            }
        }

    private var animation: ObjectAnimator? = null

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimation()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == View.VISIBLE) {
            startAnimation()
        } else {
            stopAnimation()
        }
    }

    private fun startAnimation() {
        if (animation == null) animation = defaultAnim
        animation?.start()
    }

    private fun stopAnimation() {
        animation?.end()
    }

}
