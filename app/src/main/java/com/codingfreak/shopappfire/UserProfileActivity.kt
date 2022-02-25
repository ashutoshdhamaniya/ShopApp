package com.codingfreak.shopappfire

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import com.codingfreak.models.User
import com.codingfreak.utils.Constants
import com.codingfreak.utils.MSPEditText

class UserProfileActivity : AppCompatActivity() {

    private lateinit var firstName : MSPEditText
    private lateinit var lastName : MSPEditText
    private lateinit var email : MSPEditText
    private lateinit var mobileNumber : MSPEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        firstName = findViewById(R.id.et_first_name)
        lastName = findViewById(R.id.et_last_name)
        email = findViewById(R.id.et_email)
        mobileNumber = findViewById(R.id.et_mobile_number)

        var userDetails = User()

        if(intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            userDetails = intent.getParcelableExtra<User>(Constants.EXTRA_USER_DETAILS)!!
        }

        firstName.isEnabled = false
        lastName.isEnabled = false
        email.isEnabled = false

        firstName.setText(userDetails.firstName)
        lastName.setText(userDetails.lastName)
        email.setText(userDetails.email)

        
    }
}