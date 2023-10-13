package com.bhavya.weatherapp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bhavya.weatherapp.R
import com.bhavya.weatherapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() , MainFragment.Callbacks, ErrorFragment.RetryCallback {

    private lateinit var binding: ActivityMainBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment == null) {
            launchWeatherScreen();
        }
    }


    override fun onError(msg:String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
//        val fragment = ErrorFragment.newInstance(msg)
//        supportFragmentManager
//            .beginTransaction()
//            .replace(R.id.fragment_container, fragment)
//            .addToBackStack(null)
//            .commit()
    }

    override fun onRetry() {
        launchWeatherScreen()

    }

    private fun launchWeatherScreen() {
        val fragment = MainFragment.newInstance()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }


}