package com.polito.timebanking.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Skill(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String
)