package com.example.hotelmanagementsystem.ui.fragments

import android.app.ProgressDialog
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
import com.example.hotelmanagementsystem.adapter.EmployeeAdapter
import com.example.hotelmanagementsystem.models.customers.Customer
import com.example.hotelmanagementsystem.models.employee.Employee
import com.example.hotelmanagementsystem.models.room.RoomMasters
import com.example.hotelmanagementsystem.onClickInterface.OnItemClickListener
import com.example.hotelmanagementsystem.ui.activity.MainActivity
import com.example.hotelmanagementsystem.ui.activity.employee.AddEmployee
import com.example.hotelmanagementsystem.ui.viewmodel.HotelViewModel
import com.example.hotelmanagementsystem.utils.Resource
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class Employee : Fragment(R.layout.fragment_employee), OnItemClickListener {

    private lateinit var rvEmployee: RecyclerView
    private lateinit var hotelViewModel: HotelViewModel
    private lateinit var employeeAdapter: EmployeeAdapter
    private lateinit var fab:FloatingActionButton
    private lateinit var progressDialog:ProgressDialog
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hotelViewModel = (activity as MainActivity).hotelViewModel

        rvEmployee = view.findViewById(R.id.rvEmployee)
        fab = view.findViewById(R.id.fab)
        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container)
        setUpRecyclerview()

        // Initialize the progress dialog
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)

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
            }
        })

        fab.setOnClickListener {
            val intent = Intent(context, AddEmployee::class.java)
            startActivity(intent)
        }

        //handle view click Listener
        employeeAdapter.setOnItemClickListener {
            val intent = Intent(activity, AddEmployee::class.java)
            val bundle = Bundle()
            bundle.putSerializable("employee_data", it)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    private fun setUpRecyclerview(){
        employeeAdapter = EmployeeAdapter(this)
        rvEmployee.apply {
            adapter = employeeAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    override fun onItemClickOfEmployee(position: Int, menuItem: MenuItem, item: Employee) {
        when(menuItem.itemId){
            R.id.edit ->{
                val intent = Intent(requireContext(), AddEmployee::class.java)
                val bundle = Bundle()
                bundle.putSerializable("employee_data_update",item)
                intent.putExtras(bundle)
                startActivity(intent)
            }
            R.id.delete ->{
                item.id?.let { hotelViewModel.deleteEmployee(it) }
                employeeAdapter.notifyItemRemoved(position)
                hotelViewModel.getAllEmployee()
                view.let {
                    if (it != null) {
                        Snackbar.make(it, "Employee Successfully Deleted", Snackbar.LENGTH_LONG).apply {
                            setAction("Undo") {
                                hotelViewModel.addEmployee(item)
                                hotelViewModel.getAllEmployee()
                            }
                            show()
                        }
                    }
                }
            }
        }
    }

    override fun onItemClick(position: Int, menuItem: MenuItem, item: RoomMasters) {
        TODO("Not yet implemented")
    }

    override fun onItemClickOfCustomer(position: Int, menuItem: MenuItem, item: Customer) {
        TODO("Not yet implemented")
    }

}