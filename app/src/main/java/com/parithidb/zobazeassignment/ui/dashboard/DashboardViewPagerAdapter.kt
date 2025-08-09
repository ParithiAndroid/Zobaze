package com.parithidb.zobazeassignment.ui.dashboard

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.parithidb.zobazeassignment.ui.expense.ExpenseListFragment
import com.parithidb.zobazeassignment.ui.expense.ExpenseReportFragment

class DashboardViewPagerAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    private val fragments: List<Fragment> = listOf(
        ExpenseReportFragment(),
        ExpenseListFragment()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}
