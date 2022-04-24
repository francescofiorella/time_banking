package com.polito.timebanking.utils

import android.view.View
import android.widget.AutoCompleteTextView
import androidx.fragment.app.FragmentActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.sql.Time
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

open class TimePickerButton(
    private val layout: TextInputLayout,
    private val textView: AutoCompleteTextView,
    private val context: FragmentActivity
) {
    companion object {
        private const val TIME_PICKER_TAG: String = "TIME_PICKER"
        private const val TIME_PICKER_TITLE: String = "Seleziona l'ora"
    }

    private val listener = View.OnClickListener {
        val calendar = Calendar.getInstance()
        val builder = MaterialTimePicker.Builder()
        builder.setTimeFormat(TimeFormat.CLOCK_24H)
        if (hour != null && minute != null) {
            builder.setHour(hour!!)
            builder.setMinute(minute!!)
        } else {
            builder.setHour(calendar.get(Calendar.HOUR_OF_DAY))
            builder.setMinute(calendar.get(Calendar.MINUTE))
        }
        builder.setTitleText(TIME_PICKER_TITLE)
        val materialTimePicker = builder.build()

        showTimePicker(materialTimePicker)
    }

    var hour: Int? = null
        set(value) {
            field = value
            textView.setText(timeString)
        }

    var minute: Int? = null
        set(value) {
            field = value
            textView.setText(timeString)
        }

    val timeString: String?
        get() {
            return timeToString(hour, minute)
        }

    init {
        hour = LocalTime.now().hour
        minute = LocalTime.now().minute

        setOnClickListener()
    }

    private fun setOnClickListener() {
        layout.setEndIconOnClickListener(listener)
        textView.setOnClickListener(listener)
    }

    private fun removeOnClickListener() {
        layout.setEndIconOnClickListener(listener)
        textView.setOnClickListener(null)
    }

    private fun showTimePicker(materialTimePicker: MaterialTimePicker) {
        if (!materialTimePicker.isAdded) {
            materialTimePicker.show(context.supportFragmentManager, TIME_PICKER_TAG)
            materialTimePicker.addOnPositiveButtonClickListener {
                hour = materialTimePicker.hour
                minute = materialTimePicker.minute

                onPositiveBtnClickListener()
            }
        }
    }

    open fun onPositiveBtnClickListener() {}
}