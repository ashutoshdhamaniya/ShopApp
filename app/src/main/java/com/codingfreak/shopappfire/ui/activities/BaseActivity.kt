package com.codingfreak.shopappfire.ui.activities

import android.app.Dialog
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.codingfreak.shopappfire.R
import com.google.android.material.snackbar.Snackbar

open class BaseActivity : AppCompatActivity() {

    private var doublePressToExitPressedOnce = false

    private lateinit var progressDialog: Dialog

    fun showErrorSnackBar(message: String, errorMessage: Boolean) {
        val snackbar: Snackbar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)
        val snackBarView = snackbar.view

        if (errorMessage) {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    this@BaseActivity,
                    R.color.snackBarFailure
                )
            )
        } else {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    this@BaseActivity,
                    R.color.snackBarSuccess
                )
            )
        }

        snackbar.show()
    }

    fun showProgressDialog() {
        progressDialog = Dialog(this)

        progressDialog.setContentView(R.layout.dialog_progress)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setCancelable(false)

        progressDialog.show()
    }

    fun hideProgressDialog() {
        progressDialog.dismiss()
    }

    fun doubleBackToExit() {
        if (doublePressToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        doublePressToExitPressedOnce = true
        Toast.makeText(
            this,
            resources.getString(R.string.please_click_back_again),
            Toast.LENGTH_SHORT
        ).show()

        //  If user doesn't press back within 2 second then it is not consider as double back pressed
        Handler().postDelayed({ doublePressToExitPressedOnce = false }, 200)
    }
}