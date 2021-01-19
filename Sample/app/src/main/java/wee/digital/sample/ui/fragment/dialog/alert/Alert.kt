package wee.digital.sample.ui.fragment.dialog.alert

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import wee.digital.sample.R

class Alert {

    class Arg constructor(

            var icon: Int = R.drawable.ic_placeholder,

            var title: String = "",

            var message: String? = null,

            @ColorRes
            var iconBackgroundTint: Int = R.color.colorPrimary,

            @DrawableRes
            var acceptBackground: Int = R.color.colorWhite,

            var acceptLabel: String = "Accept",

            var cancelLabel: String = "Cancel",

            var acceptOnClick: (AlertFragment) -> Unit = {},

            var cancelOnClick: (AlertFragment) -> Unit = {},

            var onDismiss: () -> Unit = {},

            var hideCancel: Boolean = false
    )

}