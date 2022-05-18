package com.polito.timebanking.models

data class User(
    var uid: String,
    var fullName: String,
    var nickname: String,
    var email: String,
    var location: String? = null,
    var description: String? = null,
    var photoPath: String? = null
)