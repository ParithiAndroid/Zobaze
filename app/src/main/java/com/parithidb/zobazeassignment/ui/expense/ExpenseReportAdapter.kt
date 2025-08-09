package com.parithidb.zobazeassignment.ui.expense

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.animation.AnimationUtils.lerp
import com.google.android.material.carousel.MaskableFrameLayout
import com.parithidb.zobazeassignment.data.database.model.Total
import com.parithidb.zobazeassignment.databinding.ItemDailyTotalBinding

class ExpenseReportAdapter :
    ListAdapter<Total, ExpenseReportAdapter.ViewHolder>(ExpenseDiffCallback()) {

    class ExpenseDiffCallback : DiffUtil.ItemCallback<Total>() {
        override fun areItemsTheSame(oldItem: Total, newItem: Total): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Total, newItem: Total): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemDailyTotalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.binding.tvDate.text = item.title
            holder.binding.tvAmount.text = String.format("₹ %.2f", item.totalAmount)
        }

        // Assuming your root layout is MaskableFrameLayout
        val maskableLayout = holder.itemView as? MaskableFrameLayout
        maskableLayout?.setOnMaskChangedListener { maskRect ->
            // Animate your text views inside the ViewHolder
            holder.binding.tvDate.translationX = maskRect.left.toFloat()

            // lerp from 1 to 0 alpha as mask moves from 0 to 80 px left
            holder.binding.tvDate.alpha = lerp(1f, 0f, 0f, 80f, maskRect.left.toFloat())

            holder.binding.tvAmount.translationX = maskRect.left.toFloat()
            holder.binding.tvAmount.alpha = lerp(1f, 0f, 0f, 80f, maskRect.left.toFloat())
        }
    }

    inner class ViewHolder(val binding: ItemDailyTotalBinding) :
        RecyclerView.ViewHolder(binding.root)
}