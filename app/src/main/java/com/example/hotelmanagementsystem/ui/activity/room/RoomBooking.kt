package com.example.hotelmanagementsystem.ui.activity.room

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.hotelmanagementsystem.R
import com.example.hotelmanagementsystem.models.customers.Address
import com.example.hotelmanagementsystem.models.customers.Customer
import com.example.hotelmanagementsystem.models.customers.Guest
import com.example.hotelmanagementsystem.models.paymentInfo.Payment
import com.example.hotelmanagementsystem.models.room.RoomMasters
import com.example.hotelmanagementsystem.networkmanager.NetworkManager
import com.example.hotelmanagementsystem.repository.Repository
import com.example.hotelmanagementsystem.ui.activity.MainActivity
import com.example.hotelmanagementsystem.ui.viewmodel.HotelViewModel
import com.example.hotelmanagementsystem.ui.viewmodel.HotelViewModelFactory
import com.example.hotelmanagementsystem.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.util.*

class RoomBooking : AppCompatActivity(),PaymentResultListener {

    private lateinit var roomImage: ImageView
    private lateinit var roomNumber: TextView
    private lateinit var price:TextView
    private lateinit var fullName:EditText
    private lateinit var age:EditText
    private lateinit var gender:EditText
    private lateinit var pinCode:EditText
    private lateinit var state:EditText
    private lateinit var city:EditText
    private lateinit var area:EditText
    private lateinit var contactNumber:EditText
    private lateinit var checkInDate:EditText
    private lateinit var checkOutDate:EditText
    private lateinit var guestName:EditText
    private lateinit var guestAge:EditText
    private lateinit var guestGender:EditText
    private lateinit var hotelViewModel:HotelViewModel
    private lateinit var submit:Button
    private lateinit var btnPay:Button
    private lateinit var btnCancel:Button
    private lateinit var documentImage:ImageView
    private lateinit var roomStatus:TextView
    private lateinit var orderId:TextView
    private lateinit var totalBillingAmount:TextView
    private lateinit var tvRoomCategory:TextView
    private lateinit var btnUpdateBooking:Button
    private lateinit var storageRef: StorageReference
    private lateinit var firebaseFireStore: FirebaseFirestore
    private var imageUri: Uri? = null
    private lateinit var progressDialog:ProgressDialog
    private var bookedRoomDetails:Customer? = null
    private var roomDetails:RoomMasters? = null
    private lateinit var orderIdCreated :String

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_NoActionBar)
        setContentView(R.layout.activity_room_booking)

        initializeViews()

        Checkout.preload(applicationContext)
        val co = Checkout()

        // Initialize the progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)

        //viewModel initialization
        val repository = Repository()
        val network = NetworkManager()
        val factory = HotelViewModelFactory(repository,network,application)
        hotelViewModel = ViewModelProvider(this, factory).get(HotelViewModel::class.java)

        //Receiving bundle to get data of room
        val intent = this.intent
        val bundle = intent.extras?.getBundle("room_masters")
        roomDetails = bundle?.getSerializable("room_masters") as RoomMasters?

        //Receiving bundle for booked room data
        val bookedData = this.intent
        val bundles = bookedData.extras?.getBundle("customer_data")
        bookedRoomDetails = bundles?.getSerializable("customer_data") as Customer?

        //checking for if receiving null data
        if(roomDetails!=null){
            roomNumber.text = roomDetails?.roomNo.toString()
            price.text = roomDetails?.price.toString()
            roomStatus.text = roomDetails?.status.toString()
            tvRoomCategory.text = roomDetails?.roomCategory.toString()
            Picasso.get().load(roomDetails?.roomPicture).error(R.drawable.hotel_room).into(roomImage)
        }else{
            hideLoadingDialogBox()
            Toast.makeText(this,"Some error occurred",Toast.LENGTH_SHORT).show()
        }

        //Method to set text data from booked room data
        if (bookedRoomDetails?.name != null){
            setBookedRoomDetails()
            btnPay.visibility = View.INVISIBLE
            btnUpdateBooking.visibility = View.VISIBLE
        }

        hideUpdateButton()

        //observing difference in dates in days from viewModel
        hotelViewModel.diffDays.observe(this, Observer {totalAmount->
            totalBillingAmount.text = totalAmount.toString()
        })

        //observing live data of order id created
        hotelViewModel.orderId.observe(this, Observer { id->
            orderId.text = id.toString()
        })

        hotelViewModel.addBookingResponse.observe(this, Observer { response->
            when(response){
                is Resource.Success ->{
                    hideLoadingDialogBox()
                    Toast.makeText(this,"Room booking successfully",Toast.LENGTH_SHORT).show()
                    val intents = Intent(this, MainActivity::class.java)
                    startActivity(intents)
                    finish()
                }
                is Resource.Loading->{
                    showLoadingDialogBox()
                }
                is Resource.Error->{
                    hideLoadingDialogBox()
                    response.message.let {
                        Toast.makeText(this, "An error occurred:- ${response.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })

        checkInDate.setOnClickListener {
            hotelViewModel.setDateInEditText(checkInDate)
        }

        checkOutDate.setOnClickListener {
            hotelViewModel.setDateInEditText(checkOutDate)
        }

        checkOutDate.doOnTextChanged { _, _, _, _ ->
            checkOutDate.text?.let { checkOut ->
                checkInDate.text?.let { checkIn ->
                    // if both checkIn and checkOut dates have valid values, calculate the amount
                    if (checkIn.isNotBlank() && checkOut.isNotBlank()) {
                        val startDate = checkIn.toString()
                        val endDate = checkOut.toString()
                        roomDetails?.price?.let { hotelViewModel.calculateAmount(startDate, endDate, it) }
                        hotelViewModel.generateOrderId(totalBillingAmount)
                    }
                }
            }
        }

        btnPay.setOnClickListener {
            if (allRequiredFields()){
               startPayment()
            }else{
                hideLoadingDialogBox()
                Toast.makeText(this,"Fill all required fields",Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            showLoadingDialogBox()
            Toast.makeText(this,"Booking canceled",Toast.LENGTH_SHORT).show()
            val intents = Intent(this, MainActivity::class.java)
            startActivity(intents)
            finish()
        }

        submit.setOnClickListener {
            showLoadingDialogBox()
            val roomInt = roomNumber.text.toString()
            val address = Address(city.text.toString(),null,null, null,
                pinCode.text.toString(),area.text.toString(),state.text.toString())

            //customer model class object to post data
            val customer = Customer(address,age.text.toString(), checkInDate.text.toString(),
                checkOutDate.text.toString(), gender.text.toString(), Guest(null,
                    guestName.text.toString(),guestGender.text.toString(),guestAge.text.toString()),
                null,roomDetails?.id.toString(),fullName.text.toString(),contactNumber.text.toString(),
                totalBillingAmount.text.toString(), roomInt.toInt(), imageUri.toString(),orderId.text.toString())

            val payment = Payment(null,checkInDate.text.toString(),totalBillingAmount.text.toString(),
            fullName.text.toString())

            //room master object for room details
            val roomMasters = RoomMasters(null,roomDetails?.price.toString(),roomDetails?.roomCategory.toString(),
            roomDetails?.roomNo.toString(),roomDetails?.roomPicture.toString(),"Booked",
                roomDetails?.forGuest.toString(), roomDetails?.roomDescription.toString())
            if (roomDetails != null) {
                roomDetails?.id?.let { it1 ->
                    //post request for changing room status to booked
                    hotelViewModel.updateRoom(it1,roomMasters) }
            }
            //uploading image
            uploadImage()
            //calling post method to book rom
            hotelViewModel.bookRoomWithAddCustomer(customer)
            //calling post method to add payment details
            hotelViewModel.addPayment(payment)
        }

        initVars()

        //Launch image from gallery to upload
        documentImage.setOnClickListener {
            resultLauncher.launch("images/*")
        }

        btnUpdateBooking.setOnClickListener {
            showLoadingDialogBox()
            val roomInt = roomNumber.text.toString()
            val address = Address(city.text.toString(),null,null, null,
                pinCode.text.toString(),area.text.toString(),state.text.toString())

            //customer model class object to post data
            val customer = Customer(address,age.text.toString(), checkInDate.text.toString(),
                checkOutDate.text.toString(), gender.text.toString(), Guest(null,
                    guestName.text.toString(),guestGender.text.toString(),guestAge.text.toString()),
                null,roomDetails?.id.toString(),fullName.text.toString(),roomStatus.text.toString(),contactNumber.text.toString(),
                roomInt.toInt(),imageUri.toString(),orderIdCreated)

            if (roomDetails != null) {
                roomDetails?.id?.let { it1 -> hotelViewModel.updateBookingDetails(it1,customer) }
            }
            //uploading image
            uploadImage()

        }

    }

    //returns true if all required fields are not empty
    private fun allRequiredFields():Boolean{
        return (fullName.text.toString().isNotEmpty() && age.text.toString().isNotEmpty() &&
                gender.text.toString().isNotEmpty() && pinCode.text.toString().isNotEmpty() &&
                area.text.toString().isNotEmpty() && checkInDate.toString().isNotEmpty()
                && checkOutDate.toString().isNotEmpty())
    }

    //variable for select image from device
    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()){

        imageUri = it
        documentImage.setImageURI(it)
    }

    //Initializing variables for firebase
    private fun initVars(){
        storageRef = FirebaseStorage.getInstance().reference.child("Images")
        firebaseFireStore = FirebaseFirestore.getInstance()
    }

    //upload image to firebase method
    private fun uploadImage(){
        storageRef = storageRef.child(System.currentTimeMillis().toString())
        imageUri?.let {
            storageRef.putFile(it).addOnCompleteListener{task->
                if (task.isSuccessful){

                    storageRef.downloadUrl.addOnSuccessListener {uri->

                        val map = HashMap<String,Any>()
                        map["pic"] = uri.toString()
                        firebaseFireStore.collection("images").add(map).addOnCompleteListener{firestoreTask->

                            if (firestoreTask.isSuccessful){
                                Toast.makeText(this,"Successfully uploaded", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, MainActivity::class.java)
                                hideLoadingDialogBox()
                                startActivity(intent)
                            }else{
                                hideLoadingDialogBox()
                                Toast.makeText(this,firestoreTask.exception?.message, Toast.LENGTH_SHORT).show()
                            }
                            documentImage.setImageResource(R.drawable.document_image)
                        }
                    }
                }else{
                    hideLoadingDialogBox()
                    Toast.makeText(this,task.exception?.message,Toast.LENGTH_SHORT).show()
                    documentImage.setImageResource(R.drawable.document_image)
                }
            }
        }
    }

    private fun startPayment() {
        /*
        *  You need to pass the current activity to let Razorpay create CheckoutActivity
        * */
        val activity: Activity = this
        val co = Checkout()
        co.setKeyID("rzp_test_QOZMYQ6cJ1Luo2")

        try {
            val options = JSONObject()
            options.put("name","Taj Hotels")
            options.put("description","Room Booking Charges")
            //You can omit the image option to fetch the image from the dashboard
            options.put("image","https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg")
            options.put("theme.color", "#3399cc")
            options.put("currency","INR")
            options.put("order_id", orderId.text.toString())
            options.put("amount",(totalBillingAmount.text.toString().toInt()*100).toString())//pass amount in currency subunits

            val retryObj = JSONObject()
            retryObj.put("enabled", true)
            retryObj.put("max_count", 4)
            options.put("retry", retryObj)

            val prefill = JSONObject()
            prefill.put("email","gaurav.kumar@example.com")
            prefill.put("contact",contactNumber.text.toString())

            options.put("prefill",prefill)
            co.open(activity,options)
        }catch (e: Exception){
            Toast.makeText(activity,"Error in payment: "+ e.message,Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        hideLoadingDialogBox()
        submit.visibility = View.VISIBLE
        btnCancel.visibility =View.INVISIBLE
        btnPay.visibility = View.INVISIBLE
        Toast.makeText(this,"Payment Successful",Toast.LENGTH_LONG).show()
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        hideLoadingDialogBox()
        Toast.makeText(this,"Payment Failed",Toast.LENGTH_LONG).show()
    }

    private fun showLoadingDialogBox(){
// Show the progress dialog
        progressDialog.show()
    }

    private fun hideLoadingDialogBox(){
// Show the progress dialog
        progressDialog.hide()
    }

    private fun hideUpdateButton(){
        if (bookedRoomDetails?.orderId?.toString() == null){
            btnUpdateBooking.visibility = View.INVISIBLE
        }else{
            btnUpdateBooking.visibility = View.VISIBLE
            btnPay.visibility = View.INVISIBLE
        }
    }

    private fun setBookedRoomDetails() {
        roomNumber.text = bookedRoomDetails?.roomNo.toString()
        Picasso.get().load(bookedRoomDetails?.documentImage).error(R.drawable.document_image).into(documentImage)
        fullName.setText(bookedRoomDetails?.name)
        age.setText(bookedRoomDetails?.age)
        gender.setText(bookedRoomDetails?.gender)
        contactNumber.setText(bookedRoomDetails?.contactNumber.toString())
        roomStatus.text = roomDetails?.status.toString()
        orderId.text = bookedRoomDetails?.orderId.toString()
        checkOutDate.setText(bookedRoomDetails?.checkOutDate.toString())
        checkInDate.setText(bookedRoomDetails?.checkInDate.toString())
        city.setText(bookedRoomDetails?.address?.city)
        state.setText(bookedRoomDetails?.address?.state)
        pinCode.setText(bookedRoomDetails?.address?.pinCode)
        area.setText(bookedRoomDetails?.address?.societyName)
        guestName.setText(bookedRoomDetails?.guests?.name)
        guestGender.setText(bookedRoomDetails?.guests?.gender)
        guestAge.setText(bookedRoomDetails?.guests?.age)

    }

    private fun initializeViews(){
        //Initializing views
        roomImage = findViewById(R.id.ivImageRoom)
        roomNumber = findViewById(R.id.tvRoomNumber)
        price = findViewById(R.id.tvPrice)
        fullName = findViewById(R.id.etFullName)
        age = findViewById(R.id.etAge)
        gender = findViewById(R.id.etGender)
        pinCode = findViewById(R.id.etPinCode)
        state = findViewById(R.id.etState)
        city = findViewById(R.id.etCity)
        area = findViewById(R.id.etArea)
        contactNumber = findViewById(R.id.etContactNumber)
        checkInDate = findViewById(R.id.etCheckInDate)
        checkOutDate = findViewById(R.id.etCheckOutDate)
        guestName = findViewById(R.id.etGuestName)
        guestAge = findViewById(R.id.etGuestAge)
        guestGender = findViewById(R.id.etGuestGender)
        submit = findViewById(R.id.btnBookRoom)
        documentImage = findViewById(R.id.ivDocumentImage)
        btnPay = findViewById(R.id.btnPay)
        btnCancel = findViewById(R.id.btnCancel)
        roomStatus = findViewById(R.id.tvStatus)
        btnUpdateBooking = findViewById(R.id.btnUpdateBooking)
        orderId = findViewById(R.id.tvOrderId)
        totalBillingAmount = findViewById(R.id.tvTotalBill)
        tvRoomCategory = findViewById(R.id.tvRoomCategory)
    }

}