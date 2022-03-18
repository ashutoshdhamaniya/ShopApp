package com.codingfreak.shopappfire.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codingfreak.shopappfire.Firestore.FirestoreClass
import com.codingfreak.shopappfire.R
import com.codingfreak.shopappfire.models.Product
import com.codingfreak.shopappfire.ui.activities.AddProductActivity
import com.codingfreak.shopappfire.ui.adapters.ProductsListAdapter
import org.w3c.dom.Text

class ProductsFragment : BaseFragment() {

    // private lateinit var homeViewModel: HomeViewModel
    private lateinit var productRecyclerView : RecyclerView
    private lateinit var noProductText : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    fun successProductsListFromFirestore(productList: ArrayList<Product>) {
        hideFragmentProgressDialog()

        if(productList.size > 0) {
            productRecyclerView.visibility = View.VISIBLE
            noProductText.visibility = View.GONE

            productRecyclerView.layoutManager = LinearLayoutManager(activity)
            productRecyclerView.setHasFixedSize(true)
            val productListAdapter = ProductsListAdapter(requireActivity() , productList)
            productRecyclerView.adapter = productListAdapter
        } else {
            productRecyclerView.visibility = View.GONE
            noProductText.visibility = View.VISIBLE
        }
    }

    private fun getProductListFromFirestore() {
        showFragmentProgressDialog()
        FirestoreClass().getProductDetails(this)
    }

    override fun onResume() {
        super.onResume()
        getProductListFromFirestore()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_products, container, false)

        productRecyclerView = root.findViewById(R.id.rv_my_product_items)
        noProductText = root.findViewById(R.id.tv_no_products_found)

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_product_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.add_product -> {
                startActivity(Intent(activity, AddProductActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}