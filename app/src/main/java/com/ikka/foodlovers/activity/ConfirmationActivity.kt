package com.ikka.foodlovers.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.ikka.foodlovers.R

class ConfirmationActivity : AppCompatActivity() {

    lateinit var btnOK : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)

        btnOK = findViewById(R.id.btnOK)
        btnOK.setOnClickListener(){

            startActivity(Intent(this@ConfirmationActivity,
                MainActivity::class.java))
        }
    }
    override fun onBackPressed()
    {
        startActivity(Intent( this@ConfirmationActivity , MainActivity::class.java ))
    }
}