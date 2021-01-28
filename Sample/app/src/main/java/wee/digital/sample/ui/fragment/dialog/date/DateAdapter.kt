package wee.digital.sample.ui.fragment.dialog.date

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.date_item.view.*
import wee.digital.library.adapter.BaseRecyclerAdapter
import wee.digital.library.extension.color
import wee.digital.sample.R
import java.lang.ref.WeakReference

class DateAdapter : BaseRecyclerAdapter<Int>() {

    override fun layoutResource(model: Int, position: Int): Int {
        return R.layout.date_item
    }

    override fun View.onBindModel(model: Int, position: Int, layout: Int) {
        if (model != currentValue) {
            dateTextView.color(R.color.colorBlack)
        } else {
            dateTextView.color(R.color.colorPrimary)
        }
        dateTextView.text = if (model != 0) model.toString() else "-"
    }

    /**
     * [DateAdapter] properties
     */
    var currentValue: Int = -1

    private val snapHelper = LinearSnapHelper()

    private var weakRecyclerView: WeakReference<RecyclerView?> = WeakReference(null)

    val snapPosition: Int
        get() {
            val recyclerView = weakRecyclerView.get() ?: return 0
            val view = snapHelper.findSnapView(recyclerView.layoutManager) ?: return 0
            return recyclerView.getChildAdapterPosition(view)
        }

    val snapValue: Int
        get() = get(snapPosition) ?: 0

    fun snap(view: RecyclerView, onSnap: (Int) -> Unit) {
        weakRecyclerView = WeakReference(view)
        this.bind(view)
        snapHelper.attachToRecyclerView(view)
        view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        val fSnapPosition = snapPosition
                        if (currentValue != fSnapPosition) {
                            currentValue = get(fSnapPosition) ?: 0
                            notifyItemRangeChanged(fSnapPosition - 1, 3)
                            onSnap(currentValue)
                        }
                    }
                }
            }
        })
    }

    fun scrollToValue(value: Int, diff: Int = -1) {
        currentValue = value
        val position = centerIndexOf(value)
        weakRecyclerView.get()?.also {
            (it.layoutManager as? LinearLayoutManager)
                    ?.scrollToPositionWithOffset(position + diff, 0)
            it.post {
                notifyItemRangeChanged(position - 1, 3)
            }
        }
    }

    private fun centerIndexOf(element: Int): Int {
        val centerPosition = itemCount / 2
        var i = 0
        if (centerPosition == element) return element
        while (true) {
            val left = centerPosition - i
            if (left >= 0) {
                if (get(left) == element) return left
            }
            val right = centerPosition + i
            if (right < itemCount) {
                if (get(right) == element) return right
            }
            if (left < 0 && right > lastPosition) return -1
            i++
        }
    }

}