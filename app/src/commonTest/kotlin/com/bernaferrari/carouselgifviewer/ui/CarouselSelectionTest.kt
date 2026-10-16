package com.bernaferrari.carouselgifviewer.ui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CarouselSelectionTest {
    @Test fun emptyCollectionsDoNotProduceAnInvalidIndex() {
        assertNull(resolveCarouselSelection(emptyList(), "removed", 100))
    }
    @Test fun identitySurvivesAnInsertionAndAReorder() {
        assertEquals(2, resolveCarouselSelection(listOf("new", "b", "a"), "a", 0))
        assertEquals(0, resolveCarouselSelection(listOf("a", "b"), "a", 1))
    }
    @Test fun removingTheSelectedItemClampsTheFallback() {
        assertEquals(1, resolveCarouselSelection(listOf("a", "b"), "removed", 200))
        assertEquals(0, resolveCarouselSelection(listOf("a"), "removed", -20))
    }
    @Test fun firstLaunchUsesTheRestoredRepositoryIndex() {
        assertEquals(1, resolveCarouselSelection(listOf("a", "b", "c"), null, 1))
    }
    @Test fun giphyPreviewsUseStillFramesAndRetainTheQuery() {
        assertEquals(
            "https://media4.giphy.com/media/id/giphy_s.gif?token=example",
            "https://media4.giphy.com/media/id/giphy.gif?token=example".toStillThumbnailUrl(),
        )
    }
    @Test fun localAndUnrelatedUrlsAreUnchanged() {
        for (url in listOf("file:///downloads/giphy.gif", "https://example.com/giphy.gif", 
            "https://media4.giphy.com.example.com/media/id/giphy.gif", "not a url", 
            "https://media4.giphy.com/media/id/giphy_s.gif")) {
            assertEquals(url, url.toStillThumbnailUrl())
        }
    }
}
