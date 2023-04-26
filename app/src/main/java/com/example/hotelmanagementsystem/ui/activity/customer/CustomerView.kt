package com.example.hotelmanagementsystem.ui.activity.customer

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.hotelmanagementsystem.R
import com.example.hotelmanagementsystem.models.customers.Customer
import com.example.hotelmanagementsystem.models.room.RoomMasters
import com.example.hotelmanagementsystem.networkmanager.NetworkManager
import com.example.hotelmanagementsystem.repository.Repository
import com.example.hotelmanagementsystem.ui.activity.MainActivity
import com.example.hotelmanagementsystem.ui.viewmodel.HotelViewModel
import com.example.hotelmanagementsystem.ui.viewmodel.HotelViewModelFactory
import com.example.hotelmanagementsystem.utils.Resource
import com.squareup.picasso.Picasso

class CustomerView : AppCompatActivity() {

    private lateinit var roomImage: ImageView
    private lateinit var roomNumber: TextView
    private lateinit var price: TextView
    private lateinit var fullName: TextView
    private lateinit var age: TextView
    private lateinit var gender: TextView
    private lateinit var pinCode: TextView
    private lateinit var state: TextView
    private lateinit var city: TextView
    private lateinit var area: TextView
    private lateinit var contactNumber: TextView
    private lateinit var checkInDate: TextView
    private lateinit var checkOutDate: TextView
    private lateinit var guestName: TextView
    private lateinit var guestAge: TextView
    private lateinit var guestGender: TextView
    private lateinit var hotelViewModel: HotelViewModel
    private lateinit var btnCheckOut: Button
    private lateinit var btnCancel: Button
    private lateinit var documentImage: ImageView
    private lateinit var roomStatus: TextView
    private lateinit var orderId: TextView
    private lateinit var totalPayment: TextView
    private lateinit var tvRoomCategory: TextView
    private lateinit var bookingDetails:Customer
    private lateinit var roomData:RoomMasters
    private lateinit var progressDialog:ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_NoActionBar)
        setContentView(R.layout.activity_customer_view)

        //Initializing views
          initializeViewsId()

        //viewModel initialization
        val repository = Repository()
        val network = NetworkManager()
        val factory = HotelViewModelFactory(repository,network,application)
        hotelViewModel = ViewModelProvider(this, factory).get(HotelViewModel::class.java)

        //Receiving bundle to get data of room
        val intent = this.intent
        val bundle = intent.extras
        bookingDetails = bundle?.getSerializable("customer_data") as Customer
        roomData = bundle.getSerializable("room_data") as RoomMasters

        // Initialize the progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)

        if (bookingDetails.name != null){
            //set text views data
            settingTextViews()
        }else{
            Toast.makeText(this,"Error occurred",Toast.LENGTH_LONG).show()
        }

        hotelViewModel.deleteBookRoomResponse.observe(this, Observer { response->
            when(response){
                is Resource.Success ->{
                    progressDialog.show()
                }
                is Resource.Loading->{
                    progressDialog.show()
                }
                is Resource.Error->{
                    response.message.let {
                        Toast.makeText(this, "An error occurred:- ${response.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })

        hotelViewModel.updateRoomResponse.observe(this, Observer {response->
            when(response){
                is Resource.Success ->{
                    progressDialog.hide()
                    val cancelIntent = Intent(this, MainActivity::class.java)
                    startActivity(cancelIntent)
                    finish()
                }
                is Resource.Loading->{
                    progressDialog.show()
                }
                is Resource.Error->{
                    response.message.let {
                        Toast.makeText(this, "An error occurred:- ${response.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })

        btnCancel.setOnClickListener {
            val cancelIntent = Intent(this, MainActivity::class.java)
            startActivity(cancelIntent)
            finish()
        }

        btnCheckOut.setOnClickListener {
            bookingDetails.id?.let { it1 -> hotelViewModel.deleteCustomer(it1) }
            val roomUpdatedData = RoomMasters(null,roomData.price,roomData.roomCategory,roomData.roomNo,
            roomData.roomPicture,"Available",roomData.forGuest,roomData.roomDescription)
            roomData.id?.let { it1 -> hotelViewModel.updateRoom(it1,roomUpdatedData) }
        }

    }

    private fun initializeViewsId(){
        roomImage = findViewById(R.id.ivImageRoom)
        roomNumber = findViewById(R.id.tvRoomNumber)
        tvRoomCategory = findViewById(R.id.tvRoomCategory)
        roomStatus = findViewById(R.id.tvStatus)
        price = findViewById(R.id.tvPrice)
        fullName = findViewById(R.id.tvFullName)
        age = findViewById(R.id.tvAge)
        gender = findViewById(R.id.tvGender)
        contactNumber = findViewById(R.id.tvContactNumber)
        state = findViewById(R.id.tvState)
        city = findViewById(R.id.tvCity)
        pinCode = findViewById(R.id.tvPinCode)
        area = findViewById(R.id.tvArea)
        checkInDate = findViewById(R.id.tvCheckInDate)
        checkOutDate = findViewById(R.id.tvCheckOutDate)
        guestName = findViewById(R.id.tvGuestName)
        guestAge = findViewById(R.id.tvGuestAge)
        guestGender = findViewById(R.id.tvGuestGender)
        orderId = findViewById(R.id.tvOrderId)
        totalPayment = findViewById(R.id.tvTotalBill)
        btnCheckOut = findViewById(R.id.btnCheckOut)
        btnCancel = findViewById(R.id.btnCancelBooking)
        documentImage = findViewById(R.id.ivDocumentImage)
    }

    private fun settingTextViews(){
        fullName.text = bookingDetails.name
        age.text = bookingDetails.age
        gender.text = bookingDetails.gender
        contactNumber.text = bookingDetails.contactNumber
        state.text = bookingDetails.address.state
        city.text = bookingDetails.address.city
        pinCode.text = bookingDetails.address.pinCode
        area.text = bookingDetails.address.societyName
        checkInDate.text = bookingDetails.checkInDate.toString()
        checkOutDate.text = bookingDetails.checkOutDate.toString()
        guestName.text = bookingDetails.guests.name
        guestGender.text = bookingDetails.guests.gender
        guestAge.text = bookingDetails.guests.age
        Picasso.get().load(bookingDetails.documentImage).error(R.drawable.document_image).into(documentImage)
        roomNumber.text = roomData.roomNo
        tvRoomCategory.text = roomData.roomCategory
        Picasso.get().load(roomData.roomPicture).error(R.drawable.room_image).into(roomImage)
        roomStatus.text = roomData.status
        price.text = roomData.price
        orderId.text = bookingDetails.orderId.toString()
        totalPayment.text = bookingDetails.totalAmount.toString()
    }

}