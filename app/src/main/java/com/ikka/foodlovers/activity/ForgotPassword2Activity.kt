package com.ikka.foodlovers.activity

import android.app.DownloadManager
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

class ForgotPassword2Activity : AppCompatActivity() {

    lateinit var etOTP : EditText
    lateinit var etPassword : EditText
    lateinit var etConfirmPassword : EditText
    lateinit var btnSubmit : Button
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password2)

        etOTP = findViewById(R.id.etOTP)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnSubmit = findViewById(R.id.btnSubmit)

        sharedPreferences = getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )

        btnSubmit.setOnClickListener {

            var OTP = etOTP.text.toString()
            var password1 = etPassword.text.toString()
            var password2 = etConfirmPassword.text.toString()
            var mobile  = sharedPreferences.getString("mobile_number","null")

            println("mobile is ${mobile}")

            if(password1.length < 4)
                Toast.makeText(this@ForgotPassword2Activity, "Password should have minimum 4 characters!!!", Toast.LENGTH_LONG).show()
            else
            {
                if(password1 == password2)
                {
                    val queue = Volley.newRequestQueue(this@ForgotPassword2Activity)

                    val url = "http://13.235.250.119/v2/reset_password/fetch_result"

                    if(ConnectionManager().checkConnectivity(this@ForgotPassword2Activity))
                    {
                        println("inside connection manager")
                        val jsonParams = JSONObject()

                        jsonParams.put("mobile_number",mobile)
                        jsonParams.put("password",password1)
                        jsonParams.put("otp",OTP)


                        println("paramneters are ready")

                        val jsonObjectRequest = object : JsonObjectRequest(
                            Request.Method.POST, url, jsonParams,
                            Response.Listener {
                                println("Response is $it")
                                try {
                                    println("inside try block")
                                    val bdata = it.getJSONObject("data")
                                    val success = bdata.getBoolean("success")
                                    if (success) {
                                        println("succcess")
                                        val success_message = bdata.getString("successMessage")

                                        Toast.makeText(this@ForgotPassword2Activity,success_message,Toast.LENGTH_LONG).show()

                                        val intent = Intent(this@ForgotPassword2Activity , LoginActivity::class.java)
                                        startActivity(intent)
                                        finish()

                                    } else {
                                        val message = bdata.getString("errorMessage")
                                        Toast.makeText(
                                            this@ForgotPassword2Activity,
                                            message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }//else closing of if(success block implying data received correctly
                                }//try closing
                                catch (e: JSONException) {
                                    Toast.makeText(
                                        this@ForgotPassword2Activity,
                                        "JSONException error occured!!!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }//catch block closing
                            },
                            Response.ErrorListener{
                                println("Response has some error and error is $it")
                                Toast.makeText(
                                    this@ForgotPassword2Activity,
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
                        println(jsonObjectRequest)
                        queue.add(jsonObjectRequest)
                    }//if closing of connection manager
                }//closing of if block of passsword matching
                else
                    Toast.makeText(this@ForgotPassword2Activity,"Passwords do not Match",Toast.LENGTH_SHORT).show()
            }//closing the clse block of password check of length <= 4
        }//closing of button listener
    }
}
