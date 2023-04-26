package com.example.hotelmanagementsystem.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.hotelmanagementsystem.R
import com.example.hotelmanagementsystem.networkmanager.NetworkManager
import com.example.hotelmanagementsystem.repository.Repository
import com.example.hotelmanagementsystem.ui.activity.authentication.LoginActivity
import com.example.hotelmanagementsystem.ui.activity.policy.AppPolicy
import com.example.hotelmanagementsystem.ui.activity.search.SearchActivity
import com.example.hotelmanagementsystem.ui.viewmodel.HotelViewModel
import com.example.hotelmanagementsystem.ui.viewmodel.HotelViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationViews: BottomNavigationView
    lateinit var hotelViewModel: HotelViewModel
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_HotelManagementSystem)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = "Hotel Management System"
        firebaseAuth = FirebaseAuth.getInstance()
        bottomNavigationViews = findViewById(R.id.bnView)

        val repository = Repository()
        val network = NetworkManager()
        val factory = HotelViewModelFactory(repository,network,application)
        hotelViewModel = ViewModelProvider(this, factory).get(HotelViewModel::class.java)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView2)
        val navController = navHostFragment?.findNavController()
        if (navController != null){
            bottomNavigationViews.setupWithNavController(navController)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.top_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
         when(id){
            R.id.policy -> {
                val intent = Intent(this,AppPolicy::class.java)
                startActivity(intent)
                finish()
            }
            R.id.feedback ->{
                val email = "Kk4712621@gmail.com"
                val addresses = email.split(",").toString()
                val intent = Intent(Intent.ACTION_SENDTO)
                    intent.data = Uri.parse("mailto:$email")
                    intent.putExtra(Intent.EXTRA_EMAIL,addresses)

                if (intent.resolveActivity(this.packageManager)!=null){
                    startActivity(intent)
                }
                Toast.makeText(this, "Clicked", Toast.LENGTH_LONG).show()
            }
            R.id.search ->{
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
            }
             R.id.signOut ->{
                 firebaseAuth.signOut()
                 Toast.makeText(this,"Sign out successfully",Toast.LENGTH_SHORT).show()
                 val intent = Intent(this,LoginActivity::class.java)
                 startActivity(intent)
                 finish()
             }
        }
       return super.onOptionsItemSelected(item)
    }
}