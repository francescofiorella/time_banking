package com.polito.timebanking.models

data class Chat(
    val id: String? = null,
    val tsid: String? = null,
    val uids: List<String>? = null,
    val timestamp: Long? = null
)

// id: made of
// timeslot id + applicant id

// uids: array of:
// 1) timeslot owner id
// 2) applicant id