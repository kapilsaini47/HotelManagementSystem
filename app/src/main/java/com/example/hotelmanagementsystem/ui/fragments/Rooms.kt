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
import com.example.hotelmanagementsystem.adapter.RoomAdapter
import com.example.hotelmanagementsystem.models.customers.Customer
import com.example.hotelmanagementsystem.models.employee.Employee
import com.example.hotelmanagementsystem.models.room.RoomMasters
import com.example.hotelmanagementsystem.onClickInterface.OnItemClickListener
import com.example.hotelmanagementsystem.ui.activity.room.AddRoom
import com.example.hotelmanagementsystem.ui.activity.MainActivity
import com.example.hotelmanagementsystem.ui.activity.room.RoomBooking
import com.example.hotelmanagementsystem.ui.activity.room.RoomView
import com.example.hotelmanagementsystem.ui.viewmodel.HotelViewModel
import com.example.hotelmanagementsystem.utils.Resource
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class Rooms : Fragment(R.layout.fragment_rooms), OnItemClickListener {

    private lateinit var rvRooms:RecyclerView
    private lateinit var roomAdapter: RoomAdapter
    private lateinit var hotelViewModel: HotelViewModel
    private lateinit var fab: FloatingActionButton
    private lateinit var shimmerView:ShimmerFrameLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hotelViewModel = (activity as MainActivity).hotelViewModel
        fab = view.findViewById(R.id.fab)
        rvRooms = view.findViewById(R.id.rvRooms)
        shimmerView = view.findViewById(R.id.shimmer_view_container)

        setUpRecyclerView()

        hotelViewModel.roomResponse.observe(viewLifecycleOwner, Observer { response->
            when(response){
                is Resource.Success ->{
                    shimmerView.stopShimmer()
                    shimmerView.visibility = View.GONE
                    rvRooms.visibility = View.VISIBLE
                    roomAdapter.differ.submitList(response.data?.roomMastersList)
                    //condition to show an image if the adapter list is empty
                    if (roomAdapter.differ.currentList.size == 0){
                        Toast.makeText(requireContext(),"No room added",Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading->{
                    shimmerView.visibility = View.VISIBLE
                    shimmerView.startShimmer()
                }
                is Resource.Error->{
                    shimmerView.stopShimmer()
                    shimmerView.visibility = View.GONE
                    response.message.let {
                        Toast.makeText(context, "An error occurred:- ${response.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })

        fab.setOnClickListener {
            val intent = Intent(activity, AddRoom::class.java)
            startActivity(intent)
        }

        //Views or item click listener handler
        roomAdapter.setOnItemClickListener {
            if (it.status == "Booked"){
                Toast.makeText(requireContext(),"Already Booked",Toast.LENGTH_LONG).show()
            }else{
                val intent = Intent(activity, RoomBooking::class.java)
                val bundle = Bundle()
                bundle.putSerializable("room_masters", it)
                intent.putExtra("room_masters", bundle)
                startActivity(intent)
            }
        }
    }

    //Initializing recycler
    private fun setUpRecyclerView(){
        roomAdapter = RoomAdapter(this)
        rvRooms.apply {
            adapter = roomAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    override fun onItemClick(position: Int, menuItem: MenuItem, item: RoomMasters) {
        when(menuItem.itemId){
            R.id.edit ->{
                val intent = Intent(requireContext(), RoomView::class.java)
                val bundle = Bundle()
                bundle.putSerializable("room_masterss", item)
                intent.putExtra("room_masterss", bundle)
                startActivity(intent)
            }
            R.id.delete ->{
                if (item.status == "Booked"){
                    Toast.makeText(requireContext(),"Booked room can't be deleted",Toast.LENGTH_LONG).show()
                }else{
                    item.id?.let { hotelViewModel.deleteRoom(it) }
                    roomAdapter.notifyItemRemoved(position)
                    //hotelViewModel.getAllRooms()
                    view?.let {
                        Snackbar.make(it, "Room Successfully deleted", Snackbar.LENGTH_LONG).apply {
                            setAction("Undo") {
                                hotelViewModel.addRoom(item)
                                roomAdapter.notifyItemInserted(position)
                                //roomAdapter.notifyDataSetChanged()
                                //hotelViewModel.getAllRooms()
                            }
                            show()
                        }
                    }
                }
            }
        }
    }

    override fun onItemClickOfCustomer(position: Int, menuItem: MenuItem, item: Customer) {
        TODO("Not yet implemented")
    }

    override fun onItemClickOfEmployee(position: Int, menuItem: MenuItem, item: Employee) {
        TODO("Not yet implemented")
    }

}