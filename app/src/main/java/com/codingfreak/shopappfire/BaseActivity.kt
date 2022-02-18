package com.codingfreak.shopappfire

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

open class BaseActivity : AppCompatActivity() {

    private lateinit var progressDialog : Dialog

    fun showErrorSnackBar(message : String , errorMessage : Boolean) {
        val snackbar : Snackbar = Snackbar.make(findViewById(android.R.id.content) , message ,Snackbar.LENGTH_SHORT)
        val snackBarView = snackbar.view

        if(errorMessage) {
            snackBarView.setBackgroundColor(ContextCompat.getColor(this@BaseActivity , R.color.snackBarFailure))
        } else {
            snackBarView.setBackgroundColor(ContextCompat.getColor(this@BaseActivity , R.color.snackBarSuccess))
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

}