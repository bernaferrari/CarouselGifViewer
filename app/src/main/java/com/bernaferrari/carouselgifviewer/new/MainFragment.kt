package com.bernaferrari.carouselgifviewer.new

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.activityViewModel
import com.bernaferrari.carouselgifviewer.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.yarolegovich.discretescrollview.DiscreteScrollView
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.main_fragment.*
import java.util.concurrent.TimeUnit

class MainFragment : BaseMainFragment(),
    DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder> {

    private val viewModel: RxViewModelDictionary by activityViewModel()

    private var previousAdapterPosition = 0

    val disposableManager = CompositeDisposable()

    override fun epoxyController(): MvRxEpoxyController = simpleController(viewModel) { state ->

        if (state.items is Loading) loadingRow { this.id("loading") }

        if (state.items()?.isEmpty() == true) {
            emptyContent {
                this.id("empty")
                this.label("Nenhum resultado encontrado")
            }
        }

        val itemHeight = getScreenWidth(requireActivity())

        state.items()?.forEach {
            GifImagePlayerBindingModel_()
                .id(it.gifId)
                .gifId(it.gifId)
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
            val nextPosition = viewModel.currentList.indexOfFirst { fir -> fir.gifId == it }
            recyclerDiscrete.smoothScrollToPosition(nextPosition) // position becomes selected with animated scroll
        }

        disposableManager += viewModel.closeRelay.subscribe { bts.state = STATE_COLLAPSED }
    }

    override fun onCurrentItemChanged(viewHolder: RecyclerView.ViewHolder?, adapterPosition: Int) {
        // starts the timer to show a loading bar in case the video is not loaded fast
        showProgressIfNecessary()

        println("size: 0 $adapterPosition currentListSize ${viewModel.currentList.size}")

        if (adapterPosition < 0 || viewModel.currentList.isEmpty()) {
            isVideoShown = false
            card.isVisible = false
            return
        }

        val item = viewModel.currentList[adapterPosition]

        video_view?.setVideoURI("https://thumbs.gfycat.com/${item.gifId}-mobile.mp4".toUri())
        titlecontent.text = item.title

//        // scroll to +1 or -1 the index, so user can still see the next/previous item
//        recycler.scrollToIndex(previousAdapterPosition, adapterPosition, viewModel.currentList.size)
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
                    .subscribe {
                        // progressBar.visibility = View.VISIBLE
                    }
    }

}
