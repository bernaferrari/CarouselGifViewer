package com.bernaferrari.carouselgifviewer.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DatabaseDataSource(
    private val delayMs: Long = 500
) : DictDataSource {

    override fun getItems(): Flow<List<GifItem>> = flow {
        delay(delayMs)
        emit(
            listOf(
                GifItem(
                    id = "1",
                    title = "João",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?w=600&q=80&auto=format&fit=crop"
                ),
                GifItem(
                    id = "2",
                    title = "Pecador",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600&q=80&auto=format&fit=crop"
                ),
                GifItem(
                    id = "3",
                    title = "Êxodo",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1470770841072-f978cf4d019e?w=600&q=80&auto=format&fit=crop"
                ),
                GifItem(
                    id = "4",
                    title = "Salomão",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1482192596544-9eb780fc7f66?w=600&q=80&auto=format&fit=crop"
                ),
                GifItem(
                    id = "5",
                    title = "Rute",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1489515217757-5fd1be406fef?w=600&q=80&auto=format&fit=crop"
                ),
                GifItem(
                    id = "6",
                    title = "Antigo Testamento",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1501785888041-af3ef285b470?w=600&q=80&auto=format&fit=crop"
                ),
                GifItem(
                    id = "7",
                    title = "Maria Madalena",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?w=600&q=80&auto=format&fit=crop"
                ),
                GifItem(
                    id = "8",
                    title = "Amém",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1472214103451-9374bd1c798e?w=600&q=80&auto=format&fit=crop"
                ),
                GifItem(
                    id = "9",
                    title = "Ídolo",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?w=600&q=80&auto=format&fit=crop"
                ),
                GifItem(
                    id = "10",
                    title = "Romanos",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/VolkswagenGTIReview.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=600&q=80&auto=format&fit=crop"
                ),
                GifItem(
                    id = "11",
                    title = "Samaritano",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?w=600&q=80&auto=format&fit=crop"
                ),
                GifItem(
                    id = "12",
                    title = "Gênesis",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WhatCarCanYouGetForAGrand.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1482192596544-9eb780fc7f66?w=600&q=80&auto=format&fit=crop"
                ),
                GifItem(
                    id = "13",
                    title = "Eli (o sumo sacerdote)",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4",
                    thumbnailUrl = "https://images.unsplash.com/photo-1472214103451-9374bd1c798e?w=600&q=80&auto=format&fit=crop"
                )
            )
        )
    }
}
