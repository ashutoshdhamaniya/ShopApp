package com.codingfreak.shopappfire.Firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.codingfreak.shopappfire.models.Product
import com.codingfreak.shopappfire.models.User
import com.codingfreak.shopappfire.ui.activities.*
import com.codingfreak.shopappfire.ui.fragments.ProductsFragment
import com.codingfreak.shopappfire.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

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
                        is SettingsActivity -> {
                            activity.userDetailsSuccess(user!!)
                        }
                    }
                }
            }.addOnFailureListener { it ->
                run {
                    when (activity) {
                        is LoginActivity -> {
                            activity.hideProgressDialog()
                        }
                        is SettingsActivity -> {
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

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        myFirestore.collection(Constants.USERS).document(getCurrentUserId()).update(userHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.userProfileUpdateSuccess()
                    }
                }
            }.addOnFailureListener {
                run {
                    when (activity) {
                        is UserProfileActivity -> {
                            activity.hideProgressDialog()
                        }
                    }
                }

                Log.d("Ashu", "Error While Updating")
            }
    }

    fun uploadImageToCloudFirestore(activity: Activity, imageFileUri: Uri?, imageType: String) {
        val storageReference: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "." + Constants.getFileExtension(
                activity,
                imageFileUri
            )
        )

        storageReference.putFile(imageFileUri!!).addOnSuccessListener { it ->
            run {
                Log.e("Firebase Image Url", it.metadata!!.reference!!.downloadUrl.toString())

                it.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    run {
                        when (activity) {
                            is UserProfileActivity -> {
                                activity.imageUploadSuccess(it.toString())
                            }
                            is AddProductActivity -> {
                                activity.imageUploadSuccess(it.toString())
                            }
                        }
                    }
                }
            }
        }.addOnFailureListener {
            when (activity) {
                is UserProfileActivity -> {
                    activity.hideProgressDialog()
                }
                is AddProductActivity -> {
                    activity.hideProgressDialog()
                }
            }

            Log.e(activity.javaClass.simpleName, it.message, it)
        }
    }

    fun uploadProductDetails(activity: AddProductActivity, productInfo: Product) {
        myFirestore.collection(Constants.PRODUCTS).document().set(productInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.productUploadSuccess()
            }.addOnFailureListener { e ->
                run {
                    activity.hideProgressDialog()
                    Log.e(
                        activity.javaClass.simpleName,
                        "Error While Uploading The Product Details !!"
                    )
                }
            }
    }

    fun getProductDetails(fragment: Fragment) {
        myFirestore.collection(Constants.PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId()).get().addOnSuccessListener { it ->
                Log.d("Ashu", it.documents.toString())
                val productList: ArrayList<Product> = ArrayList()

                for (i in it.documents) {
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id

                    productList.add(product)
                }

                when (fragment) {
                    is ProductsFragment -> {
                        fragment.successProductsListFromFirestore(productList)
                    }
                }
            }
    }

}