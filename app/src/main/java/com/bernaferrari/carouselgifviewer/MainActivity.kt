package com.bernaferrari.carouselgifviewer

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bernaferrari.carouselgifviewer.extensions.*
import com.bernaferrari.carouselgifviewer.handler.OpenBrowserItemHandler
import com.bernaferrari.carouselgifviewer.handler.ShareItemHandler
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxrelay2.BehaviorRelay
import com.orhanobut.logger.Logger
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import com.yarolegovich.discretescrollview.DiscreteScrollView
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.gif_activity.*
import java.util.concurrent.TimeUnit

/**
 * The MainActivity which will put together everything on this sample to display the
 * Carousel Gif Viewer.
 */
class MainActivity : AppCompatActivity(),
    DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder> {

    private lateinit var discreteScrollAdapter: RecyclerView.Adapter<*>
    private val sideListAdapter = GroupAdapter<ViewHolder>()
    private var isVideoShown = true
    private var previousAdapterPosition = 0

    private var progressDisposable: Disposable? = null
    private var disposables = CompositeDisposable()

    private val cardCornerRadius by lazy { 8 * resources.displayMetrics.density }

    private val items = listOf(
        DictionaryItem("DeliriousBitterIcelandicsheepdog", "Amém"),
        DictionaryItem("OfficialSpotlessCurassow", "Ídolo"),
        DictionaryItem("DistortedKindlyArmedcrab", "Romanos"),
        DictionaryItem("EdibleFarAntipodesgreenparakeet", "Samaritano"),
        DictionaryItem("MellowCompleteArachnid", "Gênesis"),
        DictionaryItem("FreshOnlyDarklingbeetle", "João"),
        DictionaryItem("MadeupShallowGallowaycow", "Pecador"),
        DictionaryItem("LivelyCarefulFalcon", "Êxodo"),
        DictionaryItem("WholeGenerousGrunion", "Salomão"),
        DictionaryItem("PlumpGivingBandicoot", "Rute")
    )

    private val mySection = Section(items)

    override fun onCurrentItemChanged(viewHolder: RecyclerView.ViewHolder?, adapterPosition: Int) {
        // starts the timer to show a loading bar in case the video is not loaded fast
        showProgressIfNecessary()
        video_view?.setVideoURI("https://thumbs.gfycat.com/${items[adapterPosition].gifId}-mobile.mp4".toUri())
        titlecontent.text = items[adapterPosition].gifTitle

        // deselects currently selected item
        items[previousAdapterPosition].isSelected = false
        sideListAdapter.notifyItemChanged(previousAdapterPosition)

        // scroll to +1 or -1 the index, so user can still see the next/previous item
        filters.scrollToIndex(previousAdapterPosition, adapterPosition, items.size)
        previousAdapterPosition = adapterPosition

        // selects new item at adapterPosition
        items[adapterPosition].isSelected = true
        sideListAdapter.notifyItemChanged(adapterPosition)
        mySection.notifyChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gif_activity)

        share_content.setCompoundDrawablesWithIntrinsicBounds(
            null,
            ContextCompat.getDrawable(this, R.drawable.ic_share),
            null,
            null
        )

        sharecontent.setOnClickListener { drawer.openDrawer(GravityCompat.END) }

        info.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }

        setUpShareContent()
        setUpVideoView()
        setUpRecyclerView()

        drawer.setBackgroundColor(ContextCompat.getColor(this, R.color.md_blue_500))

        val selector = MutableLiveData<String>()
        setDrawer(selector)
        selector.observe(this, Observer { liveId ->
            Logger.d("smooth scroll to.. " + items.indexOfFirst { it.gifId == liveId })
            val nextPosition = items.indexOfFirst { it.gifId == liveId }
            drawer.closeDrawer(GravityCompat.END)
            item_picker.smoothScrollToPosition(nextPosition) // position becomes selected with animated scroll
        })
    }

    private fun setUpShareContent() {
        share_content.setOnClickListener {
            if (items.size > previousAdapterPosition) {
                ShareItemHandler(
                    this,
                    "https://gfycat.com/gifs/detail/${items[previousAdapterPosition].gifId}"
                ).invoke()
            }
        }

        share_content.setOnLongClickListener {
            if (items.size > previousAdapterPosition) {
                OpenBrowserItemHandler(
                    this,
                    "https://gfycat.com/gifs/${items[previousAdapterPosition].gifId}"
                ).invoke()
            }
            true
        }
    }

    private fun setUpRecyclerView() {
        val windowDimensions = Point()
        windowManager.defaultDisplay.getSize(windowDimensions)
        val itemHeight = Math.round(Math.min(windowDimensions.y, windowDimensions.x) * 0.7f)

        FrameLayout.LayoutParams(itemHeight, itemHeight).let { params ->
            params.gravity = Gravity.CENTER
            card.layoutParams = params
            card.radius = cardCornerRadius
        }

        discreteScrollAdapter = ImagesAdapter(
            results = items,
            activity = this,
            itemSize = itemHeight,
            cornerRadius = cardCornerRadius
        )

        item_picker.addOnItemChangedListener(this)

        item_picker.apply {
            this.adapter = discreteScrollAdapter

            this.setItemTransitionTimeMillis(150)

            this.setItemTransformer(
                ScaleTransformer.Builder()
                    .setMinScale(0.8f)
                    .build()
            )

            this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        isVideoShown = true
                    } else {
                        isVideoShown = false
                        card.isVisible = false
                    }
                    super.onScrollStateChanged(recyclerView, newState)
                }
            })
        }
    }

    private fun setUpVideoView() {
        video_view.apply {
            this.setOnPreparedListener {
                Logger.d("video_view.setOnPreparedListener")

                if (isVideoShown) {
                    card.visibility = View.VISIBLE
                    progressDisposable?.dispose()
                    progressBar.isVisible = false
                }

                this.start()
            }

            this.setOnErrorListener {
                Logger.d("video_view.setOnErrorListener")

                Snackbar.make(
                    drawer,
                    context.getString(R.string.gif_error),
                    Snackbar.LENGTH_LONG
                ).show()

                this.setVideoURI(this.videoUri)

                true
            }

            this.setOnCompletionListener {
                Logger.d("video_view.setOnCompletionListener")
                this.restart()
            }
        }
    }

    private fun setDrawer(selector: MutableLiveData<String>) {

        // make sure the input is empty
        queryInput.setText("")
        TooltipCompat.setTooltipText(queryClear, queryClear.contentDescription)
        queryClear.setOnClickListener {
            queryInput.setText("")
        }

        // on a real case scenario, these four lines would be called after data has been loaded.
        progressRound.isVisible = false
        queryInput.hint = resources.getString(R.string.search_gifs_hint_loaded, items.size)
        mySection.update(items)
        discreteScrollAdapter.notifyDataSetChanged()

        // a relay is created, so search can be made very efficient.
        // This was adapted from SDK Search (Jake Wharton)
        val relay = BehaviorRelay.create<String>()

        // when a change is detected, filter will be processed.
        disposables.add(relay
            .distinctUntilChanged()
            .debounce(200, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.newThread())
            .map {
                if (it.isBlank()) {
                    items
                } else {
                    items.filter { item ->
                        item.gifTitle.normalizeString().contains(it.normalizeString())
                    }
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                // Always reset the scroll position to the top when the query changes.
                filters.scrollToPosition(0)
                mySection.update(it)
            }
        )

        queryInput.onTextChanged {
            queryClear.isVisible = it.isNotEmpty()
            relay.accept(it.toString())
        }

        setupHideKeyboardWhenNecessary()

        filters.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = sideListAdapter
        }

        sideListAdapter.add(mySection)
        sideListAdapter.setOnItemClickListener { item, _ ->
            if (item is DictionaryItem) {
                selector.value = item.gifId
            }
        }
    }

    private fun setupHideKeyboardWhenNecessary() {

        val inputMethodManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getSystemService<InputMethodManager>()
        } else {
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return
        } ?: return

        // hide keyboard when user scrolls
        val touchSlop = ViewConfiguration.get(this).scaledTouchSlop
        var totalDy = 0
        filters.onScroll { _, dy ->
            if (dy > 0) {
                totalDy += dy
                if (totalDy >= touchSlop) {
                    totalDy = 0
                    inputMethodManager.hideKeyboard()
                }
            }
        }

        // hide keyboard when user taps enter
        queryInput.onKey {
            if (it.keyCode == KeyEvent.KEYCODE_ENTER) {
                inputMethodManager.hideKeyboard()
                true
            } else {
                false
            }
        }

        // hide keyboard when user taps to go
        queryInput.onEditorAction {
            if (it == EditorInfo.IME_ACTION_GO) {
                inputMethodManager.hideKeyboard()
                true
            } else {
                false
            }
        }

        drawer.onChanged {
            inputMethodManager.hideKeyboard()
        }
    }

    private fun InputMethodManager.hideKeyboard() {
        this.hideSoftInputFromWindow(
            queryInput.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    /*
    This will start a 750ms timer. If the GIF is not loaded in this period, the progress bar will
    be shown, so the user knows it is taking longer than expected but it is still loading.
     */
    private fun showProgressIfNecessary() {
        progressDisposable?.dispose()
        progressDisposable =
                Completable.timer(750, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                    .subscribe {
                        progressBar.visibility = View.VISIBLE
                    }
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        video_view.start()
    }

    override fun onStop() {
        video_view.pause()
        super.onStop()
    }

    override fun onDestroy() {
        progressDisposable?.dispose()
        disposables.clear()
        super.onDestroy()
    }
}
