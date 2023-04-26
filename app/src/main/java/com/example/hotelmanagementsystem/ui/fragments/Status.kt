package com.example.hotelmanagementsystem.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.hotelmanagementsystem.ui.tabs.PaymentStatusTab
import com.example.hotelmanagementsystem.adapter.PagerAdapter
import com.example.hotelmanagementsystem.R
import com.example.hotelmanagementsystem.ui.tabs.EmployeeStatusTab
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class Status : Fragment(R.layout.fragment_status) {

    private lateinit var tabLayout: TabLayout
    private lateinit var pager2: ViewPager2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabLayout = view.findViewById(R.id.tablayout)
        pager2 = view.findViewById(R.id.pager)

        initComponents()
    }

    private fun initComponents() {
        val oneFragment = EmployeeStatusTab()
        val twoFragment = PaymentStatusTab()

        val adapter = PagerAdapter(
            childFragmentManager, lifecycle, mutableListOf(
               oneFragment,twoFragment
            )
        )

        pager2.isSaveEnabled = false
        pager2.adapter = adapter
        pager2.currentItem = 2
        TabLayoutMediator(tabLayout, pager2) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.Employee)
                }
                1 ->{
                    tab.text = getString(R.string.Payment)
                }
            }

        }.attach()
    }
}