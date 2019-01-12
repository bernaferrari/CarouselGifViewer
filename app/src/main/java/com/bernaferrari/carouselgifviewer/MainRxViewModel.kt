package com.bernaferrari.carouselgifviewer

import com.bernaferrari.carouselgifviewer.extensions.normalizeString
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables

/**
 * initialState *must* be implemented as a constructor parameter.
 */
class MainRxViewModel(initialState: HelloWorldState) :
    MvRxViewModel<HelloWorldState>(initialState) {

    val relay: BehaviorRelay<String> = BehaviorRelay.create<String>()

    init {
        fetchData()
    }

    private fun fetchData() = withState { _ ->
        Observables.combineLatest(
            Observable.just(items),
            relay
        ) { list, filter ->
            if (filter.isBlank()) {
                list
            } else {
                list.filter { item ->
                    item.gifTitle.normalizeString().contains(filter.normalizeString())
                }
            }
        }.execute {
            copy(items = it)
        }
    }

    private val items = listOf(
        DictionaryItem("DeliriousBitterIcelandicsheepdog", "Amém"),
        DictionaryItem("OfficialSpotlessCurassow", "Ídolo"),
        DictionaryItem("DistortedKindlyArmedcrab", "Romanos"),
        DictionaryItem("EdibleFarAntipodesgreenparakeet", "Samaritano"),
        DictionaryItem("MellowCompleteArachnid", "Gênesis"),
        DictionaryItem("FreshOnlyDarklingbeetle", "João"),
        DictionaryItem("MadeupShallowGallowaycow", "Pecador"),
        DictionaryItem("LivelyCarefulFalcon", "Êxodo"),
        DictionaryItem("WholeGenerousGrunion", "Salomão"),
        DictionaryItem("PlumpGivingBandicoot", "Rute")
    )

}