package com.codingfreak.shopappfire.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.codingfreak.shopappfire.R
import com.codingfreak.shopappfire.utils.MSPButton
import com.codingfreak.shopappfire.utils.MSPEditText
import com.google.firebase.auth.FirebaseAuth

class ForgetPasswordActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        setUpActionBar()

        findViewById<MSPButton>(R.id.btn_submit).setOnClickListener {
            val email =
                findViewById<MSPEditText>(R.id.et_forget_email).text.toString().trim() { it <= ' ' }

            if (email.isEmpty()) {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
            } else {
                showProgressDialog()

                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { taskId ->
                        run {
                            if (taskId.isSuccessful) {
                                hideProgressDialog()
                                Toast.makeText(
                                    this,
                                    "Email Send to reset your password !!!",
                                    Toast.LENGTH_LONG
                                ).show()
                                finish()
                            } else {
                                hideProgressDialog()
                                taskId.exception?.message?.let { it1 ->
                                    showErrorSnackBar(
                                        it1,
                                        true
                                    )
                                }
                            }
                        }
                    }
            }
        }
    }

    private fun setUpActionBar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar_forgot_password_activity)
        toolbar.title = ""
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_left_24)
        }

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}