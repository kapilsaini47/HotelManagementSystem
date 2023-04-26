package com.example.hotelmanagementsystem.ui.activity.authentication

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.hotelmanagementsystem.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var email:EditText
    private lateinit var password:EditText
    private lateinit var confirmPassword:EditText
    private lateinit var register:Button
    private lateinit var login:TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog:ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_NoActionBar)
        setContentView(R.layout.activity_register)

        initializeViews()
        loadingBox()

        login.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        register.setOnClickListener {
            progressDialog.show()
            if (checkRequirements()){
                val email = email.text.toString()
                val password = confirmPassword.text.toString()
                auth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(this,"Successfully Registered",Toast.LENGTH_SHORT).show()
                            val intent = Intent(this,LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            progressDialog.dismiss()
                            // If sign in fails, display a message to the user.
                            Toast.makeText(baseContext, "Authentication failed.",Toast.LENGTH_SHORT).show()

                        }
                    }
            }
        }

    }

    private fun loadingBox(){
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading..")
    }

    private fun initializeViews(){
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        email = findViewById(R.id.etRegisterMail)
        password = findViewById(R.id.etRegisterPassword)
        confirmPassword = findViewById(R.id.etPasswordConfirm)
        register = findViewById(R.id.btnRegister)
        login = findViewById(R.id.tvLogin)
    }

    private fun checkRequirements():Boolean{
        return if (TextUtils.isEmpty(email.text)){
            Toast.makeText(this,"Fill email id",Toast.LENGTH_SHORT).show()
            false
        }else if (TextUtils.isEmpty(password.text)){
            Toast.makeText(this,"Fill password",Toast.LENGTH_SHORT).show()
            false
        }else if (TextUtils.isEmpty(confirmPassword.text)){
            Toast.makeText(this,"Fill confirm password",Toast.LENGTH_SHORT).show()
            false
        }else if (password.text.toString() != confirmPassword.text.toString()){
            Toast.makeText(this,"Confirm password is not same", Toast.LENGTH_LONG).show()
            false
        }else {
            true
        }
    }

}