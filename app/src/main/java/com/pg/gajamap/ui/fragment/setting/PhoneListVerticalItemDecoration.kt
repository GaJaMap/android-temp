package com.pg.gajamap.ui.fragment.setting

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class PhoneListVerticalItemDecoration: RecyclerView.ItemDecoration(){

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val offset = 26
        outRect.bottom = offset

    }
}