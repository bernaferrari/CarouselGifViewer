package com.bernaferrari.carouselgifviewer.main

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.activityViewModel
import com.bernaferrari.carouselgifviewer.GifMainCarouselBindingModel_
import com.bernaferrari.carouselgifviewer.R
import com.bernaferrari.carouselgifviewer.core.MvRxEpoxyController
import com.bernaferrari.carouselgifviewer.core.simpleController
import com.bernaferrari.carouselgifviewer.extensions.getScreenPercentSize
import com.bernaferrari.carouselgifviewer.extensions.openBrowserItemHandler
import com.bernaferrari.carouselgifviewer.extensions.shareItemHandler
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.snackbar.Snackbar
import com.yarolegovich.discretescrollview.DiscreteScrollView
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.gif_frag_main.*
import java.util.concurrent.TimeUnit

class MainFragment : BaseMainFragment(),
    DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder> {

    private val viewModel: RxViewModelDictionary by activityViewModel()

    private var previousAdapterPosition = 0

    private var currentIdSelected = ""

    override fun epoxyController(): MvRxEpoxyController = simpleController(viewModel) { state ->

        viewModel.showProgressBar.accept(state.items is Loading)

        viewModel.showErrorMessage.accept(state.items is Fail)

        val itemHeight = requireActivity().getScreenPercentSize()

        state.items()?.fullList?.forEach {
            GifMainCarouselBindingModel_()
                .id(it.gifId)
                .gifId(it.gifId)
                .onClick { _, _, _, position ->
                    recyclerDiscrete.smoothScrollToPosition(position)
                }
                .customWidth(itemHeight)
                .addTo(this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerDiscrete.addOnItemChangedListener(this)

        val bts = BottomSheetBehavior.from(bottomSheet)

        disposableManager += viewModel.idSelected.subscribe {
            bts.state = STATE_COLLAPSED
            val nextPosition = viewModel.fullList.indexOfFirst { fir -> fir.gifId == it }
            recyclerDiscrete.smoothScrollToPosition(nextPosition) // position becomes selected with animated scroll
        }

        shareContent.setOnClickListener { _ ->
            val item = viewModel.fullList.first { it.gifId == currentIdSelected }
            requireActivity().shareItemHandler(item.title, "https://gfycat.com/${item.gifId}")
        }

        shareContent.setOnLongClickListener {
            it.context.openBrowserItemHandler("https://gfycat.com/$currentIdSelected")
            true
        }

        disposableManager += viewModel.closeRelay
            .subscribe { bts.state = STATE_COLLAPSED }

        disposableManager += viewModel.showProgressBar
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { itemsProgress.isVisible = it }

        disposableManager += viewModel.showErrorMessage
            .observeOn(AndroidSchedulers.mainThread())
            .skipWhile { !it }
            .subscribe {
                Snackbar.make(recyclerDiscrete, R.string.gif_error, Snackbar.LENGTH_LONG).show()
            }
    }

    override fun onCurrentItemChanged(viewHolder: RecyclerView.ViewHolder?, adapterPosition: Int) {
        // starts the timer to show a loading bar in case the video is not loaded fast
        showProgressIfNecessary()

        println("size: 0 $adapterPosition currentListSize ${viewModel.fullList.size}")

        if (adapterPosition < 0 || viewModel.fullList.isEmpty()) {
            isVideoShown = false
            card.isVisible = false
            return
        }

        val item = viewModel.fullList[adapterPosition]

        video_view?.setVideoURI("https://thumbs.gfycat.com/${item.gifId}-mobile.mp4".toUri())
        titleContent.text = item.title
        currentIdSelected = item.gifId

        viewModel.itemSelectedRelay.accept(adapterPosition)
        //  scroll to +1 or -1 the index, so user can still see the next/previous item
        //  recycler.scrollToIndex(previousAdapterPosition, adapterPosition, viewModel.fullList.size)
        previousAdapterPosition = adapterPosition
    }

    override fun onStart() {
        super.onStart()
        video_view.start()
    }

    override fun onStop() {
        video_view.pause()
        super.onStop()
    }

    /*
        This will start a 750ms timer. If the GIF is not loaded in this period, the progress bar will
        be shown, so the user knows it is taking longer than expected but it is still loading.
        */
    private fun showProgressIfNecessary() {
        progressDisposable?.dispose()
        progressDisposable =
                Completable.timer(750, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                    .subscribe { progressBar.show() }
    }

}
