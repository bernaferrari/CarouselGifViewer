package com.bernaferrari.carouselgifviewer

import android.graphics.Typeface.BOLD
import android.text.style.StyleSpan
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.buildSpannedString
import androidx.core.text.set
import androidx.core.text.toSpannable
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter
import com.bernaferrari.base.misc.normalizeString
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

@BindingAdapter("fetchGifPreviewFromGlide")
fun loadGifFromUrl(view: ImageView, id: String?) {
    glideLoader(
        view,
        id != null,
        "https://thumbs.gfycat.com/$id-mobile.jpg"
    )
}

fun glideLoader(view: ImageView, notNull: Boolean, url: String) {
    if (notNull) {
        Glide.with(view)
            .load(url)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    } else {
        Glide.with(view).clear(view)
    }
}

@BindingAdapter("boxedGifSize")
fun customBoxSize(view: FrameLayout, width: Int) {
    view.updateLayoutParams {
        this.height = width
        this.width = width
    }
}

@BindingAdapter("query", "title")
fun customQuery(view: TextView, query: String, text: String) {

    val start = text.normalizeString().indexOf(query.normalizeString())
    if (start > -1 && query.isNotBlank()) {
        val className = text.toSpannable()
        className[start, start + query.length] = StyleSpan(BOLD)
        view.text = buildSpannedString { append(className) }
    } else {
        view.text = text
    }
}
