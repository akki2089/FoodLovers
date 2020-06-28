package com.ikka.foodlovers.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

data class OrderedItemsEntity (
    @PrimaryKey val ItemID: Int,
    @ColumnInfo(name = "item_name") val item_name : String,
    @ColumnInfo(name = "cost") val cost : Int,
    @ColumnInfo(name = "restaurant_name") val restaurant_name : String,
    @ColumnInfo(name = "restaurant_id") val restaurant_id : String
)