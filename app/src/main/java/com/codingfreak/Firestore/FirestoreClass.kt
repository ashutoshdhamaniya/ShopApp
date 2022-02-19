package com.codingfreak.Firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.codingfreak.models.User
import com.codingfreak.shopappfire.LoginActivity
import com.codingfreak.shopappfire.RegisterActivity
import com.codingfreak.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {

    private val myFirestore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: User) {

        myFirestore.collection(Constants.USERS).document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }.addOnFailureListener { e ->
                run {
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error While Registering User", e)
                }
            }
    }

    fun getCurrentUserId(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserId: String = ""
        if (currentUser != null) {
            currentUserId = currentUser.uid
        }

        return currentUserId
    }

    fun getUserDetails(activity: Activity) {
        myFirestore.collection(Constants.USERS).document(getCurrentUserId()).get()
            .addOnSuccessListener { it ->
                run {
                    Log.i(activity.javaClass.simpleName, it.toString())
                    val user = it.toObject(User::class.java)

                    val sharedPreferences: SharedPreferences = activity.getSharedPreferences(
                        Constants.MY_SHOP_PREFERENCES,
                        Context.MODE_PRIVATE
                    )
                    val editor: SharedPreferences.Editor = sharedPreferences.edit().putString(
                        Constants.LOGGED_IN_USERNAME,
                        "${user?.firstName} ${user?.lastName}"
                    )

                    editor.apply()

                    when (activity) {
                        is LoginActivity -> {
                            if (user != null) {
                                activity.userLoggedInSuccess(user)
                            }
                        }
                    }
                }
            }.addOnFailureListener { it ->
            run {
                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details.",
                    it
                )
            }
        }
    }
}