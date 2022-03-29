package com.polito.timebanking

data class User(
    var fullName: String,
    var nickName: String,
    var email: String,
    var location: String? = null,
    var skills: String? = null,
    var description: String? = null
)
