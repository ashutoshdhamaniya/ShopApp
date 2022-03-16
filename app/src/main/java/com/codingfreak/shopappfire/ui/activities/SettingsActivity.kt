package com.codingfreak.shopappfire.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import com.codingfreak.shopappfire.Firestore.FirestoreClass
import com.codingfreak.shopappfire.R
import com.codingfreak.shopappfire.models.User
import com.codingfreak.shopappfire.utils.*
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : BaseActivity(), View.OnClickListener {

    private lateinit var settingsToolbar: Toolbar
    private lateinit var profileImage: ImageView
    private lateinit var userName: MSPTextViewBold
    private lateinit var gender: MSPTextView
    private lateinit var email: MSPTextView
    private lateinit var mobileNumber: MSPTextView
    private lateinit var logoutButton : MSPButton
    private lateinit var editButton : MSPTextViewBold

    private lateinit var userDetails : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        settingsToolbar = findViewById(R.id.toolbar_settings_activity)
        profileImage = findViewById(R.id.iv_user_photo)
        userName = findViewById(R.id.tv_name)
        gender = findViewById(R.id.tv_gender)
        email = findViewById(R.id.tv_email)
        mobileNumber = findViewById(R.id.tv_mobile_number)
        logoutButton = findViewById(R.id.btn_logout)
        editButton = findViewById(R.id.tv_edit)

        logoutButton.setOnClickListener(this)
        editButton.setOnClickListener(this)

        setSupportActionBar(settingsToolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_left_24)
        }

        settingsToolbar.setNavigationOnClickListener { onBackPressed() }
    }

    // Get The User Details From Firestore
    private fun getUserDetails() {
        showProgressDialog()
        FirestoreClass().getUserDetails(this)
    }

    @SuppressLint("SetTextI18n")
    fun userDetailsSuccess(user: User) {
        userDetails = user

        hideProgressDialog()

        // Load The Data Inside Settings Activity
        GlideLoader(this).loadUserPicture(user.image, profileImage)
        userName.text = "${user.firstName} ${user.lastName}"
        gender.text = user.gender
        email.text = user.email
        mobileNumber.text = "${user.mobileNumber}"
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {

                R.id.tv_edit -> {
                    val profileIntent : Intent = Intent(this , UserProfileActivity::class.java)
                    profileIntent.putExtra(Constants.EXTRA_USER_DETAILS , userDetails)
                    startActivity(profileIntent)
                }

                R.id.btn_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val loginIntent: Intent =
                        Intent(this@SettingsActivity, LoginActivity::class.java)
                    loginIntent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(loginIntent)
                    finish()
                }
            }
        }
    }
}