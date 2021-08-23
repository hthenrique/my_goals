package com.example.mygoalskotlin.Splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.mygoalskotlin.Login.View.LoginActivity
import com.example.mygoalskotlin.Main.MainActivity
import com.example.mygoalskotlin.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            val startIntent = Intent(this, LoginActivity::class.java)
            startActivity(startIntent)
            finish()
        },3000)
    }
}
