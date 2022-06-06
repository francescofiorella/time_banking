package com.polito.timebanking.view.adapters

import com.polito.timebanking.models.Skill

interface SkillListener {
    fun onClickListener(skill: Skill, position: Int)
}