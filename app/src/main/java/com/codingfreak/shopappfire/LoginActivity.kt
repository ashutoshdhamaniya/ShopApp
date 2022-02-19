package com.codingfreak.shopappfire

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.codingfreak.Firestore.FirestoreClass
import com.codingfreak.models.User
import com.codingfreak.utils.MSPButton
import com.codingfreak.utils.MSPEditText
import com.codingfreak.utils.MSPTextView
import com.codingfreak.utils.MSPTextViewBold
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

        Log.d("Ashu" , user.firstName)
        Log.d("Ashu" , user.lastName)
        Log.d("Ashu" , user.email)

        if(user.profileComplete == 0) {
            val intent = Intent(this@LoginActivity , UserProfileActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this@LoginActivity , MainActivity::class.java)
            startActivity(intent)
        }
        finish()
    }
}