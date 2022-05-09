package com.polito.timebanking.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["email"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var fullName: String,
    var nickname: String,
    var email: String,
    var location: String,
    var description: String?,
    var photoPath: String?
)