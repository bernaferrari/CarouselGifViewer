package com.bernaferrari.carouselgifviewer.main

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.activityViewModel
import com.bernaferrari.base.mvrx.MvRxEpoxyController
import com.bernaferrari.base.mvrx.simpleController
import com.bernaferrari.carouselgifviewer.GifDetailsBindingModel_
import com.bernaferrari.carouselgifviewer.R
import com.bernaferrari.carouselgifviewer.emptyContent
import com.bernaferrari.carouselgifviewer.extensions.calculateNoOfColumns
import com.bernaferrari.carouselgifviewer.extensions.shareItemHandler
import com.bernaferrari.carouselgifviewer.loadingRow
import com.bernaferrari.ui.search.BaseSearchFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign

class DetailsFragment : BaseSearchFragment() {

    private val viewModel: ViewModelDictionary by activityViewModel()
//    @Inject lateinit var detailsViewModelFactory: ViewModelDictionary.Factory

    private var numOfColumns = 2

    private val fullLineSpan =
        EpoxyModel.SpanSizeOverrideCallback { _, _, _ -> numOfColumns }

    override fun layoutManager(): RecyclerView.LayoutManager {
        numOfColumns = context.calculateNoOfColumns(default = 2)
        return GridLayoutManager(context, numOfColumns)
    }

    override fun onTextChanged(searchText: String) {
        viewModel.filterRelay.accept(searchText)
    }

    override fun dismiss() {
        requireActivity().onBackPressed()
    }

    override fun epoxyController(): MvRxEpoxyController = simpleController(viewModel) { state ->

        if (state.items is Loading) {
            loadingRow {
                this.id("loading")
                this.spanSizeOverride(fullLineSpan)
            }
        }

        if (state.items()?.filteredList?.isEmpty() == true) {
            emptyContent {
                this.id("empty")
                this.spanSizeOverride(fullLineSpan)
                this.label(getString(com.bernaferrari.carouselgifviewer.R.string.no_results))
            }
        }

        state.items()?.filteredList?.forEach {
            GifDetailsBindingModel_()
                .id(it.gifId)
                .gifId(it.gifId)
                .title(it.title)
                .query(viewModel.filterRelay.value)
                .onClick { _, _, _, position ->
                    viewModel.idSelected.accept(it.gifId)
                }
                .onLongClick { _ ->
                    requireActivity().shareItemHandler(it.title, it.gifId)
                    true
                }
//                .isSelected(it.gifId == state.selected)
                .addTo(this)
        }
    }

    override val closeIconRes: Int = R.drawable.ic_keyboard_down

    override fun onDestroyView() {
        disposableManager.clear()
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        // already starts a search with the text on search bar
//        // if empty, nothing changes.
        viewModel.filterRelay.accept(getInputText())

        disposableManager += viewModel.maxListSize.observeOn(AndroidSchedulers.mainThread())
            .subscribe { setInputHint(getString(R.string.search_gifs_hint_loaded, it)) }

        disposableManager += viewModel.itemSelectedRelay.observeOn(AndroidSchedulers.mainThread())
            // only call scrollToPosition when user is not searching.
            .skipWhile { viewModel.filterRelay.value?.isNotBlank() == true }
            .subscribe { scrollToPosition(it) }
    }
}