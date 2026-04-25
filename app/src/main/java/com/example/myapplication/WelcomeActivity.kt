package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.NavOptions
import com.example.myapplication.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startButton.setOnClickListener {
            // Навигация через NavController
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController

            navController.navigate(
                R.id.action_startFragment_to_collectionFragment,  // ID action из nav_graph.xml
                null,
                NavOptions.Builder()
                    .setPopUpTo(R.id.startFragment, true)  // Убрать StartFragment из back-stack
                    .build()
            )
        }
    }
}