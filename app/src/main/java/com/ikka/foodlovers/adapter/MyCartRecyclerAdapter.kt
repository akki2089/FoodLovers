package com.ikka.foodlovers.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ikka.foodlovers.R
import com.ikka.foodlovers.database.OrderedItemsEntity

class MyCartRecyclerAdapter(val context: Context, val itemList : List<OrderedItemsEntity> ) : RecyclerView.Adapter <MyCartRecyclerAdapter.MyCartViewHolder>()
{
    var cost = 0
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyCartRecyclerAdapter.MyCartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_order_history_items_single_row,parent,false)
        return MyCartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: MyCartRecyclerAdapter.MyCartViewHolder, position: Int) {

        println("I have REACHED HERE TO WAIT")
        var orderedItem = itemList[position]
        holder.txtItemName.text = orderedItem.item_name
        holder.txtItemPrice.text = "₹ " + orderedItem.cost.toString()
        cost = cost + orderedItem.cost
        println("present cost is ------- ${cost}")
    }

    class MyCartViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        var txtItemName : TextView = view.findViewById(R.id.txtItemName)
        var txtItemPrice : TextView = view.findViewById(R.id.txtItemPrice)
    }



}