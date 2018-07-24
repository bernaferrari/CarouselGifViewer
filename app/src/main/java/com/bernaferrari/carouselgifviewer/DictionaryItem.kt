package com.bernaferrari.carouselgifviewer

import android.support.v4.content.ContextCompat
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.gif_item_search.*

/**
 * A Groupie item for displaying selectable items with a preview
 *
 * @property gifId the id for the gif.
 * @property gifTitle the title for the gif.
 * @property isSelected the property that will show if value is selected or not.
 */
class DictionaryItem(
    val gifId: String,
    val gifTitle: String,
    var isSelected: Boolean = false
) : Item() {

    override fun getLayout() = R.layout.gif_item_search

    override fun unbind(holder: ViewHolder) {
        holder.selected.visibility = View.GONE
        super.unbind(holder)
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val url = "https://thumbs.gfycat.com/$gifId-mobile.jpg"

        viewHolder.run {
            title.text = gifTitle

            Glide.with(this.thumbnail)
                .load(url)
                .transition(withCrossFade())
                .into(this.thumbnail)

            if (isSelected) {
                title.setTextColor(
                    ContextCompat.getColor(
                        viewHolder.containerView.context,
                        R.color.md_blue_A200
                    )
                )

                selected.visibility = View.VISIBLE
            } else {
                title.setTextColor(
                    ContextCompat.getColor(
                        viewHolder.containerView.context,
                        R.color.FontStrong
                    )
                )
            }

            share.visibility = View.GONE
        }
    }
}
