package com.ikka.foodlovers.database

import androidx.room.Database
import androidx.room.RoomDatabase

abstract class OrderedItemsDatabase : RoomDatabase() {
    abstract fun OrderedItemsDao() : OrderedItemsDao
}