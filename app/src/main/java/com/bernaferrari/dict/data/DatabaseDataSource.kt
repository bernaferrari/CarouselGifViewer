package com.bernaferrari.dict.data

import io.reactivex.Observable
import java.util.concurrent.TimeUnit

// This will override the one from dict library.
class DatabaseDataSource(
    private val delayMs: Long = 500
) : DictDataSource {

    override fun getItems(): Observable<List<GifItem>> {
        return Observable.just(
            listOf(
                GifItem("FreshOnlyDarklingbeetle", "João"),
                GifItem("MadeupShallowGallowaycow", "Pecador"),
                GifItem("LivelyCarefulFalcon", "Êxodo"),
                GifItem("WholeGenerousGrunion", "Salomão"),
                GifItem("PlumpGivingBandicoot", "Rute"),
                GifItem("SereneAmazingAyeaye", "Antigo Testamento"),
                GifItem("LimpSmoothJapanesebeetle", "Maria Madalena"),
                GifItem("DeliriousBitterIcelandicsheepdog", "Amém"),
                GifItem("OfficialSpotlessCurassow", "Ídolo"),
                GifItem("DistortedKindlyArmedcrab", "Romanos"),
                GifItem("EdibleFarAntipodesgreenparakeet", "Samaritano"),
                GifItem("MellowCompleteArachnid", "Gênesis"),
                GifItem(
                    "BlissfulFaroffFruitfly",
                    "Eli (o sumo sacerdote)"
                )
            )
        ).delay(delayMs, TimeUnit.MILLISECONDS)
    }
}
