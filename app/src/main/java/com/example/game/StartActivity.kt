package com.example.game

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.game.databinding.ActivityStartBinding

// StartActivity class extending AppCompatActivity
class StartActivity : AppCompatActivity() {

    // Late-initialized variable for binding
    private lateinit var binding: ActivityStartBinding

    // onCreate method
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using ViewBinding
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set onClickListener for the start button
        binding.btnStart.setOnClickListener {

            // Start MainActivity when the button is clicked
            startActivity(Intent(this@StartActivity, MainActivity::class.java))
        }
    }
}
