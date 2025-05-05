package com.example.nathali.util

import android.content.Context
import android.widget.Toast
import com.example.nathali.model.Transaction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter

object TransactionBackup {

    private const val FILE_NAME = "transactions_backup.json"

    // Export transactions to a JSON file
    fun exportTransactionsToFile(context: Context, transactions: List<Transaction>): Boolean {
        val gson = Gson()
        val json = gson.toJson(transactions)  // Convert the transactions list to JSON

        return try {
            val fileOutputStream: FileOutputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)
            val outputStreamWriter = OutputStreamWriter(fileOutputStream)
            outputStreamWriter.write(json)
            outputStreamWriter.close()

            Toast.makeText(context, "Data exported successfully", Toast.LENGTH_SHORT).show()
            true  // Indicate successful export
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to export data", Toast.LENGTH_SHORT).show()
            false  // Indicate failure
        }
    }

    // Restore transactions from a JSON file
    fun restoreTransactionsFromFile(context: Context): List<Transaction> {
        val fileInputStream: FileInputStream?

        return try {
            // Open the file for reading
            fileInputStream = context.openFileInput(FILE_NAME)
            val inputStreamReader = InputStreamReader(fileInputStream)
            val transactionsType = object : TypeToken<List<Transaction>>() {}.type
            val transactions = Gson().fromJson<List<Transaction>>(inputStreamReader, transactionsType)
            inputStreamReader.close()

            if (transactions.isNotEmpty()) {
                Toast.makeText(context, "Data restored successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "No data found in the backup", Toast.LENGTH_SHORT).show()
            }

            transactions  // Return the restored list of transactions
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to restore data", Toast.LENGTH_SHORT).show()
            emptyList()  // Return an empty list in case of failure
        }
    }
}
