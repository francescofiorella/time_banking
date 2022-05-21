package com.polito.timebanking.models

data class User(
    val id: Int,
    var fullName: String,
    var nickname: String,
    var email: String,
    var location: String,
    var description: String?,
    var photoPath: String?
)