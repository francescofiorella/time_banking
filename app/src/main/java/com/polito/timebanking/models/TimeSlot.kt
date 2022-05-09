package com.polito.timebanking.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.polito.timebanking.utils.dateToString
import com.polito.timebanking.utils.timeToString

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"]
        )
    ]
)
data class TimeSlot(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var title: String,
    var description: String,
    var year: Int,
    var month: Int,
    var day: Int,
    var hour: Int,
    var minute: Int,
    var duration: String,
    var location: String,
    @ColumnInfo(index = true)
    var userId: Int = 0
) {
    constructor(userId: Int) : this(
        0,
        "",
        "",
        0,
        0,
        0,
        99,
        99,
        "",
        "",
        userId
    )

    fun getDate(): String = dateToString(day, month, year).takeIf { it != "00/00/0" } ?: ""
    fun getTime(): String = timeToString(hour, minute).takeIf { it != "99:99" } ?: ""

    override fun toString(): String = "{ id: $id, title: \"$title\" }"

    fun isEmpty(): Boolean {
        return title == ""
    }
}
