package com.bernaferrari.carouselgifviewer

import android.content.res.Resources
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter
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

@BindingAdapter("isItemVisible")
fun isItemVisible(view: View, isVisible: Boolean) {
    view.isVisible = isVisible
}

@BindingAdapter("srcRes")
fun imageViewSrcRes(view: ImageView, drawableRes: Int) {
    if (drawableRes != 0) {
        view.setImageResource(drawableRes)
    } else {
        view.setImageDrawable(null)
    }
}

@BindingAdapter("visibilityFromTotalCount")
fun visibilityFromTotalCount(view: Button, totalCount: Int) {
    view.visibility = if (totalCount > 6) View.VISIBLE else View.GONE
}

fun dp(value: Int, resources: Resources) = (resources.displayMetrics.density * value).toInt()
