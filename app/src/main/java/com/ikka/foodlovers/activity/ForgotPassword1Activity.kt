package com.ikka.foodlovers.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

class ForgotPassword1Activity : AppCompatActivity() {

    lateinit var etMobileNumber : EditText
    lateinit var etEmailID : EditText
    lateinit var btnNext : Button
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password1)

        etMobileNumber = findViewById(R.id.etMobileNumber)
        etEmailID = findViewById(R.id.etEmailID)
        btnNext = findViewById(R.id.btnNext)

        sharedPreferences = getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )

        btnNext.setOnClickListener {

            var mobile = etMobileNumber.text.toString()
            var email = etEmailID.text.toString()

            val queue = Volley.newRequestQueue(this@ForgotPassword1Activity)

            val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

            if(ConnectionManager().checkConnectivity(this@ForgotPassword1Activity))
            {
                //println("inside connection manager")
                val jsonParams = JSONObject()

                jsonParams.put("mobile_number",mobile)
                jsonParams.put("email",email)

                //println("paramneters are ready")

                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.POST, url, jsonParams,
                    Response.Listener {
                        //println("Response is $it")
                        try {
                            //println("inside try block")
                            val bdata = it.getJSONObject("data")
                            val success = bdata.getBoolean("success")
                            if (success) {
                                //println("succcess")
                                val first_try = bdata.getBoolean("first_try")

                                if(first_try)
                                    Toast.makeText(this@ForgotPassword1Activity,"Taking you to Password Reset Page.The OTP is being sent to you through email.",
                                        Toast.LENGTH_LONG).show()
                                else
                                    Toast.makeText(this@ForgotPassword1Activity,"Taking you to Password Reset Page.The OTP has already been sent to you through emailCheck email please.",
                                        Toast.LENGTH_LONG).show()

                                sharedPreferences.edit().putString("mobile_number", mobile).apply()
                                sharedPreferences.edit().putString("email_id", email).apply()

                                val intent = Intent( this@ForgotPassword1Activity , ForgotPassword2Activity::class.java )
                                startActivity(intent)

                            } else {
                                val message = bdata.getString("errorMessage")
                                Toast.makeText(
                                    this@ForgotPassword1Activity,
                                    message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }//else closing of if(success block implying data received correctly
                        }//try closing
                        catch (e: JSONException) {
                            Toast.makeText(
                                this@ForgotPassword1Activity,
                                "JSONException error occured!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }//catch block closing
                    },
                    Response.ErrorListener{
                        //println("Response has some error and error is $it")
                        Toast.makeText(
                            this@ForgotPassword1Activity,
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
        }//closing of the button on click listener block
    }
    override fun onBackPressed()
    {
        sharedPreferences.edit().clear().apply()
        finish()
        startActivity(Intent( this@ForgotPassword1Activity , LoginActivity::class.java ))
    }

}

