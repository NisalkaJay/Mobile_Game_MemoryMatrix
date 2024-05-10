package com.example.game

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.edit
import androidx.core.view.forEach
import androidx.lifecycle.lifecycleScope
import com.example.game.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private var score = 0
    private var HighScore = 0
    private var result: String = ""
    private var userAnswer: String = ""

    // Define shared preferences file name
    private val sharedPrefName = "MyGamePrefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            panel1.setOnClickListener(this@MainActivity)
            panel2.setOnClickListener(this@MainActivity)
            panel3.setOnClickListener(this@MainActivity)
            panel4.setOnClickListener(this@MainActivity)
            startGame()
        }

        // Load high score from shared preferences when the activity is created
        loadHighScore()
    }

    // Function to load high score from shared preferences
    private fun loadHighScore() {
        val sharedPrefs = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        HighScore = sharedPrefs.getInt("highScore", 0) // Default value is 0 if high score is not stored
        binding.highScore.text = HighScore.toString()
        
    }

    // Function to save high score to shared preferences
    private fun saveHighScore() {
        val sharedPrefs = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        sharedPrefs.edit {
            putInt("highScore", HighScore)
            apply() // Apply changes asynchronously
        }
    }

    private fun disableButtons() {
        binding.root.forEach { view ->
            if (view is Button) {
                view.isEnabled = false
            }
        }
    }

    private fun enableButtons() {
        binding.root.forEach { view ->
            if (view is Button) {
                view.isEnabled = true
            }
        }
    }

    private fun startGame() {
        result = ""
        userAnswer = ""
        disableButtons()
        lifecycleScope.launch {
            val round = (3..5).random()
            repeat(round) {
                delay(400)
                val randomPanel = (1..4).random()
                result += randomPanel.toString() // Convert to String for comparison
                val panel = when (randomPanel) {
                    1 -> binding.panel1
                    2 -> binding.panel2
                    3 -> binding.panel3
                    else -> binding.panel4
                }
                val drawableYellow = ActivityCompat.getDrawable(this@MainActivity, R.drawable.btn_yellow)
                val drawableDefault = ActivityCompat.getDrawable(this@MainActivity, R.drawable.btn_state)
                panel.background = drawableYellow
                delay(1000)
                panel.background = drawableDefault
            }
            runOnUiThread {
                enableButtons()
            }
        }
    }

    private fun loseAnimation() {
        binding.apply {
            if (score > HighScore) {
                HighScore = score
                highScore.text = HighScore.toString()
                saveHighScore() // Save high score to shared preferences
            }
            score = 0
            binding.currentScore.text = score.toString() // Update score text view
            disableButtons()
            val drawableLose = ActivityCompat.getDrawable(this@MainActivity, R.drawable.btn_lose)
            val drawableDefault = ActivityCompat.getDrawable(this@MainActivity, R.drawable.btn_state)
            lifecycleScope.launch {
                binding.root.forEach { view ->
                    if (view is Button) {
                        view.background = drawableLose
                        delay(300)
                        view.background = drawableDefault
                    }
                }
                delay(1000)
                Toast.makeText(this@MainActivity, "Game Over", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@MainActivity, StartActivity::class.java))
            }
        }
    }

    override fun onClick(view: View?) {
        view?.let {
            userAnswer += when (it.id) {
                R.id.panel1 -> "1"
                R.id.panel2 -> "2"
                R.id.panel3 -> "3"
                R.id.panel4 -> "4"
                else -> ""
            }
            if (userAnswer == result) {
                Toast.makeText(this@MainActivity, "Won the round ", Toast.LENGTH_SHORT).show()
                score++
                binding.currentScore.text = score.toString() // Update score text view
                startGame()
            } else if (userAnswer.length >= result.length) {
                loseAnimation()
            }
        }
    }
}
