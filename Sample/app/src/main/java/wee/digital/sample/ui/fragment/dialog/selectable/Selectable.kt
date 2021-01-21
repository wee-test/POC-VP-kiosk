package wee.digital.sample.ui.fragment.dialog.selectable

import wee.digital.library.extension.string

open class Selectable {
    var id: Int
    var text: String
    var icon: Int?

    constructor(id: Int, text: String, icon: Int? = null) {
        this.id = id
        this.text = text
        this.icon = icon
    }

    constructor(id: Int, text: Int, icon: Int? = null) : this(id, string(text), icon)

    override fun toString(): String {
        return text
    }
}

