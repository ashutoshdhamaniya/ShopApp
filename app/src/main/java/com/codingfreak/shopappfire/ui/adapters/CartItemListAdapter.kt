package com.codingfreak.shopappfire.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.codingfreak.shopappfire.Firestore.FirestoreClass
import com.codingfreak.shopappfire.R
import com.codingfreak.shopappfire.models.CartItem
import com.codingfreak.shopappfire.ui.activities.CartListActivity
import com.codingfreak.shopappfire.utils.Constants
import com.codingfreak.shopappfire.utils.GlideLoader
import com.codingfreak.shopappfire.utils.MSPTextView
import com.codingfreak.shopappfire.utils.MSPTextViewBold
import kotlinx.android.synthetic.main.item_cart_layout.view.*

class CartItemListAdapter(
    private val context: Context,
    private val cartItemList: ArrayList<CartItem>,
    private val updateCartItem : Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CartViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_cart_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val cartModel = cartItemList[position]

        if (holder is CartViewHolder) {
            GlideLoader(context).loadUserPicture(cartModel.image, holder.productImage)
            holder.cartItemTitle.text = cartModel.title
            holder.cartItemPrice.text = "$ ${cartModel.price}"
            holder.cartItemQuantity.text = cartModel.cart_quantity

            if (cartModel.cart_quantity == "0") {
                holder.removeProduct.visibility = View.GONE
                holder.addProduct.visibility = View.GONE

                if(updateCartItem) {
                    holder.deleteItemFromCart.visibility = View.VISIBLE
                } else {
                    holder.deleteItemFromCart.visibility = View.GONE
                }

                holder.cartItemQuantity.text =
                    context.resources.getString(R.string.lbl_out_of_stock)

                holder.cartItemQuantity.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.snackBarFailure
                    )
                )
            } else {
                if(updateCartItem) {
                    holder.removeProduct.visibility = View.VISIBLE
                    holder.addProduct.visibility = View.VISIBLE
                    holder.deleteItemFromCart.visibility = View.VISIBLE
                } else {
                    holder.removeProduct.visibility = View.GONE
                    holder.addProduct.visibility = View.GONE
                    holder.deleteItemFromCart.visibility = View.GONE
                }

                holder.cartItemQuantity.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.secondaryTextColor
                    )
                )
            }

            holder.deleteItemFromCart.setOnClickListener {
                when (context) {
                    is CartListActivity -> {
                        context.showProgressDialog()
                    }
                }

                FirestoreClass().removeItemFromCart(context, cartModel.id)
            }

            holder.addProduct.setOnClickListener {
                val cartQuantity: Int = cartModel.cart_quantity.toInt()

                if (cartQuantity < cartModel.stock_quantity.toInt()) {

                    val itemHashMap = HashMap<String, Any>()

                    itemHashMap[Constants.CART_QUANTITY] = (cartQuantity + 1).toString()

                    // Show the progress dialog.
                    if (context is CartListActivity) {
                        context.showProgressDialog()
                    }

                    FirestoreClass().updateMyCart(context, cartModel.id, itemHashMap)
                } else {
                    if (context is CartListActivity) {
                        context.showErrorSnackBar(
                            context.resources.getString(
                                R.string.msg_for_available_stock,
                                cartModel.stock_quantity
                            ),
                            true
                        )
                    }
                }
            }

            holder.removeProduct.setOnClickListener {
                if (cartModel.cart_quantity == "1") {
                    FirestoreClass().removeItemFromCart(context, cartModel.id)
                } else {

                    val cartQuantity: Int = cartModel.cart_quantity.toInt()

                    val itemHashMap = HashMap<String, Any>()

                    itemHashMap[Constants.CART_QUANTITY] = (cartQuantity - 1).toString()

                    // Show the progress dialog.

                    if (context is CartListActivity) {
                        context.showProgressDialog()
                    }

                    FirestoreClass().updateMyCart(context, cartModel.id, itemHashMap)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return cartItemList.size
    }

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView = view.findViewById(R.id.iv_cart_item_image)
        val cartItemTitle: MSPTextView = view.findViewById(R.id.tv_cart_item_title)
        val cartItemPrice: MSPTextViewBold = view.findViewById(R.id.tv_cart_item_price)
        val cartItemQuantity: MSPTextView = view.findViewById(R.id.tv_cart_quantity)
        val removeProduct: ImageButton = view.findViewById(R.id.ib_remove_cart_item)
        val addProduct: ImageButton = view.findViewById(R.id.ib_add_cart_item)
        val deleteItemFromCart: ImageButton = view.findViewById(R.id.ib_delete_cart_item)
    }
}