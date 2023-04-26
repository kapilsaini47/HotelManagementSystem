package com.example.hotelmanagementsystem.ui.activity.authentication

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.loader.content.Loader
import com.example.hotelmanagementsystem.R
import com.example.hotelmanagementsystem.ui.activity.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var email:EditText
    private lateinit var password:EditText
    private lateinit var forgotPassword:TextView
    private lateinit var register:TextView
    private lateinit var btnLogin:Button
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog:ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_NoActionBar)
        setContentView(R.layout.activity_login)

        initializeViews()

        loadingBox()

        register.setOnClickListener {
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnLogin.setOnClickListener {

            if (checkRequiredDetails()){
                progressDialog.show()
                auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            val intent = Intent(this,MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            progressDialog.dismiss()
                            // If sign in fails, display a message to the user.
                            Toast.makeText(baseContext, task.exception.toString(),
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }

        forgotPassword.setOnClickListener {
            val intent = Intent(this,ForgetPassword::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun loadingBox(){
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading..")
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun initializeViews(){
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        email = findViewById(R.id.etEmail)
        password = findViewById(R.id.etPassword)
        forgotPassword = findViewById(R.id.tvForgotPassword)
        register = findViewById(R.id.tvRegister)
        btnLogin = findViewById(R.id.btnLogin)
    }

    private fun checkRequiredDetails():Boolean{
        if (TextUtils.isEmpty(email.text)){
            Toast.makeText(this,"Enter email id",Toast.LENGTH_SHORT).show()
            return false
        }else if (TextUtils.isEmpty(password.text)){
            Toast.makeText(this,"Enter password",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

}