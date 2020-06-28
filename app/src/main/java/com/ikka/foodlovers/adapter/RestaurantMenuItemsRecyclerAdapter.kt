package com.ikka.foodlovers.adapter

import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.ikka.foodlovers.R
import com.ikka.foodlovers.database.OrderedItemsDatabase
import com.ikka.foodlovers.database.OrderedItemsEntity
import com.ikka.foodlovers.model.RestaurantMenuItems

class RestaurantMenuItemsRecyclerAdapter(val context: Context, val itemList : ArrayList<RestaurantMenuItems>, val RestaurantID : String, val RestaurantName : String, var btnProceedToCart : Button) : RecyclerView.Adapter <RestaurantMenuItemsRecyclerAdapter.RestaurantMenuItemsViewHolder>() {

    var count = 0
    var flag = 0
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RestaurantMenuItemsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_restaurants_details_single_row,parent,false)
        return RestaurantMenuItemsViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: RestaurantMenuItemsViewHolder, position: Int) {
        val RestaurantMenuItems = itemList[position]
        holder.txtItemID.text = RestaurantMenuItems.ItemID
        holder.txtMenuItem.text = RestaurantMenuItems.ItemName
        holder.txtcostForOne.text = "₹ " + RestaurantMenuItems.CostForOne

        val orderedItemsEntity = OrderedItemsEntity(
            RestaurantMenuItems.ItemID.toInt(),
            RestaurantMenuItems.ItemName,
            RestaurantMenuItems.CostForOne.toInt(),
            RestaurantName,
            RestaurantID
        )

        if(flag == 0)
        {
            DBAsyncTask(context,orderedItemsEntity,5).execute().get()
            flag=1
        }


        if(DBAsyncTask(context,orderedItemsEntity,1).execute().get())
        {
            //if item already added to ordered items
            holder.btnAddItem.text = "Remove"
            holder.btnAddItem.setBackgroundResource(R.color.btnRemove)

        }
        else
        {
            holder.btnAddItem.text = "Add"
            //holder.btnAddItem.setBackgroundColor(R.color.btnAdd)
            holder.btnAddItem.setBackgroundResource(R.color.btnAdd)
        }
        holder.btnAddItem.setOnClickListener {
            Toast.makeText(context, "Clicked on ${holder.txtMenuItem.text}", Toast.LENGTH_SHORT).show()


            if(DBAsyncTask(context,orderedItemsEntity,1).execute().get())
            {
                //remove from ordered items
                val Async = DBAsyncTask(context,orderedItemsEntity,3).execute()
                val result = Async.get()
                if(result)
                {
                    Toast.makeText(context,"Removed from Ordered Items", Toast.LENGTH_SHORT).show()
                    holder.btnAddItem.text = "Add"
                    holder.btnAddItem.setBackgroundResource(R.color.btnAdd)
                    DBAsyncTask(context,orderedItemsEntity,4).execute().get()
                    count = count - 1
                    if(count == 0)
                        btnProceedToCart.visibility = View.GONE
                }
                else
                {
                    Toast.makeText(context,"Error while removing", Toast.LENGTH_SHORT).show()
                }

            }//if block -> dedicated to insert item in ordered items

            else
            {
                //adding to favourite
                val Async = DBAsyncTask(context,orderedItemsEntity,2).execute()
                val result = Async.get()
                if(result)
                {
                    Toast.makeText(context,"Added To Ordred Items", Toast.LENGTH_SHORT).show()
                    holder.btnAddItem.text = "Remove"
                    holder.btnAddItem.setBackgroundResource(R.color.btnRemove)
                    DBAsyncTask(context,orderedItemsEntity,4).execute().get()
                    count = count + 1
                    if(count == 1)
                        btnProceedToCart.visibility = View.VISIBLE

                }
                else
                {
                    Toast.makeText(context,"Error while adding item to ordered items", Toast.LENGTH_SHORT).show()
                }

            }//else block -> dedicated to insert an item to ordered items



        }//closing of the button add item
    }

    class RestaurantMenuItemsViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        val txtItemID : TextView = view.findViewById(R.id.txtItemID)
        val txtMenuItem : TextView = view.findViewById(R.id.txtItemName)
        val txtcostForOne : TextView = view.findViewById(R.id.txtCostForOne)
        val btnAddItem : Button = view.findViewById(R.id.btnAddItem)
        //val btnProceedToCart : Button = findViewById(R.id.btnProceedToCart)
    }
    class DBAsyncTask(val context: Context, val orderedItemsEntity: OrderedItemsEntity, val mode:Int) :
        AsyncTask<Void, Void, Boolean>()
    {
        val db = Room.databaseBuilder(context, OrderedItemsDatabase::class.java,"orderedItems_db").build()

        override fun doInBackground(vararg params: Void?): Boolean
        {
            when (mode)
            {
                1 -> {
                    //check if item already present in database or not
                    val item : OrderedItemsEntity? = db.OrderedItemsDao().getItemByID((orderedItemsEntity.ItemID.toString()))
                    db.close()
                    return item != null
                }

                2 -> {
                    //insert item in databse making it ordered
                    db.OrderedItemsDao().insertItem(orderedItemsEntity)
                    db.close()
                    return true
                }

                3 -> {
                    //delete book from database i.e no longer in favourites
                    db.OrderedItemsDao().deleteItem(orderedItemsEntity)
                    db.close()
                    return true
                }

                4 -> {
                    println("data is ${db.OrderedItemsDao().getAllOrderedItems()}")
                    db.close()
                    return true
                }

                5 ->{
                    db.OrderedItemsDao().deleteAll()
                    db.close()
                    return true
                }
            }//when closing
            return false
        }//doInBackground function closing

    }//AsyncTask class closing
}