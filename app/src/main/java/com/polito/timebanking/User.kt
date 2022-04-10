package com.polito.timebanking

data class User(
    var fullName: String,
    var nickName: String,
    var email: String,
    var location: String = "",
    var skills: ArrayList<String> = arrayListOf(),
    var description: String = ""
)