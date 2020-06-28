package com.ikka.foodlovers.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ordereditems")
class OrderHistoryItems (
    @PrimaryKey val ItemID: Int,
    @ColumnInfo(name = "name") val name : String,
    @ColumnInfo(name = "cost") val cost : String
    )