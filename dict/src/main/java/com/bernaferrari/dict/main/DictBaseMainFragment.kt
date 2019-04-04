package com.bernaferrari.dict.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.BaseMvRxFragment
import com.bernaferrari.base.mvrx.MvRxEpoxyController
import com.bernaferrari.dict.R
import com.bernaferrari.dict.core.AboutDialog
import com.bernaferrari.dict.extensions.getScreenPercentSize
import com.google.android.material.snackbar.Snackbar
import com.orhanobut.logger.Logger
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.gif_frag_main.*

abstract class DictBaseMainFragment : BaseMvRxFragment() {

    private val epoxyController by lazy { epoxyController() }

    var isVideoShown = true

    val disposableManager = CompositeDisposable()

    abstract fun epoxyController(): MvRxEpoxyController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        epoxyController.onRestoreInstanceState(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.gif_frag_main, container, false)

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

        BottomSheetConfig(this.activity, bottomSheet, frag_behavior, header_behavior)
            .onViewLoaded()

        info.setOnClickListener {
            AboutDialog.show(requireActivity().supportFragmentManager)
        }

        updateCardSize()
        setUpVideoView()
    }

    private fun updateCardSize() = card.updateLayoutParams {
        val itemHeight = requireActivity().getScreenPercentSize()
        height = itemHeight
        width = itemHeight
    }

    var progressDisposable: Disposable? = null

    private fun setUpVideoView() {
        video_view.apply {
            this.setOnPreparedListener {
                Logger.d("video_view.setOnPreparedListener")

                if (isVideoShown) {
                    card.visibility = View.VISIBLE
                    progressDisposable?.dispose()
                    progressBar.hide()
                }

                this.start()
            }

            this.setOnErrorListener {
                Logger.d("video_view.setOnErrorListener")

                Snackbar.make(
                    recyclerDiscrete,
                    context.getString(R.string.gif_error),
                    Snackbar.LENGTH_LONG
                ).show()

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
        disposableManager.clear()
        progressDisposable?.dispose()
        epoxyController.cancelPendingModelBuild()
        super.onDestroyView()
    }
}
