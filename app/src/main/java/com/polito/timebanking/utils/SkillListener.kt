package com.polito.timebanking.utils

import com.polito.timebanking.models.Skill

interface SkillListener {
    fun onClickListener(skill: Skill, position: Int)
}