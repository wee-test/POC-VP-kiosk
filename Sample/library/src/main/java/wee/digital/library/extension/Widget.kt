package wee.digital.library.extension

import android.R
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.text.InputFilter
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.NestedScrollView
import wee.digital.library.Library
import java.text.Normalizer


/**
 * @param actionId: see [android.view.inputmethod.EditorInfo]
 */
fun EditText.addEditorActionListener(actionId: Int, block: (String?) -> Unit) {
    imeOptions = actionId
    setImeActionLabel(null, actionId)
    setOnEditorActionListener(object : TextView.OnEditorActionListener {
        override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
            if (imeOptions == actionId) {
                isSelected = false
                block(text.toString())
                (context as? Activity)?.hideKeyboard()
                clearFocus()
                return true
            }
            return false
        }
    })
}

fun EditText.addActionNextListener(block: (String?) -> Unit) {
    imeOptions = EditorInfo.IME_ACTION_NEXT
    isSingleLine = true
    setImeActionLabel("Next", EditorInfo.IME_ACTION_NEXT)
    setOnEditorActionListener(object : TextView.OnEditorActionListener {
        override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
            if (imeOptions == actionId) {
                isSelected = false
                block(text.toString())
                (context as? Activity)?.hideKeyboard()
                clearFocus()
                return true
            }
            return false
        }
    })
}

/**
 * Ex: "Kotlin   Language   Extension"
 * @return: "KotlinLanguageExtension"
 */
val TextView?.trimIndentText: String
    get() {
        this ?: return ""
        var s = text?.toString()
        if (s.isNullOrEmpty()) return ""
        s = s.replace("\\s+", " ").trim()
        text = s
        if (this is EditText) {
            //setSelection(s.length)
        }
        return s
    }


fun TextView.filterChar(value: CharArray) {
    val arrayList = arrayListOf<InputFilter>()
    this.filters?.apply { arrayList.addAll(this) }
    arrayList.add(InputFilter { source, start, end, _, _, _ ->
        when {
            end > start -> for (index in start until end) {
                if (!String(value).contains(source[index].toString())) {
                    return@InputFilter ""
                }
            }
        }
        return@InputFilter null
    })
    this.filters = arrayList.toArray(arrayOfNulls<InputFilter>(arrayList.size))
    this.inputType = EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
}

fun TextView.filterChar() {
    val arrayList = arrayListOf<InputFilter>()
    var string: String
    val regex = Regex("[^\\p{ASCII}]")
    this.filters?.apply { arrayList.addAll(this) }
    arrayList.add(InputFilter { source, start, end, _, _, _ ->
        string = ""
        when {
            end > start -> for (index in start until end) {
                val decomposed: String = Normalizer.normalize(source[index].toString(), Normalizer.Form.NFD)
                val removed: String = decomposed.replace(regex, "")
                string += removed
            }
        }
        return@InputFilter string
    })
    this.filters = arrayList.toArray(arrayOfNulls<InputFilter>(arrayList.size))
    this.inputType = EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
}

/**
 * Ex: "Kotlin   Language   Extension"
 * @return: "Kotlin Language Extension"
 */
val TextView?.trimText: String
    get() {
        this ?: return ""
        var s = text?.toString()
        if (s.isNullOrEmpty()) return ""
        s = s.replace("\\s+", " ").trimIndent()
        text = s
        if (this is EditText) {
            setSelection(s.length)
        }
        return s
    }


fun NestedScrollView.scrollToCenter(view: View) {
    post {
        val top = view.top
        val bot = view.bottom
        val height = this.height
        this.scrollTo(0, (top + bot - height) / 2)
    }
}

fun TextView.textColor(@ColorRes colorRes: Int) {
    this.post {
        setTextColor(ContextCompat.getColor(Library.app, colorRes))
    }
}

fun TextView.fontFamily(int: Int) {
    this.typeface = ResourcesCompat.getFont(Library.app, int)
}

fun TextView.textColor(colorStr: String) {
    this.post {
        val s = if (colorStr.firstOrNull() != '#') "#$colorStr" else colorStr
        setTextColor(Color.parseColor(s))
    }
}

fun TextView.bold() {
    setTypeface(this.typeface, Typeface.BOLD)
}

fun TextView.regular() {
    setTypeface(this.typeface, Typeface.NORMAL)
}

fun TextView.drawableStart(drawable: Drawable?) {
    this.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
}

fun TextView.drawableEnd(drawable: Drawable?) {
    this.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
}

fun TextView.setHyperText(@StringRes res: Int, vararg args: Any?) {
    setHyperText(string(res), * args)
}

fun TextView.setHyperText(s: String?, vararg args: Any?) {
    post {
        text = when {
            s.isNullOrEmpty() -> null
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> Html.fromHtml(s.format(*args), 1)
            else -> @Suppress("DEPRECATION")
            Html.fromHtml(s.format(*args))
        }
    }
}

fun TextView.gradientHorizontal(@ColorRes color1: Int, @ColorRes color2: Int = color1) {
    paint.shader = LinearGradient(0f, 0f, this.width.toFloat(), 0f,
            ContextCompat.getColor(context, color1),
            ContextCompat.getColor(context, color2),
            Shader.TileMode.CLAMP)
}

fun TextView.gradientVertical(@ColorRes color1: Int, @ColorRes color2: Int = color1) {
    paint.shader = LinearGradient(0f, 0f, 0f, this.height.toFloat(),
            ContextCompat.getColor(context, color1),
            ContextCompat.getColor(context, color2),
            Shader.TileMode.CLAMP)
}


fun ViewGroup.inflate(@LayoutRes layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun RadioGroup.addOnCheckedChangeListener(block: (RadioButton) -> Unit) {
    setOnCheckedChangeListener { _, checkedId ->
        val button = (context as Activity).findViewById<RadioButton>(checkedId)
        block(button)
    }
}

fun TextSwitcher.setCrossFadeText(context: Context) {
    val `in`: Animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
    val out: Animation = AnimationUtils.loadAnimation(context, R.anim.fade_out)


    this.setFactory {
        val t = TextView(context)
        t.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        t
    }

    this.inAnimation = `in`
    this.outAnimation = out
}

fun toast(message: String?) {
    message ?: return
    if (Looper.myLooper() == Looper.getMainLooper()) {
        Toast.makeText(Library.app, message.toString(), Toast.LENGTH_SHORT).show()
    } else Handler(Looper.getMainLooper()).post {
        Toast.makeText(Library.app, message.toString(), Toast.LENGTH_SHORT).show()
    }
}

