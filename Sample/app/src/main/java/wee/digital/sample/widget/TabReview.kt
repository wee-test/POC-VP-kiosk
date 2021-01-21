package wee.digital.sample.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.widget_tab_bar_item.view.*
import wee.digital.library.adapter.ViewPager2Adapter
import wee.digital.sample.R
import wee.digital.sample.ui.fragment.review.ReviewCardFragment
import wee.digital.sample.ui.fragment.review.ReviewFormFragment
import wee.digital.sample.ui.fragment.review.ReviewInfoFragment

class TabReview : TabBarView {

    override var itemLayoutRes: Int = R.layout.widget_tab_bar_item

    override var onTabCallback: (View, Int) -> Unit = { view, index ->
        when (index) {
            0 -> {
                view.tabItemTitle.text = "Thông tin cá nhân"
                view.tabItemTitle.setTextColor(context.getColor(R.color.colorWhite))
            }
            1 -> {
                view.tabItemTitle.text = "Thông tin thẻ"
            }
            2 -> {
                view.tabItemTitle.text = "Hình thức nhận thẻ"
            }
        }
    }

    /**
     * [TabBarView] override
     */
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)

    override fun onTabSelected(view: View, position: Int) {
        view.tabItemTitle.setTextColor(context.getColor(R.color.colorWhite))
        val typeface = ResourcesCompat.getFont(context, R.font.svn_gilroy_bold)
        view.tabItemTitle.typeface = typeface
    }

    override fun onTabUnselected(view: View, position: Int) {
        view.tabItemTitle.setTextColor(context.getColor(R.color.black60))
        val typeface = ResourcesCompat.getFont(context, R.font.svn_gilroy_regular)
        view.tabItemTitle.typeface = typeface
    }

    fun setUpViewPager(fragment: Fragment, frg1: ReviewInfoFragment, frg2 : ReviewCardFragment, frg3 : ReviewFormFragment, viewPager: ViewPager2) {
        indicator.setBackgroundResource(R.drawable.bg_gradient_rounded)
        val adapter = ViewPager2Adapter(fragment.childFragmentManager, fragment.viewLifecycleOwner).apply {
            addFragments(frg1, frg2, frg3)
        }
        viewPager.adapter = adapter
        attachViewPager(viewPager)
    }

}