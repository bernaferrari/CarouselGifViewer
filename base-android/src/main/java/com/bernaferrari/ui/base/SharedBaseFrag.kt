package com.bernaferrari.ui.base

import android.os.Bundle
import com.airbnb.mvrx.MvRxView
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

abstract class SharedBaseFrag : TiviMvRxFragment(), MvRxView, CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Main + Job()

    val disposableManager = CompositeDisposable()

    open val closeIconRes: Int? = 0

    open val showMenu = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(showMenu)
    }

    open fun dismiss() {
        activity?.onBackPressed()
    }

    override fun onDestroy() {
        coroutineContext.cancel()
        disposableManager.clear()
        super.onDestroy()
    }
}
