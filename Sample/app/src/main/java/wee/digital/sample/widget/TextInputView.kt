package wee.digital.sample.widget

import android.app.Activity
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Paint
import android.graphics.Rect
import android.text.Editable
import android.text.InputFilter
import android.util.AttributeSet
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.widget_text_input.view.*
import wee.digital.library.extension.*
import wee.digital.library.widget.AppCustomView
import wee.digital.sample.R
import wee.digital.sample.ui.fragment.dialog.selectable.Selectable
import wee.digital.sample.ui.fragment.dialog.selectable.SelectableAdapter
import wee.digital.sample.ui.main.Main
import wee.digital.sample.ui.main.MainVM

class TextInputView : AppCustomView,
        SimpleMotionTransitionListener,
        OnFocusChangeListener,
        SimpleTextWatcher {

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)

    override fun layoutResource(): Int {
        return R.layout.widget_text_input
    }

    override fun onInitialize(context: Context, types: TypedArray) {
        hint = types.hint
        textColorHint = types.textColorHint
        onIconInitialize(inputImageViewIcon, types)
        onEditTextInitialize(inputEditText, types)
        inputViewLayout.addTransitionListener(this)

        if (text.isNullOrEmpty()) {
            inputViewLayout.setTransition(R.id.unfocused, R.id.focused)
            updateViewOnFocus(false)
        } else {
            inputViewLayout.setTransition(R.id.focused, R.id.unfocused)
            updateViewOnFocus(true)
        }
    }

    private fun onIconInitialize(it: AppCompatImageView, types: TypedArray) {
        val color = types.getColor(R.styleable.CustomView_android_tint, -1)
        if (color != -1) {
            it.setColorFilter(color)
        }
        src = types.srcRes
    }

    private fun onEditTextInitialize(it: AppCompatEditText, types: TypedArray) {
        it.setText(types.text)
        it.setTextColor(types.textColor)
        it.addTextChangedListener(this)

        it.onFocusChangeListener = this@TextInputView
        it.paintFlags = it.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()

        if (null == types) {
            it.maxLines = 1
            it.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(256))
            return
        }
        // Text filter
        val sFilters = arrayListOf<InputFilter>()

        val textAllCaps = types.getBoolean(R.styleable.CustomView_android_textAllCaps, false)
        if (textAllCaps) sFilters.add(InputFilter.AllCaps())

        val sMaxLength = types.getInt(R.styleable.CustomView_android_maxLength, 256)
        sFilters.add(InputFilter.LengthFilter(sMaxLength))

        val array = arrayOfNulls<InputFilter>(sFilters.size)
        it.filters = sFilters.toArray(array)

        // Input type
        val customInputType = types.getInt(R.styleable.CustomView_android_inputType, EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
        if (customInputType == EditorInfo.TYPE_NULL) {
            disableFocus()
        } else {
            it.inputType = customInputType or
                    EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS or
                    EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or
                    EditorInfo.TYPE_TEXT_VARIATION_FILTER or
                    EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING
        }

        it.maxLines = types.getInt(R.styleable.CustomView_android_maxLines, 1)

        // Ime option
        val imeOption = types.getInt(R.styleable.CustomView_android_imeOptions, -1)
        if (imeOption != -1) it.imeOptions = imeOption
        it.privateImeOptions = "nm,com.google.android.inputmethod.latin.noMicrophoneKey"

        // Gesture
        it.setOnLongClickListener {
            return@setOnLongClickListener true
        }
        it.setTextIsSelectable(false)
        it.customSelectionActionModeCallback = object : ActionMode.Callback {
            override fun onPrepareActionMode(mode: ActionMode, menu: Menu) = false
            override fun onDestroyActionMode(mode: ActionMode) {}
            override fun onCreateActionMode(mode: ActionMode, menu: Menu) = false
            override fun onActionItemClicked(mode: ActionMode, item: MenuItem) = false
        }
        it.isLongClickable = false
        it.setOnCreateContextMenuListener { menu, _, _ -> menu.clear() }
    }

    override fun requestFocus(direction: Int, previouslyFocusedRect: Rect?): Boolean {
        if (!inputEditText.hasFocus()) {
            inputEditText.requestFocus(FOCUS_DOWN)
        }
        return false
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        super.setOnClickListener(listener)
        if (null == listener) {
            enableFocus()
            inputTextViewHint.setOnClickListener(null)
            inputEditText.setOnClickListener(null)
        } else {
            disableFocus()
            val onClick = OnClickListener {
                listener?.onClick(this@TextInputView)
            }
            inputTextViewHint.addViewClickListener(onClick)
            inputEditText.addViewClickListener(onClick)
        }
    }

    override fun setOnFocusChangeListener(l: OnFocusChangeListener?) {
        if (l != null) {
            onFocusChange.add(l)
        } else {
            onFocusChange.clear()
        }
    }

    override fun onDetachedFromWindow() {
        inputViewLayout.clearAnimation()
        onFocusChange.clear()
        super.onDetachedFromWindow()
    }

    override fun isFocused(): Boolean {
        return inputEditText.isFocused
    }

    override fun hasFocus(): Boolean {
        return inputEditText.hasFocus()
    }

    override fun clearFocus() {
        super.clearFocus()
        inputEditText.clearFocus()
    }

    /**
     * [OnFocusChangeListener] implements
     */
    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        onFocusChange.forEach { it.onFocusChange(this, hasFocus) }
        transitionOnFocus(hasFocus)
        updateViewOnFocus(hasFocus)
    }

    private fun updateViewOnFocus(hasFocus: Boolean) {
        when {
            hasFocus -> {
                inputEditText.backgroundTint(ContextCompat.getColor(context, R.color.colorInputFocused))
            }
            !hasFocus && text.isNullOrEmpty() -> {
                inputEditText.backgroundTint(ContextCompat.getColor(context, R.color.colorInputUnfocused))
            }
            !hasFocus && !text.isNullOrEmpty() -> {
                inputEditText.backgroundTint(ContextCompat.getColor(context, R.color.colorInputUnfocused))
            }
        }
    }

    private fun transitionOnFocus(hasFocus: Boolean) {
        when {
            hasFocus -> {
                inputViewLayout.safeTransitionTo(R.id.focused)
            }
            !hasFocus && text.isNullOrEmpty() -> {
                inputViewLayout.safeTransitionTo(R.id.unfocused)
            }
            !hasFocus && !text.isNullOrEmpty() -> {
                inputViewLayout.safeTransitionTo(R.id.focused)
            }
        }
    }

    /**
     * [SimpleTextWatcher] implements
     */
    override fun afterTextChanged(s: Editable?) {
        when {
            isSilent -> {
                return
            }
            hasError -> {
                error = null
                inputEditText.backgroundTintRes(R.color.colorAccent)
            }
        }
    }

    var isSilent: Boolean = false

    var hint: String?
        get() = inputTextViewHint.text?.toString()
        set(value) {
            inputTextViewHint.text = value

        }

    var text: String?
        get() = inputEditText.text?.toString()
        set(value) {
            isSilent = true
            inputEditText.setText(value)
            error = null
            onFocusChange(null, isFocused)
            isSilent = false
        }

    var error: String?
        get() = inputTextViewError.text?.toString()
        set(value) {
            inputTextViewError.text = value
            if (error != null) {
                inputEditText.backgroundTintRes(R.color.colorInputError)
            }
        }

    @ColorInt
    var textColorHint: Int = 0

    private val onFocusChange = mutableListOf<OnFocusChangeListener>()

    @DrawableRes
    var src: Int = 0
        set(value) {
            val isGone = value <= 0
            inputImageViewIcon.isGone(isGone)
            inputImageViewIcon.setImageResource(value)
        }

    val isTextEmpty: Boolean get() = text.isNullOrEmpty()

    val hasError: Boolean get() = !error.isNullOrEmpty()

    fun addActionNextListener(block: (String?) -> Unit) {
        inputEditText.addActionNextListener(block)
    }

    fun disableFocus() {
        inputEditText.apply {
            isFocusable = false
            isCursorVisible = false
        }
    }

    fun enableFocus() {
        inputEditText.apply {
            isFocusable = true
            isCursorVisible = true
        }
    }


    fun clear() {
        inputEditText.text = null
        error = null
    }

    fun addDateWatcher() {
        inputEditText.addDateWatcher()
    }

    var selectable: Selectable? = null

    fun <T : Selectable> buildSelectable(
            mainVM: MainVM,
            data: List<T>?,
            adaptive: SelectableAdapter<T>.() -> Unit = {}
    ) {

        data ?: return

        if (!data.isNullOrEmpty()){
            inputImageViewDropdown.setImageResource(R.drawable.drw_text_input_dropdown)
        }
        val showDialog = {
            (context as? Activity)?.hideKeyboard()
            val adapter = SelectableAdapter<T>().also {
                it.set(data)
                @Suppress("UNCHECKED_CAST")
                it.selectedItem = selectable as? T
                it.onDismiss = {
                    when (this) {
                        is TextInputView -> {
                            text = selectable?.text

                        }
                    }
                }
                it.addOnItemClick { model, _ ->
                    selectable = model
                }
            }
            adapter.adaptive()
            mainVM.selectableLiveData.value = adapter
            mainVM.dialogLiveData.value = Main.selectable
        }
        addViewClickListener {
            when (this) {
                is TextInputView -> onFocusChange(null, true)
            }
            showDialog()
        }
    }

}