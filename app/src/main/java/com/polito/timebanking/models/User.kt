package com.polito.timebanking.models

data class User(
    var fullName: String,
    var nickname: String,
    var email: String,
    var location: String = "",
    var skills: ArrayList<String> = arrayListOf(),
    var description: String = ""
)