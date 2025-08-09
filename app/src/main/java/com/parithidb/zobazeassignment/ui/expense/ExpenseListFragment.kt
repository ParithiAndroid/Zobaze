package com.parithidb.zobazeassignment.ui.expense

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.parithidb.zobazeassignment.R
import com.parithidb.zobazeassignment.data.database.entities.ExpenseEntity
import com.parithidb.zobazeassignment.databinding.FragmentExpenseListBinding
import com.parithidb.zobazeassignment.ui.dashboard.DashboardFragmentDirections
import com.parithidb.zobazeassignment.ui.dashboard.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class ExpenseListFragment : Fragment() {

    private lateinit var binding: FragmentExpenseListBinding
    private val viewModel: DashboardViewModel by viewModels()
    private val adapter = ExpenseListAdapter(
        onClick = { expense ->
            val action = DashboardFragmentDirections.actionDashboardFragmentToExpenseEntryFragment(expense)
            findNavController().navigate(action)
        }
    )

    private val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault())
    private var selectedDate: LocalDate = LocalDate.now()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExpenseListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etDate.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(
                    selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                )
                .build()

            picker.addOnPositiveButtonClickListener { millis ->
                selectedDate = Instant.ofEpochMilli(millis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                binding.etDate.setText(selectedDate.format(dateFormatter))

                loadExpensesForDate(selectedDate)
            }

            picker.show(parentFragmentManager, "datePicker")
        }

        binding.rvExpenses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvExpenses.adapter = adapter

        // Init with today's date
        binding.etDate.setText(selectedDate.format(dateFormatter))
        loadExpensesForDate(selectedDate)

        binding.toggleGroupBy.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnGroupCategory -> groupByCategory()
                    R.id.btnGroupTime -> groupByTime()
                }
            }
        }


    }

    private fun loadExpensesForDate(date: LocalDate) {
        viewModel.getExpensesByDate(date).observe(viewLifecycleOwner) { expenses ->
            adapter.submitList(expenses)
            updateSummary(expenses)
            toggleEmptyState(expenses.isEmpty())
        }
    }

    private fun updateSummary(expenses: List<ExpenseEntity>) {
        val totalCount = expenses.size
        val totalAmount = expenses.sumOf { it.amount }

        binding.tvTotalCount.text = "Count: $totalCount"
        binding.tvTotalAmount.text = "₹ %.2f".format(totalAmount)
    }

    private fun toggleEmptyState(isEmpty: Boolean) {
        binding.tvEmptyList.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.rvExpenses.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun groupByCategory() {
        viewModel.getExpensesByDate(selectedDate).observe(viewLifecycleOwner) { expenses ->
            val grouped = expenses.sortedWith(compareBy<ExpenseEntity> { it.category }.thenByDescending { it.timestamp })
            adapter.submitList(grouped)
        }
    }

    private fun groupByTime() {
        viewModel.getExpensesByDate(selectedDate).observe(viewLifecycleOwner) { expenses ->
            val sorted = expenses.sortedByDescending { it.timestamp }
            adapter.submitList(sorted)
        }
    }


}