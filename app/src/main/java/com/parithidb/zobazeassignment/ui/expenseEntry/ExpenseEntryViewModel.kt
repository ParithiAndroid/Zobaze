package com.parithidb.zobazeassignment.ui.expenseEntry

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parithidb.zobazeassignment.data.database.entities.ExpenseEntity
import com.parithidb.zobazeassignment.data.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseEntryViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val expenseId: Int = savedStateHandle["expenseId"] ?: -1

    fun insertExpense(expenseEntity: ExpenseEntity) {
        viewModelScope.launch {
            expenseRepository.insertExpense(expenseEntity)
        }
    }

    fun getTotalSpentToday(): LiveData<Double?> {
        return expenseRepository.getTotalSpentToday()
    }

    fun getExpenseById(): LiveData<ExpenseEntity> {
        return expenseRepository.getExpenseById(expenseId)
    }

    fun updateExpenseById(expenseEntity: ExpenseEntity) {
        viewModelScope.launch {
            expenseRepository.updateExpenseById(expenseEntity)
        }
    }

    fun deleteExpenseById(id: Int) {
        viewModelScope.launch {
            expenseRepository.deleteExpenseById(id)
        }
    }

}