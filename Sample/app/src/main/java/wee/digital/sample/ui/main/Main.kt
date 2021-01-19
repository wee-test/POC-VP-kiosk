package wee.digital.sample.ui.main

import wee.digital.sample.MainDirections

object Main {

    val com get() = MainDirections.actionGlobalComFragment()

    /**
     * Dialog destinations
     */
    val alert get() = MainDirections.actionGlobalAlertFragment()

    val selectable get() = MainDirections.actionGlobalSelectableFragment()

    /**
     * Fragment destinations
     */
    val splash get() = MainDirections.actionGlobalSplashFragment()

    val home get() = MainDirections.actionGlobalHomeFragment()

    val login get() = MainDirections.actionGlobalLoginFragment()

    val register get() = MainDirections.actionGlobalRegisterFragment()

}