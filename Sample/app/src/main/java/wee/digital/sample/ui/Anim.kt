package wee.digital.sample.ui

import android.graphics.Bitmap
import android.transition.TransitionManager
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import wee.digital.library.extension.show

fun ConstraintLayout.animOcrCaptured(bitmap: Bitmap?, animView: ImageView, targetView: ImageView, view : View, onCompleted: () -> Unit) {
    animView.setImageBitmap(bitmap)
    animView.show()
    TransitionManager.beginDelayedTransition(this)
    val id = animView.id
    ConstraintSet().apply {
        clone(this)
        constrainWidth(id, targetView.width)
        constrainHeight(id, targetView.height)
        connect(id, ConstraintSet.START, targetView.id, ConstraintSet.START)
        connect(id, ConstraintSet.TOP, targetView.id, ConstraintSet.TOP)
    }.applyTo(this)
    postDelayed({
        animView.setImageBitmap(null)
        targetView.setImageBitmap(bitmap)
        onCompleted()
        resetAnim(animView, this, view)
    }, 400)
}

private fun resetAnim(imageAnim: ImageView, container: ConstraintLayout, view: View) {
    TransitionManager.beginDelayedTransition(container)
    val set = ConstraintSet()
    set.clone(container)

    set.clear(imageAnim.id, ConstraintSet.BOTTOM)
    set.clear(imageAnim.id, ConstraintSet.TOP)
    set.clear(imageAnim.id, ConstraintSet.START)
    set.clear(imageAnim.id, ConstraintSet.END)

    set.connect(imageAnim.id, ConstraintSet.BOTTOM, view.id, ConstraintSet.BOTTOM)
    set.connect(imageAnim.id, ConstraintSet.TOP, view.id, ConstraintSet.TOP)
    set.connect(imageAnim.id, ConstraintSet.START, container.id, ConstraintSet.START)
    set.connect(imageAnim.id, ConstraintSet.END, container.id, ConstraintSet.END)

    set.applyTo(container)
}