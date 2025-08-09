package com.parithidb.zobazeassignment.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parithidb.zobazeassignment.data.database.entities.ExpenseEntity
import com.parithidb.zobazeassignment.data.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    fun insertExpenses(expenses: List<ExpenseEntity>) {
        viewModelScope.launch {
            expenseRepository.insertExpenses(expenses)
        }
    }

    fun getExpensesByDate(date: LocalDate): LiveData<List<ExpenseEntity>> {
        return expenseRepository.getExpensesByDate(date)
    }

    fun getExpensesFromLastWeek(): LiveData<List<ExpenseEntity>> {
        return expenseRepository.getExpensesFromLastWeek()
    }
}