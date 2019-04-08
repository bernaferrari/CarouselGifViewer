package com.bernaferrari.dict.main

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.mvrx.activityViewModel
import com.bernaferrari.base.misc.toDp
import com.bernaferrari.base.mvrx.MvRxEpoxyController
import com.bernaferrari.base.mvrx.simpleController
import com.bernaferrari.dict.GifDetailsBindingModel_
import com.bernaferrari.dict.R
import com.bernaferrari.dict.emptyContent
import com.bernaferrari.dict.extensions.calculateNoOfColumns
import com.bernaferrari.dict.extensions.shareItemHandler
import com.bernaferrari.dict.loadingRow
import com.bernaferrari.ui.search.BaseSearchFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign

class DetailsFragment : BaseSearchFragment() {

    private val viewModel: DictViewModel by activityViewModel()

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

    override val showKeyboardWhenLoaded = false

    override val sidePadding by lazy { 8.toDp(resources) }

    override fun epoxyController(): MvRxEpoxyController = simpleController(viewModel) { state ->

        if (state.isLoading) {
            loadingRow {
                this.id("loading")
                this.spanSizeOverride(fullLineSpan)
            }
        } else if (state.filteredList.isEmpty()) {
            emptyContent {
                this.id("empty")
                this.spanSizeOverride(fullLineSpan)
                this.label(getString(R.string.no_results))
            }
        }

        state.filteredList.forEach {
            GifDetailsBindingModel_()
                .id(it.gifId)
                .gifId(it.gifId)
                .title(it.title)
                .query(state.filterQuery)
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

        // already starts a search with the text on search bar
        // if empty, nothing changes.
        viewModel.filterRelay.accept(getInputText())

        disposableManager += viewModel.maxListSize.observeOn(AndroidSchedulers.mainThread())
            .subscribe { setInputHint(getString(R.string.search_gifs_hint_loaded, it)) }

        disposableManager += viewModel.itemSelectedRelay.observeOn(AndroidSchedulers.mainThread())
            // only call scrollToPosition when user is not searching.
            .skipWhile { viewModel.filterRelay.value?.isNotBlank() == true }
            .subscribe { scrollToPosition(it) }
    }
}
