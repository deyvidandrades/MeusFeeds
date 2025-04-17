package com.deyvidandrades.meusfeeds.adapters

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.deyvidandrades.meusfeeds.fragments.FragmentoFontes
import com.deyvidandrades.meusfeeds.fragments.FragmentoHome
import com.deyvidandrades.meusfeeds.fragments.FragmentoSalvos


class AdaptadorFragmentos(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    private val arrayFragmentos = arrayOf(
        FragmentoHome(),
        FragmentoFontes(),
        FragmentoSalvos()
    )

    override fun getItemCount() = arrayFragmentos.count()

    override fun createFragment(position: Int): Fragment {
        return arrayFragmentos[position]
    }
}