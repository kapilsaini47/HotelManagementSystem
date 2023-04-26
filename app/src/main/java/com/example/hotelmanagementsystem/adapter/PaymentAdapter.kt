package com.example.hotelmanagementsystem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.hotelmanagementsystem.R
import com.example.hotelmanagementsystem.models.paymentInfo.Payment

class PaymentAdapter:RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder>(){

    inner class PaymentViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        private val date:TextView = itemView.findViewById(R.id.tvDate)
        private val customerName:TextView = itemView.findViewById(R.id.tvCustomerName)
        private val amountPaid:TextView = itemView.findViewById(R.id.tvPayment)

        fun bindViews(payment: Payment){
            date.text = payment.paymentDate.toString()
            amountPaid.text = payment.paymentAmount.toString()
            customerName.text = payment.customerName.toString()
        }

    }

    private val differCallBack = object : DiffUtil.ItemCallback<Payment>() {
        override fun areItemsTheSame(oldItem: Payment, newItem: Payment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Payment, newItem: Payment): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        return PaymentViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.single_view_payment,parent,false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        val listPosition = differ.currentList[position]
        holder.bindViews(listPosition)
    }
}