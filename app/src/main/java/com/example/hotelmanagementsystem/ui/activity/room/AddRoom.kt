package com.example.hotelmanagementsystem.ui.activity.room

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.hotelmanagementsystem.R
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

class AddRoom : AppCompatActivity() {

    private lateinit var roomImage: ImageView
    private lateinit var submitButton: Button
    private lateinit var roomNo:EditText
    private lateinit var category:EditText
    private lateinit var price:EditText
    private lateinit var description:EditText
    private lateinit var guest:EditText
    private lateinit var status: EditText
    private lateinit var hotelViewModel: HotelViewModel
    private lateinit var storageRef: StorageReference
    private lateinit var firebaseFireStore: FirebaseFirestore
    private var imageUri: Uri? = null
    private lateinit var pbRoom:ProgressBar
    private lateinit var progressDialog:ProgressDialog

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_NoActionBar)
        setContentView(R.layout.activity_add_room)

        pbRoom = findViewById(R.id.pbRoom)
        roomImage = findViewById(R.id.ivRoomPic)
        submitButton = findViewById(R.id.btnRoomSubmit)
        roomNo = findViewById(R.id.etRoomNumber)
        category = findViewById(R.id.etCategoryRoom)
        price = findViewById(R.id.etPrice)
        description = findViewById(R.id.etRoomDescription)
        guest = findViewById(R.id.etNoOfGuest)
        status = findViewById(R.id.etStatus)

        // Initialize the progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)

        val repository = Repository()
        val network = NetworkManager()
        val vmFactory  = HotelViewModelFactory(repository,network,application)
        hotelViewModel = ViewModelProvider(this,vmFactory).get(HotelViewModel::class.java)

        hotelViewModel.addRoomResponse.observe(this, Observer { response->
            when(response){
                is Resource.Success ->{
                    hideLoadingDialogBox()
                    Toast.makeText(this,"Room Added Successfully",Toast.LENGTH_SHORT).show()
                    val intents = Intent(this, MainActivity::class.java)
                    startActivity(intents)
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

        roomImage.setOnClickListener{
            resultLauncher.launch("images/*")
        }

        submitButton.setOnClickListener {

            val priceInput = price.text.toString()
            val roomNumberInput = roomNo.text.toString()
            val categoryInput = category.text.toString()
            val guestInput = guest.text.toString()
            val statusInput = status.text.toString()
            val roomImg = imageUri.toString()
            val description = description.text.toString()

            val roomDetails = RoomMasters(null,
               priceInput , categoryInput,roomNumberInput, roomImg,statusInput,description,guestInput)

            if (allFillings()){
                showLoadingDialogBox()
                hotelViewModel.addRoom(roomDetails)
                uploadImage()
            }else{
                hideLoadingDialogBox()
                Toast.makeText(this,"Fill required details", Toast.LENGTH_SHORT).show()
            }
        }

        initVars()

    }

    private fun allFillings():Boolean{
        return (roomNo.text.toString().isNotEmpty() && category.text.toString().isNotEmpty() && price.text.toString().isNotEmpty()
                && description.text.toString().isNotEmpty() && guest.text.toString().isNotEmpty())
        }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()){

        imageUri = it
        roomImage.setImageURI(it)
    }

    private fun initVars(){
        storageRef = FirebaseStorage.getInstance().reference.child("Images")
        firebaseFireStore = FirebaseFirestore.getInstance()
    }

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
                            roomImage.setImageResource(R.drawable.hotel_room)
                        }
                    }
                }else{
                    hideLoadingDialogBox()
                    Toast.makeText(this,task.exception?.message,Toast.LENGTH_SHORT).show()
                    roomImage.setImageResource(R.drawable.hotel_room)
                }
            }
        }
    }

    private fun showLoadingDialogBox(){
// Show the progress dialog
        progressDialog.show()
    }

    private fun hideLoadingDialogBox(){
// Show the progress dialog
        progressDialog.hide()
    }


}