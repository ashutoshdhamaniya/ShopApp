package com.codingfreak.shopappfire.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codingfreak.shopappfire.Firestore.FirestoreClass
import com.codingfreak.shopappfire.R
import com.codingfreak.shopappfire.models.Product
import com.codingfreak.shopappfire.ui.activities.CartListActivity
import com.codingfreak.shopappfire.ui.activities.ProductDetailsActivity
import com.codingfreak.shopappfire.ui.activities.SettingsActivity
import com.codingfreak.shopappfire.ui.adapters.DashboardItemsListAdapter
import com.codingfreak.shopappfire.utils.Constants

class DashboardFragment : BaseFragment() {

    // private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var dashboardProductRecyclerView: RecyclerView
    private lateinit var dashboardNoProductText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)

        dashboardProductRecyclerView = root.findViewById(R.id.rv_dashboard_items)
        dashboardNoProductText = root.findViewById(R.id.tv_no_dashboard_items_found)

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onResume() {
        super.onResume()
        getDashboardItemsList()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.settings -> {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            R.id.action_cart -> {
                startActivity(Intent(activity , CartListActivity::class.java))
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun successDashboardItemsList(productList: ArrayList<Product>) {
        hideFragmentProgressDialog()

        if (productList.size > 0) {
            dashboardProductRecyclerView.visibility = View.VISIBLE
            dashboardNoProductText.visibility = View.GONE

            dashboardProductRecyclerView.layoutManager = GridLayoutManager(activity, 2)
            dashboardProductRecyclerView.setHasFixedSize(true)
            val dashboardProductListAdapter =
                DashboardItemsListAdapter(requireActivity(), productList)
            dashboardProductRecyclerView.adapter = dashboardProductListAdapter

//            dashboardProductListAdapter.setOnClickListener(object :
//                DashboardItemsListAdapter.OnClickListener {
//                override fun onClick(position: Int, product: Product) {
//                    val productDetailsIntent = Intent(context, ProductDetailsActivity::class.java)
//                    productDetailsIntent.putExtra(Constants.EXTRA_PRODUCT_ID, product.product_id)
//                    startActivity(productDetailsIntent)
//                }
//            })

        } else {
            dashboardProductRecyclerView.visibility = View.GONE
            dashboardNoProductText.visibility = View.VISIBLE
        }
    }

    private fun getDashboardItemsList() {
        showFragmentProgressDialog()
        FirestoreClass().getDashboardItemsList(this)
    }
}