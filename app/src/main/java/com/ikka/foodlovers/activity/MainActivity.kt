package com.ikka.foodlovers.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.akshay.FoodLoverApp.fragment.FavouriteRestaurantsFragment
import com.google.android.material.navigation.NavigationView
import com.ikka.foodlovers.R
import com.ikka.foodlovers.fragment.DashboardFragment
import com.ikka.foodlovers.fragment.FaqsFragment
import com.ikka.foodlovers.fragment.MyProfileFragment
import com.ikka.foodlovers.fragment.OrderHistoryFragment
import com.akshay.FoodLoverApp.*
import com.akshay.FoodLoverApp.fragment.*

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout : DrawerLayout
    lateinit var coordinatorLayout : CoordinatorLayout
    lateinit var frame : FrameLayout
    lateinit var toolbar : androidx.appcompat.widget.Toolbar
    lateinit var navigationview : NavigationView
    var previousMenuItem: MenuItem? = null
    lateinit var sharedPreferences: SharedPreferences
    lateinit var user_id : TextView
    lateinit var mobile_number : TextView
    lateinit var header : View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        frame = findViewById(R.id.frame)
        toolbar = findViewById(R.id.toolbar)
        navigationview = findViewById(R.id.navigationView)

        setUpToolbar()
        val actionBarDrawerToggle = ActionBarDrawerToggle(this@MainActivity,drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        openDashboard()

        sharedPreferences = getSharedPreferences(
                getString(R.string.preference_file_name),
                Context.MODE_PRIVATE
        )

        val user = sharedPreferences.getString("user_name","XYZ")
        val mobile = "+91-" + sharedPreferences.getString("mobile_number","0123456789")

        println(user)
        println(mobile)

        header = navigationview.getHeaderView(0)
        user_id = header.findViewById(R.id.user_id)
        mobile_number = header.findViewById(R.id.mobile_number)

        user_id.text = user
        mobile_number.text = mobile

        navigationview.setNavigationItemSelectedListener {

            if(previousMenuItem != null)
                previousMenuItem?.isChecked = false
            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it



            when(it.itemId) {
                R.id.dashboard -> {

                    openDashboard()
                    drawerLayout.closeDrawers()
                }
                R.id.myProfile -> {
                    supportFragmentManager.beginTransaction().replace(
                            R.id.frame,
                            MyProfileFragment()
                    ).commit()
                    supportActionBar?.title = "My Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.favouriteRestaurants -> {
                    supportFragmentManager.beginTransaction().replace(
                            R.id.frame,
                            FavouriteRestaurantsFragment()
                    ).commit()
                    supportActionBar?.title = "Favourite Restaurants"
                    drawerLayout.closeDrawers()
                }
                R.id.orderHistory -> {
                    supportFragmentManager.beginTransaction().replace(
                            R.id.frame,
                            OrderHistoryFragment()
                    ).commit()
                    supportActionBar?.title = "My Previous Orders"
                    drawerLayout.closeDrawers()
                }
                R.id.faqs -> {
                    supportFragmentManager.beginTransaction().replace(
                            R.id.frame,
                            FaqsFragment()
                    ).commit()
                    supportActionBar?.title = "FAQs"
                    drawerLayout.closeDrawers()
                }
                R.id.logOut -> {
                    println("clicked logOut")

                    //Toast.makeText(this@MainActivity,"CLICKED ON LOG OUT",Toast.LENGTH_SHORT).show()
                    //drawerLayout.closeDrawers()
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("Confirmation")
                    dialog.setMessage("Are you sure you want to Log Out ???")
                    dialog.setPositiveButton("Yes")
                    {
                        text, listener -> println("pressed yes")
                        var intent = Intent(this@MainActivity, LoginActivity::class.java)
                        Toast.makeText(this@MainActivity,"Logging You Out", Toast.LENGTH_SHORT).show()
                        sharedPreferences.edit().clear().apply()
                        finish()
                        startActivity(intent)
                    }
                    dialog.setNegativeButton("No")
                    {
                        text , listener -> println("pressed no")
                    }
                    dialog.create()
                    dialog.show()
                    //drawerLayout.closeDrawers()
                } //log out closes

            }//when clsoing
            return@setNavigationItemSelectedListener true
        }//navigation select closung

    }//on create closing

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if(id == android.R.id.home)
            drawerLayout.openDrawer(GravityCompat.START)
        return super.onOptionsItemSelected(item)
    }

    fun setUpToolbar()
    {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "FoodLover App"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun openDashboard()
    {
        val fragment =
                DashboardFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment)
        transaction.commit()
        supportActionBar?.title = "All Restaurants"
    }

    override fun onBackPressed()
    {
        val fragment = supportFragmentManager.findFragmentById(R.id.frame)
        when (fragment) {
            !is DashboardFragment -> openDashboard()
            else -> super.onBackPressed()

        }
    }
}
