package com.polito.timebanking

data class User(
    var fullName: String,
    var nickName: String,
    var email: String,
    var location: String? = null,
    var skills : ArrayList<String>,
    var description: String? = null) {
    constructor(
        fullName: String,
        nickName: String,
        email: String,
        location: String?,
        Description: String?
    ) : this(fullName, nickName, email, location, arrayListOf<String>(), Description)
}



