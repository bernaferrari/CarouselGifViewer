package com.bernaferrari.carouselgifviewer.handler

internal interface ItemHandler {

    /**
     * Other handlers will implement the invoke, and other classes will invoke it when necessary.
     */
    operator fun invoke()
}