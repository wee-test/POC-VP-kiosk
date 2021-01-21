package wee.digital.library.widget

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.animation.Interpolator
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import wee.digital.library.R

class ExpandableLayout : ConstraintLayout {

    companion object {
        const val COLLAPSED = 0
        const val COLLAPSING = 1
        const val EXPANDING = 2
        const val EXPANDED = 3
        const val KEY_SUPER_STATE = "super_state"
        const val KEY_EXPANSION = "expansion"
        const val HORIZONTAL = 0
        const val VERTICAL = 1
        private const val DEFAULT_DURATION = 300
    }

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        val a = getContext().obtainStyledAttributes(attrs, R.styleable.ExpandableLayout)
        duration = a.getInt(R.styleable.ExpandableLayout_el_duration, DEFAULT_DURATION)
        expansion = if (a.getBoolean(R.styleable.ExpandableLayout_el_expanded, false)) 1f else 0f
        orientation = a.getInt(R.styleable.ExpandableLayout_android_orientation, VERTICAL)
        parallax = a.getFloat(R.styleable.ExpandableLayout_el_parallax, 1f)
        a.recycle()
        state = if (expansion == 0f) COLLAPSED else EXPANDED
    }

    var duration = DEFAULT_DURATION

    var parallax: Float = 0f
        set(value) {
            var sP = value
            sP = Math.min(1f, Math.max(0f, sP))
            field = sP
        }

    var orientation: Int = 0
        set(value) {
            require(!(value < 0 || value > 1)) { "Orientation must be either 0 (horizontal) or 1 (vertical)" }
            field = value
        }

    var expansion: Float = 0f
        set(value) {
            if (expansion == value) return
            // Infer state from previous value
            val delta = value - this.expansion
            state = when {
                value == 0f -> COLLAPSED
                value == 1f -> EXPANDED
                delta < 0 -> COLLAPSING
                delta > 0 -> EXPANDING
                else -> state
            }
            visibility = if (state == COLLAPSED) GONE else VISIBLE
            field = value
            requestLayout()
            listener?.onExpansionUpdate(value, state)
        }

    /**
     * Get expansion state
     *
     * @return one of [State]
     */
    var state = 0
        private set
    private var interpolator: Interpolator = FastOutSlowInInterpolator()
    private var animator: ValueAnimator? = null
    private var listener: OnExpansionUpdateListener? = null

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val bundle = Bundle()
        expansion = if (isExpanded) 1f else 0f
        bundle.putFloat(KEY_EXPANSION, expansion)
        bundle.putParcelable(KEY_SUPER_STATE, superState)
        return bundle
    }

    override fun onRestoreInstanceState(parcelable: Parcelable) {
        val bundle = parcelable as Bundle
        expansion = bundle.getFloat(KEY_EXPANSION)
        state = if (expansion == 1f) EXPANDED else COLLAPSED
        val superState = bundle.getParcelable<Parcelable>(KEY_SUPER_STATE)
        super.onRestoreInstanceState(superState)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth
        val height = measuredHeight
        val size = if (orientation == LinearLayout.HORIZONTAL) width else height
        visibility = if (expansion == 0f && size == 0) GONE else VISIBLE
        val expansionDelta = size - Math.round(size * expansion)
        if (parallax > 0) {
            val parallaxDelta = expansionDelta * parallax
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (orientation == HORIZONTAL) {
                    var direction = -1
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && layoutDirection == LAYOUT_DIRECTION_RTL) {
                        direction = 1
                    }
                    child.translationX = direction * parallaxDelta
                } else {
                    child.translationY = -parallaxDelta
                }
            }
        }
        if (orientation == HORIZONTAL) {
            setMeasuredDimension(width - expansionDelta, height)
        } else {
            setMeasuredDimension(width, height - expansionDelta)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        animator?.cancel()
        super.onConfigurationChanged(newConfig)
    }

    /**
     * Convenience method - same as calling setExpanded(expanded, true)
     */
    var isExpanded: Boolean
        get() = state == EXPANDING || state == EXPANDED
        set(expand) {
            setExpanded(expand, true)
        }

    @JvmOverloads
    fun toggle(animate: Boolean = true) {
        if (isExpanded) {
            collapse(animate)
        } else {
            expand(animate)
        }
    }

    @JvmOverloads
    fun expand(animate: Boolean = true) {
        setExpanded(true, animate)
    }

    @JvmOverloads
    fun collapse(animate: Boolean = true) {
        setExpanded(false, animate)
    }

    fun setExpanded(expand: Boolean, animate: Boolean) {
        if (expand == isExpanded) {
            return
        }
        val targetExpansion = if (expand) 1 else 0
        if (animate) {
            animateSize(targetExpansion)
        } else {
            expansion = targetExpansion.toFloat()
        }
    }

    private fun animateSize(targetExpansion: Int) {
        if (animator != null) {
            animator?.cancel()
            animator = null
        }
        animator = ValueAnimator.ofFloat(expansion, targetExpansion.toFloat()).also {
            it.interpolator = interpolator
            it.duration = duration.toLong()
            it.addUpdateListener { valueAnimator ->
                expansion = valueAnimator.animatedValue as Float
            }
            it.addListener(ExpansionListener(targetExpansion))
            it.start()
        }
    }

    interface OnExpansionUpdateListener {
        /**
         * Callback for expansion updates
         *
         * @param expansionFraction Value between 0 (collapsed) and 1 (expanded) representing the the expansion progress
         * @param state             One of [State] repesenting the current expansion state
         */
        fun onExpansionUpdate(expansionFraction: Float, state: Int)
    }

    private inner class ExpansionListener(private val targetExpansion: Int) : Animator.AnimatorListener {

        private var canceled = false

        override fun onAnimationStart(animation: Animator) {
            state = if (targetExpansion == 0) COLLAPSING else EXPANDING
        }

        override fun onAnimationEnd(animation: Animator) {
            if (!canceled) {
                state = if (targetExpansion == 0) COLLAPSED else EXPANDED
                expansion = targetExpansion.toFloat()
            }
        }

        override fun onAnimationCancel(animation: Animator) {
            canceled = true
        }

        override fun onAnimationRepeat(animation: Animator) {}
    }


}
