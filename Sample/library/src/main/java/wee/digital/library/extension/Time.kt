package wee.digital.library.extension

import java.lang.reflect.InvocationTargetException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

val SECOND: Long get() = 1000

val MIN: Long get() = 60 * SECOND

val HOUR: Long get() = 60 * MIN

val DAY: Long get() = 24 * HOUR

val MONTH: Long get() = 31 * DAY

val YEAR: Long get() = 365 * DAY

val nowInMillis: Long get() = System.currentTimeMillis()

val calendar: Calendar get() = Calendar.getInstance()

val nowInSecond: Long get() = System.currentTimeMillis() / SECOND

fun nowFormat(format: String): String {
    return nowInMillis.timeFormat(format)
}

fun nowFormat(sdf: SimpleDateFormat): String {
    return nowInMillis.timeFormat(sdf)
}

// if give up time in second convert to time in millis
fun Long.correctTime(): Long {
    return if (this < 1000000000000L) this * 1000 else this
}

fun Long.timeFormat(formatter: SimpleDateFormat): String {
    return try {
        formatter.format(Date(this.correctTime()))
    } catch (e: ParseException) {
        ""
    } catch (e: InvocationTargetException) {
        ""
    }
}

fun Long.timeFormat(formatter: String): String {
    return timeFormat(SimpleDateFormat(formatter))
}

/**
 * [String] time convert
 */
fun String?.timeFormat(formatter: String): Long? {
    return timeFormat(SimpleDateFormat(formatter))
}

fun String?.timeFormat(formatter1: SimpleDateFormat, formatter2: SimpleDateFormat): String? {
    this ?: return null
    return try {
        val date = this.dateFormat(formatter1)
        formatter2.format(date)
    } catch (e: ParseException) {
        null
    } catch (e: InvocationTargetException) {
        null
    }
}

fun String?.timeFormat(formatter: SimpleDateFormat): Long? {
    this ?: return null
    return try {
        formatter.parse(this)?.time ?: 0
    } catch (e: ParseException) {
        null
    } catch (e: InvocationTargetException) {
        null
    }
}

fun String?.dateFormat(formatter: SimpleDateFormat): Date {
    this ?: return Date()
    return try {
        formatter.parse(this) ?: Date()
    } catch (e: ParseException) {
        return Date()
    } catch (e: InvocationTargetException) {
        return Date()
    }
}

fun Date?.dateFormat(formatter: SimpleDateFormat): String? {
    this ?: return null
    return try {
        formatter.format(this)
    } catch (e: ParseException) {
        return null
    } catch (e: InvocationTargetException) {
        return null
    }
}

val Long.secondText: String
    get() {
        val seconds = this / 1000
        return "%02d:%02d".format(seconds / 60, seconds % 60)
    }

/**
 * [Calendar] time convert
 */
fun Calendar.timeFormat(formatter: SimpleDateFormat): String {
    return try {
        formatter.format(this.time)
    } catch (e: ParseException) {
        ""
    } catch (e: InvocationTargetException) {
        ""
    }
}

fun Calendar.timeFormat(formatter: String): String {
    return timeFormat(SimpleDateFormat(formatter))
}

fun Calendar.isCurrentDay(momentCal: Calendar = calendar): Boolean {
    if (this.get(Calendar.YEAR) != momentCal.get(Calendar.YEAR)) return false
    if (this.get(Calendar.MONTH) + 1 != momentCal.get(Calendar.MONTH) + 1) return false
    return this.get(Calendar.DAY_OF_MONTH) == momentCal.get(Calendar.DAY_OF_MONTH)
}

fun Calendar.isYesterday(momentCal: Calendar = calendar): Boolean {
    if (this.get(Calendar.YEAR) != momentCal.get(Calendar.YEAR)) return false
    if (this.get(Calendar.MONTH) + 1 != momentCal.get(Calendar.MONTH) + 1) return false
    return this.get(Calendar.DAY_OF_MONTH) - momentCal.get(Calendar.DAY_OF_MONTH) == -1
}

fun Calendar.isTomorrow(momentCal: Calendar = calendar): Boolean {
    if (this.get(Calendar.YEAR) != momentCal.get(Calendar.YEAR)) return false
    if (this.get(Calendar.MONTH) != momentCal.get(Calendar.MONTH)) return false
    return this.get(Calendar.DAY_OF_MONTH) - momentCal.get(Calendar.DAY_OF_MONTH) == 1
}

val Calendar.year: Int get() = this.get(Calendar.YEAR)

val Calendar.month: Int get() = this.get(Calendar.MONTH) + 1

val Calendar.day: Int get() = this.get(Calendar.DAY_OF_MONTH)

val Calendar.maxDayOfMonth: Int get() = this.getActualMaximum(Calendar.DAY_OF_MONTH)




