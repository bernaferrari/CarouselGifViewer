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
import com.bernaferrari.carouselgifviewer.*
import com.bernaferrari.carouselgifviewer.extensions.onTextChanged
import com.bernaferrari.carouselgifviewer.new.RxViewModelDictionary
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.details_fragment.*

class DetailsFragment : BaseDetailsFragment() {

    private val viewModel: RxViewModelDictionary by activityViewModel()

    private val disposableManager = CompositeDisposable()

    private val inputMethodManager by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.getSystemService<InputMethodManager>()
        } else {
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        } ?: throw Exception("null activity. Can't bind inputMethodManager")
    }

    override fun epoxyController(): MvRxEpoxyController = simpleController(viewModel) { state ->

        if (state.items is Loading) loadingRow { this.id("loading") }

        state.items()?.forEach {
            GifListableBindingModel_()
                .id(it.gifId)
                .gifId(it.gifId)
                .title(it.title)
//                .isSelected(it.gifId == state.selected)
                .onClick { v ->
                    viewModel.idSelected.accept(it.gifId)
                }
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

        queryInput.onTextChanged {
            queryClear.isVisible = it.isNotEmpty()
            viewModel.filterRelay.accept(it.toString())
        }

        queryClear.setOnClickListener { queryInput.setText("") }

        hideKeyboardWhenNecessary(requireActivity(), inputMethodManager, recycler, queryInput)
    }


}
