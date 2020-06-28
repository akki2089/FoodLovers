package com.ikka.foodlovers.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class RestaurantEntity(
    @PrimaryKey val RestaurantID: Int,
    @ColumnInfo(name = "restaurant_name") val RestaurantName : String,
    @ColumnInfo(name = "restaurant_rating") val RestaurantRating: String,
    @ColumnInfo(name = "restaurant_cost_per_person") val costPerPerson : String,
    @ColumnInfo(name = "restaurant_image") val RestaurantImage : String
)