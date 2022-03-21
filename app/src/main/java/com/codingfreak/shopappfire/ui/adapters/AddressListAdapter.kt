package com.codingfreak.shopappfire.ui.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.codingfreak.shopappfire.R
import com.codingfreak.shopappfire.models.Address
import com.codingfreak.shopappfire.ui.activities.AddEditAddressActivity
import com.codingfreak.shopappfire.ui.activities.CheckoutActivity
import com.codingfreak.shopappfire.utils.Constants
import com.codingfreak.shopappfire.utils.MSPTextView
import com.codingfreak.shopappfire.utils.MSPTextViewBold

class AddressListAdapter(
    private val context: Context,
    private var addressList: ArrayList<Address>,
    private val selectAddress : Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AddressViewHolder(LayoutInflater.from(context).inflate(R.layout.item_address_layout , parent , false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val addressModel = addressList[position]

        if(holder is AddressViewHolder) {
            holder.fullname.text = addressModel.name
            holder.addressType.text = addressModel.type
            holder.addressDetails.text = "${addressModel.address} ${addressModel.zipCode}"
            holder.addressMobileNumber.text = addressModel.mobileNumber

            if(selectAddress) {
                holder.itemView.setOnClickListener{
                    val checkoutIntent = Intent(context , CheckoutActivity::class.java)
                    checkoutIntent.putExtra(Constants.EXTRA_SELECTED_ADDRESS , addressModel)
                    context.startActivity(checkoutIntent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return addressList.size
    }

    class AddressViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val fullname : MSPTextViewBold = view.findViewById(R.id.tv_address_full_name)
        val addressType : MSPTextView = view.findViewById(R.id.tv_address_type)
        val addressDetails : MSPTextView = view.findViewById(R.id.tv_address_details)
        val addressMobileNumber : MSPTextView = view.findViewById(R.id.tv_address_mobile_number)
    }

    fun notifyEditItem(activity : Activity , position : Int) {
        val intent = Intent(context , AddEditAddressActivity::class.java)
        intent.putExtra(Constants.EXTRA_ADDRESS_DETAILS , addressList[position])
        activity.startActivityForResult(intent , Constants.ADD_ADDRESS_REQUEST_CODE)
        notifyItemChanged(position)
    }
}