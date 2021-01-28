package wee.digital.sample.ui.fragment.dialog.date

import wee.digital.library.extension.dateFormat
import wee.digital.library.extension.maxDate50
import wee.digital.sample.R
import wee.digital.sample.shared.Configs
import java.text.SimpleDateFormat
import java.util.*

class DateArg(
        val key: String = Configs.DEFAULT_ARG_KEY,
        var title: Int = R.string.app_name,
        var format: SimpleDateFormat = Configs.DEFAULT_DATE_FMT,
        var selectedDate: String? = "" /*nowFormat(format)*/,
        var minDate: String = "1/1/1940",
        var maxDate: String = maxDate50(),
        var onDateSelected: (String) -> Unit
) {

    var minYear: Int = 0
    var minMonth: Int = 0
    var minDay: Int = 0
    var maxYear: Int = 0
    var maxMonth: Int = 0
    var maxDay: Int = 0
    lateinit var years: MutableList<Int>
    lateinit var minMonths: MutableList<Int>
    lateinit var months: MutableList<Int>
    lateinit var maxMonths: MutableList<Int>
    lateinit var minDays: MutableList<Int>
    lateinit var maxDays: MutableList<Int>

    var selectedYear: Int = 0
    var selectedMonth: Int = 0
    var selectedDay: Int = 0

    fun initProperties() {
        Calendar.getInstance().also {
            it.time = minDate.dateFormat(format)
            minYear = it.year
            minMonth = it.month
            minDay = it.day
            minMonths = dateValue(IntRange(minMonth, 12))
            minDays = dateValue(IntRange(minDay, it.maxDayOfMonth))
        }
        Calendar.getInstance().also {
            it.time = maxDate.dateFormat(format)
            maxYear = it.year
            maxMonth = it.month
            maxDay = it.day
            maxMonths = dateValue(IntRange(1, maxMonth))
            maxDays = dateValue(IntRange(1, maxDay))
        }
        Calendar.getInstance().also {
            it.time = selectedDate?.dateFormat(format) ?: maxDate.dateFormat(format)
            selectedYear = it.year
            selectedMonth = it.month
            selectedDay = it.day
        }
        years = dateValue(IntRange(minYear, maxYear))
        months = dateValue(IntRange(1, 12))
    }

    private val Calendar.year: Int get() = this.get(Calendar.YEAR)

    private val Calendar.month: Int get() = this.get(Calendar.MONTH) + 1

    private val Calendar.day: Int get() = this.get(Calendar.DAY_OF_MONTH)

    private val Calendar.maxDayOfMonth: Int get() = this.getActualMaximum(Calendar.DAY_OF_MONTH)

    companion object {
        fun dateValue(range: IntRange): MutableList<Int> {
            return mutableListOf<Int>().also {
                it.add(0)
                it.addAll(range)
                it.add(0)
            }
        }
    }
}