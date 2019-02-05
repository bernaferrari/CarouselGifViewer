package com.bernaferrari.carouselgifviewer.main

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.bernaferrari.carouselgifviewer.core.MvRxViewModel
import com.bernaferrari.carouselgifviewer.extensions.normalizeString
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import java.util.concurrent.TimeUnit

data class GifItem(val gifId: String, val title: String) : MvRxState

class GifState(val fullList: List<GifItem>, val filteredList: List<GifItem>)

data class BibleState(val items: Async<GifState> = Loading()) : MvRxState

class RxViewModelDictionary(initialState: BibleState) : MvRxViewModel<BibleState>(initialState) {

    val closeRelay = PublishRelay.create<Unit>()

    var itemSelectedRelay = PublishRelay.create<Int>()

    val idSelected = BehaviorRelay.create<String>()

    val filterRelay = BehaviorRelay.create<String>()

    val showProgressBar = BehaviorRelay.create<Boolean>()

    val showErrorMessage = BehaviorRelay.create<Boolean>()

    val maxListSize = BehaviorRelay.create<Int>()

    var fullList = listOf<GifItem>()

    private val items = listOf(
        GifItem("DeliriousBitterIcelandicsheepdog", "Amém"),
        GifItem("OfficialSpotlessCurassow", "Ídolo"),
        GifItem("DistortedKindlyArmedcrab", "Romanos"),
        GifItem("EdibleFarAntipodesgreenparakeet", "Samaritano"),
        GifItem("MellowCompleteArachnid", "Gênesis"),
        GifItem("FreshOnlyDarklingbeetle", "João"),
        GifItem("MadeupShallowGallowaycow", "Pecador"),
        GifItem("LivelyCarefulFalcon", "Êxodo"),
        GifItem("WholeGenerousGrunion", "Salomão"),
        GifItem("PlumpGivingBandicoot", "Rute"),
        GifItem("SereneAmazingAyeaye", "Antigo Testamento"),
        GifItem("LimpSmoothJapanesebeetle", "Maria Madalena"),
        GifItem("BlissfulFaroffFruitfly", "Eli (o sumo sacerdote)")
    )

    init {
        fetchData()
    }

    private fun fetchData() = withState { _ ->
        Observables.combineLatest(
            Observable.just(items),
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

            GifState(list, filtered)
        }
            .delay(1500, TimeUnit.MILLISECONDS) // great for testing
            .execute { copy(items = it) }
    }
}
