package com.codingfreak.shopappfire

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.codingfreak.Firestore.FirestoreClass
import com.codingfreak.models.User
import com.codingfreak.utils.Constants
import com.codingfreak.utils.GlideLoader
import com.codingfreak.utils.MSPButton
import com.codingfreak.utils.MSPEditText
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.IOException

class UserProfileActivity : BaseActivity(), View.OnClickListener {

    private lateinit var firstName: MSPEditText
    private lateinit var lastName: MSPEditText
    private lateinit var email: MSPEditText
    private lateinit var mobileNumber: MSPEditText
    private lateinit var btnSave: MSPButton

    private lateinit var userDetails: User

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
        btnSave = findViewById(R.id.btn_submit)

        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            userDetails = intent.getParcelableExtra<User>(Constants.EXTRA_USER_DETAILS)!!
        }

        firstName.isEnabled = false
        lastName.isEnabled = false
        email.isEnabled = false

        firstName.setText(userDetails.firstName)
        lastName.setText(userDetails.lastName)
        email.setText(userDetails.email)

        findViewById<ImageView>(R.id.iv_user_photo).setOnClickListener(this@UserProfileActivity)
        btnSave.setOnClickListener(this@UserProfileActivity)
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
                        val userHashMap = HashMap<String, Any>()

                        val mobileNumber = mobileNumber.text.toString().trim() { it <= ' ' }
                        val gender = if (rb_male.isChecked) {
                            Constants.MALE
                        } else {
                            Constants.FEMALE
                        }

                        if(mobileNumber.isNotEmpty()) {
                            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
                        }
                        userHashMap[Constants.GENDER] = gender

                        showProgressDialog()

                        FirestoreClass().updateUserProfileData(this , userHashMap)
                    }
                }
            }
        }
    }

    fun userProfileUpdateSuccess() {
        hideProgressDialog()
        Toast.makeText(this , resources.getString(R.string.msg_profile_update_success) , Toast.LENGTH_SHORT).show()
        startActivity(Intent(this@UserProfileActivity , MainActivity::class.java))
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
                        val selectedImageURI = data.data!!
//                        findViewById<ImageView>(R.id.iv_user_photo).setImageURI(selectedImageURI)
                        GlideLoader(this).loadUserPicture(
                            selectedImageURI,
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
}