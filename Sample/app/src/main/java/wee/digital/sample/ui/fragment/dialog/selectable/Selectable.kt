package wee.digital.sample.ui.fragment.dialog.selectable

import wee.digital.library.extension.string

open class Selectable {

    open val id: Int
    open val text: String
    open val icon: Int

    constructor(id: Int, text: String, icon: Int = 0) {
        this.id = id
        this.text = text
        this.icon = icon
    }

    constructor(id: Int, text: Int, icon: Int = 0) {
        this.id = id
        this.text = string(text)
        this.icon = icon
    }
}

