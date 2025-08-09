package com.parithidb.zobazeassignment.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.tabs.TabLayoutMediator
import com.parithidb.zobazeassignment.R
import com.parithidb.zobazeassignment.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment: Fragment() {
    private lateinit var binding: FragmentDashboardBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup ViewPager + Tabs
        val adapter = DashboardViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        val tabTitles = listOf("Report", "Expenses")

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()


        binding.fabExpenseEntry.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_dashboardFragment_to_expenseEntryFragment)
        }
    }
}