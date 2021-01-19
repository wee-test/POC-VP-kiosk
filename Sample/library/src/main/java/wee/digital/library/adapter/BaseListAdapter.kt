package wee.digital.library.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.*
import wee.digital.library.extension.addViewClickListener

abstract class BaseListAdapter<T> : ListAdapter<T, RecyclerView.ViewHolder> {

    private val differ: AsyncListDiffer<T>

    constructor(itemCallback: DiffUtil.ItemCallback<T> = DiffItemCallback()) : super(itemCallback) {
        differ = asyncListDiffer(itemCallback)
    }

    /**
     * [RecyclerView.Adapter] override.
     */
    override fun getItemCount(): Int {
        if (blankLayoutResource != 0 || footerLayoutResource != 0) {
            return size + 1
        }
        return size
    }

    override fun getItemViewType(position: Int): Int {
        if (dataIsEmpty && blankLayoutResource != 0) {
            return blankLayoutResource
        }
        if (dataNotEmpty && footerLayoutResource != 0 && position == size) {
            return footerLayoutResource
        }
        val model = get(position) ?: return 0
        return layoutResource(model, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = if (viewType == 0) {
            View(parent.context).apply { visibility = View.GONE }
        } else {
            LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        }
        return ViewHolder(v)
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

        viewHolder.itemView.addViewClickListener(0) {
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

    override fun getCurrentList(): MutableList<T> {
        return differ.currentList
    }

    override fun submitList(list: MutableList<T>?) {
        differ.submitList(list)
    }

    override fun submitList(list: MutableList<T>?, commitCallback: Runnable?) {
        differ.submitList(list, commitCallback)
    }


    /**
     * [BaseRecyclerAdapter] abstractions
     */
    @LayoutRes
    protected abstract fun layoutResource(model: T, position: Int): Int

    protected abstract fun View.onBindModel(model: T, position: Int, @LayoutRes layout: Int)

    private var blankLayoutResource = blankLayoutResource()

    @LayoutRes
    open fun blankLayoutResource(): Int {
        return 0
    }

    open fun View.onBindBlank(layout: Int) {
    }

    @Volatile
    private var footerIndexed: Int = -1

    private var footerLayoutResource = footerLayoutResource()

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

    var durationDelay = 300L

    /**
     * Data list
     */
    val size: Int get() = currentList.size

    var lastBindIndex: Int = -1

    val lastIndex: Int get() = currentList.lastIndex

    val dataIsEmpty: Boolean get() = currentList.isEmpty()

    val dataNotEmpty: Boolean get() = currentList.isNotEmpty()

    /**
     * Data update
     */
    open fun submit() {
        set(currentList)
    }

    open fun get(position: Int): T? {
        if (position in 0..lastIndex) return currentList[position]
        return null
    }

    open fun set(collection: Collection<T>?) {
        lastBindIndex = -1
        submitList(if (collection != null) ArrayList(collection) else null)
    }

    open fun set(list: MutableList<T>?) {
        lastBindIndex = -1
        submitList(if (list != null) ArrayList(list) else null)
    }

    open fun set(array: Array<T>?) {
        lastBindIndex = -1
        submitList(array?.toMutableList())
    }

    open fun setElseEmpty(collection: Collection<T>?) {
        if (collection.isNullOrEmpty()) return
        set(ArrayList(collection))
    }

    open fun setElseEmpty(list: MutableList<T>?) {
        if (list.isNullOrEmpty()) return
        set(ArrayList(list))
    }

    open fun setElseEmpty(array: Array<T>?) {
        if (array.isNullOrEmpty()) return
        set(array.toMutableList())
    }

    open fun add(collection: Collection<T>?) {
        if (collection.isNullOrEmpty()) return
        currentList.addAll(collection)
        submit()
    }

    open fun add(array: Array<T>?) {
        if (array == null || array.isEmpty()) return
        currentList.addAll(array)
        submit()
    }

    open fun add(model: T?) {
        model ?: return
        currentList.add(model)
        submit()
    }

    open fun add(position: Int, model: T?) {
        model ?: return
        currentList.add(position, model)
        submit()
    }

    open fun edit(position: Int, model: T?) {
        model ?: return
        if (position in 0..lastIndex) {
            currentList[position] = model
            submit()
        }
    }

    open fun remove(index: Int) {
        currentList.removeAt(index)
        submit()
    }

    open fun remove(model: T?) {
        model ?: return
        val position = currentList.indexOf(model)
        remove(position)
    }

    /**
     * Utils
     */
    private fun asyncListDiffer(itemCallback: DiffUtil.ItemCallback<T>): AsyncListDiffer<T> {

        val adapterCallback = AdapterListUpdateCallback(this)
        val listCallback = object : ListUpdateCallback {
            override fun onChanged(position: Int, count: Int, payload: Any?) {
                adapterCallback.onChanged(position + 1, count, payload)
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                adapterCallback.onMoved(fromPosition + 1, toPosition + 1)
            }

            override fun onInserted(position: Int, count: Int) {
                adapterCallback.onInserted(position + 1, count + 1)
            }

            override fun onRemoved(position: Int, count: Int) {
                adapterCallback.onRemoved(position + 1, count)
            }
        }
        return AsyncListDiffer<T>(listCallback, AsyncDifferConfig.Builder<T>(itemCallback).build())
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v)

    open fun bind(recyclerView: RecyclerView, block: LinearLayoutManager.() -> Unit = {}) {
        val lm = LinearLayoutManager(recyclerView.context)
        lm.block()
        recyclerView.layoutManager = lm
        recyclerView.adapter = this
    }

    open fun bind(recyclerView: RecyclerView, spanCount: Int, block: GridLayoutManager.() -> Unit = {}) {
        val lm = GridLayoutManager(recyclerView.context, spanCount)
        lm.block()
        lm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (dataIsEmpty || position == size) lm.spanCount
                else 1
            }
        }
        recyclerView.layoutManager = lm
        recyclerView.adapter = this
    }

}