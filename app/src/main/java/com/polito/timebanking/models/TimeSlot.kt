package com.polito.timebanking.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.polito.timebanking.utils.dateToString
import com.polito.timebanking.utils.timeToString

@Entity(tableName = "timeSlots")
class TimeSlot {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var title: String = ""
    var description: String = ""
    var year: Int = 0
    var month: Int = 0
    var day: Int = 0
    var hour: Int = 99
    var minute: Int = 99
    var duration: String = ""
    var location: String = ""

    fun getDate(): String = dateToString(day, month, year).takeIf { it != "00/00/0" } ?: ""
    fun getTime(): String = timeToString(hour, minute).takeIf { it != "99:99" } ?: ""

    override fun toString(): String = "{ id: $id, title: \"$title\" }"

    fun isEmpty() : Boolean {
        return title == ""
    }
}
