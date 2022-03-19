package com.codingfreak.shopappfire.ui.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import com.codingfreak.shopappfire.Firestore.FirestoreClass
import com.codingfreak.shopappfire.R
import com.codingfreak.shopappfire.models.CartItem

class CartListActivity : BaseActivity() {

    private lateinit var cartToolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_list)

        cartToolbar = findViewById(R.id.toolbar_cart_list_activity)

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

        for (i in cartItemList) {
            Log.d("Ashu", "${i.title}")
        }
    }

    private fun getCartItemsList() {
        showProgressDialog()
        FirestoreClass().getCartList(this)
    }

    override fun onResume() {
        super.onResume()
        getCartItemsList()
    }
}