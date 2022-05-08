package com.polito.timebanking.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    primaryKeys = ["userId", "skillId"],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"]
        ),
        ForeignKey(
            entity = Skill::class,
            parentColumns = ["id"],
            childColumns = ["skillId"]
        )
    ],
    indices = [Index(value = ["skillId"])]
)
data class UserSkill(
    val userId: Long,
    val skillId: Long
)