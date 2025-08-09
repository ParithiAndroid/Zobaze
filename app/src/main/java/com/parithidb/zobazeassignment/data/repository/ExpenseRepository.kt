package com.parithidb.zobazeassignment.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.parithidb.zobazeassignment.data.database.AppDatabase
import com.parithidb.zobazeassignment.data.database.dao.ExpenseDao
import com.parithidb.zobazeassignment.data.database.entities.ExpenseEntity
import java.time.LocalDate
import javax.inject.Inject

class ExpenseRepository @Inject constructor(
    private val context: Context,
    private val database: AppDatabase,
    private val expenseDao: ExpenseDao = database.expenseDao()
) {
    suspend fun insertExpense(expenseEntity: ExpenseEntity) {
        expenseDao.insertExpense(expenseEntity)
    }

    fun getTotalSpentToday(): LiveData<Double?> {
        return expenseDao.getTotalSpentToday()
    }

    fun getExpensesByDate(date: LocalDate): LiveData<List<ExpenseEntity>> {
        return expenseDao.getExpensesByDate(date)
    }

    fun getExpenseById(expenseId: Int): LiveData<ExpenseEntity> {
        return expenseDao.getExpenseById(expenseId)
    }

    suspend fun updateExpenseById(expense: ExpenseEntity) {
        expenseDao.updateExpenseById(expense)
    }

    suspend fun deleteExpenseById(id: Int) {
        expenseDao.deleteExpenseById(id)
    }

}