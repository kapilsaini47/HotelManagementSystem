package com.example.hotelmanagementsystem.ui.tabs

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hotelmanagementsystem.R
import com.example.hotelmanagementsystem.adapter.EmployeeStatusAdapter
import com.example.hotelmanagementsystem.ui.activity.MainActivity
import com.example.hotelmanagementsystem.ui.viewmodel.HotelViewModel
import com.example.hotelmanagementsystem.utils.Resource
import com.facebook.shimmer.ShimmerFrameLayout


class EmployeeStatusTab : Fragment(R.layout.fragment_employee_status_tab) {

    private lateinit var rvEmployee: RecyclerView
    private lateinit var employeeAdapter: EmployeeStatusAdapter
    private lateinit var hotelViewModel:HotelViewModel
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hotelViewModel = (activity as MainActivity).hotelViewModel
        rvEmployee = view.findViewById(R.id.rvEmployee)
        shimmerFrameLayout = view.findViewById(R.id.shimmerLayout)

        setupRecyclerView()

        hotelViewModel.getAllEmployee()
        shimmerFrameLayout.startShimmer()
        hotelViewModel.employeeResponse.observe(viewLifecycleOwner, Observer { response->
            when(response){
                is Resource.Success ->{
                    shimmerFrameLayout.stopShimmer()
                    shimmerFrameLayout.visibility = View.GONE
                    rvEmployee.visibility = View.VISIBLE
                    employeeAdapter.differ.submitList(response.data?.employeeList)
                }
                is Resource.Loading -> {
                    shimmerFrameLayout.startShimmer()
                    shimmerFrameLayout.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    shimmerFrameLayout.stopShimmer()
                    shimmerFrameLayout.visibility = View.GONE
                    Toast.makeText(requireContext(),response.message.toString(),Toast.LENGTH_LONG).show()
                }
            }
        })


    }

    private fun setupRecyclerView(){
        employeeAdapter = EmployeeStatusAdapter()
        rvEmployee.apply {
            adapter = employeeAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }


}
