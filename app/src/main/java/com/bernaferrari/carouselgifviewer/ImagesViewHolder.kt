package com.bernaferrari.carouselgifviewer

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView

internal class ImagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val gifView: ImageView = itemView.findViewById(R.id.image)
}