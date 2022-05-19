package com.polito.timebanking.models

data class User(
    var uid: String = "",
    var fullName: String = "",
    var email: String = "",
    var nickname: String? = null,
    var location: String? = null,
    var description: String? = null,
    var photoPath: String? = null,
    var skills: MutableList<Skill>? = mutableListOf()
)