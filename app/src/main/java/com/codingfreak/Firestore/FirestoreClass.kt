package com.codingfreak.Firestore

import android.util.Log
import com.codingfreak.models.User
import com.codingfreak.shopappfire.RegisterActivity
import com.codingfreak.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {

    private val myFirestore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: User) {

        myFirestore.collection(Constants.USERS).document(userInfo.id).set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }.addOnFailureListener { e ->
            run {
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error While Registering User", e)
            }
        }
    }

    fun getCurrentUserId() : String {
        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserId : String = ""
        if(currentUser != null) {
            currentUserId = currentUser.uid
        }

        return currentUserId
    }
}