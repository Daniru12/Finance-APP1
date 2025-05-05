package com.example.nathali

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.nathali.model.Transaction
import com.example.nathali.util.TransactionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionActivity : AppCompatActivity() {

    private var selectedDate: Long = System.currentTimeMillis() // Default to current time
    private var isUpdateMode = false  // Flag to check if we are updating an existing transaction
    private var transactionToUpdate: Transaction? = null  // Holds the transaction data to update

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        // Initialize views
        val titleEditText = findViewById<TextInputEditText>(R.id.editTitle)
        val amountEditText = findViewById<TextInputEditText>(R.id.editAmount)
        val categoryDropdown = findViewById<AutoCompleteTextView>(R.id.categoryDropdown)
        val dateButton = findViewById<Button>(R.id.btnSelectDate)
        val saveButton = findViewById<MaterialButton>(R.id.btnSaveTransaction)
        val transactionTypeRadioGroup = findViewById<RadioGroup>(R.id.transactionTypeRadioGroup)

        // Set up category dropdown
        setupCategoryDropdown(categoryDropdown)

        // Check if we're in update mode
        handleUpdateMode(titleEditText, amountEditText, categoryDropdown, dateButton, transactionTypeRadioGroup)

        // Set up date picker
        setupDatePicker(dateButton)

        // Save button logic
        saveButton.setOnClickListener {
            handleSaveButtonClick(titleEditText, amountEditText, categoryDropdown, transactionTypeRadioGroup)
        }
    }

    private fun setupCategoryDropdown(categoryDropdown: AutoCompleteTextView) {
        val categories = arrayOf("Meal", "Clothing", "Entertainment", "Utilities", "Transport", "Salary")
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        categoryDropdown.setAdapter(categoryAdapter)
    }

    private fun handleUpdateMode(
        titleEditText: TextInputEditText,
        amountEditText: TextInputEditText,
        categoryDropdown: AutoCompleteTextView,
        dateButton: Button,
        transactionTypeRadioGroup: RadioGroup
    ) {
        transactionToUpdate = intent.getParcelableExtra("transaction")
        transactionToUpdate?.let { transaction ->
            isUpdateMode = true
            titleEditText.setText(transaction.title)
            amountEditText.setText(transaction.amount.toString())
            categoryDropdown.setText(transaction.category, false)
            selectedDate = transaction.date
            dateButton.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selectedDate))

            // Set the radio button based on transaction type
            if (transaction.type == "income") {
                transactionTypeRadioGroup.check(R.id.radioIncome)
            } else {
                transactionTypeRadioGroup.check(R.id.radioExpense)
            }
        }
    }

    private fun setupDatePicker(dateButton: Button) {
        dateButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            android.app.DatePickerDialog(this, { _, year, month, day ->
                calendar.set(year, month, day)
                selectedDate = calendar.timeInMillis
                dateButton.text =
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
            }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH]).show()
        }
    }

    private fun handleSaveButtonClick(
        titleEditText: TextInputEditText,
        amountEditText: TextInputEditText,
        categoryDropdown: AutoCompleteTextView,
        transactionTypeRadioGroup: RadioGroup
    ) {
        val title = titleEditText.text.toString().trim()
        val amountString = amountEditText.text.toString().trim()
        val amount = amountString.toDoubleOrNull() ?: 0.0
        val category = categoryDropdown.text.toString().trim()

        // Validation for missing fields
        if (title.isEmpty() || category.isEmpty() || amount <= 0) {
            Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
            return
        }

        // Get the selected transaction type from the RadioGroup
        val transactionType = when (transactionTypeRadioGroup.checkedRadioButtonId) {
            R.id.radioIncome -> "income"
            R.id.radioExpense -> "expense"
            else -> "income" // Default to income if no selection
        }

        // If the transaction type is "expense", we need to check if it's not below the total amount.
        if (transactionType == "expense") {
            val transactions = TransactionManager.getTransactions(this)
            val totalIncome = transactions.filter { it.type == "income" }.sumOf { it.amount }
            val totalExpense = transactions.filter { it.type == "expense" }.sumOf { it.amount }

            // Check if the total amount would go below 0 if the expense is added
            if (totalIncome - (totalExpense + amount) < 0) {
                Toast.makeText(this, "Cannot add expense. Total balance is insufficient.", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Create a new transaction object
        val transaction = Transaction(
            title = title,
            amount = amount,
            category = category,
            date = selectedDate,
            type = transactionType
        )

        val transactions = TransactionManager.getTransactions(this).toMutableList()

        // Update or add the transaction
        if (isUpdateMode) {
            val indexToUpdate = transactions.indexOfFirst { it.date == transactionToUpdate?.date }
            if (indexToUpdate != -1) {
                transactions[indexToUpdate] = transaction // Update the transaction
            }
        } else {
            transactions.add(transaction) // Add the new transaction
        }

        // Save the updated transactions list
        TransactionManager.saveTransactions(this, transactions)

        Toast.makeText(this, "Transaction saved!", Toast.LENGTH_SHORT).show()
        finish()  // Close the activity and return to the previous screen
    }
}
