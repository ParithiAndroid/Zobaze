package com.parithidb.zobazeassignment.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "EXPENSE")
class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("expenseId")
    val expenseId: Int = 0,
    @ColumnInfo("title")
    val title: String,
    @ColumnInfo("amount")
    val amount: Double,
    @ColumnInfo("category")
    val category: String,
    @ColumnInfo("notes")
    val notes: String?,
    @ColumnInfo("receiptUrl")
    val receiptUri: String?, // uri.toString() or null
    @ColumnInfo("timeStamp")
    val timestamp: Long = System.currentTimeMillis()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExpenseEntity

        if (expenseId != other.expenseId) return false
        if (title != other.title) return false
        if (amount != other.amount) return false
        if (category != other.category) return false
        if (notes != other.notes) return false
        if (receiptUri != other.receiptUri) return false
        if (timestamp != other.timestamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = expenseId
        result = 31 * result + title.hashCode()
        result = 31 * result + amount.hashCode()
        result = 31 * result + category.hashCode()
        result = 31 * result + (notes?.hashCode() ?: 0)
        result = 31 * result + (receiptUri?.hashCode() ?: 0)
        result = 31 * result + timestamp.hashCode()
        return result
    }
}
