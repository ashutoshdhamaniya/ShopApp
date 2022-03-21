package com.codingfreak.shopappfire.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ContentInfoCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingfreak.shopappfire.Firestore.FirestoreClass
import com.codingfreak.shopappfire.R
import com.codingfreak.shopappfire.models.Address
import com.codingfreak.shopappfire.models.CartItem
import com.codingfreak.shopappfire.models.Order
import com.codingfreak.shopappfire.models.Product
import com.codingfreak.shopappfire.ui.adapters.CartItemListAdapter
import com.codingfreak.shopappfire.utils.Constants
import kotlinx.android.synthetic.main.activity_checkout.*

class CheckoutActivity : BaseActivity() {

    private var selectedAddressDetails : Address? = null
    private lateinit var mProductsList : ArrayList<Product>
    private lateinit var mCartItemList : ArrayList<CartItem>
    private var subTotal : Double = 0.0
    private var totalAmount : Double = 0.0
    private lateinit var ordersDetails : Order

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        setUpActionBar()

        if (intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)) {
            selectedAddressDetails = intent.getParcelableExtra(Constants.EXTRA_SELECTED_ADDRESS)
        }

        if (selectedAddressDetails != null) {
            tv_checkout_address_type.text = selectedAddressDetails?.type
            tv_checkout_full_name.text = selectedAddressDetails?.name
            tv_checkout_address.text = "${selectedAddressDetails!!.address}, ${selectedAddressDetails!!.zipCode}"
            tv_checkout_additional_note.text = selectedAddressDetails?.additionalNote

            if (selectedAddressDetails?.otherDetails!!.isNotEmpty()) {
                tv_checkout_other_details.text = selectedAddressDetails?.otherDetails
            }
            tv_checkout_mobile_number.text = selectedAddressDetails?.mobileNumber
        }

        getProductList()

        btn_place_order.setOnClickListener {
            placeAnOrder()
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(toolbar_checkout_activity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_left_24)
        }

        toolbar_checkout_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getProductList() {
        showProgressDialog()
        FirestoreClass().getAllProductList(this)
    }

    fun successProductsListFromFirestore(productList : ArrayList<Product>) {
        mProductsList = productList
        getCartItemsList()
    }

    private fun getCartItemsList() {
        FirestoreClass().getCartList(this)
    }

    fun successCartItemsList(cartList : ArrayList<CartItem>) {
        hideProgressDialog()

        for(product in mProductsList) {
            for(cartItem in cartList) {
                if(product.product_id == cartItem.product_id){
                    cartItem.stock_quantity = product.stock_quantity
                }
            }
        }

        mCartItemList = cartList

        rv_cart_list_items.layoutManager = LinearLayoutManager(this)
        rv_cart_list_items.setHasFixedSize(true)

        val cartListAdapter = CartItemListAdapter(this , mCartItemList , false)
        rv_cart_list_items.adapter = cartListAdapter

        for(item in mCartItemList) {
            val availableQuantity = item.stock_quantity.toInt()
            if(availableQuantity > 0) {
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()
                subTotal += (price*quantity)
            }
        }

        tv_checkout_sub_total.text = "$${subTotal}"
        tv_checkout_shipping_charge.text = "$10.0"

        if(subTotal > 0) {
            ll_checkout_place_order.visibility = View.VISIBLE
            totalAmount = subTotal + 10.0
            tv_checkout_total_amount.text = "$${totalAmount}"
        }else {
            ll_checkout_place_order.visibility = View.GONE
        }
    }

    private fun placeAnOrder() {
        showProgressDialog()

        if(selectedAddressDetails != null) {
            ordersDetails = Order(
                FirestoreClass().getCurrentUserId(),
                mCartItemList,
                selectedAddressDetails!!,
                "My order ${System.currentTimeMillis()}",
                mCartItemList[0].image,
                subTotal.toString(),
                "10.0", // The Shipping Charge is fixed as $10 for now in our case.
                totalAmount.toString(),
                System.currentTimeMillis()
            )

            FirestoreClass().placeOrder(this , ordersDetails)
        }
    }

    fun orderPlacedSuccess() {
        FirestoreClass().updateAllDetails(this , mCartItemList , ordersDetails)
    }

    fun allDetailsUpdatedSuccessfully() {
        hideProgressDialog()
        Toast.makeText(this , "Your order was placed successfully" , Toast.LENGTH_SHORT).show()

        val dashboardIntent = Intent(this , DashboardActivity::class.java)
        dashboardIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(dashboardIntent)
        finish()
    }
}