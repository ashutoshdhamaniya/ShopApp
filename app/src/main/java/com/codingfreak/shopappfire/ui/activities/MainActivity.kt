package com.codingfreak.shopappfire.ui.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.codingfreak.shopappfire.R
import com.codingfreak.shopappfire.utils.Constants

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences(Constants.MY_SHOP_PREFERENCES , Context.MODE_PRIVATE)
        val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME , "")

        findViewById<TextView>(R.id.tv_main).text = "Hello $username"

    }
}