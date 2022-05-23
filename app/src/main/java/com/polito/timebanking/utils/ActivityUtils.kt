package com.polito.timebanking.utils

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

fun Activity.snackBar(
    message: String,
    anchor: View? = null,
    length: Int = BaseTransientBottomBar.LENGTH_SHORT,
    isDismissible: Boolean = false
): Snackbar {
    val root = findViewById<ViewGroup>(android.R.id.content)

    Snackbar.make(root, message, length).also { snackBar ->
        anchor?.let {
            snackBar.setAnchorView(it)
        }
        if (isDismissible) {
            snackBar.setAction("DISMISS") {}
        }
        snackBar.show()

        return snackBar
    }
}