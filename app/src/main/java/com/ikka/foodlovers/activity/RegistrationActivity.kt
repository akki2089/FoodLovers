package com.ikka.foodlovers.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ikka.foodlovers.R
import com.ikka.foodlovers.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class RegistrationActivity : AppCompatActivity() {

    lateinit var etUserName : EditText
    lateinit var etEmailID : EditText
    lateinit var etMobileNumber : EditText
    lateinit var etLocation : EditText
    lateinit var etPassword : EditText
    lateinit var etConfirmPassword : EditText
    lateinit var btnRegister : Button
    lateinit var sharedPreferences: SharedPreferences
    lateinit var toolbar : androidx.appcompat.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        etUserName = findViewById(R.id.etUserName)
        etEmailID = findViewById(R.id.etEmailID)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etLocation = findViewById(R.id.etLocation)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        toolbar = findViewById(R.id.toolbar)

        setUpToolbar()

        sharedPreferences = getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )
        var intent = Intent( this@RegistrationActivity , MainActivity::class.java )

        btnRegister.setOnClickListener {


            var username = etUserName.text.toString()

            if(username.length < 3)
                Toast.makeText(this@RegistrationActivity, "Username should have minimum 3 characters!!!", Toast.LENGTH_LONG).show()
            else
            {

                var emailaddress = etEmailID.text.toString()
                var check = emailaddress.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailaddress).matches()
                if(check)
                {
                    var mobile = etMobileNumber.text.toString()
                    if(mobile.length<10)
                        Toast.makeText(this@RegistrationActivity, "Mobile number entered is not matching standards!!!.", Toast.LENGTH_LONG).show()
                    else
                    {
                        var location = etLocation.text.toString()
                        if (location.isEmpty())
                            Toast.makeText(
                                this@RegistrationActivity,
                                "Enter Location!!!",
                                Toast.LENGTH_LONG
                            ).show()
                        else
                        {
                            var password1 = etPassword.text.toString()
                            if(password1.length < 4)
                                Toast.makeText(this@RegistrationActivity, "Password should have minimum 4 characters!!!", Toast.LENGTH_LONG).show()
                            else
                            {
                                var password2 = etConfirmPassword.text.toString()
                                if (password1 == password2)
                                {
                                    //all requirements met ready to register

                                    //ready to make arrangements for posting a request
                                    val queue = Volley.newRequestQueue(this@RegistrationActivity)

                                    val url = "http://13.235.250.119/v2/register/fetch_result"

                                    //http://13.235.250.119/v2/place_order/fetch_result/

                                    if(ConnectionManager().checkConnectivity(this@RegistrationActivity))
                                    {
                                        //println("inside connection manager")
                                        val jsonParams = JSONObject()
                                        jsonParams.put("name",username)
                                        jsonParams.put("mobile_number",mobile)
                                        jsonParams.put("password",password1)
                                        jsonParams.put("address",location)
                                        jsonParams.put("email",emailaddress)
                                        //println(jsonParams)
                                        //println("paramneters are ready")
                                        val jsonObjectRequest = object : JsonObjectRequest(
                                            Request.Method.POST, url, jsonParams,
                                            Response.Listener {
                                            //println("Response is $it")
                                            try {
                                                println("inside try block")
                                                val bdata = it.getJSONObject("data")
                                                val success = bdata.getBoolean("success")
                                                if (success) {
                                                    //println("succcess")
                                                    val data = bdata.getJSONObject("data")

                                                    val user_id = data.getString("user_id")
                                                    username = data.getString("name")
                                                    emailaddress = data.getString("email")
                                                    mobile = data.getString("mobile_number")
                                                    location = data.getString("address")

                                                    //println("name is ${username}")

                                                    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                                                    sharedPreferences.edit().putString("user_id", user_id).apply()
                                                    sharedPreferences.edit().putString("user_name",username).apply()
                                                    sharedPreferences.edit().putString("mobile_number", mobile).apply()
                                                    sharedPreferences.edit().putString("email_id", emailaddress).apply()
                                                    sharedPreferences.edit().putString("location", location).apply()

                                                    //println("transerring to new activity using intent")

                                                    startActivity(intent)
                                                    finish()

                                                }//if block of success closing
                                                else {
                                                    val message = bdata.getString("errorMessage")
                                                    //println("response is $it")
                                                    //println("error")
                                                    Toast.makeText(
                                                        this@RegistrationActivity,
                                                        message,
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }//else closing of if(success block implying data received correctly
                                            }//try closing
                                            catch (e: JSONException) {
                                                Toast.makeText(
                                                    this@RegistrationActivity,
                                                    "JSONException error occured!!!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }//catch block closing
                                        },
                                            Response.ErrorListener{
                                                //println("Response has some error and error is $it")
                                                Toast.makeText(
                                                    this@RegistrationActivity,
                                                    "Volley error occured!!!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            })
                                        {
                                            override fun getHeaders(): MutableMap<String, String> {
                                                val headers = HashMap<String,String>()
                                                headers["Content-type"] = "application/json"
                                                headers["token"] = "99d2334c9304fa"
                                                return headers
                                            }

                                        }
                                        queue.add(jsonObjectRequest)
                                    }//if closing of connection manager

                                } //if block of password match
                                else
                                {
                                    Toast.makeText(
                                        this@RegistrationActivity,
                                        "Passwors do not match!!!.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }//else block for password match
                            }//else block for password length
                        }//else block for location
                    }//else block for mobile number
                }//if block for email address
                else
                {
                    Toast.makeText(this@RegistrationActivity, "Email Address does not match standards!!!.", Toast.LENGTH_LONG).show()
                }//else block for email address
            }//else block for user name
        }//closing of the register button listener
    }//closing of on create
    override fun onPause() {
        super.onPause()
        finish()
    }

    override fun onBackPressed()
    {
        sharedPreferences.edit().clear().apply()
        finish()
        startActivity(Intent( this@RegistrationActivity , LoginActivity::class.java ))
    }
    fun setUpToolbar()
    {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
