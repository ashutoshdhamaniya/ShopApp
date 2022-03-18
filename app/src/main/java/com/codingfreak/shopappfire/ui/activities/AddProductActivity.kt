package com.codingfreak.shopappfire.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.codingfreak.shopappfire.Firestore.FirestoreClass
import com.codingfreak.shopappfire.R
import com.codingfreak.shopappfire.models.Product
import com.codingfreak.shopappfire.utils.Constants
import com.codingfreak.shopappfire.utils.GlideLoader
import com.codingfreak.shopappfire.utils.MSPButton
import com.codingfreak.shopappfire.utils.MSPEditText
import java.io.IOException

class AddProductActivity : BaseActivity(), View.OnClickListener {

    private lateinit var addProductToolbar: Toolbar
    private lateinit var addProductImage: ImageView
    private lateinit var productImage: ImageView
    private lateinit var productTitle: MSPEditText
    private lateinit var productPrice: MSPEditText
    private lateinit var productDescription: MSPEditText
    private lateinit var productQuantity: MSPEditText
    private lateinit var addProduct: MSPButton

    private var selectedImageFileURI: Uri? = null
    private var productImageUrl : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        addProductToolbar = findViewById(R.id.toolbar_add_product_activity)
        addProductImage = findViewById(R.id.iv_add_update_product)
        productImage = findViewById(R.id.iv_product_image)
        productTitle = findViewById(R.id.et_product_title)
        productPrice = findViewById(R.id.et_product_price)
        productDescription = findViewById(R.id.et_product_description)
        productQuantity = findViewById(R.id.et_product_quantity)
        addProduct = findViewById(R.id.btn_submit_add_product)

        setUpActionBar()

        addProductImage.setOnClickListener(this)
        addProduct.setOnClickListener(this)
    }

    private fun setUpActionBar() {
        setSupportActionBar(addProductToolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_left_24)
        }

        addProductToolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.iv_add_update_product -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Constants.showImageChooser(this)
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }
                R.id.btn_submit_add_product -> {
                    if (validateProductDetails()) {
                        uploadProductImage()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //showErrorSnackBar("Storage Permission Granted !!!" , false)
                Constants.showImageChooser(this)
            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    addProductImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_vector_edit
                        )
                    )
                    selectedImageFileURI = data.data!!

                    try {
                        GlideLoader(this).loadUserPicture(selectedImageFileURI!!, productImage)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.d("Ashu", "Image Selection Cancelled")
        }
    }

    private fun validateProductDetails(): Boolean {
        return when {

            selectedImageFileURI == null -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_product_image), true)
                false
            }

            TextUtils.isEmpty(productTitle.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_title), true)
                false
            }

            TextUtils.isEmpty(productPrice.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_price), true)
                false
            }

            TextUtils.isEmpty(productDescription.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_product_description),
                    true
                )
                false
            }

            TextUtils.isEmpty(productQuantity.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_product_quantity),
                    true
                )
                false
            }
            else -> {
                true
            }
        }
    }

    private fun uploadProductImage() {
        showProgressDialog()
        FirestoreClass().uploadImageToCloudFirestore(
            this,
            selectedImageFileURI,
            Constants.PRODUCT_IMAGE
        )
    }

    fun productUploadSuccess() {
        hideProgressDialog()

        Toast.makeText(
            this,
            resources.getString(R.string.product_upload_success_message),
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }

    fun imageUploadSuccess(imageUrl: String) {
//        hideProgressDialog()
//        showErrorSnackBar("Product Image Uploaded Successfully $imageUrl", false)

        productImageUrl = imageUrl
        uploadProductDetails()
    }

    private fun uploadProductDetails() {
        val username = this.getSharedPreferences(Constants.MY_SHOP_PREFERENCES , Context.MODE_PRIVATE).getString(Constants.LOGGED_IN_USERNAME , "")

        val product = Product(
            FirestoreClass().getCurrentUserId(),
            username.toString(),
            productTitle.text.toString().trim(){it <= ' '},
            productPrice.text.toString().trim(){it <= ' '},
            productDescription.text.toString().trim(){it <= ' '},
            productQuantity.text.toString().trim(){it <= ' '},
            productImageUrl
        )

        FirestoreClass().uploadProductDetails(this , product)
    }
}