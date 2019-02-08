//package com.bernaferrari.carouselgifviewer.main
//
//import android.graphics.Rect
//import android.view.View
//
//import androidx.recyclerview.widget.RecyclerView
//
// to use RecyclerView without DiscreteScrollView. Currently unstable.
//class ItemDecoration : RecyclerView.ItemDecoration() {
//
//    override fun getItemOffsets(
//        outRect: Rect, view: View, parent: RecyclerView,
//        state: RecyclerView.State
//    ) {
//        val totalWidth = parent.width
//        val viewWidth = view.width
//        val totalPadding = ((totalWidth - viewWidth) / 2*0.9).toInt()
//
//        view.translationY = parent.height / 4f
//
//        //first element
//        if (parent.getChildAdapterPosition(view) == 0) {
//            outRect.set(totalPadding, 0, 0, 0)
//        }
//
//        //last element
//        if (parent.adapter != null && parent.getChildAdapterPosition(view) == parent.adapter!!.itemCount - 1 && parent.adapter!!.itemCount > 1) {
//            outRect.set(0, 0, totalPadding, 0)
//        }
//    }
//}
