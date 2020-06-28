package com.ikka.foodlovers.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ikka.foodlovers.R
import com.ikka.foodlovers.model.OrderHistoryItems

class OrderHistoryItemsRecyclerAdapter(val context: Context, val itemList : ArrayList<OrderHistoryItems>) : RecyclerView.Adapter <OrderHistoryItemsRecyclerAdapter.OrderHistoryItemsViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderHistoryItemsRecyclerAdapter.OrderHistoryItemsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_order_history_items_single_row,parent,false)
        return OrderHistoryItemsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(
        holder: OrderHistoryItemsRecyclerAdapter.OrderHistoryItemsViewHolder,
        position: Int
    ) {
        println("reached inside child adapter class")
        val OrderHistoryItems = itemList[position]
        holder.txtItemName.text = OrderHistoryItems.name
        holder.txtItemPrice.text = "₹ " + OrderHistoryItems.cost
    }

    class OrderHistoryItemsViewHolder(view : View) : RecyclerView.ViewHolder(view)
    {
        val txtItemName : TextView = view.findViewById(R.id.txtItemName)
        val txtItemPrice : TextView = view.findViewById(R.id.txtItemPrice)
    }
}