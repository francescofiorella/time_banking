package com.polito.timebanking.models

data class User(
    var uid: String = "",
    var fullName: String = "",
    var email: String = "",
    var nickname: String? = null,
    var location: String? = null,
    var description: String? = null,
    var photoUrl: String? = null,
    var skills: List<Skill>? = emptyList()
)