package com.codingfreak.shopappfire.Firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.codingfreak.shopappfire.models.*
import com.codingfreak.shopappfire.ui.activities.*
import com.codingfreak.shopappfire.ui.fragments.DashboardFragment
import com.codingfreak.shopappfire.ui.fragments.OrdersFragment
import com.codingfreak.shopappfire.ui.fragments.ProductsFragment
import com.codingfreak.shopappfire.ui.fragments.SoldProductsFragment
import com.codingfreak.shopappfire.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.myshoppal.models.SoldProduct

class FirestoreClass {

    private val myFirestore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: User) {

        myFirestore.collection(Constants.USERS).document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }.addOnFailureListener { e ->
                run {
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error While Registering User", e)
                }
            }
    }

    fun getCurrentUserId(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserId: String = ""
        if (currentUser != null) {
            currentUserId = currentUser.uid
        }

        return currentUserId
    }

    fun getUserDetails(activity: Activity) {
        myFirestore.collection(Constants.USERS).document(getCurrentUserId()).get()
            .addOnSuccessListener { it ->
                run {
                    Log.i(activity.javaClass.simpleName, it.toString())
                    val user = it.toObject(User::class.java)

                    val sharedPreferences: SharedPreferences = activity.getSharedPreferences(
                        Constants.MY_SHOP_PREFERENCES,
                        Context.MODE_PRIVATE
                    )
                    val editor: SharedPreferences.Editor = sharedPreferences.edit().putString(
                        Constants.LOGGED_IN_USERNAME,
                        "${user?.firstName} ${user?.lastName}"
                    )

                    editor.apply()

                    when (activity) {
                        is LoginActivity -> {
                            if (user != null) {
                                activity.userLoggedInSuccess(user)
                            }
                        }
                        is SettingsActivity -> {
                            activity.userDetailsSuccess(user!!)
                        }
                    }
                }
            }.addOnFailureListener { it ->
                run {
                    when (activity) {
                        is LoginActivity -> {
                            activity.hideProgressDialog()
                        }
                        is SettingsActivity -> {
                            activity.hideProgressDialog()
                        }
                    }

                    Log.e(
                        activity.javaClass.simpleName,
                        "Error while getting user details.",
                        it
                    )
                }
            }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        myFirestore.collection(Constants.USERS).document(getCurrentUserId()).update(userHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.userProfileUpdateSuccess()
                    }
                }
            }.addOnFailureListener {
                run {
                    when (activity) {
                        is UserProfileActivity -> {
                            activity.hideProgressDialog()
                        }
                    }
                }

                Log.d("Ashu", "Error While Updating")
            }
    }

    fun uploadImageToCloudFirestore(activity: Activity, imageFileUri: Uri?, imageType: String) {
        val storageReference: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "." + Constants.getFileExtension(
                activity,
                imageFileUri
            )
        )

        storageReference.putFile(imageFileUri!!).addOnSuccessListener { it ->
            run {
                Log.e("Firebase Image Url", it.metadata!!.reference!!.downloadUrl.toString())

                it.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    run {
                        when (activity) {
                            is UserProfileActivity -> {
                                activity.imageUploadSuccess(it.toString())
                            }
                            is AddProductActivity -> {
                                activity.imageUploadSuccess(it.toString())
                            }
                        }
                    }
                }
            }
        }.addOnFailureListener {
            when (activity) {
                is UserProfileActivity -> {
                    activity.hideProgressDialog()
                }
                is AddProductActivity -> {
                    activity.hideProgressDialog()
                }
            }

            Log.e(activity.javaClass.simpleName, it.message, it)
        }
    }

    fun uploadProductDetails(activity: AddProductActivity, productInfo: Product) {
        myFirestore.collection(Constants.PRODUCTS).document().set(productInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.productUploadSuccess()
            }.addOnFailureListener { e ->
                run {
                    activity.hideProgressDialog()
                    Log.e(
                        activity.javaClass.simpleName,
                        "Error While Uploading The Product Details !!"
                    )
                }
            }
    }

    fun getProductDetails(fragment: Fragment) {
        myFirestore.collection(Constants.PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId()).get().addOnSuccessListener { it ->
                Log.d("Ashu", it.documents.toString())
                val productList: ArrayList<Product> = ArrayList()

                for (i in it.documents) {
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id

                    productList.add(product)
                }

                when (fragment) {
                    is ProductsFragment -> {
                        fragment.successProductsListFromFirestore(productList)
                    }
                }
            }
    }

    fun getDashboardItemsList(fragment: DashboardFragment) {
        myFirestore.collection(Constants.PRODUCTS).get().addOnSuccessListener { it ->
            val productList: ArrayList<Product> = ArrayList()

            for (i in it.documents) {
                val product = i.toObject(Product::class.java)
                product!!.product_id = i.id
                productList.add(product)
            }

            fragment.successDashboardItemsList(productList)
        }.addOnFailureListener {
            fragment.hideFragmentProgressDialog()
            Log.d("Ashu", "Error While Featching the Dashboard Products.")
        }
    }

    /*
    * This function is used to delete the product of ProductFragment Screen
    * */
    fun deleteProduct(fragment: ProductsFragment, productId: String) {
        myFirestore.collection(Constants.PRODUCTS).document(productId).delete()
            .addOnSuccessListener {
                fragment.productDeleteSuccess()
            }.addOnFailureListener { it ->
                fragment.hideFragmentProgressDialog()
                Log.d("Ashu", "Error While Deleting The Product From Firebase Database")
            }
    }

    /*
    * This Function is used to get the details of the single product for
    * the ProductDetailsActivity
    * */
    fun getProductDetails(activity: ProductDetailsActivity, productId: String) {
        myFirestore.collection(Constants.PRODUCTS).document(productId).get().addOnSuccessListener {
            val product = it.toObject(Product::class.java)

            if (product != null) {
                activity.productDetailsSuccess(product)
            }
        }.addOnFailureListener {
            activity.hideProgressDialog()
            Log.d("Ashu", "Error While Featching the Product Details")
        }
    }

    /*
    * This function is used to add the product into cart
    * */
    fun addCartItems(activity: ProductDetailsActivity, addToCart: CartItem) {
        myFirestore.collection(Constants.CART_ITEMS).document().set(addToCart, SetOptions.merge())
            .addOnSuccessListener {
                activity.addToCartSuccess()
            }.addOnFailureListener { it ->
                activity.hideProgressDialog()
                Log.d("Ashu", "Error While Adding the product into cart !!!")
            }
    }

    fun checkIfItemExistInCart(activity: ProductDetailsActivity, productId: String) {
        myFirestore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .whereEqualTo(Constants.PRODUCT_ID, productId).get().addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, it.documents.toString())

                if (it.documents.size > 0) {
                    activity.productExistInCart()
                } else {
                    activity.hideProgressDialog()
                }

            }.addOnFailureListener {
                activity.hideProgressDialog()
                Log.d("Ashu", "Error While Checking the exisitng cart list")
            }
    }

    fun getCartList(activity: Activity) {
        myFirestore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId()).get().addOnSuccessListener {
                val cartItemsList: ArrayList<CartItem> = ArrayList()

                for (i in it.documents) {
                    val cartItem = i.toObject(CartItem::class.java)
                    cartItem!!.id = i.id
                    cartItemsList.add(cartItem)
                }

                when (activity) {
                    is CartListActivity -> {
                        activity.successCartItemsList(cartItemsList)
                    }
                    is CheckoutActivity -> {
                        activity.successCartItemsList(cartItemsList)
                    }
                }
            }.addOnFailureListener { it ->

                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }
                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.d("Ashu", "Error while featching cart items")
            }
    }

    fun getAllProductList(activity: Activity) {
        myFirestore.collection(Constants.PRODUCTS).get().addOnSuccessListener { it ->
            Log.d("Ashu", it.documents.toString())
            val productList: ArrayList<Product> = ArrayList()
            for (i in it.documents) {
                val product = i.toObject(Product::class.java)
                product!!.product_id = i.id
                productList.add(product)
            }
            when (activity) {
                is CartListActivity -> {
                    activity.successProductListFromFirestore(productList)
                }
                is CheckoutActivity -> {
                    activity.successProductsListFromFirestore(productList)
                }
            }

        }.addOnFailureListener {
            when (activity) {
                is CartListActivity -> {
                    activity.hideProgressDialog()
                }
                is CheckoutActivity -> {
                    activity.hideProgressDialog()
                }
            }
            Log.d("Ashu", "Error While getting all products list")
        }
    }

    fun removeItemFromCart(context: Context, cart_id: String) {
        // Cart items collection name
        myFirestore.collection(Constants.CART_ITEMS)
            .document(cart_id) // cart id
            .delete()
            .addOnSuccessListener {

                // Notify the success result of the removed cart item from the list to the base class.
                when (context) {
                    is CartListActivity -> {
                        context.itemRemovedSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is any error.
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e(
                    context.javaClass.simpleName,
                    "Error while removing the item from the cart list.",
                    e
                )
            }
    }

    /*
    * This function is used to update the cart items
    * increase
    * decrease
    * */
    fun updateMyCart(context: Context, cart_id: String, itemHashMap: HashMap<String, Any>) {
        myFirestore.collection(Constants.CART_ITEMS).document(cart_id).update(itemHashMap)
            .addOnSuccessListener {
                when (context) {
                    is CartListActivity -> {
                        context.itemUpdateSuccess()
                    }
                }
            }.addOnFailureListener { it ->
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }

                Log.d("Ashu", "Error While Updating the cart items")
            }
    }

    /*
    * This function is used to add the address to the firestore collection
    * */
    fun addAddress(activity: AddEditAddressActivity, addressInfo: Address) {
        myFirestore.collection(Constants.ADDRESSES).document().set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.addUpdateAddressSuccess()
            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.d("Ashu", "Error While Adding the address to the firestore")
            }
    }

    fun getAddressList(activity: AddressListActivity) {
        myFirestore.collection(Constants.ADDRESSES)
            .whereEqualTo(Constants.USER_ID, getCurrentUserId()).get().addOnSuccessListener {
                val addressList: ArrayList<Address> = ArrayList()

                for (i in it.documents) {
                    val address = i.toObject(Address::class.java)
                    address!!.id = i.id
                    addressList.add(address)
                }

                activity.successAddressListFromFirestore(addressList)
            }.addOnFailureListener { it ->
                activity.hideProgressDialog()
                Log.d("Ashu", "Error While getting the address")
            }
    }

    /*
    * This method is used to update the address in the firebase database
    * */
    fun updateAddress(activity: AddEditAddressActivity, addressInfo: Address, addressId: String) {
        myFirestore.collection(Constants.ADDRESSES)
            .document(addressId)
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                // Here call a function of base activity for transferring the result to it.
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the Address.",
                    e
                )
            }
    }

    /**
     * A function to delete the existing address from the cloud firestore.
     *
     * @param activity Base class
     * @param addressId existing address id
     */
    fun deleteAddress(activity: AddressListActivity, addressId: String) {

        myFirestore.collection(Constants.ADDRESSES)
            .document(addressId)
            .delete()
            .addOnSuccessListener {
                // Here call a function of base activity for transferring the result to it.
                activity.deleteAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while deleting the address.",
                    e
                )
            }
    }

    fun placeOrder(activity: CheckoutActivity, order: Order) {
        myFirestore.collection(Constants.ORDERS).document().set(order, SetOptions.merge())
            .addOnSuccessListener {
                activity.orderPlacedSuccess()
            }.addOnFailureListener {
                activity.hideProgressDialog()
                Log.d("Ashu", "Error while placing an order")
            }
    }

    /*
    * this function is used to update the stock_quantity after the successful order place
    * */
    fun updateAllDetails(activity: CheckoutActivity, cartList: ArrayList<CartItem>, order: Order) {
        val writeBatch = myFirestore.batch()

        for (cartItem in cartList) {
//            val productHashMap = HashMap<String, Any>()
//            productHashMap[Constants.STOCK_QUANTITY] =
//                (cartItem.stock_quantity.toInt() - cartItem.cart_quantity.toInt()).toString()
//            val documentReference =
//                myFirestore.collection(Constants.PRODUCTS).document(cartItem.product_id)
//            writeBatch.update(documentReference, productHashMap)
            val soldProduct = SoldProduct(
                // Here the user id will be of product owner.
                cartItem.product_owner_id,
                cartItem.title,
                cartItem.price,
                cartItem.cart_quantity,
                cartItem.image,
                order.title,
                order.order_datetime,
                order.sub_total_amount,
                order.shipping_charge,
                order.total_amount,
                order.address
            )

            // TODO Step 12: Make an entry for sold product in cloud firestore.
            // START
            val documentReference = myFirestore.collection(Constants.SOLD_PRODUCTS)
                .document()
            writeBatch.set(documentReference, soldProduct)
        }

        for (cartItem in cartList) {
            val documentReference =
                myFirestore.collection(Constants.CART_ITEMS).document(cartItem.id)
            writeBatch.delete(documentReference)
        }

        writeBatch.commit().addOnSuccessListener {
            activity.allDetailsUpdatedSuccessfully()
        }.addOnFailureListener {
            activity.hideProgressDialog()
            Log.d("Ashu", "Error while updating the details after the successful order place")
        }
    }

    fun getMyOrdersList(fragment: OrdersFragment) {
        myFirestore.collection(Constants.ORDERS).whereEqualTo(Constants.USER_ID, getCurrentUserId())
            .get().addOnSuccessListener {
                val ordersList: ArrayList<Order> = ArrayList()

                for (i in it.documents) {
                    val orderItem = i.toObject(Order::class.java)
                    orderItem!!.id = i.id

                    ordersList.add(orderItem)
                }

                fragment.populateOrdersListInUI(ordersList)

            }.addOnFailureListener {
                fragment.hideFragmentProgressDialog()
                Log.d("Ashu", "Error while getting orders list")
            }
    }

    fun getSoldProductsList(fragment: SoldProductsFragment) {

        myFirestore.collection(Constants.SOLD_PRODUCTS).whereEqualTo(
            Constants.USER_ID, getCurrentUserId()
        ).get().addOnSuccessListener {
            val soldProductList : ArrayList<SoldProduct> = ArrayList()

            for (i in it.documents) {
                val soldProduct = i.toObject(SoldProduct::class.java)
                soldProduct!!.id = i.id
                soldProductList.add(soldProduct)
            }
            fragment.successSoldProductsList(soldProductList)

        }.addOnFailureListener {
            fragment.hideFragmentProgressDialog()
            Log.d("Ashu" , "Error while getting sold products list")
        }
    }
}