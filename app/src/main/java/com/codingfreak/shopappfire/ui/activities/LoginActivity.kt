package com.codingfreak.shopappfire.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.codingfreak.shopappfire.Firestore.FirestoreClass
import com.codingfreak.shopappfire.R
import com.codingfreak.shopappfire.models.User
import com.codingfreak.shopappfire.utils.*
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        findViewById<MSPButton>(R.id.btn_login).setOnClickListener(this)
        findViewById<MSPTextView>(R.id.tv_forgot_password).setOnClickListener(this)
        findViewById<MSPTextViewBold>(R.id.tv_register).setOnClickListener(this)

    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.tv_forgot_password -> {
                startActivity(Intent(this , ForgetPasswordActivity::class.java))
            }

            R.id.btn_login -> {
                loginUser()

            }

            R.id.tv_register -> {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
        }
    }

    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(findViewById<MSPEditText>(R.id.et_email).text?.trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(findViewById<MSPEditText>(R.id.et_password).text?.trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                //showErrorSnackBar("Login Success !!!", false)
                true
            }
        }
    }

    private fun loginUser() {
        if (validateLoginDetails()) {
            showProgressDialog()

            val email =
                findViewById<MSPEditText>(R.id.et_email).text.toString().trim() { it <= ' ' }
            val password =
                findViewById<MSPEditText>(R.id.et_password).text.toString().trim() { it <= ' ' }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { taskId ->
                    run {
                        if (taskId.isSuccessful) {
                            FirestoreClass().getUserDetails(this@LoginActivity)
                        } else {
                            hideProgressDialog()
                            taskId.exception?.message?.let { showErrorSnackBar(it, true) }
                        }
                    }
                }
        }
    }

    fun userLoggedInSuccess(user : User) {
        hideProgressDialog()

        if(user.profileComplete == 0) {
            Log.d("Ashu" , "User Profile Success")
            val intent = Intent(this@LoginActivity , UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS , user)
            startActivity(intent)
        } else {
            Log.d("Ashu" , "Dashboard Success")
            val intent = Intent(this@LoginActivity , DashboardActivity::class.java)
            startActivity(intent)
        }
        finish()
    }
}