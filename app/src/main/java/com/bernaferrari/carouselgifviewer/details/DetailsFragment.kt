package com.bernaferrari.carouselgifviewer.details

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.activityViewModel
import com.bernaferrari.carouselgifviewer.GifDetailsBindingModel_
import com.bernaferrari.carouselgifviewer.core.MvRxEpoxyController
import com.bernaferrari.carouselgifviewer.core.simpleController
import com.bernaferrari.carouselgifviewer.extensions.hideKeyboardWhenNecessary
import com.bernaferrari.carouselgifviewer.extensions.onTextChanged
import com.bernaferrari.carouselgifviewer.extensions.shareItemHandler
import com.bernaferrari.carouselgifviewer.main.RxViewModelDictionary
import com.bernaferrari.carouselgifviewer.main.loadingRow
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.gif_frag_details.*

class DetailsFragment : BaseDetailsFragment() {

    private val viewModel: RxViewModelDictionary by activityViewModel()

    private val inputMethodManager by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.getSystemService<InputMethodManager>()
        } else {
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        } ?: throw Exception("null activity. Can't bind inputMethodManager")
    }

    override fun epoxyController(): MvRxEpoxyController = simpleController(viewModel) { state ->

        if (state.items is Loading) loadingRow { this.id("loading") }

        state.items()?.filteredList?.forEach {
            GifDetailsBindingModel_()
                .id(it.gifId)
                .gifId(it.gifId)
                .title(it.title)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        close.setOnClickListener { viewModel.closeRelay.accept(Unit) }

        // already starts a search with the text on search bar
        // if empty, nothing changes.
        viewModel.filterRelay.accept(queryInput.text.toString())

        disposableManager += viewModel.maxListSize.observeOn(AndroidSchedulers.mainThread())
            .subscribe { queryInput.hint = "Pesquisar $it GIFs.." }

        disposableManager += viewModel.itemSelectedRelay.observeOn(AndroidSchedulers.mainThread())
            // only call scrollToPosition when user is not searching.
            .skipWhile { viewModel.filterRelay.value.isNotBlank() }
            .subscribe { recycler.scrollToPosition(it) }

        queryInput.onTextChanged {
            queryClear.isVisible = it.isNotEmpty()
            viewModel.filterRelay.accept(it.toString())
            recycler.scrollToPosition(0)
        }

        queryClear.setOnClickListener { queryInput.setText("") }

        hideKeyboardWhenNecessary(
            requireActivity(),
            inputMethodManager,
            recycler,
            queryInput
        )
    }
}
