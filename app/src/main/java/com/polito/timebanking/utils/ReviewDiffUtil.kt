package com.polito.timebanking.utils

import androidx.recyclerview.widget.DiffUtil
import com.polito.timebanking.models.Feedback

class ReviewDiffUtil(
    private val oldList: List<Feedback>,
    private val newList: List<Feedback>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}