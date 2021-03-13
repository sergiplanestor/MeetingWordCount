package com.revolhope.presentation.feature.dashboard.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.revolhope.domain.feature.word.model.WordModel
import com.revolhope.presentation.library.components.wordview.WordView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardContentAdapter : RecyclerView.Adapter<DashboardContentAdapter.ViewHolder>() {

    val items: MutableList<WordModel> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            WordView(parent.context).apply {
                layoutParams = RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT
                )
            }
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.bind(items[position])
    }

    override fun getItemCount(): Int = items.count()

    fun updateItems(newItems: List<WordModel>) {
        if (newItems.isEmpty()) {
            items.clear()
            notifyDataSetChanged()
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                val diffResult = calculateDiffResult(newItems)
                items.clear()
                items.addAll(newItems)
                diffResult.dispatchUpdatesTo(this@DashboardContentAdapter)
            }
        }
    }

    private suspend fun calculateDiffResult(newData: List<WordModel>): DiffUtil.DiffResult =
        withContext(Dispatchers.IO) {
            DiffUtil.calculateDiff(object : DiffUtil.Callback() {

                override fun getOldListSize(): Int = items.size

                override fun getNewListSize(): Int = newData.size

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                    items[oldItemPosition].id == newData[newItemPosition].id

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean = items[oldItemPosition] == newData[newItemPosition]
            })
        }

    inner class ViewHolder(wordView: WordView) : RecyclerView.ViewHolder(wordView) {
        val view get() = itemView as WordView
    }

}