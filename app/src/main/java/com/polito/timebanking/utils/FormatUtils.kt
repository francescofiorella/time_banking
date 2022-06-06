package com.polito.timebanking.utils

import java.text.SimpleDateFormat
import java.util.*

fun dateToString(dayOfMonth: Int?, month: Int?, year: Int?): String? {
    var formattedDate: String? = null
    dayOfMonth?.let {
        month?.let {
            year?.let {
                val dayString = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
                val monthString = if (month < 10) "0$month" else month.toString()
                formattedDate = "$dayString/$monthString/$year"
            }
        }
    }

    return formattedDate
}

fun timeToString(hour: Int?, minute: Int?): String? {
    var formattedTime: String? = null
    hour?.let {
        minute?.let {
            val hourString = if (hour < 10) "0$hour" else hour.toString()
            val minuteString = if (minute < 10) "0$minute" else minute.toString()
            formattedTime = "$hourString:$minuteString"
        }
    }
    return formattedTime
}

fun timestampToDateString(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val date = Date(timestamp)
    return dateFormat.format(date)
}

fun timestampToTimeString(timestamp: Long): String {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val date = Date(timestamp)
    return timeFormat.format(date)
}