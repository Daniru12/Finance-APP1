package com.example.nathali

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.nathali.util.TransactionManager
import com.example.nathali.util.TransactionBackup
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private lateinit var fabAdd: ExtendedFloatingActionButton
    private lateinit var totalBalanceText: TextView
    private lateinit var incomeText: TextView
    private lateinit var expenseText: TextView
    private lateinit var lastCategoryText: TextView
    private lateinit var lastAmountText: TextView
    private lateinit var btnSeeAll: Button
    private lateinit var btnSetBudget: Button
    private lateinit var btnExportData: MaterialButton
    private lateinit var btnRestoreData: MaterialButton

    private lateinit var pieChart: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        fabAdd = findViewById(R.id.fabAdd)
        totalBalanceText = findViewById(R.id.totalBalanceValue)
        incomeText = findViewById(R.id.incomeValue)
        expenseText = findViewById(R.id.expenseValue)
        lastCategoryText = findViewById(R.id.lastCategoryValue)
        lastAmountText = findViewById(R.id.lastAmountValue)
        btnSeeAll = findViewById(R.id.btnSeeAll)
        btnSetBudget = findViewById(R.id.btnSetBudget)
        btnExportData = findViewById(R.id.btnExportData)
        btnRestoreData = findViewById(R.id.btnRestoreData)

        pieChart = findViewById(R.id.pieChart)

        // Set onClickListeners
        fabAdd.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }

        btnSeeAll.setOnClickListener {
            val intent = Intent(this, TransactionListActivity::class.java)
            startActivity(intent)
        }

        btnExportData.setOnClickListener {
            exportData() // Handle export logic
        }

        btnRestoreData.setOnClickListener {
            restoreData() // Handle restore logic
        }

        // Set onClickListener for btnSetBudget to navigate to BudgetActivity
    }

    override fun onResume() {
        super.onResume()
        updateDashboard()  // Update dashboard when the activity is resumed
    }

    private fun updateDashboard() {
        val transactions = TransactionManager.getTransactions(this)

        // Calculate income and expense
        val income = transactions.filter { it.type == "income" }.sumOf { it.amount }
        val expense = transactions.filter { it.type == "expense" }.sumOf { it.amount }
        val balance = income - expense

        // Update UI with totals
        totalBalanceText.text = "LKR %.2f".format(balance)
        incomeText.text = "LKR %.2f".format(income)
        expenseText.text = "LKR %.2f".format(expense)

        // Set data for PieChart
        updatePieChart(income, expense)

        // Update last transaction details (just show the last transaction)
        if (transactions.isNotEmpty()) {
            val last = transactions.last()
            lastCategoryText.text = last.category
            lastAmountText.text = "LKR %.2f".format(last.amount)
        } else {
            lastCategoryText.text = "No transactions"
            lastAmountText.text = "LKR 0.00"
        }
    }

    private fun updatePieChart(income: Double, expense: Double) {
        // Prepare data for the PieChart
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(income.toFloat(), "Income"))
        entries.add(PieEntry(expense.toFloat(), "Expense"))

        val dataSet = PieDataSet(entries, "Income vs Expense")
        // Set colors for the income and expense sections
        dataSet.setColors(intArrayOf(Color.GREEN, Color.RED), 255)

        val pieData = PieData(dataSet)
        pieChart.data = pieData
        pieChart.invalidate()  // Refresh the chart
    }

    private fun exportData() {
        val transactions = TransactionManager.getTransactions(this)  // Get transactions
        val success = TransactionBackup.exportTransactionsToFile(this, transactions)  // Export data
        if (success) {
            Toast.makeText(this, "Data exported successfully!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Failed to export data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun restoreData() {
        val restoredTransactions = TransactionBackup.restoreTransactionsFromFile(this)  // Restore data
        if (restoredTransactions.isNotEmpty()) {
            // Update the UI with the restored transactions
            TransactionManager.saveTransactions(this, restoredTransactions)
            updateDashboard()  // Update the dashboard with the restored data
            Toast.makeText(this, "Data restored successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No data to restore", Toast.LENGTH_SHORT).show()
        }
    }

}
