package com.polito.timebanking.models

import com.polito.timebanking.utils.dateToString
import com.polito.timebanking.utils.timeToString

data class TimeSlot(
    var title: String? = null,
    var description: String? = null,
    var year: Int?= null,
    var month: Int? = null,
    var day: Int? = null,
    var hour: Int? = null,
    var minute: Int?= null,
    var duration: String? = null,
    var location: String? = null
) {
    fun getDate(): String? = dateToString(day, month, year)
    fun getTime(): String? = timeToString(hour, minute)
}
