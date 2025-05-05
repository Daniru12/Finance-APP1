package com.example.nathali

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.example.nathali.model.Transaction

class TransactionAdapter(
    private val transactions: List<Transaction>,
    private val onItemClickListener: OnItemClickListener // Interface to handle button clicks
) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    interface OnItemClickListener {


        fun onUpdateClick(position: Int) // Handle update button click
        fun onDeleteClick(position: Int) // Handle delete button click
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.transactionTitle)
        val amountText: TextView = view.findViewById(R.id.transactionAmount)
        val updateButton: Button = view.findViewById(R.id.btnUpdate)
        val deleteButton: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.titleText.text = transaction.title
        holder.amountText.text = "Rs.${transaction.amount}"

        // Update button click handler
        holder.updateButton.setOnClickListener {
            onItemClickListener.onUpdateClick(position)
        }

        // Delete button click handler
        holder.deleteButton.setOnClickListener {
            onItemClickListener.onDeleteClick(position)
        }
    }

    override fun getItemCount(): Int = transactions.size
}
