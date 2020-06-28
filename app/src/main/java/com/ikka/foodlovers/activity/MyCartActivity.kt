package com.ikka.foodlovers.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ikka.foodlovers.R
import com.ikka.foodlovers.adapter.MyCartRecyclerAdapter
import com.ikka.foodlovers.database.OrderedItemsDatabase
import com.ikka.foodlovers.database.OrderedItemsEntity
import com.ikka.foodlovers.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MyCartActivity : AppCompatActivity() {

    lateinit var txtRestaurantName: TextView
    lateinit var btnPlaceOrder: Button
    lateinit var recyclerMyCart: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: MyCartRecyclerAdapter
    lateinit var sharedPreferences: SharedPreferences
    lateinit var toolbar: androidx.appcompat.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_cart)

        sharedPreferences = getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )

        val user_id = sharedPreferences.getString("user_id", "0")

        var cost = 0
        val jsonArray = JSONArray()
        val dbOrderedItemsList = RetrieveMyOrder(this@MyCartActivity as Context).execute().get()

        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        txtRestaurantName = findViewById(R.id.txtRestaurantName)
        toolbar = findViewById(R.id.toolbar)
        setUpToolbar()

        //val user_id = "1828"
        var jsonObject = JSONObject()
        for (i in 0 until dbOrderedItemsList.size) {
            cost = cost + dbOrderedItemsList[i].cost
            jsonObject = JSONObject()
            jsonObject.put("food_item_id", dbOrderedItemsList[i].ItemID.toString())
            jsonArray.put(i, jsonObject)
        }
        //println(jsonArray)
        val jsonParams = JSONObject()
        jsonParams.put("user_id", user_id)
        jsonParams.put("restaurant_id", dbOrderedItemsList[0].restaurant_id)
        jsonParams.put("total_cost", cost.toString())
        jsonParams.put("food", jsonArray)

        println(jsonParams)

        recyclerMyCart = findViewById(R.id.recyclerMyCart)
        recyclerAdapter = MyCartRecyclerAdapter(this@MyCartActivity as Context, dbOrderedItemsList)
        layoutManager = LinearLayoutManager(this@MyCartActivity)
        recyclerMyCart.layoutManager = layoutManager
        recyclerMyCart.adapter = recyclerAdapter

        val queue = Volley.newRequestQueue(this@MyCartActivity)
        val url = "http://13.235.250.119/v2/place_order/fetch_result"

        btnPlaceOrder.text = btnPlaceOrder.text.toString() + "(Rs " + cost.toString() + ")"
        txtRestaurantName.text =
            txtRestaurantName.text.toString() + dbOrderedItemsList[0].restaurant_name

        btnPlaceOrder.setOnClickListener() {
            if (ConnectionManager().checkConnectivity(this@MyCartActivity)) {

                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.POST, url, jsonParams, Response.Listener {
                        println("response is $it")
                        try {
                            var bdata = it.getJSONObject("data")
                            val success = bdata.getBoolean("success")
                            if (success) {
                                println("success")
                                val intent =
                                    Intent(this@MyCartActivity, ConfirmationActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                println("error")
                                Toast.makeText(
                                    this@MyCartActivity,
                                    "Some error occured at fetch!!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }//else closing
                        }//try closing
                        catch (e: JSONException) {
                            Toast.makeText(
                                this@MyCartActivity,
                                "JSONException error occured!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }//catch block closing
                    }, Response.ErrorListener {
                        println("error is $it")
                        Toast.makeText(
                            this@MyCartActivity,
                            "Volley error occured!!!",
                            Toast.LENGTH_SHORT
                        ).show()

                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "99d2334c9304fa"
                        return headers
                    }

                }
                queue.add(jsonObjectRequest)
            }//connection manager if block closing
        }//button closing
    }

    class RetrieveMyOrder(val context: Context) :
        AsyncTask<Void, Void, List<OrderedItemsEntity>>() {
        val db = Room.databaseBuilder(context, OrderedItemsDatabase::class.java, "orderedItems_db")
            .build()

        override fun doInBackground(vararg params: Void?): List<OrderedItemsEntity> {

            println("data is ${db.OrderedItemsDao().getAllOrderedItems()}")
            return db.OrderedItemsDao().getAllOrderedItems()

        }
    }

    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}


