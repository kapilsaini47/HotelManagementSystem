package com.example.hotelmanagementsystem.ui.tabs

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hotelmanagementsystem.R
import com.example.hotelmanagementsystem.adapter.PaymentAdapter
import com.example.hotelmanagementsystem.ui.activity.MainActivity
import com.example.hotelmanagementsystem.ui.viewmodel.HotelViewModel
import com.example.hotelmanagementsystem.utils.Resource
import com.facebook.shimmer.ShimmerFrameLayout

class PaymentStatusTab : Fragment(R.layout.fragment_payment_status_tab) {

    private lateinit var shimmer: ShimmerFrameLayout
    private lateinit var hotelViewModel: HotelViewModel
    private lateinit var rvPayment : RecyclerView
    private lateinit var paymentAdapter: PaymentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hotelViewModel = (activity as MainActivity).hotelViewModel
        rvPayment = view.findViewById(R.id.rvPayment)
        shimmer = view.findViewById(R.id.shimmerLayout)

        setupRecyclerView()
        hotelViewModel.getAllPayments()
        hotelViewModel.paymentResponse.observe(viewLifecycleOwner, Observer { response->
            when(response){
                is Resource.Success ->{
                    shimmer.stopShimmer()
                    shimmer.visibility = View.GONE
                    rvPayment.visibility = View.VISIBLE
                    paymentAdapter.differ.submitList(response.data?.paymentsList)
                }
                is Resource.Loading ->{
                    shimmer.startShimmer()
                    shimmer.visibility = View.VISIBLE
                }
                is Resource.Error ->{
                    shimmer.stopShimmer()
                    shimmer.visibility = View.GONE
                    Toast.makeText(requireContext(),response.message.toString(),Toast.LENGTH_LONG).show()
                }
            }
        })

    }

    private fun setupRecyclerView(){
        paymentAdapter = PaymentAdapter()
        rvPayment.apply {
            adapter = paymentAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }


}