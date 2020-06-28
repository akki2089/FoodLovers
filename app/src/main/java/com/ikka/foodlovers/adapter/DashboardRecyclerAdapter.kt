package com.akshay.FoodLoverApp.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.ikka.foodlovers.R
import com.ikka.foodlovers.activity.RestaurantDetailsActivity
import com.ikka.foodlovers.database.RestaurantDatabase
import com.ikka.foodlovers.database.RestaurantEntity
import com.ikka.foodlovers.model.Restaurant
import com.squareup.picasso.Picasso

class DashboardRecyclerAdapter(val context:Context , val itemList : ArrayList<Restaurant> ) : RecyclerView.Adapter <DashboardRecyclerAdapter.DashboardViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DashboardRecyclerAdapter.DashboardViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_dashboard_single_row,parent,false)
        return DashboardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int)
    {


        val Restaurant = itemList[position]

        holder.txtRestaurantName.text = Restaurant.RestaurantName
        holder.txtRestaurantRating.text = Restaurant.RestaurantRating
        holder.txtcostPerPerson.text = "₹ " + Restaurant.costPerPerson + "/person"
        Picasso.get().load(Restaurant.RestaurantImage).error(R.drawable.default_restaurant_image).into(holder.imgRestaurantImage)

        holder.llcontent.setOnClickListener{
            Toast.makeText(context,"Clicked on ${holder.txtRestaurantName.text}", Toast.LENGTH_SHORT).show()
            var intent = Intent(context,
                RestaurantDetailsActivity::class.java)
            intent.putExtra("restaurant_id",Restaurant.RestaurantID)
            intent.putExtra("restaurant_name",Restaurant.RestaurantName)

            context.startActivity(intent)
        }
        val id = Restaurant.RestaurantID

        val restaurantEntity = RestaurantEntity(
            id?.toInt() ,
            Restaurant.RestaurantName,
            Restaurant.RestaurantRating,
            Restaurant.costPerPerson,
            Restaurant.RestaurantImage
        )
        //println("i came here no error")

        if(DBAsyncTask(context,restaurantEntity,1).execute().get())
        {
            //if its favourite giving the dark heart image to it
            holder.imgchooseFavourite.setImageResource(R.drawable.ic_made_favourite)
        }
        else
        {
            holder.imgchooseFavourite.setImageResource(R.drawable.ic_favourite_restaurants)
        }
        holder.imgchooseFavourite.setOnClickListener{
            Toast.makeText(context,"Clicked on Favorite Button", Toast.LENGTH_SHORT).show()
            if(DBAsyncTask(context,restaurantEntity,1).execute().get())
            {
                //remove from favourites
                val Async = DBAsyncTask(context,restaurantEntity,3).execute()
                val result = Async.get()
                if(result)
                {
                    Toast.makeText(context,"Removed from Favourites", Toast.LENGTH_SHORT).show()
                    holder.imgchooseFavourite.setImageResource(R.drawable.ic_favourite_restaurants)
                    //context.startActivity(Intent(context, FavouriteRestaurantsFragment::class.java))
                }
                else
                {
                    Toast.makeText(context,"Error while removing", Toast.LENGTH_SHORT).show()
                }

            }//if block -> dedicated to insert book in favourites
            else
            {
                //adding to favourite
                val Async = DBAsyncTask(context,restaurantEntity,2).execute()
                val result = Async.get()
                if(result)
                {
                    Toast.makeText(context,"Added To Favourites", Toast.LENGTH_SHORT).show()
                    holder.imgchooseFavourite.setImageResource(R.drawable.ic_made_favourite)
                }
                else
                {
                    Toast.makeText(context,"Error while inserting", Toast.LENGTH_SHORT).show()
                }

            }//else block -> dedicated to insert a book in favourites
        }//condition of fav button clicked


    }//ob bind function closing

    class DashboardViewHolder(view : View) : RecyclerView.ViewHolder(view)
    {
        val txtRestaurantName : TextView = view.findViewById(R.id.txtRestaurantName)
        val txtcostPerPerson : TextView = view.findViewById(R.id.txtcostPerPerson)
        val txtRestaurantRating : TextView = view.findViewById(R.id.txtRestaurantRating)
        val llcontent : LinearLayout = view.findViewById(R.id.llcontent)
        val imgchooseFavourite : ImageView = view.findViewById(R.id.imgChooseFavourite)
        val imgRestaurantImage : ImageView = view.findViewById(R.id.imgRestaurantImage)
    }

    class DBAsyncTask(val context: Context,val restaurantEntity: RestaurantEntity,val mode:Int) :
        AsyncTask<Void,Void,Boolean>()
    {
        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java,"restaurant_db").build()

        override fun doInBackground(vararg params: Void?): Boolean
        {

            when (mode)
            {
                1 -> {
                    //check if restaurant already present in database or not
                    val restaurant: RestaurantEntity? = db.restaurantDao()
                        .getRestaurantByID(restaurantEntity.RestaurantID.toString())
                    db.close()
                    return restaurant != null
                }

                2 -> {
                    //insert restauant in databse making it a favourite
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }

                3 -> {
                    //delete restaurant from database i.e no longer in favourites
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
            }//when closing
            return false
        }//doInBackground function closing

    }//AsyncTask class closing

}//main class DashboardRecyclerAdapterClosing


