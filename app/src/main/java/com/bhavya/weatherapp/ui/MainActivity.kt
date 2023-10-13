package com.bhavya.weatherapp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bhavya.weatherapp.R
import com.bhavya.weatherapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() , MainFragment.Callbacks {

    private lateinit var binding: ActivityMainBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment == null) {

            val fragment = MainFragment.newInstance()

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
    }


    override fun showMessage(msg:String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
    }


}