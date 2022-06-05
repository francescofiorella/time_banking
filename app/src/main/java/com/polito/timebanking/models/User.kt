package com.polito.timebanking.models

data class User(
    var uid: String? = null,
    var fullName: String? = null,
    var email: String? = null,
    var timeCredit: Int? = null,
    var nickname: String? = null,
    var location: String? = null,
    var description: String? = null,
    var photoUrl: String? = null,
    var skills: List<Skill>? = emptyList()
)