package com.codingfreak.shopappfire.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.codingfreak.shopappfire.R
import com.codingfreak.shopappfire.models.Product
import com.codingfreak.shopappfire.ui.activities.ProductDetailsActivity
import com.codingfreak.shopappfire.utils.Constants
import com.codingfreak.shopappfire.utils.GlideLoader
import com.codingfreak.shopappfire.utils.MSPTextView
import com.codingfreak.shopappfire.utils.MSPTextViewBold

class DashboardItemsListAdapter(
    private val context: Context,
    private val dashboardItemList: ArrayList<Product>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DashboardViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_dashboard_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = dashboardItemList[position]

        if (holder is DashboardViewHolder) {
            GlideLoader(context).loadProductPicture(model.image, holder.itemImage)
            holder.itemName.text = model.title
            holder.itemPrice.text = "$ ${model.price}"

            holder.itemView.setOnClickListener {
                val productDetailsIntent = Intent(context, ProductDetailsActivity::class.java)
                productDetailsIntent.putExtra(Constants.EXTRA_PRODUCT_ID, model.product_id)
                productDetailsIntent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID , model.user_id)
                context.startActivity(productDetailsIntent)
//                if (onClickListener != null) {
//                    onClickListener!!.onClick(position, model)
//                }
            }
        }
    }

    override fun getItemCount(): Int {
        return dashboardItemList.size
    }

    class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemImage: ImageView = view.findViewById(R.id.iv_dashboard_item_image)
        val itemName: MSPTextViewBold = view.findViewById(R.id.tv_dashboard_item_title)
        val itemPrice: MSPTextView = view.findViewById(R.id.tv_dashboard_item_price)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, product: Product)
    }
}