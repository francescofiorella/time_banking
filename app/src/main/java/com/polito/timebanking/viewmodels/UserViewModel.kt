package com.polito.timebanking.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.polito.timebanking.models.User

class UserViewModel(application: Application) : AndroidViewModel(application) {
    var user = User(
        "Mario Rossi",
        "m_rossi",
        "mariorossi@example.com",
        "Corso Duca degli Abruzzi, 7",
        description = "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium."
    )
}