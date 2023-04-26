package com.example.hotelmanagementsystem.ui.activity.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.hotelmanagementsystem.R
import com.google.firebase.auth.FirebaseAuth

class ForgetPassword : AppCompatActivity() {

    private lateinit var login:TextView
    private lateinit var btnForget:Button
    private lateinit var etEmail:EditText
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        initializeViews()

        login.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnForget.setOnClickListener {
            if (TextUtils.isEmpty(etEmail.text)){
                Toast.makeText(this,"Enter email",Toast.LENGTH_SHORT).show()
            }else{
                auth.sendPasswordResetEmail(etEmail.text.toString())
                    .addOnCompleteListener{ response->
                        if (response.isSuccessful){
                            Toast.makeText(this,"Check your email",Toast.LENGTH_SHORT).show()
                            val intent = Intent(this,LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }else{
                            Toast.makeText(this,response.exception.toString(),Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }

    private fun initializeViews(){
        login = findViewById(R.id.tvLogin)
        btnForget = findViewById(R.id.btnForget)
        etEmail = findViewById(R.id.etEmail)
        auth = FirebaseAuth.getInstance()
    }

}