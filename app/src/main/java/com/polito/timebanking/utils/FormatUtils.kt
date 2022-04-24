package com.polito.timebanking.utils

fun dateToString(dayOfMonth: Int?, month: Int?, year: Int?): String? {
    var formattedDate: String? = null
    dayOfMonth?.let { day ->
        month?.let { month ->
            year?.let { year ->
                val dayString: String = if (day < 10) {
                    "0$day"
                } else {
                    day.toString()
                }
                val monthString: String = if (month < 10) {
                    "0$month"
                } else {
                    month.toString()
                }
                formattedDate = "$dayString/$monthString/$year"
            }
        }
    }

    return formattedDate
}
