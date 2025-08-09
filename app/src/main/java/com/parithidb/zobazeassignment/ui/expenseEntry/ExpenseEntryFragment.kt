package com.parithidb.zobazeassignment.ui.expenseEntry

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.parithidb.zobazeassignment.R
import com.parithidb.zobazeassignment.data.database.entities.ExpenseEntity
import com.parithidb.zobazeassignment.databinding.FragmentExpenseEntryBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class ExpenseEntryFragment : Fragment() {

    private lateinit var binding: FragmentExpenseEntryBinding
    private val viewModel: ExpenseEntryViewModel by viewModels()
    private var currentReceiptUri: String? = null
    private val categories = listOf("Staff", "Travel", "Food", "Utility")
    private var expenseId: Int? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExpenseEntryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner
        ) {
            findNavController().navigateUp()
        }

        handleAddOrEdit()

        viewModel.getTotalSpentToday().observe(viewLifecycleOwner, this::handleTotalExpenseToday)

        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        binding.actvCategory.setAdapter(adapter)

        binding.actvCategory.threshold = 0
        binding.actvCategory.setOnClickListener {
            binding.actvCategory.showDropDown()
        }

        binding.actvCategory.setOnItemClickListener { _, _, position, _ ->
            binding.actvCategory.setText(categories[position], false)
        }


        // Notes counter
        binding.etNotes.addTextChangedListener { s ->
            val count = s?.length ?: 0
            binding.tilNotes.helperText = "$count/100"
        }

        // Buttons
        binding.btnUploadReceipt.setOnClickListener {
            pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnCaptureReceipt.setOnClickListener {
            captureImageLauncher.launch(null)
        }


        binding.btnSubmit.setOnClickListener { handleSubmission() }

        binding.btnUpdate.setOnClickListener {
            val updatedExpense = ExpenseEntity(
                expenseId = expenseId!!,
                title = binding.etTitle.text.toString(),
                amount = binding.etAmount.text.toString().toDouble(),
                category = binding.actvCategory.text.toString(),
                notes = binding.etNotes.text.toString(),
                receiptUri = currentReceiptUri
            )
            viewModel.updateExpenseById(updatedExpense)
            Toast.makeText(requireContext(), "Updated", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }

        binding.btnDelete.setOnClickListener {
            viewModel.deleteExpenseById(expenseId!!)
            Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }

    }

    private fun handleAddOrEdit() {
        val args = arguments?.let { ExpenseEntryFragmentArgs.fromBundle(it) }
        expenseId = args?.expenseId

        if (expenseId != null && expenseId != -1) {
            viewModel.getExpenseById().observe(viewLifecycleOwner) { expense ->
                expense?.let {
                    binding.etTitle.setText(it.title)
                    binding.etAmount.setText(it.amount.toString())
                    binding.actvCategory.setText(it.category, false)
                    binding.etNotes.setText(it.notes ?: "")
                    currentReceiptUri = it.receiptUri
                    it.receiptUri?.let { uri ->
                        binding.ivReceipt.setImageURI(Uri.parse(uri))
                    }

                    // Show update/delete buttons
                    binding.btnSubmit.visibility = View.GONE
                    binding.btnUpdate.visibility = View.VISIBLE
                    binding.btnDelete.visibility = View.VISIBLE
                }
            }
        } else {
            // New entry
            binding.btnSubmit.visibility = View.VISIBLE
            binding.btnUpdate.visibility = View.GONE
            binding.btnDelete.visibility = View.GONE
        }

    }

    private fun handleTotalExpenseToday(amount: Double?) {
        if (amount != null) {
            binding.tvTotalAmount.text = "₹ $amount"
        } else {
            binding.tvTotalAmount.text = "₹ 0.00"
        }
    }

    private fun handleSubmission() {
        val title = binding.etTitle.text?.toString()?.trim().orEmpty()
        val amountStr = binding.etAmount.text?.toString()?.trim().orEmpty()
        val notes = binding.etNotes.text?.toString()?.trim().orEmpty()
        val category = binding.actvCategory.text?.toString()?.trim().orEmpty()

        if (category !in categories) {
            binding.tilCategory.error = "Please select from list"
            return
        } else binding.tilCategory.error = null

        if (title.isEmpty()) {
            binding.tilTitle.error = "Please enter title"
            return
        } else binding.tilTitle.error = null

        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0.00) {
            binding.tilAmount.error = "Enter valid amount"
            return
        } else binding.tilAmount.error = null

        val chosenCategory = category.ifBlank { "Other" }

        val expense = ExpenseEntity(
            title = title,
            amount = amount,
            category = chosenCategory,
            notes = notes.ifBlank { null },
            receiptUri = currentReceiptUri
        )

        viewModel.insertExpense(expense)
        animateNewEntry(expense.amount)

        Toast.makeText(requireContext(), "Expense added", Toast.LENGTH_SHORT).show()

        // Clear form
        clearForm()
    }

    private fun animateNewEntry(expense: Double) {
        binding.tvAmountAnimation.apply {
            text = "+₹${expense}"
            alpha = 0f
            translationY = 0f
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .translationY(-150f)
                .setDuration(800)
                .withEndAction { visibility = View.GONE }
                .start()
        }
    }


    private fun clearForm() {
        binding.etTitle.text?.clear()
        binding.etAmount.text?.clear()
        binding.actvCategory.setText("")
        binding.etNotes.text?.clear()
        binding.ivReceipt.setImageResource(R.drawable.ic_image_24)
        currentReceiptUri = null
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            currentReceiptUri = it.toString()
            binding.ivReceipt.setImageURI(it)
        }
    }

    private val captureImageLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            val uri = saveBitmapToCache(it)
            currentReceiptUri = uri.toString()
            binding.ivReceipt.setImageBitmap(it)
        }
    }

    private fun saveBitmapToCache(bitmap: Bitmap): Uri {
        val file = File(requireContext().cacheDir, "receipt_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
        return FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            file
        )
    }



}