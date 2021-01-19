package wee.digital.library.extension

import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.ChangeBounds
import androidx.transition.Transition
import androidx.transition.TransitionManager

fun ConstraintLayout.editConstraint(block: ConstraintSet.() -> Unit) {
    ConstraintSet().also {
        it.clone(this)
        it.block()
        it.applyTo(this)
    }
}

fun Transition.beginTransition(layout: ConstraintLayout, block: ConstraintSet.() -> Unit): Transition {
    TransitionManager.beginDelayedTransition(layout, this)
    layout.editConstraint(block)
    return this
}

fun Transition.beginTransition(layout: ConstraintLayout, block: ConstraintSet.() -> Unit, onEnd: () -> Unit = {}): Transition {

    addListener(object : SimpleTransitionListener {
        override fun onTransitionEnd(transition: Transition) {
            transition.removeListener(this)
            onEnd()
        }
    })
    TransitionManager.beginDelayedTransition(layout, this)
    layout.editConstraint(block)
    return this
}

fun ConstraintLayout.beginTransition(duration: Long, block: ConstraintSet.() -> Unit) {
    ChangeBounds().also {
        it.duration = duration
        it.startDelay = 0
    }.beginTransition(this, block)
}

fun ConstraintLayout.beginTransition(duration: Long, block: ConstraintSet.() -> Unit, onEnd: () -> Unit = {}) {
    ChangeBounds().also {
        it.duration = duration
        it.startDelay = 0
    }.beginTransition(this, block, onEnd)

}

fun MotionLayout.transitionToState(transitionId: Int, onCompleted: () -> Unit) {
    addTransitionListener(object : SimpleMotionTransitionListener {
        override fun onTransitionCompleted(layout: MotionLayout?, currentId: Int) {
            removeTransitionListener(this)
            onCompleted()
        }
    })
    transitionToState(transitionId)

}

class ConstraintBuilder(private val constraintLayout: ConstraintLayout) {

    private val constraintSetList = mutableListOf<ConstraintSet.() -> Unit>()

    private val transitionList = mutableListOf<Transition>()

    fun transform(_duration: Long = 400, block: ConstraintSet.() -> Unit): ConstraintBuilder {
        transitionList.add(ChangeBounds().also {
            it.duration = _duration
        })
        constraintSetList.add(block)
        return this
    }

    fun start() {
        if (transitionList.isEmpty()) return
        for (i in 0..transitionList.lastIndex) {
            transitionList[i].addListener(object : SimpleTransitionListener {
                override fun onTransitionEnd(transition: Transition) {
                    transitionList[i].removeListener(this)
                    if (i < transitionList.lastIndex) {
                        transitionList[i + 1].beginTransition(constraintLayout, constraintSetList[i + 1])
                    }
                }
            })
        }
        transitionList[0].beginTransition(constraintLayout, constraintSetList[0])
    }

}