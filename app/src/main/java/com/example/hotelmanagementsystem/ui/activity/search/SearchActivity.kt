package com.example.hotelmanagementsystem.ui.activity.search

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hotelmanagementsystem.R
import com.example.hotelmanagementsystem.adapter.CustomerAdapter
import com.example.hotelmanagementsystem.models.customers.Customer
import com.example.hotelmanagementsystem.models.employee.Employee
import com.example.hotelmanagementsystem.models.room.RoomMasters
import com.example.hotelmanagementsystem.networkmanager.NetworkManager
import com.example.hotelmanagementsystem.repository.Repository
import com.example.hotelmanagementsystem.onClickInterface.OnItemClickListener
import com.example.hotelmanagementsystem.ui.activity.customer.CustomerView
import com.example.hotelmanagementsystem.ui.activity.room.RoomBooking
import com.example.hotelmanagementsystem.ui.viewmodel.HotelViewModel
import com.example.hotelmanagementsystem.ui.viewmodel.HotelViewModelFactory
import com.example.hotelmanagementsystem.utils.Resource
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.snackbar.Snackbar

class SearchActivity : AppCompatActivity(), OnItemClickListener {

    private lateinit var hotelViewModel: HotelViewModel
    private lateinit var searchView:SearchView
    private lateinit var rvSearch:RecyclerView
    private lateinit var customerAdapter: CustomerAdapter
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private var findRoomSuccessfully:Boolean = false
    private lateinit var roomMasterData:RoomMasters

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_HotelManagementSystem)
        setContentView(R.layout.activity_search)

        searchView = findViewById(R.id.svSearchActivity)
        rvSearch =findViewById(R.id.rvSearchView)
        shimmerFrameLayout = findViewById(R.id.svSearch)

        val repository = Repository()
        val network = NetworkManager()
        val factory = HotelViewModelFactory(repository,network,application)
        hotelViewModel = ViewModelProvider(this, factory).get(HotelViewModel::class.java)

        supportActionBar?.apply {
            title = "Search"
            setDisplayHomeAsUpEnabled(true)
        }

        setupRecyclerView()
        shimmerFrameLayout.stopShimmer()
        shimmerFrameLayout.visibility = View.GONE
        hotelViewModel.findCustomerResponse.observe(this, Observer { response->
            when(response){
                is Resource.Success ->{
                    shimmerFrameLayout.stopShimmer()
                    shimmerFrameLayout.visibility = View.GONE
                    rvSearch.visibility = View.VISIBLE
                   customerAdapter.differ.submitList(response.data?.customerList)
                }
                is Resource.Loading ->{
                    shimmerFrameLayout.visibility = View.VISIBLE
                    shimmerFrameLayout.startShimmer()
                }
                is Resource.Error ->{
                    shimmerFrameLayout.stopShimmer()
                    shimmerFrameLayout.visibility = View.GONE
                    Toast.makeText(this,response.message.toString(),Toast.LENGTH_LONG).show()
                    Log.e("SearchActivity",response.message.toString())
                }
                else -> {}
            }
        })

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                hotelViewModel.searchCustomerByName(query.toString())
                return  true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        customerAdapter.setOnItemClickListener {
                it.roomId?.toInt()?.let { it1 -> hotelViewModel.findRoomById(it1) }
                if (findRoomSuccessfully){
                    if (roomMasterData.id != null){
                        val intent = Intent(this, CustomerView::class.java)
                        val bundle = Bundle()
                        bundle.putSerializable("customer_data",it)
                        bundle.putSerializable("room_data",roomMasterData)
                        intent.putExtras(bundle)
                        startActivity(intent)
                    }
                }


        }


        handleFindRoomCall()


    }

    private fun setupRecyclerView(){
        customerAdapter = CustomerAdapter(this)
        rvSearch.apply {
            adapter = customerAdapter
            layoutManager = LinearLayoutManager(this@SearchActivity)
        }
    }

    private fun handleFindRoomCall(){
        hotelViewModel.findRoomByIdResponse.observe(this, Observer { response->
            when(response){
                is Resource.Success ->{
                    findRoomSuccessfully = true
                    roomMasterData = response.data!!
                }
                is Resource.Loading->{
                   // shimmerFrameLayout.stopShimmer()
                }
                is Resource.Error->{
                   // shimmerFrameLayout.stopShimmer()
                    response.message.let {
                        Toast.makeText(this, "An error occurred:- ${response.message}", Toast.LENGTH_LONG).show()
                    }
                }
                else -> {}
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onItemClick(position: Int, menuItem: MenuItem, item: RoomMasters) {
        TODO("Not yet implemented")
    }

    override fun onItemClickOfCustomer(position: Int, menuItem: MenuItem, item: Customer) {
        when(menuItem.itemId){
            R.id.edit ->{
                val intent = Intent(this, RoomBooking::class.java)
                val bundle = Bundle()
                bundle.putSerializable("customer_data",item)
                intent.putExtra("customer_data",bundle)
                startActivity(intent)
            }

            R.id.delete ->{
                item.id?.let { hotelViewModel.deleteCustomer(it) }
                customerAdapter.notifyItemRemoved(position)
                        Snackbar.make(View(this), "Customer Booking Deleted", Snackbar.LENGTH_LONG).apply {
                            setAction("Undo") {
                                hotelViewModel.bookRoomWithAddCustomer(item)
                                customerAdapter.notifyItemInserted(position)
                                //hotelViewModel.getAllCustomer()
                            }
                            show()
                        }
                }
        }
    }

    override fun onItemClickOfEmployee(position: Int, menuItem: MenuItem, item: Employee) {
        TODO("Not yet implemented")
    }


}