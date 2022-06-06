package com.polito.timebanking.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
        private val iconIV: ImageView = v.findViewById(R.id.icon)

        fun bind(skill: Skill, action: (v: View) -> Unit) {
            nameTV.text = skill.name
            layout.setOnClickListener(action)
            iconIV.setImageResource(
                when (skill.name) {
                    "Magician" -> R.drawable.ic_cruelty_free
                    "Factotum" -> R.drawable.ic_hardware
                    "Petlover" -> R.drawable.ic_pets
                    "Babysitter" -> R.drawable.ic_child_friendly
                    "Gardener" -> R.drawable.ic_local_florist
                    "Scientist" -> R.drawable.ic_science
                    "Carwasher" -> R.drawable.ic_local_car_wash
                    "Caregiver" -> R.drawable.ic_health_and_safety
                    else -> R.drawable.ic_star
                }
            )
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