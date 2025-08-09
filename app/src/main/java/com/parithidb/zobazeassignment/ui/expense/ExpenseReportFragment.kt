package com.parithidb.zobazeassignment.ui.expense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.HeroCarouselStrategy
import com.google.android.material.carousel.UncontainedCarouselStrategy
import com.parithidb.zobazeassignment.data.database.entities.ExpenseEntity
import com.parithidb.zobazeassignment.data.database.model.Total
import com.parithidb.zobazeassignment.databinding.FragmentExpenseReportBinding
import com.parithidb.zobazeassignment.ui.dashboard.DashboardViewModel
import com.parithidb.zobazeassignment.util.SharedPrefHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.random.Random

@AndroidEntryPoint
class ExpenseReportFragment : Fragment() {
    private lateinit var binding: FragmentExpenseReportBinding
    private val viewmodel: DashboardViewModel by viewModels()
    private lateinit var dailyTotalsAdapter: ExpenseReportAdapter
    private lateinit var categoryTotalsAdapter: ExpenseReportAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExpenseReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref: SharedPrefHelper = SharedPrefHelper(requireContext())

        if (!sharedPref.hasInsertedMockData()) {
            val mockData = generateMockExpensesLast7Days()
            viewmodel.insertExpenses(mockData)
            sharedPref.setInsertedMockData()
        }

        viewmodel.getExpensesFromLastWeek()
            .observe(viewLifecycleOwner, this::handleExpensesFromLastWeek)
    }

    private fun handleExpensesFromLastWeek(expenseEntities: List<ExpenseEntity>?) {
        if (expenseEntities.isNullOrEmpty()) {
            binding.rvDailyTotals.visibility = View.GONE
            binding.rvCategoryTotals.visibility = View.GONE
            binding.chartContainer.visibility = View.GONE
            binding.exportButtonContainer.visibility = View.GONE
            binding.tvNoDailyTotals.visibility = View.VISIBLE
            binding.tvNoCategoryTotals.visibility = View.VISIBLE
            binding.tvNoChart.visibility = View.VISIBLE

            return
        }

        binding.rvDailyTotals.visibility = View.VISIBLE
        binding.rvCategoryTotals.visibility = View.VISIBLE
        binding.chartContainer.visibility = View.VISIBLE
        binding.exportButtonContainer.visibility = View.VISIBLE
        binding.tvNoDailyTotals.visibility = View.GONE
        binding.tvNoCategoryTotals.visibility = View.GONE
        binding.tvNoChart.visibility = View.GONE

        // Process data
        val dailyTotals = calculateDailyTotals(expenseEntities)
        val categoryTotals = calculateCategoryTotals(expenseEntities)

        val snapHelper = CarouselSnapHelper()

        dailyTotalsAdapter = ExpenseReportAdapter()
        binding.rvDailyTotals.layoutManager = CarouselLayoutManager(UncontainedCarouselStrategy())

        snapHelper.attachToRecyclerView(binding.rvDailyTotals)
        binding.rvDailyTotals.adapter = dailyTotalsAdapter
        dailyTotalsAdapter.submitList(dailyTotals)

        categoryTotalsAdapter = ExpenseReportAdapter()
        binding.rvCategoryTotals.layoutManager = CarouselLayoutManager(UncontainedCarouselStrategy())
        snapHelper.attachToRecyclerView(binding.rvCategoryTotals)
        binding.rvCategoryTotals.adapter = categoryTotalsAdapter
        categoryTotalsAdapter.submitList(categoryTotals)

    }

    private fun calculateDailyTotals(expenses: List<ExpenseEntity>): List<Total> {
        return expenses.groupBy {
            // Format timestamp to yyyy-MM-dd string for grouping
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            sdf.format(Date(it.timestamp))
        }.map { (date, list) ->
            Total(date, list.sumOf { it.amount })
        }.sortedBy { it.title }
    }

    private fun calculateCategoryTotals(expenses: List<ExpenseEntity>): List<Total> {
        return expenses.groupBy { it.category }
            .map { (category, list) -> Total(category, list.sumOf { it.amount }) }
            .sortedByDescending { it.totalAmount }
    }

    private fun generateMockExpensesLast7Days(): List<ExpenseEntity> {
        val calendar = Calendar.getInstance()
        calendar.time = Date() // today

        val categories = listOf("Food", "Transport", "Shopping", "Bills", "Entertainment")

        val random = Random(System.currentTimeMillis())

        val expenses = mutableListOf<ExpenseEntity>()

        for (i in 0 until 7) {
            val dateMillis = calendar.timeInMillis

            // Generate random 1-3 expenses per day
            val expenseCount = random.nextInt(1, 4)
            repeat(expenseCount) { index ->
                val category = categories.random(random)
                val amount = String.format("%.2f", random.nextDouble(10.0, 500.0)).toDouble()
                val title = "Mock Expense $i-$index"
                val notes = "Some notes"

                expenses.add(
                    ExpenseEntity(
                        expenseId = 0,
                        title = title,
                        amount = amount,
                        category = category,
                        notes = notes,
                        receiptUri = null,
                        timestamp = dateMillis
                    )
                )
            }

            calendar.add(Calendar.DATE, -1) // previous day
        }

        return expenses
    }

}