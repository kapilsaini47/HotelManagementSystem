package com.example.hotelmanagementsystem.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.hotelmanagementsystem.R
import com.example.hotelmanagementsystem.ui.activity.MainActivity
import com.example.hotelmanagementsystem.ui.viewmodel.HotelViewModel

class Policy : Fragment(R.layout.fragment_policy) {

    private lateinit var hotelViewModel: HotelViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hotelViewModel = (activity as MainActivity).hotelViewModel


    }
}