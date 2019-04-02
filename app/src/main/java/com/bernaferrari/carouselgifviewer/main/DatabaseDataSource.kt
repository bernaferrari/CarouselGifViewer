package com.bernaferrari.carouselgifviewer.main

import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class DatabaseDataSource(
    private val delayMs: Long = 1500
) : DictDataSource {

    override fun getItems(): Observable<List<GifItem>> {
        return Observable.just(
            listOf(
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
        ).delay(delayMs, TimeUnit.MILLISECONDS)
    }
}
