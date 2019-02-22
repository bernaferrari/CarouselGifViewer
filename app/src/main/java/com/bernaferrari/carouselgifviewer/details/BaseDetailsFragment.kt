package com.bernaferrari.carouselgifviewer.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.mvrx.BaseMvRxFragment
import com.bernaferrari.carouselgifviewer.R
import com.bernaferrari.carouselgifviewer.core.MvRxEpoxyController
import com.bernaferrari.carouselgifviewer.extensions.calculateNoOfColumns
import com.bernaferrari.carouselgifviewer.extensions.onScroll
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.gif_frag_details.*

abstract class BaseDetailsFragment : BaseMvRxFragment() {

    private val epoxyController by lazy { epoxyController() }

    val disposableManager = CompositeDisposable()

    var numOfColumns = 2

    abstract fun epoxyController(): MvRxEpoxyController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        epoxyController.onRestoreInstanceState(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.gif_frag_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // todo re-calculate and re-set it every time onConfigurationChanged is called
        numOfColumns = Math.min(view.context.calculateNoOfColumns(), 2)
        recycler.layoutManager = GridLayoutManager(context, numOfColumns)
        recycler.setController(epoxyController)

        recycler.onScroll { dx, dy ->
            // this will take care of titleElevation
            // recycler might be null when back is pressed
            val raiseTitleBar = dy > 0 || recycler?.computeVerticalScrollOffset() != 0
            title_bar?.isActivated = raiseTitleBar // animated via a StateListAnimator
        }

        close.setOnClickListener { requireActivity().onBackPressed() }
    }

    override fun invalidate() {
        recycler.requestModelBuild()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        epoxyController.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        disposableManager.clear()
        epoxyController.cancelPendingModelBuild()
        super.onDestroyView()
    }
}
