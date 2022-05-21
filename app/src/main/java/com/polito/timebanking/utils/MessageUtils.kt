package com.polito.timebanking.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun showSnackbar(view: View, message: String) {
    Snackbar
        .make(
            view,
            message,
            Snackbar.LENGTH_LONG
        )
        .setAction("DISMISS") {}
        .show()
}