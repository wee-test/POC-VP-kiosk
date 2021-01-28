package wee.digital.sample.ui.fragment.dialog.date

import android.view.View
import kotlinx.android.synthetic.main.date.*
import wee.digital.library.extension.maxDayOfMonth
import wee.digital.library.extension.timeFormat
import wee.digital.sample.R
import wee.digital.sample.shared.Configs
import wee.digital.sample.ui.base.setNavResult
import wee.digital.sample.ui.main.MainDialog
import java.util.*

class DateFragment : MainDialog() {

    private val yearAdapter = DateAdapter()

    private val monthAdapter = DateAdapter()

    private val dayAdapter = DateAdapter()

    private val currentDate: String
        get() {
            val cal = Calendar.getInstance()
            cal.set(yearAdapter.snapValue, monthAdapter.snapValue - 1, dayAdapter.snapValue)
            return cal.timeFormat(mainVM.dateLiveData.value?.format ?: Configs.DEFAULT_DATE_FMT)
        }

    /**
     * [BaseDialog] override
     */
    override fun layoutResource(): Int {
        return R.layout.date
    }

    override fun onViewCreated() {
        addClickListener(headerViewIcon, dateActionConfirm)
    }

    override fun onLiveDataObserve() {
        mainVM.dateLiveData.observe {
            it ?: return@observe
            mainVM.dateLiveData.removeObservers(viewLifecycleOwner)
            it.initProperties()
            onDateListInit(it)
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            headerViewIcon -> {
                dismissAllowingStateLoss()
            }
            dateActionConfirm -> {
                mainVM.dateLiveData.value?.onDateSelected?.also {
                    it(currentDate)
                }
                setNavResult(mainVM.dateLiveData.value?.key, currentDate)
                dismissAllowingStateLoss()
            }
        }
    }

    /**
     * [DateFragment] properties
     */
    private fun onDateListInit(arg: DateArg) {
        yearAdapter.snap(dateRecyclerViewYear) {
            onYearChanged(arg, it)
        }
        monthAdapter.snap(dateRecyclerViewMonth) {
            onMonthChanged(arg, it)
        }
        dayAdapter.snap(dateRecyclerViewDay) {
            arg.selectedDay = it
        }
        yearAdapter.listItem = arg.years
        yearAdapter.scrollToValue(arg.selectedYear)
        onYearChanged(arg, arg.selectedYear)
    }

    private fun onYearChanged(arg: DateArg, year: Int) {
        arg.selectedYear = year
        when (year) {
            arg.minYear -> {
                if (arg.selectedMonth < arg.minMonth) {
                    arg.selectedMonth = arg.minMonth
                }
                monthAdapter.set(arg.minMonths)
            }
            arg.maxYear -> {
                if (arg.selectedMonth > arg.maxMonth) {
                    arg.selectedMonth = arg.maxMonth
                }
                monthAdapter.set(arg.maxMonths)
            }
            else -> {
                monthAdapter.set(arg.months)
            }
        }
        monthAdapter.scrollToValue(arg.selectedMonth)
        onMonthChanged(arg, arg.selectedMonth)
    }

    private fun onMonthChanged(arg: DateArg, month: Int) {
        arg.selectedMonth = month
        val cal = Calendar.getInstance().also {
            it.set(arg.selectedYear, month - 1, 1)
        }
        when {
            arg.selectedYear == arg.minYear && month == arg.minMonth -> {
                if (arg.selectedDay < arg.minDay) {
                    arg.selectedDay = arg.minDay
                }
                dayAdapter.set(arg.minDays)
            }
            arg.selectedYear == arg.maxYear && month == arg.maxMonth -> {
                if (arg.selectedDay > arg.maxDay) {
                    arg.selectedDay = arg.maxDay
                }
                dayAdapter.set(arg.maxDays)
            }
            arg.selectedDay > cal.maxDayOfMonth -> {
                arg.selectedDay = cal.maxDayOfMonth
                dayAdapter.set(DateArg.dateValue(IntRange(1, cal.maxDayOfMonth)))
            }
            else -> {
                dayAdapter.set(DateArg.dateValue(IntRange(1, cal.maxDayOfMonth)))
            }
        }
        dayAdapter.scrollToValue(arg.selectedDay)
        log.d("%s - %s - %s".format(arg.selectedDay, arg.selectedMonth, arg.selectedYear))
    }

}