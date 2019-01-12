package com.bernaferrari.carouselgifviewer.new

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.BaseMvRxFragment
import com.bernaferrari.carouselgifviewer.AboutDialog
import com.bernaferrari.carouselgifviewer.MvRxEpoxyController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.orhanobut.logger.Logger
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.main_fragment.*

abstract class BaseMainFragment : BaseMvRxFragment() {

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

    val epoxyController by lazy { epoxyController() }

    var isVideoShown = true

    abstract fun epoxyController(): MvRxEpoxyController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        epoxyController.onRestoreInstanceState(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(
        com.bernaferrari.carouselgifviewer.R.layout.main_fragment,
        container,
        false
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerDiscrete.adapter = epoxyController.adapter

        recyclerDiscrete.setItemTransitionTimeMillis(150)

        recyclerDiscrete.setItemTransformer(
            ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build()
        )

        recyclerDiscrete.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    isVideoShown = true
                } else {
                    isVideoShown = false
                    card.isVisible = false
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })

        val bts = BottomSheetBehavior.from(bottomSheet)

        bts.setBottomSheetCallback(createBottomSheetCallback(textView))

        header_behavior.setOnClickListener { bts.state = STATE_EXPANDED }

        info.setOnClickListener {
            AboutDialog.show(requireActivity().supportFragmentManager)
        }

        val itemHeight = getScreenWidth(requireActivity())

        FrameLayout.LayoutParams(itemHeight, itemHeight).let { params ->
            params.gravity = Gravity.CENTER
            card.layoutParams = params
        }

        setUpVideoView()
    }

    fun getScreenWidth(activity: Activity): Int {
        val windowDimensions = Point()
        activity.windowManager.defaultDisplay.getSize(windowDimensions)
        return Math.round(Math.min(windowDimensions.y, windowDimensions.x) * 0.7f)
    }

    var progressDisposable: Disposable? = null

    private fun setUpVideoView() {
        video_view.apply {
            this.setOnPreparedListener {
                Logger.d("video_view.setOnPreparedListener")

                if (isVideoShown) {
                    card.visibility = View.VISIBLE
                    progressDisposable?.dispose()
                    progressBar.isVisible = false
                }

                this.start()
            }

            this.setOnErrorListener {
                Logger.d("video_view.setOnErrorListener")

//                Snackbar.make(
//                    drawer,
//                    context.getString(R.string.gif_error),
//                    Snackbar.LENGTH_LONG
//                ).show()

                this.setVideoURI(this.videoUri)

                true
            }

            this.setOnCompletionListener {
                Logger.d("video_view.setOnCompletionListener")
                this.restart()
            }
        }
    }

    override fun invalidate() {
        epoxyController.requestModelBuild()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        epoxyController.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        epoxyController.cancelPendingModelBuild()
        super.onDestroyView()
    }

    private fun createBottomSheetCallback(text: TextView): BottomSheetBehavior.BottomSheetCallback {
        // Set up BottomSheetCallback
        return object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_DRAGGING -> text.text = "dragging"
                    BottomSheetBehavior.STATE_EXPANDED -> text.text = "expanded"
                    BottomSheetBehavior.STATE_COLLAPSED -> text.text = "collapsed"
                    else -> {
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                updateFilterHeadersAlpha(slideOffset)
                view?.also { hideKeyboardFrom(context, it) }
            }
        }
    }

    fun hideKeyboardFrom(context: Context?, view: View) {
        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
                ?: return
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun updateFilterHeadersAlpha(slideOffset: Float) {
        // Alpha of normal header views increases as the sheet expands, while alpha of description
        // views increases as the sheet collapses. To prevent overlap, we use a threshold at which
        // the views "trade places".
        frag_behavior.alpha = offsetToAlpha(slideOffset, ALPHA_CHANGEOVER, ALPHA_HEADER_MAX)
        header_behavior.alpha = offsetToAlpha(slideOffset, ALPHA_CHANGEOVER, ALPHA_DESC_MAX)

        frag_behavior.isInvisible = frag_behavior.alpha == 0f
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
