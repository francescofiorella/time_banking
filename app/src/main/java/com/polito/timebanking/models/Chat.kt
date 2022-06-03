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
// 0) timeslot owner id
// 1) applicant id