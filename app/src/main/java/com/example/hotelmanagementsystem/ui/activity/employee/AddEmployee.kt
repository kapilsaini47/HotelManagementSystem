package com.example.hotelmanagementsystem.ui.activity.employee

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.hotelmanagementsystem.R
import com.example.hotelmanagementsystem.models.employee.Address
import com.example.hotelmanagementsystem.models.employee.Employee
import com.example.hotelmanagementsystem.networkmanager.NetworkManager
import com.example.hotelmanagementsystem.repository.Repository
import com.example.hotelmanagementsystem.ui.activity.MainActivity
import com.example.hotelmanagementsystem.ui.viewmodel.HotelViewModel
import com.example.hotelmanagementsystem.ui.viewmodel.HotelViewModelFactory
import com.example.hotelmanagementsystem.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlin.random.Random

class AddEmployee : AppCompatActivity() {

    private lateinit var hotelViewModel: HotelViewModel
    private lateinit var imgProfile:ImageView
    private lateinit var etName:EditText
    private lateinit var etMobileNumber:EditText
    private lateinit var etDesignation:EditText
    private lateinit var etEmpId:EditText
    private lateinit var etEmail:EditText
    private lateinit var btnSubmit:Button
    private lateinit var etPin:EditText
    private lateinit var etCity:EditText
    private lateinit var etState:EditText
    private lateinit var etSociety:EditText
    private lateinit var etAge:EditText
    private lateinit var etGender:EditText
    private lateinit var storageRef: StorageReference
    private lateinit var firebaseFireStore: FirebaseFirestore
    private var imageUri: Uri? = null
    private lateinit var progressDialog:ProgressDialog
    private var employeeDetails: Employee? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_NoActionBar)
        setContentView(R.layout.activity_add_employee)

        initializeViews()

        // Initialize the progress dialog
        loadingBox()

        try{
            //receiving intent data
            val intent = this.intent
            val bundle = intent.extras
            employeeDetails = bundle?.getSerializable("employee_data") as Employee
        }catch (e:Exception){
            Log.e("Add employee", e.message.toString())
        }

        try {
            setViewsFromReceviedIntent()
        }catch (e:Exception){
            Log.e("Add employee", e.message.toString())
        }


        val repository = Repository()
        val network = NetworkManager()
        val factory = HotelViewModelFactory(repository,network,application)
        hotelViewModel = ViewModelProvider(this, factory).get(HotelViewModel::class.java)

        hotelViewModel.addEmployeeResponse.observe(this, Observer { response->
            when(response){
                is Resource.Success ->{
                    hideLoadingDialogBox()
                    Toast.makeText(this,"Employee Added Successfully",Toast.LENGTH_SHORT).show()
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

        hotelViewModel.updateEmployeeResponse.observe(this, Observer { response->
            when(response){
                is Resource.Success ->{
                    hideLoadingDialogBox()
                    Toast.makeText(this,"Employee successfully updated",Toast.LENGTH_SHORT).show()
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

        hotelViewModel.employeeId.observe(this, Observer { response->
            etEmpId.setText(response)
        })

        imgProfile.setOnClickListener{
            resultLauncher.launch("images/*")
        }

        btnSubmit.setOnClickListener {

            if (employeeDetails?.id != null){
                val address = Address(null,etCity.text.toString(),etPin.text.toString(),
                    etSociety.text.toString(),etState.text.toString(),null,null)
                val position = etDesignation.text.toString()

                val employeeObject = Employee(null,address,etAge.text.toString(),etEmpId.text.toString(),
                    etGender.text.toString(),imageUri.toString(),etName.text.toString(),position)

                if (allFieldsRequire()){
                    employeeDetails?.id?.let { it1 -> hotelViewModel.updateEmployee(it1,employeeObject) }
                    showLoadingDialogBox()
                    uploadImage()
                }else{
                    Toast.makeText(this,"Please fill all required fields",Toast.LENGTH_SHORT).show()
                }
            }else {
                val address = Address(null,etCity.text.toString(),etPin.text.toString(),
                    etSociety.text.toString(),etState.text.toString(),null,null)
                val position = etDesignation.text.toString()

                val employeeDetails = Employee(null,address,etAge.text.toString(),etEmpId.text.toString(),
                    etGender.text.toString(),imageUri.toString(),etName.text.toString(),position)

                if (allFieldsRequire()){
                    hotelViewModel.addEmployee(employeeDetails)
                    showLoadingDialogBox()
                    uploadImage()
                }else{
                    Toast.makeText(this,"Please fill all required fields",Toast.LENGTH_SHORT).show()
                }
            }
        }

        etDesignation.doOnTextChanged { _, _, _, _ ->
            hotelViewModel.generateEmployeeId()
        }

        initVars()

    }

    private fun setViewsFromReceviedIntent(){
        Picasso.get().load(employeeDetails?.profileImage).error(R.drawable.person).into(imgProfile)
        etName.setText(employeeDetails?.name)
       // etMobileNumber.setText(employeeDetails?.)
        etDesignation.setText(employeeDetails?.position)
        //etEmail.setText(employeeDetails?.)
        etGender.setText(employeeDetails?.gender)
        etAge.setText(employeeDetails?.age)
        etEmpId.setText(employeeDetails?.eId)
        etPin.setText(employeeDetails?.address?.pinCode)
        etCity.setText(employeeDetails?.address?.city)
        etState.setText(employeeDetails?.address?.state)
        etSociety.setText(employeeDetails?.address?.societyName)
    }


    private fun loadingBox() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
    }

    private fun initializeViews() {
        imgProfile = findViewById(R.id.imgProfile)
        etName = findViewById(R.id.etFullName)
        etMobileNumber = findViewById(R.id.etMobile)
        etDesignation = findViewById(R.id.etDesignation)
        etEmpId = findViewById(R.id.etEmployee)
        etEmail = findViewById(R.id.etEmail)
        btnSubmit = findViewById(R.id.btnSubmit)
        etPin = findViewById(R.id.etPin)
        etCity = findViewById(R.id.etCity)
        etState = findViewById(R.id.etState)
        etSociety = findViewById(R.id.etSociety)
        etAge = findViewById(R.id.etAge)
        etGender = findViewById(R.id.etGender)
    }

    private fun allFieldsRequire():Boolean{
        return( (etCity.text.toString().isNotEmpty() && etPin.text.toString().isNotEmpty() && etSociety.text.toString().isNotEmpty()
                && etState.text.toString().isNotEmpty()) && etAge.text.toString().isNotEmpty()
                && etEmpId.text.toString().isNotEmpty() && etGender.text.toString().isNotEmpty() &&
                etEmpId.text.toString().isNotEmpty() && etName.text.toString().isNotEmpty() && etDesignation.text.toString().isNotEmpty()
                )
    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()){

        imageUri = it
        imgProfile.setImageURI(it)
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
                            imgProfile.setImageResource(R.drawable.person)
                        }
                    }
                }else{
                    hideLoadingDialogBox()
                    Toast.makeText(this,task.exception?.message,Toast.LENGTH_SHORT).show()
                    imgProfile.setImageResource(R.drawable.person)
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