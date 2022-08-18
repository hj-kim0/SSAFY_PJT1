package com.example.todayfilm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.webkit.WebView
import com.example.todayfilm.databinding.ActivityCoverBinding


class CoverActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startService(Intent(this, ForecdTerminationService::class.java))

        val binding = ActivityCoverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        val webView : WebView = findViewById(R.id.webView)
        webView.getSettings().setJavaScriptEnabled(true)
        webView.loadUrl("file:///android_asset/html_cover.html")

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, IntroActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            startActivity(intent)
            finish()
        }, 2500)
    }
}