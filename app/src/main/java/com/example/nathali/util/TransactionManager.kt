package com.example.nathali.util

import android.content.Context
import com.example.nathali.model.Transaction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object TransactionManager {

    private const val PREF_NAME = "transactions"
    private const val KEY_TRANSACTIONS = "transaction_list"


    // Save a list of transactions to SharedPreferences
    fun saveTransactions(context: Context, transactions: List<Transaction>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val json = Gson().toJson(transactions) // Convert the transactions list to JSON string
        editor.putString(KEY_TRANSACTIONS, json) // Save the JSON string
        editor.apply() // Apply the changes asynchronously
    }

    // Get the list of transactions from SharedPreferences
    fun getTransactions(context: Context): MutableList<Transaction> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_TRANSACTIONS, null) // Retrieve the JSON string
        return if (json != null) {
            val type = object : TypeToken<List<Transaction>>() {}.type // Define the type for deserialization
            Gson().fromJson(json, type) // Deserialize JSON string back to List<Transaction>
        } else {
            mutableListOf() // Return an empty list if no transactions are found
        }
    }

}
