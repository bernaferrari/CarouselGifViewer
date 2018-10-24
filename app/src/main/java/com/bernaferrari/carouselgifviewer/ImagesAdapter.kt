package com.bernaferrari.carouselgifviewer

import android.app.Activity
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.card.MaterialCardView

internal class ImagesAdapter(
    private val results: List<DictionaryItem>,
    private val activity: Activity,
    private val itemSize: Int,
    private val cornerRadius: Float
) : RecyclerView.Adapter<ImagesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val view =
            activity.layoutInflater.inflate(R.layout.gif_item_main_exo, parent, false)

        view.updateLayoutParams {
            this.height = itemSize
            this.width = itemSize
        }
        (view as? MaterialCardView)?.radius = cornerRadius
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