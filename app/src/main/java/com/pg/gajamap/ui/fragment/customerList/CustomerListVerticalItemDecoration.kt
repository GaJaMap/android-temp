package com.pg.gajamap.ui.fragment.customerList

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class CustomerListVerticalItemDecoration: RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val offset = 45
        outRect.bottom = offset

    }
}