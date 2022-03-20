package com.codingfreak.shopappfire.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codingfreak.shopappfire.Firestore.FirestoreClass
import com.codingfreak.shopappfire.R
import com.codingfreak.shopappfire.models.CartItem
import com.codingfreak.shopappfire.models.Product
import com.codingfreak.shopappfire.ui.adapters.CartItemListAdapter
import com.codingfreak.shopappfire.utils.MSPTextView
import com.codingfreak.shopappfire.utils.MSPTextViewBold

class CartListActivity : BaseActivity() {

    private lateinit var cartToolbar: Toolbar
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var llCheckout: LinearLayout
    private lateinit var noCartItemFound: MSPTextView
    private lateinit var subTotalText: MSPTextView
    private lateinit var shippingCharge: MSPTextView
    private lateinit var totalAmount: MSPTextViewBold

    private lateinit var myProductList: ArrayList<Product>
    private lateinit var myCartListItems: ArrayList<CartItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_list)

        cartToolbar = findViewById(R.id.toolbar_cart_list_activity)
        cartRecyclerView = findViewById(R.id.rv_cart_items_list)
        llCheckout = findViewById(R.id.ll_checkout)
        noCartItemFound = findViewById(R.id.tv_no_cart_item_found)
        subTotalText = findViewById(R.id.tv_sub_total)
        shippingCharge = findViewById(R.id.tv_shipping_charge)
        totalAmount = findViewById(R.id.tv_total_amount)

        setUpActionBar()

    }

    private fun setUpActionBar() {
        setSupportActionBar(cartToolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_left_24)
        }

        cartToolbar.setNavigationOnClickListener { onBackPressed() }
    }

    fun successCartItemsList(cartItemList: ArrayList<CartItem>) {
        hideProgressDialog()

        for (product in myProductList) {
            for (cartItem in cartItemList) {
                if (product.product_id == cartItem.product_id) {
                    cartItem.stock_quantity = product.stock_quantity

                    if (product.stock_quantity.toInt() == 0) {
                        cartItem.cart_quantity = product.stock_quantity
                    }
                }
            }
        }

        myCartListItems = cartItemList

        if (myCartListItems.size > 0) {

            cartRecyclerView.visibility = View.VISIBLE
            llCheckout.visibility = View.VISIBLE
            noCartItemFound.visibility = View.GONE

            cartRecyclerView.layoutManager = LinearLayoutManager(this@CartListActivity)
            cartRecyclerView.setHasFixedSize(true)

            val cartListAdapter = CartItemListAdapter(this@CartListActivity, cartItemList)
            cartRecyclerView.adapter = cartListAdapter

            var subTotal: Double = 0.0

            for (item in myCartListItems) {
                Log.d("Shop", item.stock_quantity)
                val availableQuantity = item.stock_quantity.toInt()

                if (availableQuantity > 0) {
                    val price = item.price.toDouble()
                    val quantity = item.cart_quantity.toInt()

                    subTotal += (price * quantity)
                }
            }

            subTotalText.text = "$$subTotal"
            // Here we have kept Shipping Charge is fixed as $10 but in your case it may cary. Also, it depends on the location and total amount.
            shippingCharge.text = "$10.0"

            if (subTotal > 0) {
                llCheckout.visibility = View.VISIBLE

                val total = subTotal + 10
                totalAmount.text = "$$total"
            } else {
                llCheckout.visibility = View.GONE
            }

        } else {
            cartRecyclerView.visibility = View.GONE
            llCheckout.visibility = View.GONE
            noCartItemFound.visibility = View.VISIBLE
        }
    }

    private fun getCartItemsList() {
        //showProgressDialog()
        FirestoreClass().getCartList(this)
    }

    private fun getProductList() {
        showProgressDialog()
        FirestoreClass().getAllProductList(this)
    }

    override fun onResume() {
        super.onResume()
        //getCartItemsList()
        getProductList()
    }

    /*
    * This function is used to get all the products from firestore
    * */
    fun successProductListFromFirestore(productsList: ArrayList<Product>) {
        hideProgressDialog()
        myProductList = productsList
        getCartItemsList()
    }

    /**
     * A function to notify the user about the item removed from the cart list.
     */
    fun itemRemovedSuccess() {

        hideProgressDialog()

        Toast.makeText(
            this@CartListActivity,
            resources.getString(R.string.msg_item_removed_successfully),
            Toast.LENGTH_SHORT
        ).show()

        getCartItemsList()
    }

    /*
    * This function is used to show the success of item updation in the cart
    * increases
    * decreases
    * */
    fun itemUpdateSuccess() {
        hideProgressDialog()
        getCartItemsList()
    }
}