package com.parithidb.zobazeassignment.ui.expense

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.HeroCarouselStrategy
import com.google.android.material.carousel.UncontainedCarouselStrategy
import com.parithidb.zobazeassignment.R
import com.parithidb.zobazeassignment.data.database.entities.ExpenseEntity
import com.parithidb.zobazeassignment.data.database.model.Total
import com.parithidb.zobazeassignment.databinding.FragmentExpenseReportBinding
import com.parithidb.zobazeassignment.ui.dashboard.DashboardViewModel
import com.parithidb.zobazeassignment.util.SharedPrefHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
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
            binding.pieChart.visibility = View.GONE
            binding.exportButtonContainer.visibility = View.GONE
            binding.tvNoDailyTotals.visibility = View.VISIBLE
            binding.tvNoCategoryTotals.visibility = View.VISIBLE
            binding.tvNoChart.visibility = View.VISIBLE

            return
        }

        binding.rvDailyTotals.visibility = View.VISIBLE
        binding.rvCategoryTotals.visibility = View.VISIBLE
        binding.pieChart.visibility = View.VISIBLE
        binding.exportButtonContainer.visibility = View.VISIBLE
        binding.tvNoDailyTotals.visibility = View.GONE
        binding.tvNoCategoryTotals.visibility = View.GONE
        binding.tvNoChart.visibility = View.GONE

        // Process data
        val dailyTotals = calculateDailyTotals(expenseEntities)
        val categoryTotals = calculateCategoryTotals(expenseEntities)
        setupCategorySpendingChart(categoryTotals)

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


        binding.btnExportPdf.setOnClickListener {
            val pdfFile = generatePdfReport(dailyTotals, categoryTotals)
//            pdfFile?.let { shareFile(it, "application/pdf") }
            pdfFile?.let { openPdfInApp(it) }
        }

        binding.btnExportCsv.setOnClickListener {
            val csvFile = generateCsvReport(dailyTotals, categoryTotals)
            csvFile?.let { shareFile(it, "text/csv") }
        }

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

    private fun setupCategorySpendingChart(categoryTotals: List<Total>) {
        val pieChart = binding.pieChart

        // Convert categoryTotals to PieEntries
        val entries = categoryTotals.map { total ->
            PieEntry(total.totalAmount.toFloat(), total.title)
        }

        val dataSet = PieDataSet(entries,"")
        val categoryColors = mapOf(
            "Food" to Color.parseColor("#FFA726"),
            "Transport" to Color.parseColor("#66BB6A"),
            "Shopping" to Color.parseColor("#42A5F5"),
            "Bills" to Color.parseColor("#AB47BC"),
            "Entertainment" to Color.parseColor("#EF5350")
        )

        val colors = categoryTotals.map { categoryColors[it.title] ?: Color.GRAY }

        dataSet.colors = colors
        dataSet.valueTextSize = 12f

        val data = PieData(dataSet)
        pieChart.data = data

        pieChart.description.isEnabled = false
        pieChart.isRotationEnabled = false
        val totalAmount = categoryTotals.sumOf { it.totalAmount }
        binding.pieChart.centerText = "₹ %.2f".format(totalAmount)
        binding.pieChart.setCenterTextColor(Color.BLACK)  // Change text color as needed
        binding.pieChart.setCenterTextSize(18f)           // Optional: adjust size

        val typedValue = TypedValue()
        val theme = requireContext().theme
        theme.resolveAttribute(com.google.android.material.R.attr.colorOnErrorContainer, typedValue, true)
        val colorOnPrimary = typedValue.data

        pieChart.legend.textColor = colorOnPrimary

        pieChart.setEntryLabelColor(colorOnPrimary)
        pieChart.animateY(1000)

        pieChart.invalidate() // refresh
    }

    private fun generatePdfReport(dailyTotals: List<Total>, categoryTotals: List<Total>): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint()
        paint.color = Color.BLACK
        paint.textSize = 12f

        var y = 25
        canvas.drawText("Expense Report (Last 7 Days)", 10f, y.toFloat(), paint)
        y += 25

        canvas.drawText("Daily Totals:", 10f, y.toFloat(), paint)
        y += 20
        dailyTotals.forEach {
            canvas.drawText("${it.title}: ₹${"%.2f".format(it.totalAmount)}", 10f, y.toFloat(), paint)
            y += 20
        }

        y += 10
        canvas.drawText("Category Totals:", 10f, y.toFloat(), paint)
        y += 20
        categoryTotals.forEach {
            canvas.drawText("${it.title}: ₹${"%.2f".format(it.totalAmount)}", 10f, y.toFloat(), paint)
            y += 20
        }

        pdfDocument.finishPage(page)

        val file = File(requireContext().cacheDir, "expense_report.pdf")
        try {
            pdfDocument.writeTo(FileOutputStream(file))
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        } finally {
            pdfDocument.close()
        }

        return file
    }

    private fun generateCsvReport(dailyTotals: List<Total>, categoryTotals: List<Total>): File? {
        val csvFile = File(requireContext().cacheDir, "expense_report.csv")
        try {
            csvFile.printWriter().use { writer ->
                writer.println("Daily Totals")
                writer.println("Date,Amount")
                dailyTotals.forEach {
                    writer.println("${it.title},${"%.2f".format(it.totalAmount)}")
                }
                writer.println()
                writer.println("Category Totals")
                writer.println("Category,Amount")
                categoryTotals.forEach {
                    writer.println("${it.title},${"%.2f".format(it.totalAmount)}")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return csvFile
    }

    private fun shareFile(file: File, mimeType: String) {
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, "Share Expense Report"))
    }

    private fun openPdfInApp(pdfFile: File) {
        val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", pdfFile)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY
        }
        startActivity(Intent.createChooser(intent, "Open PDF"))
    }


}