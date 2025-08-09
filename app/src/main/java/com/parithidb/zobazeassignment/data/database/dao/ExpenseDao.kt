package com.parithidb.zobazeassignment.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.parithidb.zobazeassignment.data.database.entities.ExpenseEntity
import java.time.LocalDate

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expenseEntity: ExpenseEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExpenses(expenses: List<ExpenseEntity>)

    @Query("SELECT SUM(amount) FROM EXPENSE WHERE date(timestamp / 1000, 'unixepoch', 'localtime') = date('now', 'localtime')")
    fun getTotalSpentToday(): LiveData<Double?>

    // Get expenses for a specific date
    @Query("SELECT * FROM EXPENSE WHERE date(timestamp / 1000, 'unixepoch', 'localtime') = :date ORDER BY timestamp DESC")
    fun getExpensesByDate(date: LocalDate): LiveData<List<ExpenseEntity>>

    @Query("SELECT * FROM EXPENSE WHERE expenseId = :id")
    fun getExpenseById(id: Int): LiveData<ExpenseEntity>

    @Update
    suspend fun updateExpenseById(expense: ExpenseEntity)

    @Query("DELETE FROM expense WHERE expenseId = :id")
    suspend fun deleteExpenseById(id: Int)

    @Query("SELECT * FROM EXPENSE WHERE timeStamp >= :fromTimestamp ORDER BY timeStamp DESC")
    fun getExpensesFromLastWeek(fromTimestamp: Long = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L): LiveData<List<ExpenseEntity>>

}