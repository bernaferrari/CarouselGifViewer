package com.bernaferrari.dict.main

import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.bernaferrari.base.misc.normalizeString
import com.bernaferrari.base.mvrx.MvRxViewModel
import com.bernaferrari.dict.data.DictDataSource
import com.bernaferrari.dict.data.GifItem
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.rxkotlin.Observables

data class DictState(
    val fullList: List<GifItem> = emptyList(),
    val filteredList: List<GifItem> = emptyList(),
    val isLoading: Boolean = true
) : MvRxState

class DictViewModel(
    initialState: DictState,
    private val dictRepository: DictDataSource
) : MvRxViewModel<DictState>(initialState) {

    var itemSelectedRelay = PublishRelay.create<Int>()

    val idSelected = PublishRelay.create<String>()

    val filterRelay = BehaviorRelay.create<String>()

    val showErrorMessage = BehaviorRelay.create<Boolean>()

    val maxListSize = BehaviorRelay.create<Int>()

    var fullList = listOf<GifItem>()

    init {
        fetchData()
    }

    private fun fetchData() = withState { _ ->
        Observables.combineLatest(
            dictRepository.getItems(),
            filterRelay
        ) { list, filter ->

            maxListSize.accept(list.size)
            fullList = list

            // get the string without special characters and filter the list.
            // If the filter is not blank, it will filter the list.
            // If it is blank, it will return the original list.
            val pattern = filter.normalizeString()

            val filtered = list.takeIf { filter.isNotBlank() }
                ?.filter { pattern in it.title.normalizeString() }
                    ?: list

            DictState(list, filtered)
        }.execute {
            copy(
                fullList = it()?.fullList ?: emptyList(),
                filteredList = it()?.filteredList ?: emptyList(),
                isLoading = (it.invoke() == null) || (it()?.fullList?.isEmpty() == true)
            )
        }
    }

    companion object : MvRxViewModelFactory<DictViewModel, DictState> {

        override fun create(
            viewModelContext: ViewModelContext,
            state: DictState
        ): DictViewModel? {
            val repo = viewModelContext.activity<DictActivity>().repo
            return DictViewModel(state, repo)
        }
    }
}
