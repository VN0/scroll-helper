package dev.olog.scrollhelper.layoutmanagers

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

open class OverScrollLinearLayoutManager : LinearLayoutManager, OverScrollDelegate {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(
        context,
        orientation,
        reverseLayout
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    private val listeners = mutableSetOf<OnOverScrollListener>()
    private var recyclerView: RecyclerView? = null

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        val scrollRange = super.scrollVerticallyBy(dy, recycler, state)

        val recyclerView = this.recyclerView
        if (recyclerView != null) {
            val overScroll = dy - scrollRange
            if (overScroll != 0) {
                listeners.forEach { it.onRecyclerViewOverScroll(recyclerView, overScroll) }
            }
        }
        return scrollRange
    }

    override fun onAttachedToWindow(view: RecyclerView?) {
        super.onAttachedToWindow(view)
        this.recyclerView = view
    }

    override fun onDetachedFromWindow(view: RecyclerView?, recycler: RecyclerView.Recycler?) {
        super.onDetachedFromWindow(view, recycler)
        this.recyclerView = null
    }

    override fun addOnOverScrollListener(listener: OnOverScrollListener) {
        listeners.add(listener)
    }

    override fun removeOnOverScrollListener(listener: OnOverScrollListener) {
        listeners.remove(listener)
    }
}