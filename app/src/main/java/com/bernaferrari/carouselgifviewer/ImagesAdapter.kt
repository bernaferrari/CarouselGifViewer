package com.bernaferrari.carouselgifviewer

import android.app.Activity
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class ImagesAdapter(
    private val results: List<DictionaryItem>,
    private val activity: Activity,
    private val itemHeight: Int,
    private val cornerRadius: Float
) : RecyclerView.Adapter<ImagesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val view: CardView =
            activity.layoutInflater.inflate(R.layout.gif_item_main_exo, parent, false) as CardView
        val params = ViewGroup.LayoutParams(
            itemHeight,
            itemHeight
        )
        view.layoutParams = params
        view.radius = cornerRadius
        return ImagesViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        val result = results[position]
        val imageUrl = "https://thumbs.gfycat.com/${result.gifId}-mobile.jpg"

        Glide.with(holder.gifView.context)
            .load(imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.gifView)
    }

    override fun getItemId(i: Int): Long = 0

    override fun getItemCount(): Int = results.size
}