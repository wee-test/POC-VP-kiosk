package wee.digital.library.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import wee.digital.library.extension.addViewClickListener

abstract class BaseRecyclerAdapter<T> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v)

    /**
     * [BaseRecyclerAdapter] abstract function for initialize recycler view type.
     */
    @LayoutRes
    protected abstract fun layoutResource(model: T, position: Int): Int

    protected abstract fun View.onBindModel(model: T, position: Int, @LayoutRes layout: Int)


    /**
     * [RecyclerView.Adapter] override.
     */
    override fun getItemCount(): Int {
        return if (blankLayoutResource != 0 || footerLayoutResource != 0)
            size + 1
        else size
    }

    override fun getItemViewType(position: Int): Int {

        if (dataIsEmpty && blankLayoutResource != 0) return blankLayoutResource

        if (dataNotEmpty && footerLayoutResource != 0 && position == size) return footerLayoutResource

        val model = get(position) ?: return 0

        return layoutResource(model, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = if (viewType != 0) {
            LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        } else {
            goneView(parent.context)
        }
        return ViewHolder(view)
    }


    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

        val type = getItemViewType(position)

        if (type == 0) return

        if (type == blankLayoutResource) {
            viewHolder.itemView.onBindBlank(type)
            return
        }

        if (type == footerLayoutResource) {
            if (footerIndexed == position) return
            footerIndexed = position
            viewHolder.itemView.onBindFooter(type, position)
            return
        }

        val model = get(position) ?: return

        viewHolder.itemView.onBindModel(model, position, type)

        viewHolder.itemView.addViewClickListener {
            onItemClick(model, position)
        }

        viewHolder.itemView.setOnLongClickListener {
            onItemLongClick(model, position)
            true
        }
        if (position < lastBindIndex) {
            lastBindIndex = position
        }
    }

    private fun goneView(context: Context): View {
        val view = View(context)
        view.visibility = View.GONE
        return view
    }

    var blankLayoutResource = blankLayoutResource()

    @LayoutRes
    open fun blankLayoutResource(): Int {
        return 0
    }

    open fun View.onBindBlank(layout: Int) {
    }

    @Volatile
    private var footerIndexed: Int = -1

    var footerLayoutResource = footerLayoutResource()

    @LayoutRes
    open fun footerLayoutResource(): Int {
        return 0
    }

    open fun showFooter(@LayoutRes res: Int) {
        footerLayoutResource = res
        notifyItemChanged(size)
    }

    open fun hideFooter() {
        footerLayoutResource = 0
        notifyItemChanged(size)
    }

    private var footerVisible: (Int) -> Unit = { }

    open fun View.onBindFooter(layout: Int, position: Int) {
        footerVisible(position)
    }

    open var onItemClick: (T, Int) -> Unit = { model, position ->
        itemClickList.forEach {
            it(model, position)
        }
    }

    private val itemClickList = mutableListOf<(T, Int) -> Unit>()

    open fun addOnItemClick(block: (T, Int) -> Unit) {
        itemClickList.add(block)
    }

    var onItemLongClick: (T, Int) -> Unit = { _, _ -> }

    /**
     * Data
     */
    open var listItem: MutableList<T> = mutableListOf()

    open val size: Int get() = listItem.size

    open var lastBindIndex: Int = -1

    open val dataIsEmpty: Boolean get() = listItem.isEmpty()

    open val dataNotEmpty: Boolean get() = listItem.isNotEmpty()

    open val lastIndex: Int get() = listItem.lastIndex

    open fun indexInBound(position: Int): Boolean {
        return position > -1 && position < size
    }

    open fun get(position: Int): T? {
        if (indexInBound(position)) return listItem[position]
        return null
    }

    open fun set(collection: Collection<T>?) {
        listItem = collection?.toMutableList() ?: mutableListOf()
        notifyDataSetChanged()
    }

    open fun set(mutableList: MutableList<T>?) {
        listItem = mutableList ?: mutableListOf()
        notifyDataSetChanged()
    }

    open fun set(vararg model: T) {
        listItem = model.toMutableList()
        notifyDataSetChanged()
    }

    open fun setElseEmpty(collection: Collection<T>?) {
        if (collection.isNullOrEmpty()) return
        listItem = collection.toMutableList()
        notifyDataSetChanged()
    }

    open fun setElseEmpty(mutableList: MutableList<T>?) {
        if (mutableList.isNullOrEmpty()) return
        listItem = mutableList
        notifyDataSetChanged()
    }

    open fun setElseEmpty(array: Array<T>?) {
        if (null == array || array.isEmpty()) return
        listItem = array.toMutableList()
        notifyDataSetChanged()
    }

    open fun setElseEmpty(model: T?) {
        model ?: return
        listItem = mutableListOf(model)
        notifyDataSetChanged()
    }

    open fun add(collection: Collection<T>?) {
        if (collection.isNullOrEmpty()) return
        listItem.addAll(collection)
        notifyDataSetChanged()
    }

    open fun add(array: Array<T>?) {
        if (null == array || array.isEmpty()) return
        listItem.addAll(array)
        notifyDataSetChanged()
    }

    open fun add(model: T?) {
        model ?: return
        listItem.add(model)
        notifyDataSetChanged()
    }

    open fun addFirst(model: T?) {
        model ?: return
        listItem.add(0, model)
        notifyDataSetChanged()
    }

    open fun edit(index: Int, model: T?) {
        model ?: return
        if (indexInBound(index)) {
            listItem[index] = model
            notifyItemChanged(index)
        }
    }

    open fun remove(index: Int) {
        listItem.removeAt(index)
        notifyItemRemoved(index)
    }

    open fun remove(model: T?) {
        model ?: return
        val index = listItem.indexOf(model)
        if (indexInBound(index)) {
            listItem.remove(model)
            notifyItemRemoved(index)
        }
    }

    open fun clear() {
        listItem.clear()
        notifyDataSetChanged()
    }

    open fun unBind() {
        listItem = mutableListOf()
        notifyDataSetChanged()
    }

    open fun notifyRangeChanged() {
        val s = size
        notifyItemRangeChanged(s, size + 1)
    }

    open fun bind(recyclerView: RecyclerView, block: (LinearLayoutManager.() -> Unit) = { }) {
        val layoutManager = LinearLayoutManager(recyclerView.context)
        layoutManager.block()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = this
    }

    open fun bindHorizontal(recyclerView: RecyclerView, block: (LinearLayoutManager.() -> Unit) = { }) {
        val layoutManager = LinearLayoutManager(recyclerView.context, LinearLayoutManager.HORIZONTAL, false)
        layoutManager.block()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = this
    }

    open fun bind(
            recyclerView: RecyclerView,
            spanCount: Int = 1,
            block: (GridLayoutManager.() -> Unit) = {}
    ) {

        val layoutManager = GridLayoutManager(recyclerView.context, spanCount)
        layoutManager.spanSizeLookup =
                object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (dataIsEmpty || position == size) layoutManager.spanCount
                        else 1
                    }
                }
        layoutManager.block()
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = this
    }


}