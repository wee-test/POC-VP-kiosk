package wee.digital.library.extension

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.transition.Transition

fun View.animRotateAxisY(block: ObjectAnimator.() -> Unit): ObjectAnimator {
    return ObjectAnimator.ofFloat(this, "rotationY", 0.0f, 360f).also {
        it.interpolator = AccelerateDecelerateInterpolator()
        it.block()
    }
}

fun View.animateAlpha(duration: Int, alpha: Float, onAnimationEnd: () -> Unit = {}) {
    clearAnimation()
    val anim = AlphaAnimation(this.alpha, alpha)
    anim.duration = duration.toLong()
    anim.fillAfter = true
    anim.setAnimationListener(object : SimpleAnimationListener {
        override fun onAnimationStart(animation: Animation?) {
            this@animateAlpha.alpha = 1f
        }

        override fun onAnimationEnd(animation: Animation?) {
            this@animateAlpha.alpha = alpha
            onAnimationEnd()
        }
    })
    post {
        startAnimation(anim)
    }
}

fun View.animateAlpha(duration: Int, formAlpha: Float, toAlpha: Float, onAnimationEnd: () -> Unit = {}) {
    clearAnimation()
    val anim = AlphaAnimation(formAlpha, toAlpha)
    anim.duration = duration.toLong()
    anim.fillAfter = true
    anim.setAnimationListener(object : SimpleAnimationListener {
        override fun onAnimationEnd(animation: Animation?) {
            onAnimationEnd()
        }
    })
    post {
        startAnimation(anim)
    }
}

fun animCenterScale(duration: Long = 500): ScaleAnimation {
    return ScaleAnimation(
            0f, 1f, 0f, 1f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
    ).also {
        it.duration = duration
    }
}

fun Animation?.onAnimationStart(onStart: () -> Unit): Animation? {
    this?.setAnimationListener(object : SimpleAnimationListener {
        override fun onAnimationStart(animation: Animation?) {
            onStart()
        }
    })
    return this
}

fun Animation?.onAnimationEnd(onEnd: () -> Unit): Animation? {
    this?.setAnimationListener(object : SimpleAnimationListener {
        override fun onAnimationEnd(animation: Animation?) {
            onEnd()
        }
    })
    return this
}

fun ObjectAnimator.onAnimatorEnd(onEnd: () -> Unit): ObjectAnimator {
    this.addListener(object : SimpleAnimatorListener {
        override fun onAnimationEnd(animator: Animator?) {
            onEnd()
        }
    })
    return this
}

fun setAnim(anim: ViewPropertyAnimator, duration: Long = 300L) {
    anim.setDuration(duration).interpolator = FastOutSlowInInterpolator()
}

interface SimpleAnimationListener : Animation.AnimationListener {
    override fun onAnimationRepeat(animation: Animation?) {
    }

    override fun onAnimationEnd(animation: Animation?) {
    }

    override fun onAnimationStart(animation: Animation?) {
    }
}

interface SimpleAnimatorListener : Animator.AnimatorListener {
    override fun onAnimationRepeat(animator: Animator?) {
    }

    override fun onAnimationEnd(animator: Animator?) {
    }

    override fun onAnimationCancel(animator: Animator?) {
    }

    override fun onAnimationStart(animator: Animator?) {
    }
}

interface SimpleTransitionListener : Transition.TransitionListener {
    override fun onTransitionStart(transition: Transition) {
    }

    override fun onTransitionEnd(transition: Transition) {
    }

    override fun onTransitionCancel(transition: Transition) {
    }

    override fun onTransitionPause(transition: Transition) {
    }

    override fun onTransitionResume(transition: Transition) {
    }

}


