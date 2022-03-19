package com.codingfreak.shopappfire.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.codingfreak.shopappfire.R
import com.codingfreak.shopappfire.models.Product
import com.codingfreak.shopappfire.ui.activities.ProductDetailsActivity
import com.codingfreak.shopappfire.ui.fragments.ProductsFragment
import com.codingfreak.shopappfire.utils.Constants
import com.codingfreak.shopappfire.utils.GlideLoader
import com.codingfreak.shopappfire.utils.MSPTextView
import com.codingfreak.shopappfire.utils.MSPTextViewBold

class ProductsListAdapter(
    private val context: Context,
    private val productList: ArrayList<Product>,
    private val fragment : ProductsFragment
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_list_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = productList[position]

        if (holder is MyViewHolder) {
            GlideLoader(context).loadProductPicture(model.image, holder.itemImage)
            holder.itemName.text = model.title
            holder.itemPrice.text = "$ ${model.price}"

            holder.deleteProduct.setOnClickListener {
                fragment.deleteProduct(model.product_id)
            }

            holder.itemView.setOnClickListener {
                val productDetailsIntent = Intent(context , ProductDetailsActivity::class.java)
                productDetailsIntent.putExtra(Constants.EXTRA_PRODUCT_ID , model.product_id)
                productDetailsIntent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID , model.user_id)
                context.startActivity(productDetailsIntent)
            }
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemImage: ImageView = view.findViewById(R.id.iv_item_image)
        val itemName: MSPTextViewBold = view.findViewById(R.id.tv_item_name)
        val itemPrice: MSPTextView = view.findViewById(R.id.tv_item_price)
        val deleteProduct: ImageButton = view.findViewById(R.id.ib_delete_product)
    }
}