package com.akshay.FoodLoverApp.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.ikka.foodlovers.R
import com.ikka.foodlovers.adapter.FavouriteRestaurantRecyclerAdapter
import com.ikka.foodlovers.database.RestaurantDatabase
import com.ikka.foodlovers.database.RestaurantEntity
import com.ikka.foodlovers.model.Restaurant

/**
 * A simple [Fragment] subclass.
 */
class FavouriteRestaurantsFragment : Fragment() {

    lateinit var recyclerFavouriteRestaurants : RecyclerView
    lateinit var layoutManager : RecyclerView.LayoutManager
    lateinit var recyclerAdapter : FavouriteRestaurantRecyclerAdapter
    lateinit var txtNoneChosen : TextView

    val RestaurantInfoList = arrayListOf<Restaurant>()

    lateinit var progressBar : ProgressBar
    lateinit var progressLayout : RelativeLayout

    var dbRestaurantList = listOf<RestaurantEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_favourite_restaurant, container, false)

        progressLayout = view.findViewById(R.id.progressLayout)

        progressBar = view.findViewById(R.id.progressBar)

        txtNoneChosen = view.findViewById(R.id.txtNoneChosen)

        txtNoneChosen.visibility = View.GONE

        progressLayout.visibility = View.VISIBLE

        recyclerFavouriteRestaurants = view.findViewById(R.id.recyclerFavouriteRestaurants)

        layoutManager = LinearLayoutManager(activity)

        dbRestaurantList = RetrieveFavouriteRestaurants(activity as Context).execute().get()
        println("response is ${dbRestaurantList}")

        var numberOfRestaurants = dbRestaurantList.size

        if(activity!=null)
        {
            progressLayout.visibility = View.GONE

            if(numberOfRestaurants == 0)
                txtNoneChosen.visibility  = View.VISIBLE
            else
            {
                recyclerAdapter = FavouriteRestaurantRecyclerAdapter(activity as Context,dbRestaurantList,txtNoneChosen)
                recyclerFavouriteRestaurants.adapter = recyclerAdapter
                recyclerFavouriteRestaurants.layoutManager = layoutManager
            }
        }

        return view
    }

    class RetrieveFavouriteRestaurants(val context: Context) :
        AsyncTask<Void, Void, List<RestaurantEntity>>() {
        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {

            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java,"restaurant_db").build()
            return db.restaurantDao().getAllRestaurants()

        }
    }

}
