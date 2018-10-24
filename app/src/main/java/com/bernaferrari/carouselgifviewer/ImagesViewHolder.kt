package com.bernaferrari.carouselgifviewer

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

internal class ImagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val gifView: ImageView = itemView.findViewById(R.id.image)
}