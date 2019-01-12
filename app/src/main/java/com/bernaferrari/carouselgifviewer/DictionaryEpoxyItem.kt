package com.bernaferrari.carouselgifviewer

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.TextProp
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade

/**
 * A Groupie item for displaying selectable items with a preview
 *
 * @property gifId the id for the gif.
 * @property gifTitle the title for the gif.
 * @property isSelected the property that will show if value is selected or not.
 */

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class DictionaryEpoxyItem @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val titleView: TextView
    private val thumbnail: ImageView

    private val selected: TextView
//    private val share: ImageView

    init {
        inflate(context, R.layout.view_holder_gif_listable, this)
        titleView = findViewById(R.id.title)
        thumbnail = findViewById(R.id.thumbnail)
        selected = findViewById(R.id.selected)
//        share = findViewById(R.id.share)
    }


    @ModelProp
    fun setImage(gifId: String) {
        val url = "https://thumbs.gfycat.com/$gifId-mobile.jpg"

        Glide.with(thumbnail)
            .load(url)
            .transition(withCrossFade())
            .into(thumbnail)
    }

    @ModelProp
    fun setItemSelected(isItemSelected: Boolean) {

        if (isItemSelected) {
            val blueColor = ContextCompat.getColor(titleView.context, R.color.md_blue_A200)
            titleView.setTextColor(blueColor)
            selected.visibility = View.VISIBLE
        } else {
            val grayColor = ContextCompat.getColor(titleView.context, R.color.FontStrong)
            titleView.setTextColor(grayColor)
        }
    }

    @TextProp
    fun setTitle(title: CharSequence) {
        titleView.text = title
    }

    @CallbackProp
    fun setClickListener(clickListener: OnClickListener?) {
        setOnClickListener(clickListener)
    }
}
