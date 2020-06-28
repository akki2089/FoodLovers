package com.ikka.foodlovers.adapter

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
import com.squareup.picasso.Picasso

class FavouriteRestaurantRecyclerAdapter(val context: Context, var itemList : List<RestaurantEntity>, val txtNoneChosen : TextView) : RecyclerView.Adapter <FavouriteRestaurantRecyclerAdapter.FavouriteRestaurantViewHolder>()
{

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavouriteRestaurantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_dashboard_single_row,parent,false)
        return FavouriteRestaurantViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: FavouriteRestaurantViewHolder, position: Int) {

        val Restaurant = itemList[position]
        holder.txtRestaurantName.text = Restaurant.RestaurantName
        holder.txtRestaurantRating.text = Restaurant.RestaurantRating
        holder.txtcostPerPerson.text = "₹ " + Restaurant.costPerPerson + "/person"
        Picasso.get().load(Restaurant.RestaurantImage).error(R.drawable.default_restaurant_image)
            .into(holder.imgRestaurantImage)

        holder.imgchooseFavourite.setImageResource(R.drawable.ic_made_favourite)

        holder.llcontent.setOnClickListener {
            Toast.makeText(
                context,
                "Clicked on ${holder.txtRestaurantName.text}",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(context,
                RestaurantDetailsActivity::class.java)
            intent.putExtra("restaurant_id",Restaurant.RestaurantID.toString())
            intent.putExtra("restaurant_name",Restaurant.RestaurantName)
            //println("response is ${Restaurant.RestaurantID}")
            //println("i have reached till here")
            //println(intent.getStringExtra("restaurant_id"))
            //println("above was given by intent")
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
        holder.imgchooseFavourite.setOnClickListener{
            Toast.makeText(context,"Removing from favourites", Toast.LENGTH_SHORT).show()
            //remove from favourites
            holder.imgchooseFavourite.setImageResource(R.drawable.ic_favourite_restaurants)
            val Async = FavouriteRestaurantRecyclerAdapter.DBAsyncTask(context, restaurantEntity).execute()
            val result = Async.get()
            if(result)
            {
                Toast.makeText(context,"Removed from Favourites!!", Toast.LENGTH_SHORT).show()
                println(itemList)
                //println(itemList.drop(position))
                //itemList.reduceIndexed { position, acc, restaurantEntity ->  Restaurant}
                itemList = itemList.minusElement(Restaurant)
                println(itemList)
                if(itemList.size == 0)
                    txtNoneChosen.visibility = View.VISIBLE
                notifyDataSetChanged()

            }
            else
            {
                Toast.makeText(context,"Error while removing!!!", Toast.LENGTH_SHORT).show()
            }
        }//condition of fav button clicked
    }//on bind view holder closed

    class FavouriteRestaurantViewHolder(view : View) : RecyclerView.ViewHolder(view)
    {
        val txtRestaurantName : TextView = view.findViewById(R.id.txtRestaurantName)
        val txtcostPerPerson : TextView = view.findViewById(R.id.txtcostPerPerson)
        val txtRestaurantRating : TextView = view.findViewById(R.id.txtRestaurantRating)
        val llcontent : LinearLayout = view.findViewById(R.id.llcontent)
        val imgchooseFavourite : ImageView = view.findViewById(R.id.imgChooseFavourite)
        val imgRestaurantImage : ImageView = view.findViewById(R.id.imgRestaurantImage)
    }



    class DBAsyncTask(val context: Context, val restaurantEntity: RestaurantEntity) :
        AsyncTask<Void, Void, Boolean>()
    {
        override fun doInBackground(vararg params: Void?): Boolean
        {
            //delete book from database i.e no longer in favourites
            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java,"restaurant_db").build()
            db.restaurantDao().deleteRestaurant(restaurantEntity)
            db.close()
            return true
        }//doInBackground function closing

    }//AsyncTask class closing


}