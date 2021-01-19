package wee.digital.sample.ui.base

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import wee.digital.sample.R
import wee.digital.library.extension.ViewClickListener
import wee.digital.library.extension.hideKeyboard
import wee.digital.library.extension.post
import wee.digital.library.util.Logger
import kotlin.reflect.KClass

interface BaseView {

    val className: String get() = this::class.simpleName.toString()

    val lifecycleOwner: LifecycleOwner

    val fragmentActivity: FragmentActivity

    val log: Logger

    fun addClickListener(vararg views: View?) {
        val listener = object : ViewClickListener(300L) {
            override fun onClicks(v: View?) {
                fragmentActivity.hideKeyboard()
                onViewClick(v)
            }
        }
        views.forEach {
            it?.setOnClickListener(listener)
        }
    }

    fun onViewClick(v: View?) {}

    fun navController(): NavController?

    fun <T : Activity> navigate(cls: KClass<T>) {
        fragmentActivity.startActivity(Intent(fragmentActivity, cls.java))
    }

    fun <T : Activity> launch(cls: KClass<T>) {
        navigate(cls)
        fragmentActivity.finish()
    }

    fun navigate(directions: NavDirections, extras: Navigator.Extras? = null, block: (NavOptions.Builder.() -> Unit) = {}) {
        val options = NavOptions.Builder().also {
            it.setVerticalAnim()
            it.block()
        }.build()
        navController()?.navigate(directions.actionId, null, options, extras)
    }

    fun navigateUp() {
        navController()?.navigateUp()
    }

    fun NavOptions.Builder.setParallaxAnim(reserved: Boolean = false) {
        if (reserved) {
            setEnterAnim(R.anim.parallax_pop_enter)
            setExitAnim(R.anim.parallax_pop_exit)
            setPopEnterAnim(R.anim.parallax_enter)
            setPopExitAnim(R.anim.parallax_exit)
        } else {
            setEnterAnim(R.anim.parallax_enter)
            setExitAnim(R.anim.parallax_exit)
            setPopEnterAnim(R.anim.parallax_pop_enter)
            setPopExitAnim(R.anim.parallax_pop_exit)
        }
    }

    fun NavOptions.Builder.setHorizontalAnim(reserved: Boolean = false) {
        if (reserved) {
            setEnterAnim(R.anim.horizontal_pop_enter)
            setExitAnim(R.anim.horizontal_pop_exit)
            setPopEnterAnim(R.anim.horizontal_enter)
            setPopExitAnim(R.anim.horizontal_exit)
        } else {
            setEnterAnim(R.anim.horizontal_enter)
            setExitAnim(R.anim.horizontal_exit)
            setPopEnterAnim(R.anim.horizontal_pop_enter)
            setPopExitAnim(R.anim.horizontal_pop_exit)
        }

    }

    fun NavOptions.Builder.setVerticalAnim(): NavOptions.Builder {
        setEnterAnim(R.anim.vertical_enter)
        setExitAnim(R.anim.vertical_exit)
        setPopEnterAnim(R.anim.vertical_pop_enter)
        setPopExitAnim(R.anim.vertical_pop_exit)
        return this
    }

    fun NavOptions.Builder.setLaunchSingleTop(): NavOptions.Builder {
        setLaunchSingleTop(true)
        navController()?.graph?.id?.also {
            setPopUpTo(it, false)
        }
        return this
    }

    fun <T> LiveData<T>.observes(vararg blocks: (T) -> Unit) {
        observe(lifecycleOwner, Observer { data ->
            blocks?.forEach { unit ->
                unit(data)
            }
        })
    }

    fun <T> LiveData<T>.observe(block: (T) -> Unit) {
        observe(lifecycleOwner, Observer(block))
    }

    fun <T> LiveData<T>.removeObservers() {
        removeObservers(lifecycleOwner)
    }

    fun <T> MutableLiveData<T>.postDelayed(delay: Long, _value: T) {
        post(delay) {
            value = _value
        }
    }
}