package com.bernaferrari.carouselgifviewer.ui

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.ScrollableState
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class CarouselScrollTest {
    @Test fun stripDeltasAndConsumptionUseTheSameScale() = runTest {
        var position = 0f
        val delegate = ScrollableState { delta ->
            val next = (position + delta).coerceIn(0f, 100f)
            (next - position).also { position = next }
        }
        val strip = ScaledScrollableState(delegate) { 4f }
        strip.scroll {
            assertEquals(20f, scrollBy(20f))
            assertEquals(80f, position)
            assertEquals(5f, scrollBy(20f))
            assertEquals(100f, position)
            assertEquals(0f, scrollBy(10f))
            assertEquals(-25f, scrollBy(-40f))
            assertEquals(0f, position)
        }
    }

    @Test fun aPagerGestureCancelsAnInFlightStripGesture() = runTest {
        val pager = ScrollableState { it }
        val strip = ScaledScrollableState(pager) { 3f }
        val fling = launch { strip.scroll { awaitCancellation() } }
        runCurrent()
        assertTrue(pager.isScrollInProgress)
        pager.scroll(MutatePriority.UserInput) { scrollBy(30f) }
        fling.join()
        assertTrue(fling.isCancelled)
        assertFalse(strip.isScrollInProgress)
    }

    @Test fun aStripGestureCancelsAnInFlightPagerAnimation() = runTest {
        val pager = ScrollableState { it }
        val animation = launch { pager.scroll { awaitCancellation() } }
        runCurrent()
        ScaledScrollableState(pager) { 3f }.scroll(MutatePriority.UserInput) { scrollBy(10f) }
        animation.join()
        assertTrue(animation.isCancelled)
        assertFalse(pager.isScrollInProgress)
    }

    @Test fun scaleIsFrozenForTheDurationOfAGesture() = runTest {
        var ratio = 2f
        var position = 0f
        val strip = ScaledScrollableState(ScrollableState { position += it; it }) { ratio }
        strip.scroll {
            scrollBy(10f)
            ratio = 8f
            scrollBy(10f)
        }
        assertEquals(40f, position)
        strip.scroll { scrollBy(10f) }
        assertEquals(120f, position)
    }

    @Test fun invalidInputsNeverPropagateNanIntoLayout() = runTest {
        var position = 0f
        val strip = ScaledScrollableState(ScrollableState { position += it; it }) { Float.NaN }
        strip.scroll {
            assertEquals(0f, scrollBy(Float.NaN))
            assertEquals(0f, scrollBy(Float.POSITIVE_INFINITY))
            assertEquals(8f, scrollBy(8f))
        }
        assertEquals(8f, position)
    }

    @Test fun aSlowReleaseSnapsToTheClosestItem() {
        assertEquals(-20f, carouselSnapOffset(2.2f, 10, 100f, 0f, 400f), 0.001f)
        assertEquals(20f, carouselSnapOffset(2.8f, 10, 100f, 0f, 400f), 0.001f)
    }

    @Test fun aFlingSnapsInItsTravelDirection() {
        assertEquals(80f, carouselSnapOffset(2.2f, 10, 100f, 500f, 400f), 0.001f)
        assertEquals(-80f, carouselSnapOffset(2.8f, 10, 100f, -500f, 400f), 0.001f)
    }

    @Test fun emptySingletonAndEndpointsDoNotOverscroll() {
        for (count in listOf(0, 1)) assertEquals(0f, carouselSnapOffset(0f, count, 100f, 8000f, 400f))
        assertEquals(0f, carouselSnapOffset(0f, 10, 100f, -8000f, 400f))
        assertEquals(0f, carouselSnapOffset(9f, 10, 100f, 8000f, 400f))
        assertEquals(0f, carouselSnapOffset(Float.NaN, 10, 100f, 0f, 400f))
        assertEquals(0f, carouselSnapOffset(2f, 10, 0f, 0f, 400f))
    }

    @Test fun randomizedSnapTargetsAlwaysLandOnAnItemWithinBounds() {
        val random = Random(8205)
        repeat(10_000) {
            val count = random.nextInt(2, 500)
            val position = random.nextFloat() * (count - 1)
            val step = random.nextFloat() * 400 + 1
            val velocity = random.nextFloat() * 20_000 - 10_000
            val delta = carouselSnapOffset(position, count, step, velocity, 400f)
            val target = position + delta / step
            assertTrue(target >= -0.001f && target <= count - 1 + 0.001f)
            assertEquals(kotlin.math.round(target), target, 0.001f)
            if (abs(velocity) >= 400f) assertTrue(delta * velocity >= 0f)
        }
    }
}
