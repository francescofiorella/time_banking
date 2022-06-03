package com.polito.timebanking.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.polito.timebanking.R
import com.polito.timebanking.models.Skill

class SkillAdapter(
    private val data: List<Skill>,
    private val listener: SkillListener
) : RecyclerView.Adapter<SkillAdapter.SkillViewHolder>() {

    class SkillViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val layout: MaterialCardView = v.findViewById(R.id.card_layout)
        private val nameTV: TextView = v.findViewById(R.id.name_tv)

        fun bind(skill: Skill, action: (v: View) -> Unit) {
            nameTV.text = skill.name
            layout.setOnClickListener(action)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val vg = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.layout_skill_item, parent, false)
        return SkillViewHolder(vg)
    }

    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
        val skill = data[position]
        holder.bind(skill) { listener.onClickListener(skill, position) }
    }

    override fun getItemCount(): Int = data.size
}