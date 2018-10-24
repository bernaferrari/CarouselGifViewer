package com.bernaferrari.carouselgifviewer.extensions

import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.logger.Logger

internal inline fun RecyclerView.onScroll(crossinline body: (dx: Int, dy: Int) -> Unit) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            body(dx, dy)
        }
    })
}

// scroll to index +- 1, so that user is still able to see the next/previous item
internal fun RecyclerView.scrollToIndex(previousIndex: Int, index: Int, size: Int) {
    if (previousIndex == -1) {
        // This will run on first iteration
        Logger.d("index value: $index")
        when (index) {
            in 1 until (size - 1) -> this.scrollToPosition(index - 1)
            (size - 1) -> this.scrollToPosition(index)
            else -> this.scrollToPosition(0)
        }
    } else {
        Logger.d("index: " + index + " previousIndex = " + previousIndex + " // " + (index in 1 until (previousIndex - 1)))
        when (index) {
            in 1 until previousIndex -> this.scrollToPosition(index - 1)
            in previousIndex until (size - 1) -> this.scrollToPosition(index + 1)
            else -> this.scrollToPosition(index)
        }
    }
}
