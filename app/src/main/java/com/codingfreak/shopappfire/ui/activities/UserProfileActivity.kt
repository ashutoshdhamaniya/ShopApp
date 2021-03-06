package com.codingfreak.shopappfire.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.codingfreak.shopappfire.Firestore.FirestoreClass
import com.codingfreak.shopappfire.R
import com.codingfreak.shopappfire.models.User
import com.codingfreak.shopappfire.utils.Constants
import com.codingfreak.shopappfire.utils.GlideLoader
import com.codingfreak.shopappfire.utils.MSPButton
import com.codingfreak.shopappfire.utils.MSPEditText
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.IOException

class UserProfileActivity : BaseActivity(), View.OnClickListener {

    private lateinit var firstName: MSPEditText
    private lateinit var lastName: MSPEditText
    private lateinit var email: MSPEditText
    private lateinit var mobileNumber: MSPEditText
    private lateinit var btnSave: MSPButton
    private lateinit var userProfileToolbar : Toolbar
    private lateinit var toolbarTitle : TextView
    private lateinit var userImage : ImageView

    private lateinit var userDetails: User
    private var mySelectedImageUri: Uri? = null
    private var userProfileImageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        userProfileToolbar = findViewById(R.id.toolbar_user_profile_activity)
        toolbarTitle = findViewById(R.id.tv_title)
        userImage = findViewById(R.id.iv_user_photo)

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
        btnSave = findViewById(R.id.btn_submit)

        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            userDetails = intent.getParcelableExtra<User>(Constants.EXTRA_USER_DETAILS)!!
        }

        firstName.setText(userDetails.firstName)
        lastName.setText(userDetails.lastName)
        email.setText(userDetails.email)
        email.isEnabled = false

        if(userDetails.profileComplete == 0) {
            toolbarTitle.text = resources.getString(R.string.title_complete_profile)

            firstName.isEnabled = false
            lastName.isEnabled = false

        } else {
            setUpActionBar()
            toolbarTitle.text = resources.getString(R.string.title_edit_profile)
            GlideLoader(this).loadUserPicture(userDetails.image , userImage)

            if (userDetails.mobileNumber != 0L) {
                mobileNumber.setText(userDetails.mobileNumber.toString())
            }

            if (userDetails.gender == Constants.MALE) {
                rb_male.isChecked = true
            } else {
                rb_female.isChecked = true
            }

        }

//        firstName.isEnabled = false
//        lastName.isEnabled = false
//        email.isEnabled = false
//
//        firstName.setText(userDetails.firstName)
//        lastName.setText(userDetails.lastName)
//        email.setText(userDetails.email)

        findViewById<ImageView>(R.id.iv_user_photo).setOnClickListener(this@UserProfileActivity)
        btnSave.setOnClickListener(this@UserProfileActivity)
    }

    private fun setUpActionBar() {
        setSupportActionBar(userProfileToolbar)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_left_24)
        }

        userProfileToolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.iv_user_photo -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        //showErrorSnackBar("Permission Already Granted !!!" , false)
                        Constants.showImageChooser(this)
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }

                R.id.btn_submit -> {
                    if (validateUserProfileDetails()) {
                        //showErrorSnackBar("Fine !!! You can save your details." , false)
                        showProgressDialog()
                        if (mySelectedImageUri != null)
                            FirestoreClass().uploadImageToCloudFirestore(this, mySelectedImageUri , Constants.USER_PROFILE_IMAGE)
                        else
                            updateUserProfileDetails()
                    }
                }
            }
        }
    }

    private fun updateUserProfileDetails() {
        val userHashMap = HashMap<String, Any>()

        val firstName = firstName.text.toString().trim() {it <= ' '}
        if(firstName != userDetails.firstName) {
            userHashMap[Constants.FIRST_NAME] = firstName
        }

        val lastName = lastName.text.toString().trim() { it <= ' '}
        if(lastName != userDetails.lastName) {
            userHashMap[Constants.LAST_NAME] = lastName
        }

        val mobileNumber = mobileNumber.text.toString().trim() { it <= ' ' }
        val gender = if (rb_male.isChecked) {
            Constants.MALE
        } else {
            Constants.FEMALE
        }

        if(userProfileImageUrl.isNotEmpty()) {
            userHashMap[Constants.IMAGE] = userProfileImageUrl
        }

        if (mobileNumber.isNotEmpty() && mobileNumber != userDetails.mobileNumber.toString()) {
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }

        if(gender.isNotEmpty() && gender != userDetails.gender) {
            userHashMap[Constants.GENDER] = gender
        }

        userHashMap[Constants.GENDER] = gender
        userHashMap[Constants.COMPLETE_PROFILE] = 1
        //showProgressDialog()
        FirestoreClass().updateUserProfileData(this, userHashMap)
    }

    fun userProfileUpdateSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this,
            resources.getString(R.string.msg_profile_update_success),
            Toast.LENGTH_SHORT
        ).show()
        startActivity(Intent(this@UserProfileActivity, DashboardActivity::class.java))
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //showErrorSnackBar("Storage Permission Granted !!!" , false)
                Constants.showImageChooser(this)
            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        mySelectedImageUri = data.data!!
//                        findViewById<ImageView>(R.id.iv_user_photo).setImageURI(selectedImageURI)
                        GlideLoader(this).loadUserPicture(
                            mySelectedImageUri!!,
                            findViewById<ImageView>(R.id.iv_user_photo)
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                        showErrorSnackBar(
                            resources.getString(R.string.image_selection_failed),
                            true
                        )
                    }
                }
            }
        } else if(resultCode == Activity.RESULT_CANCELED) {
            Log.d("Ashu" , "Image Selection Cancelled")
        }
    }

    private fun validateUserProfileDetails(): Boolean {
        return when {
            TextUtils.isEmpty(mobileNumber.text.toString().trim() { it <= ' ' }) -> {
                showErrorSnackBar("Please Enter Mobile Number", true)
                false
            }
            else -> {
                true
            }
        }
    }

    fun imageUploadSuccess(imageUrl: String) {
        userProfileImageUrl = imageUrl
        updateUserProfileDetails()
    }
}