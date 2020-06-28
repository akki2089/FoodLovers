package com.ikka.foodlovers.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface OrderedItemsDao {

    @Insert
    fun insertItem(orderedItemsEntity: OrderedItemsEntity)

    @Delete
    fun deleteItem(orderedItemsEntity: OrderedItemsEntity)

    @Query("SELECT * FROM orderedItems")
    fun getAllOrderedItems():List<OrderedItemsEntity>

    @Query("SELECT * FROM orderedItems WHERE ItemID = :itemID")
    fun getItemByID(itemID :String) : OrderedItemsEntity

    @Query("DELETE FROM orderedItems")
    fun deleteAll()
}