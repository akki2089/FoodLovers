package com.ikka.foodlovers.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akshay.FoodLoverApp.adapter.DashboardRecyclerAdapter
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ikka.foodlovers.R
import com.ikka.foodlovers.model.Restaurant
import com.ikka.foodlovers.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 */
class DashboardFragment : Fragment() {

    lateinit var recyclerDashboard : RecyclerView
    lateinit var layoutManager : RecyclerView.LayoutManager
    lateinit var recyclerAdapter : DashboardRecyclerAdapter

    val RestaurantInfoList = arrayListOf<Restaurant>()

    lateinit var progressBar : ProgressBar
    lateinit var progressLayout : RelativeLayout

    var costComparator = Comparator<Restaurant>
    {resturant1,resturant2 ->
        if(resturant1.costPerPerson.compareTo(resturant2.costPerPerson,ignoreCase = true) == 0)
        {
            resturant1.RestaurantName.compareTo(resturant2.RestaurantName,true)
        }
        else
        {
            resturant1.costPerPerson.compareTo(resturant2.costPerPerson,ignoreCase = true)
        }
    }

    var ratingComparator = Comparator<Restaurant>
    { resturant1 , resturant2 ->
        if(resturant1.RestaurantRating.compareTo(resturant2.RestaurantRating,ignoreCase = true) == 0)
        {
            resturant1.RestaurantName.compareTo(resturant2.RestaurantName,true)
        }
        else
        {
            resturant1.RestaurantRating.compareTo(resturant2.RestaurantRating,ignoreCase = true)
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        setHasOptionsMenu(true)

        progressLayout = view.findViewById(R.id.progressLayout)

        progressBar = view.findViewById(R.id.progressBar)

        progressLayout.visibility = View.VISIBLE

        recyclerDashboard = view.findViewById(R.id.recyclerDashboard)

        layoutManager = LinearLayoutManager(activity)


        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if(ConnectionManager().checkConnectivity(activity as Context))
        {
            if(activity != null) {
                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.GET, url, null,
                    Response.Listener {
                        //println("Response is $it")

                        try {
                            progressLayout.visibility = View.GONE

                            val bdata = it.getJSONObject("data")

                            val success = it.getBoolean("success")
                            if (success) {
                                val data = it.getJSONArray("data")
                                for (i in 0 until data.length()) {
                                    val RestaurantJsonObject = data.getJSONObject(i)
                                    val RestaurantObject = Restaurant(

                                        RestaurantJsonObject.getString("id"),
                                        RestaurantJsonObject.getString("name"),
                                        RestaurantJsonObject.getString("rating"),
                                        RestaurantJsonObject.getString("cost_for_one"),
                                        RestaurantJsonObject.getString("image_url")
                                    )
                                    RestaurantInfoList.add(RestaurantObject)


                                    if (activity != null) {
                                        recyclerAdapter = DashboardRecyclerAdapter(
                                            activity as Context,
                                            RestaurantInfoList
                                        )

                                        recyclerDashboard.adapter = recyclerAdapter

                                        recyclerDashboard.layoutManager = layoutManager

                                        println("i have come here")
                                    }

                                }
                            } else {
                                Toast.makeText(
                                    activity as Context,
                                    "Some error occured at fetch!!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }//closing of try block
                        catch (e: JSONException) {
                            Toast.makeText(
                                activity as Context,
                                "JSONException error occured!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                    }, Response.ErrorListener {
                        //println("Response has some error and error is $it")
                        Toast.makeText(
                            activity as Context,
                            "Volley error occured!!!",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                ) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "fc994578f49102"
                        return headers
                    }

                }

                queue.add(jsonObjectRequest)
            }//closing of if(activity ! = null)
        }//closing of if block of connectivity manager which checked for internet connection

        else
        {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->

                val settingsIntent =  Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_dashboard,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item?.itemId
        if(id == R.id.costHighFirst)
        {
            Collections.sort(RestaurantInfoList,costComparator)
            RestaurantInfoList.reverse()
        }
        else if(id == R.id.costLowFirst)
        {
            Collections.sort(RestaurantInfoList,costComparator)
        }
        else if(id == R.id.cost)
        {
            Collections.sort(RestaurantInfoList,costComparator)
        }
        else if(id == R.id.rating)
        {
            Collections.sort(RestaurantInfoList,ratingComparator)
            RestaurantInfoList.reverse()
        }
        recyclerAdapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }

}
