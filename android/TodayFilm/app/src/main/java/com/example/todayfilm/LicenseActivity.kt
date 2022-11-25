package com.example.todayfilm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import kotlinx.android.synthetic.main.activity_license.*

class LicenseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license)
        val myWebView: WebView = findViewById(R.id.webView)
        myWebView.loadUrl("https://www.ridicorp.com/ridibatang/")



    }

}