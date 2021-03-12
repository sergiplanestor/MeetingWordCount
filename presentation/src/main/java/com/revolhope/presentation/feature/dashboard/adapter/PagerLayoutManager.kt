package com.revolhope.presentation.feature.dashboard.adapter

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PagerLayoutManager @JvmOverloads constructor(
    context: Context,
    orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false,
    private val onLastElementVisible: () -> Unit,
) : LinearLayoutManager(context, orientation, reverseLayout) {

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (state == RecyclerView.SCROLL_STATE_IDLE && findLastCompletelyVisibleItemPosition() == itemCount - 1) {
            onLastElementVisible.invoke()
        }
    }
}