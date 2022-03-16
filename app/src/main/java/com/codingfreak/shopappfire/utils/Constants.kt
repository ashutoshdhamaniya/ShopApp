package com.codingfreak.shopappfire.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    const val USERS = "Users"
    const val MY_SHOP_PREFERENCES = "MyShopPref"
    const val LOGGED_IN_USERNAME = "logged_in_username"
    const val EXTRA_USER_DETAILS = "extra_user_details"
    const val READ_STORAGE_PERMISSION_CODE = 2
    const val PICK_IMAGE_REQUEST_CODE = 1

    const val MALE : String = "male"
    const val FEMALE : String = "female"
    const val MOBILE : String = "mobileNumber"
    const val GENDER : String = "gender"
    const val USER_PROFILE_IMAGE = "User_Profile_Image"
    const val IMAGE : String = "image"
    const val COMPLETE_PROFILE = "profileComplete"

    fun showImageChooser(activity : Activity) {
        val galleryIntent : Intent = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent , PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity , uri: Uri?) : String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}