package com.polito.timebanking.utils

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

fun Activity.snackBar(message: String, anchor: View? = null) : Snackbar {
    val root = findViewById<ViewGroup>(android.R.id.content).rootView

    Snackbar.make(root, message, BaseTransientBottomBar.LENGTH_SHORT).also { snackBar ->
        anchor?.let {
            snackBar.setAnchorView(it)
        }
        snackBar.show()

        return snackBar
    }
}