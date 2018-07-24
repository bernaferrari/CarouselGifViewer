package com.bernaferrari.carouselgifviewer.extensions

import android.support.v4.widget.DrawerLayout
import android.view.View

internal fun DrawerLayout.onChanged(action: (newState: Int) -> Unit): DrawerLayout.DrawerListener =
    addDrawerListener(onChanged = action)

internal fun DrawerLayout.addDrawerListener(
    onOpened: ((drawerView: View) -> Unit)? = null,
    onClosed: ((drawerView: View) -> Unit)? = null,
    onSlide: ((drawerView: View, slideOffset: Float) -> Unit)? = null,
    onChanged: ((newState: Int) -> Unit)? = null
): DrawerLayout.DrawerListener {

    val listener = object : DrawerLayout.DrawerListener {
        override fun onDrawerStateChanged(newState: Int) {
            onChanged?.invoke(newState)
        }

        override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            onSlide?.invoke(drawerView, slideOffset)
        }

        override fun onDrawerClosed(drawerView: View) {
            onClosed?.invoke(drawerView)
        }

        override fun onDrawerOpened(drawerView: View) {
            onOpened?.invoke(drawerView)
        }
    }

    addDrawerListener(listener)
    return listener
}

