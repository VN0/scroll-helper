package dev.olog.scrollhelper.impl

import android.view.View
import android.view.ViewGroup
import androidx.core.math.MathUtils.clamp
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import dev.olog.scrollhelper.MultiListenerBottomSheetBehavior
import dev.olog.scrollhelper.ScrollType
import kotlin.math.abs

internal class ScrollWithSlidingPanel(
    input: ScrollType.OnlySlidingPanel,
    enableClipRecursively: Boolean,
    debugScroll: Boolean
) : AbsScroll(input, enableClipRecursively, debugScroll) {

    private val scrollSlidingPanel: Boolean = input.scrollableSlidingPanel

    private val slidingPanel: View = input.slidingPanel
    private val slidingPanelBehavior =
        from(slidingPanel) as MultiListenerBottomSheetBehavior<*>

    override fun onAttach(activity: FragmentActivity) {
    }

    override fun onDetach(activity: FragmentActivity) {
    }

    override fun restoreInitialPosition(recyclerView: RecyclerView) {
        super.restoreInitialPosition(recyclerView)
        if (!scrollSlidingPanel) {
            return
        }
        slidingPanel.animate()?.translationY(0f)
    }

    override fun onRecyclerViewScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onRecyclerViewScrolled(recyclerView, dx, dy)
        if (!scrollSlidingPanel) {
            return
        }
        val newPeekHeight = clamp(
            slidingPanel.translationY + dy,
            0f,
            slidingPanelBehavior.peekHeight.toFloat()
        )

        logVerbose {
            """
             onRecyclerViewScrolled: translating sliding panel
                from=${slidingPanel.translationY} to $newPeekHeight
        """.trimIndent()
        }

        updateOnlyIfNeeded(slidingPanel, newPeekHeight)
        fabMap.get(recyclerView.hashCode())?.let {
            updateOnlyIfNeeded(it, slidingPanel.translationY)
        }
    }

    override fun applyMarginToFab(fab: View) {
        fab.doOnPreDraw {
            val params = fab.layoutParams
            val marginsToApply = slidingPanelBehavior.peekHeight

            if (params is ViewGroup.MarginLayoutParams && params.bottomMargin < marginsToApply) {
                params.bottomMargin += marginsToApply
                fab.layoutParams = params
            }
        }
    }

    override fun updateOnlyIfNeeded(view: View, translationY: Float) {
        if (abs(view.translationY - translationY) > TOLERANCE &&
            slidingPanelBehavior.state == STATE_COLLAPSED
        ) {
            view.translationY = translationY
        }
    }

}