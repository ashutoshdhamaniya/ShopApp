package com.codingfreak.shopappfire.ui.activities

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.codingfreak.shopappfire.Firestore.FirestoreClass
import com.codingfreak.shopappfire.R
import com.codingfreak.shopappfire.models.Address
import com.codingfreak.shopappfire.utils.Constants
import com.codingfreak.shopappfire.utils.MSPButton
import com.codingfreak.shopappfire.utils.MSPEditText
import com.codingfreak.shopappfire.utils.MSPRadioButton
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_add_edit_address.*

class AddEditAddressActivity : BaseActivity() {

    private lateinit var editAddressToolbar: Toolbar
    private lateinit var fullname: MSPEditText
    private lateinit var phoneNumber: MSPEditText
    private lateinit var address: MSPEditText
    private lateinit var zipcode: MSPEditText
    private lateinit var other: MSPRadioButton
    private lateinit var additionalNoteText  : MSPEditText
    private lateinit var submitAddress: MSPButton

    private var addressDetails : Address? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_address)

        editAddressToolbar = findViewById(R.id.toolbar_add_edit_address_activity)
        fullname = findViewById(R.id.et_full_name)
        phoneNumber = findViewById(R.id.et_phone_number)
        address = findViewById(R.id.et_address)
        zipcode = findViewById(R.id.et_zip_code)
        other = findViewById(R.id.rb_other)
        additionalNoteText = findViewById(R.id.et_additional_note)
        submitAddress = findViewById(R.id.btn_submit_address)

        if(intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS)) {
            addressDetails = intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS)
        }

        if (addressDetails != null) {
            if (addressDetails!!.id.isNotEmpty()) {

                tv_title.text = resources.getString(R.string.title_edit_address)
                submitAddress.text = resources.getString(R.string.btn_lbl_update)

                fullname.setText(addressDetails?.name)
                phoneNumber.setText(addressDetails?.mobileNumber)
                address.setText(addressDetails?.address)
                zipcode.setText(addressDetails?.zipCode)
                additionalNoteText.setText(addressDetails?.additionalNote)

                when (addressDetails?.type) {
                    Constants.HOME -> {
                        rb_home.isChecked = true
                    }
                    Constants.OFFICE -> {
                        rb_office.isChecked = true
                    }
                    else -> {
                        rb_other.isChecked = true
                        til_other_details.visibility = View.VISIBLE
                        et_other_details.setText(addressDetails?.otherDetails)
                    }
                }
            }
        }

        setUpActionBar()

        submitAddress.setOnClickListener { saveAddressToFirestore() }

        findViewById<RadioGroup>(R.id.rg_type).setOnCheckedChangeListener { _, checkedId ->
            if(checkedId == R.id.rb_other) {
                findViewById<TextInputLayout>(R.id.til_other_details).visibility = View.VISIBLE
            } else {
                findViewById<TextInputLayout>(R.id.til_other_details).visibility = View.GONE
            }
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(editAddressToolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_left_24)
        }

        editAddressToolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun validateData(): Boolean {
        return when {

            TextUtils.isEmpty(fullname.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_full_name),
                    true
                )
                false
            }

            TextUtils.isEmpty(phoneNumber.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_phone_number),
                    true
                )
                false
            }

            TextUtils.isEmpty(address.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_address), true)
                false
            }

            TextUtils.isEmpty(zipcode.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code), true)
                false
            }

            other.isChecked && TextUtils.isEmpty(
                zipcode.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code), true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun saveAddressToFirestore() {

        // Here we get the text from editText and trim the space
        val fullName: String = fullname.text.toString().trim { it <= ' ' }
        val phoneNumber: String = phoneNumber.text.toString().trim { it <= ' ' }
        val address: String = address.text.toString().trim { it <= ' ' }
        val zipCode: String = zipcode.text.toString().trim { it <= ' ' }
        val additionalNote: String =
            additionalNoteText.text.toString().trim { it <= ' ' }
        val otherDetails: String =
            findViewById<MSPEditText>(R.id.et_other_details).text.toString().trim { it <= ' ' }

        if (validateData()) {

            // Show the progress dialog.
            showProgressDialog()

            val addressType: String = when {
                rb_home.isChecked -> {
                    Constants.HOME
                }
                rb_office.isChecked -> {
                    Constants.OFFICE
                }
                else -> {
                    Constants.OTHER
                }
            }

            // TODO Step 7: Prepare address info in data model class.
            // START
            val addressModel = Address(
                FirestoreClass().getCurrentUserId(),
                fullName,
                phoneNumber,
                address,
                zipCode,
                additionalNote,
                addressType,
                otherDetails
            )
            // END

            if(addressDetails != null && addressDetails!!.id.isNotEmpty()) {
                FirestoreClass().updateAddress(this , addressModel , addressDetails!!.id)
            } else {
                FirestoreClass().addAddress(this, addressModel)
            }
        }
    }

    /*
    * This Function shows that the address is added successfully
    * */
    fun addUpdateAddressSuccess() {
        hideProgressDialog()

        val notifySuccessMessage : String = if(addressDetails !== null && addressDetails!!.id.isNotEmpty()) {
            resources.getString(R.string.msg_your_address_updated_successfully)
        } else {
            resources.getString(R.string.address_added_successflly)
        }

        Toast.makeText(
            this,
            notifySuccessMessage,
            Toast.LENGTH_SHORT
        ).show()
        setResult(RESULT_OK)
        finish()
    }
}