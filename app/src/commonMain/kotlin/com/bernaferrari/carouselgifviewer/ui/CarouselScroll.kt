package com.bernaferrari.carouselgifviewer.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round
import kotlin.math.sign

/** Converts strip pixels to pager pixels while sharing the pager's mutation lock and cancellation. */
internal class ScaledScrollableState(
    private val delegate: ScrollableState,
    private val scale: () -> Float,
) : ScrollableState {
    override suspend fun scroll(scrollPriority: MutatePriority, block: suspend ScrollScope.() -> Unit) {
        delegate.scroll(scrollPriority) {
            val target = this
            val ratio = scale().takeIf { it.isFinite() && it > 0f } ?: 1f
            block(object : ScrollScope {
                override fun scrollBy(pixels: Float): Float =
                    if (pixels.isFinite()) target.scrollBy(pixels * ratio) / ratio else 0f
            })
        }
    }

    override fun dispatchRawDelta(delta: Float): Float {
        if (!delta.isFinite()) return 0f
        val ratio = scale().takeIf { it.isFinite() && it > 0f } ?: 1f
        return delegate.dispatchRawDelta(delta * ratio) / ratio
    }

    override val isScrollInProgress get() = delegate.isScrollInProgress
    override val canScrollForward get() = delegate.canScrollForward
    override val canScrollBackward get() = delegate.canScrollBackward
    override val lastScrolledForward get() = delegate.lastScrolledForward
    override val lastScrolledBackward get() = delegate.lastScrolledBackward
}

/** Native decay in strip coordinates; snapping reads the authoritative pager, never a trailing layout. */
internal class CarouselSnapLayout(
    private val position: () -> Float,
    private val pageCount: () -> Int,
    private val itemStep: () -> Float,
    private val minimumFlingVelocity: Float,
) : SnapLayoutInfoProvider {
    override fun calculateApproachOffset(velocity: Float, decayOffset: Float): Float =
        (abs(decayOffset) - itemStep()).coerceAtLeast(0f) * sign(decayOffset)

    override fun calculateSnapOffset(velocity: Float): Float =
        carouselSnapOffset(position(), pageCount(), itemStep(), velocity, minimumFlingVelocity)
}

internal fun carouselSnapOffset(
    position: Float,
    pageCount: Int,
    itemStep: Float,
    velocity: Float,
    minimumFlingVelocity: Float,
): Float {
    if (pageCount <= 1 || !position.isFinite() || !itemStep.isFinite() || itemStep <= 0f) return 0f
    val current = position.coerceIn(0f, (pageCount - 1).toFloat())
    val target = when {
        abs(velocity) < minimumFlingVelocity -> round(current)
        velocity > 0f -> ceil(current)
        else -> floor(current)
    }.coerceIn(0f, (pageCount - 1).toFloat())
    return (target - current) * itemStep
}

/** Latest request wins. Old animation cleanup cannot erase a newer requested destination. */
@Stable
internal class CarouselNavigation(private val pager: PagerState, private val scope: CoroutineScope) {
    var requestedPage: Int? by mutableStateOf(null)
        private set
    private var generation = 0L
    private var job: Job? = null

    fun moveBy(delta: Int, animated: Boolean = true) =
        moveTo((requestedPage ?: pager.currentPage) + delta, animated)

    fun moveTo(index: Int, animated: Boolean = true) {
        if (pager.pageCount == 0) return
        val target = index.coerceIn(0, pager.pageCount - 1)
        val request = ++generation
        job?.cancel()
        requestedPage = target
        job = scope.launch {
            try {
                if (animated) {
                    pager.animateScrollToPage(
                        target,
                        animationSpec = spring(dampingRatio = 1f, stiffness = Spring.StiffnessMedium),
                    )
                } else {
                    pager.scrollToPage(target)
                }
            } finally {
                if (generation == request) requestedPage = null
            }
        }
    }

    fun cancel() {
        generation++
        job?.cancel()
        requestedPage = null
    }
}

internal fun resolveCarouselSelection(ids: List<String>, selectedId: String?, fallbackIndex: Int): Int? {
    if (ids.isEmpty()) return null
    return selectedId?.let { ids.indexOf(it).takeIf { index -> index >= 0 } }
        ?: fallbackIndex.coerceIn(0, ids.lastIndex)
}
