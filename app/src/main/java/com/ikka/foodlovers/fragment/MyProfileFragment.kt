package com.ikka.foodlovers.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.ikka.foodlovers.R

/**
 * A simple [Fragment] subclass.
 */
class MyProfileFragment : Fragment() {

    lateinit var txtUser_id : TextView
    lateinit var txtMobile : TextView
    lateinit var txtEmail_id : TextView
    lateinit var txtLocation : TextView

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_my_profile, container, false)

        sharedPreferences = this.getActivity()!!.getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )

        txtUser_id = view.findViewById(R.id.txtUser_id)
        txtMobile = view.findViewById(R.id.txtMobile)
        txtEmail_id = view.findViewById(R.id.txtEmail_id)
        txtLocation = view.findViewById(R.id.txtlocation)

        txtUser_id.text  = sharedPreferences.getString("user_name","pick")
        txtMobile.text  = sharedPreferences.getString("mobile_number","pick")
        txtEmail_id.text  = sharedPreferences.getString("email_id","pick")
        txtLocation.text  = sharedPreferences.getString("location","pick")

        return view
    }

}
