package com.example.hotelmanagementsystem.ui.activity.policy

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.Toast
import com.example.hotelmanagementsystem.R
import com.example.hotelmanagementsystem.networkmanager.NetworkManager

class AppPolicy : AppCompatActivity() {

    private lateinit var webView: WebView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_NoActionBar)
        setContentView(R.layout.activity_app_policy)

        webView = findViewById(R.id.webView)

        val networkManager = NetworkManager()
        val url = "https://doc-hosting.flycricket.io/hotel-management-system-privacy-policy/53af21c6-4b24-4028-b827-fa2990158749/privacy"

        if (networkManager.hasInternetConnection(this)){
            webView.apply {
                webViewClient = WebViewClient()
                loadUrl(url)
            }
        }else{
            Toast.makeText(this, "No Network", Toast.LENGTH_LONG).show()
        }
    }
}