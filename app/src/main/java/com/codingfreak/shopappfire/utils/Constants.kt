package com.codingfreak.shopappfire.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    // Collections On Cloud Firestore
    const val USERS = "Users"
    const val PRODUCTS = "products"


    const val MY_SHOP_PREFERENCES = "MyShopPref"
    const val LOGGED_IN_USERNAME = "logged_in_username"
    const val EXTRA_USER_DETAILS = "extra_user_details"
    const val READ_STORAGE_PERMISSION_CODE = 2
    const val PICK_IMAGE_REQUEST_CODE = 1

    const val MALE : String = "male"
    const val FEMALE : String = "female"
    const val FIRST_NAME = "firstName"
    const val LAST_NAME = "lastName"
    const val MOBILE : String = "mobileNumber"
    const val GENDER : String = "gender"
    const val USER_PROFILE_IMAGE = "User_Profile_Image"
    const val IMAGE : String = "image"
    const val COMPLETE_PROFILE = "profileComplete"

    const val PRODUCT_IMAGE = "Profile_Image"
    const val USER_ID = "user_id"

    const val EXTRA_PRODUCT_ID = "extra_product_id"
    const val EXTRA_PRODUCT_OWNER_ID = "extra_product_owner_id"
    const val DEFAULT_CART_QUANTITY = "1"
    const val CART_ITEMS = "cart_items"

    const val PRODUCT_ID = "product_id"

    fun showImageChooser(activity : Activity) {
        val galleryIntent : Intent = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent , PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity , uri: Uri?) : String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}