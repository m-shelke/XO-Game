package com.example.multi_playerxogame

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.multi_playerxogame.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    //initiated view binding here
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //inflating xml.layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        //binding root of the xml.layout
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //handling clicked event on playOfflineBtn
        binding.playOfflineBtn.setOnClickListener {
            createOfflineGame()
        }
    }

    //function for creating offline game
    fun createOfflineGame(){
        startGame()
    }

    //function for start the game
    fun startGame(){

        //calling GameActivity by intent
        startActivity(Intent(this,GameActivity::class.java))
    }
}