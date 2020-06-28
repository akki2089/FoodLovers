package com.ikka.foodlovers.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ikka.foodlovers.R
import com.ikka.foodlovers.model.OrderHistory

class OrderHistoryRecyclerAdapter(val context: Context, val orderList: ArrayList<OrderHistory>) : RecyclerView.Adapter <OrderHistoryRecyclerAdapter.OrderHistoryViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderHistoryRecyclerAdapter.OrderHistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_order_history_single_row,parent,false)
        return OrderHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(
        holder: OrderHistoryRecyclerAdapter.OrderHistoryViewHolder,
        position: Int
    ) {
        println("the data size is ${orderList.size}")
        if(orderList.size == 0)
            Toast.makeText(context,"You have not placed any order yet.", Toast.LENGTH_SHORT).show()
        else
        {
            val OrderHistory = orderList[position]
            holder.txtRestaurantName.text = OrderHistory.RestaurantName
            holder.txtOrderDate.text = OrderHistory.order_placed_at.subSequence(0,8)
            val itemList = OrderHistory.OrderedItems
            val orderHistoryItemsLayoutManager = LinearLayoutManager(holder.recyclerOrderHistoryItems.context)
            val orderHistoryItemsAdapter = OrderHistoryItemsRecyclerAdapter(holder.recyclerOrderHistoryItems.context as Context,itemList)
            //println("parent adapter before setting")
            holder.recyclerOrderHistoryItems.layoutManager = orderHistoryItemsLayoutManager
            holder.recyclerOrderHistoryItems.adapter = orderHistoryItemsAdapter
            //println("parent adapter after setting")
        }
    }

    class OrderHistoryViewHolder(view : View) : RecyclerView.ViewHolder(view)
    {
        val txtRestaurantName : TextView = view.findViewById(R.id.txtRestaurantName)
        val txtOrderDate : TextView = view.findViewById(R.id.txtOrderDate)
        val recyclerOrderHistoryItems : RecyclerView = view.findViewById(R.id.recyclerOrderHistoryItems)
    }

}