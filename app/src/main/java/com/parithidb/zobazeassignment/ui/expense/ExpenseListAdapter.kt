package com.parithidb.zobazeassignment.ui.expense

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.parithidb.zobazeassignment.data.database.entities.ExpenseEntity
import com.parithidb.zobazeassignment.databinding.ItemExpensesBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExpenseListAdapter(
    private val onClick: (Int) -> Unit
) : ListAdapter<ExpenseEntity, ExpenseListAdapter.ViewHolder>(ExpenseDiffCallback()) {

    class ExpenseDiffCallback : DiffUtil.ItemCallback<ExpenseEntity>() {
        override fun areItemsTheSame(oldItem: ExpenseEntity, newItem: ExpenseEntity): Boolean {
            return oldItem.expenseId == newItem.expenseId
        }

        override fun areContentsTheSame(oldItem: ExpenseEntity, newItem: ExpenseEntity): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemExpensesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.binding.tvTitle.text = item.title
            holder.binding.tvCategory.text = item.category
            holder.binding.tvAmount.text = "₹ ${item.amount}"

            val dateTime = Date(item.timestamp)
            val formatter = SimpleDateFormat("hh:mm", Locale.getDefault())
            holder.binding.tvTime.text = formatter.format(dateTime)

            holder.binding.root.setOnClickListener {
                onClick(item.expenseId)
            }
        }
    }

    inner class ViewHolder(val binding: ItemExpensesBinding) :
        RecyclerView.ViewHolder(binding.root)
}