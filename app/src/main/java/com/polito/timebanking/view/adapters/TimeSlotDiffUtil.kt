package com.polito.timebanking.view.adapters

import androidx.recyclerview.widget.DiffUtil
import com.polito.timebanking.models.TimeSlot

class TimeSlotDiffUtil(
    private val oldList: List<TimeSlot>,
    private val newList: List<TimeSlot>
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