package com.myshoppal.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codingfreak.shopappfire.R
import com.codingfreak.shopappfire.models.Order
import com.codingfreak.shopappfire.ui.activities.MyOrdersDetailsActivity
import com.codingfreak.shopappfire.utils.Constants
import com.codingfreak.shopappfire.utils.GlideLoader
import kotlinx.android.synthetic.main.item_list_layout.view.*

// TODO Step 10: Create an adapter class for my list of orders.
// START
open class MyOrdersListAdapter(
    private val context: Context,
    private var list: ArrayList<Order>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return OrdersViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_list_layout,
                parent,
                false
            )
        )
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is OrdersViewHolder) {

            GlideLoader(context).loadProductPicture(
                model.image,
                holder.itemView.iv_item_image
            )

            holder.itemView.tv_item_name.text = model.title
            holder.itemView.tv_item_price.text = "$${model.total_amount}"

            holder.itemView.ib_delete_product.visibility = View.GONE

            holder.itemView.setOnClickListener{
                val ordersDetailsIntent = Intent(context , MyOrdersDetailsActivity::class.java)
                ordersDetailsIntent.putExtra(Constants.EXTRA_MY_ORDERS_DETAILS , model)
                context.startActivity(ordersDetailsIntent)
            }
        }
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class OrdersViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
// END