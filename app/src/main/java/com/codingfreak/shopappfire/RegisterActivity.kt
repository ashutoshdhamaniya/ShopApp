package com.codingfreak.shopappfire

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.codingfreak.Firestore.FirestoreClass
import com.codingfreak.models.User
import com.codingfreak.utils.MSPButton
import com.codingfreak.utils.MSPEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : BaseActivity() {

    private lateinit var loginText : TextView
    private lateinit var registerBtn : MSPButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        setUpActionBar()

        loginText = findViewById(R.id.tv_login)

        loginText.setOnClickListener {
            onBackPressed()
        }

        registerBtn = findViewById(R.id.btn_register)
        registerBtn.setOnClickListener {
            registerUser()
        }
    }

    private fun setUpActionBar() {
        val toolbar : Toolbar = findViewById(R.id.toolbar_register_activity)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        Log.d("Ashu" , "Showing1")
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_left_24)

            Log.d("Ashu" , "Showing")
        }

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun validateRegisterDetails(): Boolean {

        val firstName : MSPEditText = findViewById(R.id.et_first_name)
        val lastName : MSPEditText = findViewById(R.id.et_last_name)
        val email : MSPEditText = findViewById(R.id.et_email)
        val password : MSPEditText = findViewById(R.id.et_password)
        val confirmPassword : MSPEditText = findViewById(R.id.et_confirm_password)
        val terms : CheckBox = findViewById(R.id.cb_terms_and_condition)

        return when {
            TextUtils.isEmpty(firstName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }

            TextUtils.isEmpty(lastName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }

            TextUtils.isEmpty(email.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }

            TextUtils.isEmpty(confirmPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_confirm_password), true)
                false
            }

            password.text.toString().trim { it <= ' ' } != confirmPassword.text.toString()
                .trim { it <= ' ' } -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_password_and_confirm_password_mismatch), true)
                false
            }
            !terms.isChecked -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_agree_terms_and_condition), true)
                false
            }
            else -> {
                //showErrorSnackBar(resources.getString(R.string.registration_successful), false)
                true
            }
        }
    }

    private fun registerUser() {
        if(validateRegisterDetails()) {
            showProgressDialog()
            val email : String = findViewById<MSPEditText>(R.id.et_email).text.toString().trim { it <= ' ' }
            val password : String = findViewById<MSPEditText>(R.id.et_password).text.toString().trim { it <= ' ' }

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email , password).addOnCompleteListener { taskId ->
                if (taskId.isSuccessful) {
                    //hideProgressDialog()

                    val firebaseUser : FirebaseUser = FirebaseAuth.getInstance().currentUser!!

                    val user : User = User(
                        firebaseUser.uid,
                        findViewById<MSPEditText>(R.id.et_first_name).text.toString().trim() {it <= ' '},
                        findViewById<MSPEditText>(R.id.et_last_name).text.toString().trim() {it <= ' '},
                        email
                    )

                    //showErrorSnackBar("Registration Successful" , false)

                    FirestoreClass().registerUser(this@RegisterActivity , user)

//                    Handler().postDelayed({
//                        FirebaseAuth.getInstance().signOut()
//                        finish()
//                    } , 1000)

                } else {
                    hideProgressDialog()
                    taskId.exception?.message?.let { showErrorSnackBar(it , true) }
                }
            }
        }
    }

    fun userRegistrationSuccess() {
        hideProgressDialog()

        Toast.makeText(this , resources.getString(R.string.registration_successful) , Toast.LENGTH_LONG).show()
    }
}