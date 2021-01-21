package wee.digital.sample.widget

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.widget_tab_bar.view.*
import kotlinx.android.synthetic.main.widget_tab_bar_item.view.*
import wee.digital.library.adapter.ViewPager2Adapter
import wee.digital.library.extension.VerticalSlide
import wee.digital.library.widget.AppCustomView
import wee.digital.sample.R

open class TabBarView : AppCustomView, TabLayout.OnTabSelectedListener {

    open var itemLayoutRes: Int = R.layout.widget_tab_bar_item

    open var onTabCallback: (View, Int) -> Unit = { view, index ->
        println("")
    }

    val indicator: View get() = tabViewIndicator

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)

    override fun layoutResource(): Int {
        return R.layout.widget_tab_bar
    }

    override fun onInitialize(context: Context, types: TypedArray) {
        tabTabLayout.apply {
            setPadding(paddingLeft, paddingLeft, paddingLeft, paddingLeft)
            tabGravity = TabLayout.GRAVITY_FILL
            tabMode = TabLayout.MODE_FIXED
            setSelectedTabIndicator(0)
        }
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        tabTabLayout.addOnTabSelectedListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        tabTabLayout.removeOnTabSelectedListener(this)
    }

    /**
     * [TabLayout.OnTabSelectedListener] implements
     */
    final override fun onTabReselected(tab: TabLayout.Tab?) {
        tab?.customView?.also {
            onTabReselected(it, tab.position)
        }
    }

    final override fun onTabUnselected(tab: TabLayout.Tab?) {
        tab?.customView?.also {
            onTabUnselected(it, tab.position)
        }
    }

    final override fun onTabSelected(tab: TabLayout.Tab?) {
        tab?.customView?.also {
            onTabSelected(it, tab.position)
        }
    }

    open fun onTabReselected(view: View, position: Int) {
    }

    open fun onTabUnselected(view: View, position: Int) {
    }

    open fun onTabSelected(view: View, position: Int) {
    }

    /**
     * [TabBarView] properties
     */
    fun attachViewPager(viewPager: ViewPager2) {
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                val params = tabViewIndicator.layoutParams as FrameLayout.LayoutParams
                val indicatorWidth = tabTabLayout.width / tabTabLayout.tabCount
                val translationOffset = (positionOffset + position) * indicatorWidth
                params.marginStart = translationOffset.toInt()
                indicator.layoutParams = params
            }
        })
        TabLayoutMediator(tabTabLayout, viewPager) { tab, position ->
            try {
                val view = LayoutInflater.from(context).inflate(itemLayoutRes, tabTabLayout, false)
                onTabCallback(view, position)
                tab.customView = view
            } catch (e: Resources.NotFoundException) {
            }
            if (tabViewIndicator.layoutParams.width == 0) tabTabLayout.post {
                val indicatorParams = tabViewIndicator.layoutParams
                indicatorParams.width = tabTabLayout.width / tabTabLayout.tabCount
                tabViewIndicator.layoutParams = indicatorParams
            }
        }.attach()
    }


    /**
     * Example
     */
    private abstract class Example : Fragment() {

        private lateinit var viewPager: ViewPager2

        private lateinit var tabBarView: TabBarView


        // ...
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            // ... findViewById or ktx synthetic with viewPager, tabLayout
            setUpWithViewPager()
        }

        private fun setUpWithViewPager() {
            viewPager.setPageTransformer(VerticalSlide())
            viewPager.adapter = ViewPager2Adapter(childFragmentManager, viewLifecycleOwner).apply {
                addFragments(
                        Fragment(),
                        Fragment(),
                        Fragment(),
                )
            }
            tabBarView.apply {
                //indicator?.setBackgroundResource(R.drawable.bg_tab_indicator)
                itemLayoutRes = R.layout.widget_tab_bar_item
                onTabCallback = { view, index ->
                    view.tabItemTitle.text = "Item $index"
                    if (index != 0) {
                        //disable if..
                        //view.isEnabled = false
                    }
                }
            }
            tabBarView.attachViewPager(viewPager)
        }

    }
}