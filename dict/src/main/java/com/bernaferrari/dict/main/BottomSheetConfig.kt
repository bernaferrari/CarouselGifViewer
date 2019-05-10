package com.bernaferrari.dict.main

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.view.doOnLayout
import androidx.core.view.isInvisible
import androidx.fragment.app.FragmentActivity
import com.bernaferrari.base.misc.hideKeyboard
import com.google.android.material.bottomsheet.BottomSheetBehavior

class BottomSheetConfig(
    activity: FragmentActivity?,
    private val bottomSheet: FrameLayout,
    private val frag_behavior: ViewGroup?,
    private val header_behavior: ViewGroup?,
    private val callback: OnBackPressedCallback
) {

    companion object {
        // Threshold for when normal header views and description views should "change places".
        // This should be a value between 0 and 1, coinciding with a point between the bottom
        // sheet's collapsed (0) and expanded (1) states.
        private const val ALPHA_CHANGEOVER = 0.33f
        // Threshold for when description views reach maximum alpha. Should be a value between
        // 0 and [ALPHA_CHANGEOVER], inclusive.
        private const val ALPHA_DESC_MAX = 0f
        // Threshold for when normal header views reach maximum alpha. Should be a value between
        // [ALPHA_CHANGEOVER] and 1, inclusive.
        private const val ALPHA_HEADER_MAX = 0.67f
    }

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            callback.isEnabled = newState == BottomSheetBehavior.STATE_EXPANDED
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            updateFilterHeadersAlpha(slideOffset)
            activity?.hideKeyboard()
        }
    }

    fun onViewLoaded() {
        val behavior = BottomSheetBehavior.from(bottomSheet)

        behavior.setBottomSheetCallback(bottomSheetCallback)

        // This fragment is in the layout of a parent fragment, so its view hierarchy is restored
        // when the parent's hierarchy is restored. However, the dispatch order seems to traverse
        // child fragments first, meaning the views we care about have not actually been restored
        // when onViewStateRestored is called (otherwise we would do this there).
        bottomSheet.doOnLayout {
            val slideOffset = when (behavior.state) {
                BottomSheetBehavior.STATE_EXPANDED -> 1f
                BottomSheetBehavior.STATE_COLLAPSED -> 0f
                else /*BottomSheetBehavior.STATE_HIDDEN*/ -> -1f
            }
            updateFilterHeadersAlpha(slideOffset)
        }

        header_behavior?.setOnClickListener { behavior.state = BottomSheetBehavior.STATE_EXPANDED }
    }

    private fun updateFilterHeadersAlpha(slideOffset: Float) {
        // Alpha of normal header views increases as the sheet expands, while alpha of description
        // views increases as the sheet collapses. To prevent overlap, we use a threshold at which
        // the views "trade places".
        //
        // these views might trigger a crash if window is being resized (pop-up/split-screen)
        // and they are called.
        frag_behavior?.alpha = offsetToAlpha(slideOffset, ALPHA_CHANGEOVER, ALPHA_HEADER_MAX)
        header_behavior?.alpha = offsetToAlpha(slideOffset, ALPHA_CHANGEOVER, ALPHA_DESC_MAX)

        header_behavior?.isInvisible = header_behavior?.alpha == 0f
        frag_behavior?.isInvisible = frag_behavior?.alpha == 0f
    }

    /**
     * Map a slideOffset (in the range `[-1, 1]`) to an alpha value based on the desired range.
     * For example, `offsetToAlpha(0.5, 0.25, 1) = 0.33` because 0.5 is 1/3 of the way between 0.25
     * and 1. The result value is additionally clamped to the range `[0, 1]`.
     */
    private fun offsetToAlpha(value: Float, rangeMin: Float, rangeMax: Float): Float {
        return ((value - rangeMin) / (rangeMax - rangeMin)).coerceIn(0f, 1f)
    }
}
