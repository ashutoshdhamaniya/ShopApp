package com.codingfreak.shopappfire.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codingfreak.shopappfire.Firestore.FirestoreClass
import com.codingfreak.shopappfire.R
import com.codingfreak.shopappfire.models.Address
import com.codingfreak.shopappfire.ui.adapters.AddressListAdapter
import com.codingfreak.shopappfire.utils.Constants
import com.codingfreak.shopappfire.utils.MSPTextView
import com.myshoppal.utils.SwipeToDeleteCallback
import com.myshoppal.utils.SwipeToEditCallback
import kotlinx.android.synthetic.main.activity_address_list.*

class AddressListActivity : BaseActivity() {

    private lateinit var addressListToolbar: Toolbar
    private lateinit var addAddress: MSPTextView
    private var selectAddress : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)

        addressListToolbar = findViewById(R.id.toolbar_address_list_activity)
        addAddress = findViewById(R.id.tv_add_address)

        addAddress.setOnClickListener {
            val editAddressIntent = Intent(this, AddEditAddressActivity::class.java)
            startActivityForResult(editAddressIntent , Constants.ADD_ADDRESS_REQUEST_CODE)
        }

        if(intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS)) {
            selectAddress = intent.getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS , false)
        }

        if(selectAddress) {
            tv_title.text = resources.getString(R.string.title_select_address)
        }

        setUpActionBar()
        getAddressList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            getAddressList()
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(addressListToolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_left_24)
        }

        addressListToolbar.setNavigationOnClickListener { onBackPressed() }
    }

    /*
    * This function show that address is featched successfully
    * */
    fun successAddressListFromFirestore(addressList: ArrayList<Address>) {
        hideProgressDialog()

        val addressRecyclerView: RecyclerView = findViewById(R.id.rv_address_list)
        val noAddressText: MSPTextView = findViewById(R.id.tv_no_address_found)

        if (addressList.size > 0) {
            addressRecyclerView.visibility = View.VISIBLE
            noAddressText.visibility = View.GONE

            addressRecyclerView.layoutManager = LinearLayoutManager(this@AddressListActivity)
            addressRecyclerView.setHasFixedSize(true)

            val addressAdapter = AddressListAdapter(this@AddressListActivity, addressList , selectAddress)
            addressRecyclerView.adapter = addressAdapter

            if(!selectAddress) {
                /*
            * Edit Swipe Handler
            * On right swipe edit the address
            * */
                val editSwipeHandler = object : SwipeToEditCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        addressAdapter.notifyEditItem(
                            this@AddressListActivity,
                            viewHolder.adapterPosition
                        )
                    }
                }

                val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                editItemTouchHelper.attachToRecyclerView(addressRecyclerView)

                /*
                * Delete Swipe handler
                * on left swipe delete the address
                * */
                val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        showProgressDialog()
                        FirestoreClass().deleteAddress(
                            this@AddressListActivity,
                            addressList[viewHolder.adapterPosition].id
                        )
                    }
                }

                val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
                deleteItemTouchHelper.attachToRecyclerView(addressRecyclerView)
            }

        } else {
            addressRecyclerView.visibility = View.GONE
            noAddressText.visibility = View.VISIBLE
        }
    }

    fun deleteAddressSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this,
            resources.getString(R.string.err_your_address_deleted_successfully),
            Toast.LENGTH_SHORT
        ).show()
        getAddressList()
    }

    /*
    * Call the function of firestoreclass to fetch addresses
    * */
    private fun getAddressList() {
        showProgressDialog()
        FirestoreClass().getAddressList(this)
    }
}