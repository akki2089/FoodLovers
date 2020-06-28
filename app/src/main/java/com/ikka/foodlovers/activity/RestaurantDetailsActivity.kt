package com.ikka.foodlovers.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room.databaseBuilder
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ikka.foodlovers.R
import com.ikka.foodlovers.adapter.RestaurantMenuItemsRecyclerAdapter
import com.ikka.foodlovers.database.OrderedItemsDatabase
import com.ikka.foodlovers.model.RestaurantMenuItems
import com.ikka.foodlovers.util.ConnectionManager
import org.json.JSONException
import java.lang.Exception

class RestaurantDetailsActivity : AppCompatActivity() {

    lateinit var recyclerRestaurantItems : RecyclerView
    lateinit var layoutManager : RecyclerView.LayoutManager
    lateinit var recyclerAdapter : RestaurantMenuItemsRecyclerAdapter

    val RestaurantInfoList = arrayListOf<RestaurantMenuItems>()

    lateinit var progressBar : ProgressBar
    lateinit var progressLayout : RelativeLayout

    lateinit var btnProceedToCart : Button
    lateinit var toolbar : androidx.appcompat.widget.Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_details)

        //toolbar = findViewById(R.id.toolbar)

        progressLayout = findViewById(R.id.progressLayout)

        progressBar = findViewById(R.id.progressBar)

        toolbar = findViewById(R.id.toolbar)


        progressLayout.visibility = View.VISIBLE

        recyclerRestaurantItems = findViewById(R.id.recyclerRestaurantItems)

        btnProceedToCart = findViewById(R.id.btnProceedToCart)

        //imgChooseFavourite = findViewById(R.id.imgChooseFavourite)

        btnProceedToCart.visibility = View.GONE

        layoutManager = LinearLayoutManager(this@RestaurantDetailsActivity)

        val queue = Volley.newRequestQueue(this@RestaurantDetailsActivity)

        if(intent!=null)
        {
            val RestaurantID = intent.getStringExtra("restaurant_id")

            val toollbarTitle = intent.getStringExtra("restaurant_name")

            setUpToolbar(toollbarTitle)

            //println(toollbarTitle)

            //toolbar.title = toollbarTitle

            if(RestaurantID == "")
                println("data in intent is lost")
            else
            {
                var url:String = "http://13.235.250.119/v2/restaurants/fetch_result/"
                url = url + RestaurantID
                println("respone for url $url")

                if(ConnectionManager().checkConnectivity(this@RestaurantDetailsActivity))
                {
                    val jsonObjectRequest = object : JsonObjectRequest(
                        Request.Method.GET,url,null,
                        Response.Listener {
                            println("Response is $it")
                            try
                            {
                                //println("inside try")
                                progressLayout.visibility = View.GONE

                                val bdata = it.getJSONObject("data")
                                val success = bdata.getBoolean("success")
                                if(success)
                                {
                                    println("response println i had success")

                                    val data = bdata.getJSONArray("data")
                                    for(i in 0 until data.length()) {
                                        val RestaurantJsonObject = data.getJSONObject(i)
                                        val RestaurantObject = RestaurantMenuItems(
                                            RestaurantJsonObject.getString("id"),
                                            RestaurantJsonObject.getString("name"),
                                            RestaurantJsonObject.getString("cost_for_one"),
                                            RestaurantJsonObject.getString("restaurant_id")
                                        )
                                        RestaurantInfoList.add(RestaurantObject)

                                        println("response data fetch and save over")

                                        recyclerAdapter =
                                            RestaurantMenuItemsRecyclerAdapter(
                                                this@RestaurantDetailsActivity,
                                                RestaurantInfoList,RestaurantID = RestaurantID,RestaurantName = toollbarTitle ,btnProceedToCart = btnProceedToCart)

                                        recyclerRestaurantItems.adapter = recyclerAdapter
                                        recyclerRestaurantItems.layoutManager = layoutManager
                                    }

                                }
                                else
                                {
                                    Toast.makeText(this@RestaurantDetailsActivity,"Some error occured at fetch!!!",
                                        Toast.LENGTH_SHORT).show()
                                }
                            }//closing of try block
                            catch(e : JSONException)
                            {
                                Toast.makeText(this@RestaurantDetailsActivity,"JSONException error occured!!!", Toast.LENGTH_SHORT).show()
                            }
                            catch (e : Exception)
                            {
                                println("response i was herre")
                                println("response error is ")
                            }


                        },
                        Response.ErrorListener {
                            println("Response has some error and error is $it")
                            Toast.makeText(this@RestaurantDetailsActivity,"Volley error occured!!!", Toast.LENGTH_SHORT).show()

                        }
                    )
                    {
                        override fun getHeaders() : MutableMap<String,String>
                        {
                            val headers = HashMap<String,String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "99d2334c9304fa"
                            return headers
                        }

                    }

                    queue.add(jsonObjectRequest)
                }//closing of if block of connectivity manager which checked for internet connection

                else
                {
                    val dialog = AlertDialog.Builder(this@RestaurantDetailsActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection Not Found")
                    dialog.setPositiveButton("Open Settings")
                    {
                            text, listener -> val settingsIntent =  Intent(Settings.ACTION_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }
                    dialog.setNegativeButton("Exit")
                    {
                            text, listener -> ActivityCompat.finishAffinity(this@RestaurantDetailsActivity)
                    }
                    dialog.create()
                    dialog.show()
                }//else closing for dialog box

            }//closing of else block of data in intent received proper or not

        }//closing of if block of intent

        btnProceedToCart.setOnClickListener()
        {
            var intent = Intent(this@RestaurantDetailsActivity,MyCartActivity::class.java)
            startActivity(intent)
        }

    }
    override fun onBackPressed()
    {
        DBAsyncTask(this@RestaurantDetailsActivity).execute().get()
        Toast.makeText(this@RestaurantDetailsActivity,"Cart Empty Success", Toast.LENGTH_SHORT).show()
        super.onBackPressed()

    }

    class DBAsyncTask(val context: Context) :
        AsyncTask<Void, Void, Boolean>()
    {

        override fun doInBackground(vararg params: Void?) : Boolean
        {

            val dbdelete = databaseBuilder(context, OrderedItemsDatabase::class.java,"orderedItems_db").build()
            //val db = Room.databaseBuilder(context, RestaurantDatabase::class.java,"restaurant_db").build()
            dbdelete.OrderedItemsDao().deleteAll()
            dbdelete.close()
            return true

        }//doInBackground function closing

    }//AsyncTask class closing

    fun setUpToolbar(RestaurantName : String)
    {
        setSupportActionBar(toolbar)
        supportActionBar?.title = RestaurantName
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
