package com.codingfreak.shopappfire.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.codingfreak.shopappfire.Firestore.FirestoreClass
import com.codingfreak.shopappfire.R
import com.codingfreak.shopappfire.models.CartItem
import com.codingfreak.shopappfire.models.Product
import com.codingfreak.shopappfire.utils.*

class ProductDetailsActivity : BaseActivity(), View.OnClickListener {

    private lateinit var productDetailsToolbar: Toolbar
    private lateinit var productImage: ImageView
    private lateinit var productTitle: MSPTextViewBold
    private lateinit var productPrice: MSPTextView
    private lateinit var productDescription: MSPTextView
    private lateinit var productQuantity: MSPTextView
    private lateinit var addToCart: MSPButton
    private lateinit var goToCart: MSPButton

    private var productId: String = ""
    private var productOwnerId: String = ""
    private lateinit var productDetails: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        productDetailsToolbar = findViewById(R.id.toolbar_product_details_activity)
        productImage = findViewById(R.id.iv_product_detail_image)
        productTitle = findViewById(R.id.tv_product_details_title)
        productPrice = findViewById(R.id.tv_product_details_price)
        productDescription = findViewById(R.id.tv_product_details_description)
        productQuantity = findViewById(R.id.tv_product_details_stock_quantity)
        addToCart = findViewById(R.id.btn_add_to_cart)
        goToCart = findViewById(R.id.btn_go_to_cart)

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            productId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
        }
        if (intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)) {
            productOwnerId = intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
        }

        /*
        * Checking that the product is logged in user product or not
        * */
        if (FirestoreClass().getCurrentUserId() == productOwnerId) {
            addToCart.visibility = View.GONE
            goToCart.visibility = View.GONE
        } else {
            addToCart.visibility = View.VISIBLE
//            goToCart.visibility = View.VISIBLE
        }

        setUpActionBar()
        getProductDetails()

        addToCart.setOnClickListener(this)
        goToCart.setOnClickListener(this)
    }

    private fun setUpActionBar() {
        setSupportActionBar(productDetailsToolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_left_24)
        }

        productDetailsToolbar.setNavigationOnClickListener { onBackPressed() }
    }

    /*
    * this function is used to set the data into the
    * fields of ProductDetailsActivity
    * */
    fun productDetailsSuccess(product: Product) {
        productDetails = product
        hideProgressDialog()

        GlideLoader(this).loadUserPicture(product.image, productImage)
        productTitle.text = product.title
        productPrice.text = "$ ${product.price}"
        productDescription.text = product.description
        productQuantity.text = product.stock_quantity

        if (FirestoreClass().getCurrentUserId() == product.user_id) {
            hideProgressDialog()
        } else {
            FirestoreClass().checkIfItemExistInCart(this, productId)
        }
    }

    /*
    * This function is used to call FirestoreClass getProductDetails
    * function which fetch the data from firebase database
    * */
    private fun getProductDetails() {
        showProgressDialog()
        FirestoreClass().getProductDetails(this, productId)
    }

    private fun addToCart() {
        val cartItem = CartItem(
            FirestoreClass().getCurrentUserId(),
            productId,
            productDetails.title,
            productDetails.price,
            productDetails.image,
            Constants.DEFAULT_CART_QUANTITY
        )

        showProgressDialog()
        FirestoreClass().addCartItems(this@ProductDetailsActivity, cartItem)
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.btn_add_to_cart -> {
                    addToCart()
                }
                R.id.btn_go_to_cart -> {
                    startActivity(Intent(this , CartListActivity::class.java))
                }
            }
        }
    }

    fun addToCartSuccess() {
        hideProgressDialog()

        Toast.makeText(
            this,
            resources.getString(R.string.add_to_cart_success_message),
            Toast.LENGTH_SHORT
        ).show()

        addToCart.visibility = View.GONE
        goToCart.visibility = View.VISIBLE
    }

    fun productExistInCart() {
        hideProgressDialog()
        addToCart.visibility = View.GONE
        goToCart.visibility = View.VISIBLE
    }
}