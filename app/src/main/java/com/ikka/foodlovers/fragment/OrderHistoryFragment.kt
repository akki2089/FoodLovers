package com.ikka.foodlovers.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ikka.foodlovers.R
import com.ikka.foodlovers.adapter.OrderHistoryRecyclerAdapter
import com.ikka.foodlovers.model.OrderHistory
import com.ikka.foodlovers.model.OrderHistoryItems
import com.ikka.foodlovers.util.ConnectionManager
import org.json.JSONException

/**
 * A simple [Fragment] subclass.
 */
class OrderHistoryFragment : Fragment() {

    lateinit var recyclerOrderHistory : RecyclerView
    lateinit var layoutManager : RecyclerView.LayoutManager
    lateinit var recyclerAdapter : OrderHistoryRecyclerAdapter

    val OrderHistoryList = arrayListOf<OrderHistory>()
    val OrderHistoryItemsList = arrayListOf<OrderHistoryItems>()

    lateinit var progressBar : ProgressBar
    lateinit var progressLayout : RelativeLayout

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_order_history, container, false)

        sharedPreferences = this.getActivity()!!.getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )

        progressLayout = view.findViewById(R.id.progressLayout)

        progressBar = view.findViewById(R.id.progressBar)

        progressLayout.visibility = View.VISIBLE

        recyclerOrderHistory = view.findViewById(R.id.recyclerOrderHistory)

        layoutManager = LinearLayoutManager(activity)

        val queue = Volley.newRequestQueue(activity as Context)

        val user_id= sharedPreferences.getString("user_id","pick")

        val url = "http://13.235.250.119/v2/orders/fetch_result/" + user_id

        println("url formed is ${url}")

        if(ConnectionManager().checkConnectivity(activity as Context))
        {
            if(activity != null) {
                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.GET, url, null,
                    Response.Listener {
                        println("Response is $it")

                        try {
                            progressLayout.visibility = View.GONE

                            val bdata = it.getJSONObject("data")
                            val success = bdata.getBoolean("success")
                            if (success) {
                                val data = bdata.getJSONArray("data")

                                for (i in 0 until data.length()) {
                                    val orderedItems = ArrayList<OrderHistoryItems>()
                                    val OrderHistoryJsonObject = data.getJSONObject(i)
                                    val itemsData = OrderHistoryJsonObject.getJSONArray("food_items")
                                    for(j in 0 until itemsData.length())
                                    {
                                        val orderHistoryItemsJsonObject = itemsData.getJSONObject(j)
                                        val orderHistoryItemsObject =
                                            OrderHistoryItems(
                                                orderHistoryItemsJsonObject.getString("food_item_id")
                                                    .toInt(),
                                                orderHistoryItemsJsonObject.getString("name"),
                                                orderHistoryItemsJsonObject.getString("cost")
                                            )
                                        orderedItems.add(orderHistoryItemsObject)

                                    }//for loop of inner json array

                                    val OrderHistoryObject =
                                        OrderHistory(
                                            OrderHistoryJsonObject.getString("order_id").toInt(),
                                            OrderHistoryJsonObject.getString("restaurant_name"),
                                            OrderHistoryJsonObject.getString("order_placed_at"),
                                            orderedItems
                                        )
                                    OrderHistoryList.add(OrderHistoryObject)

                                    if (activity != null) {
                                        recyclerAdapter =   OrderHistoryRecyclerAdapter(
                                            activity as Context,
                                            OrderHistoryList
                                        )

                                        recyclerOrderHistory.adapter = recyclerAdapter

                                        recyclerOrderHistory.layoutManager = layoutManager

                                        println("i have come here")
                                    }

                                }//for loop of outer json array
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


                    },
                    Response.ErrorListener {
                        println("Response has some error and error is $it")
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
                        headers["token"] = "99d2334c9304fa"
                        return headers
                    }

                }

                queue.add(jsonObjectRequest)
            }//closing of if(activity ! = null)
        }//closing of if block of connectivity manager which checked for internet connection
        return view
    }

}