package wee.digital.sample.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import wee.digital.sample.BuildConfig
import wee.digital.sample.app.app
import wee.digital.library.extension.parse

class SharedHelper {

    private val convertFactory = Gson()

    private val sharePref: SharedPreferences =
            app.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)

    private val editor: SharedPreferences.Editor
        get() = sharePref.edit()

    companion object {

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            SharedHelper()
        }
    }

    fun edit(block: SharedPreferences.Editor.() -> Unit) {
        editor.also {
            it.block()
            it.apply()
        }
    }

    fun put(key: String, value: String?) {
        edit { putString(key, value) }
    }

    fun put(key: String, value: Int) {
        edit { putInt(key, value) }
    }

    fun put(key: String, value: Long) {
        edit { putLong(key, value) }
    }

    fun put(key: String, value: Boolean) {
        edit { putBoolean(key, value) }
    }

    fun <T> put(key: String, value: T) {
        val json = convertFactory.toJson(value)
        edit {
            putString(key, json)
        }
    }

    fun <T> put(key: String, list: Collection<T>) {
        val json = convertFactory.toJson(list)
        edit {
            putString(key, json)
        }
    }

    fun <T> obj(key: String, cls: Class<T>): T? {
        return str(key).parse(cls) ?: return null
    }

    fun <T> array(key: String, cls: Class<Array<T>>): List<T>? {
        return str(key).parse(cls) ?: return null
    }

    fun bool(key: String, default: Boolean = false): Boolean {
        return sharePref.getBoolean(key, default)
    }

    fun int(key: String, default: Int = -1): Int {
        return sharePref.getInt(key, default)
    }

    fun long(key: String, default: Long = -1): Long {
        return sharePref.getLong(key, default)
    }

    fun str(key: String, default: String? = null): String? {
        return sharePref.getString(key, default)
    }

}