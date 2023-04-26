package com.example.hotelmanagementsystem.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hotelmanagementsystem.R
import com.example.hotelmanagementsystem.adapter.CustomerAdapter
import com.example.hotelmanagementsystem.models.customers.Customer
import com.example.hotelmanagementsystem.models.employee.Employee
import com.example.hotelmanagementsystem.models.room.RoomMasters
import com.example.hotelmanagementsystem.onClickInterface.OnItemClickListener
import com.example.hotelmanagementsystem.ui.activity.customer.CustomerView
import com.example.hotelmanagementsystem.ui.activity.MainActivity
import com.example.hotelmanagementsystem.ui.activity.room.RoomBooking
import com.example.hotelmanagementsystem.ui.viewmodel.HotelViewModel
import com.example.hotelmanagementsystem.utils.Resource
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.snackbar.Snackbar

class Customers : Fragment(R.layout.fragment_customers), OnItemClickListener {

    private lateinit var rvCustomers: RecyclerView
    private lateinit var personAdapter: CustomerAdapter
    private lateinit var hotelViewModel: HotelViewModel
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private var findRoomSuccessfully:Boolean = false
    private lateinit var roomMasterData:RoomMasters

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hotelViewModel = (activity as MainActivity).hotelViewModel

        rvCustomers = view.findViewById(R.id.rvCustomers)
        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container)
        setUpRecyclerview()

        hotelViewModel.getAllCustomer()
        shimmerFrameLayout.startShimmer()
        hotelViewModel.getAllBookedRoomResponse.observe(viewLifecycleOwner, Observer { response->
            when(response){
                is Resource.Success ->{
                    shimmerFrameLayout.stopShimmer()
                    shimmerFrameLayout.visibility = View.GONE
                    rvCustomers.visibility = View.VISIBLE
                    personAdapter.differ.submitList(response.data?.customerList)
                    if (personAdapter.differ.currentList.size == 0){
                        Toast.makeText(requireContext(),"No customer!!", Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading->{
                    shimmerFrameLayout.startShimmer()
                }
                is Resource.Error->{
                    shimmerFrameLayout.stopShimmer()
                    shimmerFrameLayout.visibility = View.INVISIBLE
                    response.message.let {
                        Toast.makeText(context, "An error occurred:- ${response.message}", Toast.LENGTH_LONG).show()
                    }
                }
                else -> {}
            }
        })

        personAdapter.setOnItemClickListener {
            it.roomId?.toInt()?.let { it1 -> hotelViewModel.findRoomById(it1) }
            if (findRoomSuccessfully){
                if (roomMasterData.id != null){
                    val intent = Intent(requireContext(), CustomerView::class.java)
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

    private fun setUpRecyclerview(){
        personAdapter = CustomerAdapter(this)
        rvCustomers.apply {
            adapter = personAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun handleFindRoomCall(){
        hotelViewModel.findRoomByIdResponse.observe(viewLifecycleOwner, Observer { response->
            when(response){
                is Resource.Success ->{
                    findRoomSuccessfully = true
                    roomMasterData = response.data!!
                }
                is Resource.Loading->{
                    shimmerFrameLayout.stopShimmer()
                }
                is Resource.Error->{
                    shimmerFrameLayout.stopShimmer()
                    response.message.let {
                        Toast.makeText(context, "An error occurred:- ${response.message}", Toast.LENGTH_LONG).show()
                    }
                }
                else -> {}
            }
        })
    }

    override fun onItemClick(position: Int, menuItem: MenuItem, item: RoomMasters) {
        TODO("Not yet implemented")
    }

    override fun onItemClickOfCustomer(position: Int, menuItem: MenuItem, item: Customer) {
        when(menuItem.itemId){
            R.id.edit ->{
                val intent = Intent(requireContext(), RoomBooking::class.java)
                val bundle = Bundle()
                bundle.putSerializable("customer_data",item)
                intent.putExtra("customer_data",bundle)
                startActivity(intent)
            }

            R.id.delete ->{
                item.id?.let { hotelViewModel.deleteCustomer(it) }
                personAdapter.notifyItemRemoved(position)
                //hotelViewModel.getAllCustomer()
                view.let {
                    if (it != null) {
                        Snackbar.make(it, "Customer Booking Deleted", Snackbar.LENGTH_LONG).apply {
                            setAction("Undo") {
                                hotelViewModel.bookRoomWithAddCustomer(item)
                                personAdapter.notifyItemInserted(position)
                                //hotelViewModel.getAllCustomer()
                            }
                            show()
                        }
                    }
                }
            }
        }
    }

    override fun onItemClickOfEmployee(position: Int, menuItem: MenuItem, item: Employee) {
        TODO("Not yet implemented")
    }

}