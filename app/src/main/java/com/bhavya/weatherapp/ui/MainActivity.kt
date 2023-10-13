package com.bhavya.weatherapp.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bhavya.weatherapp.R
import com.bhavya.weatherapp.WeatherApplication
import com.bhavya.weatherapp.databinding.ActivityMainBinding
import com.bhavya.weatherapp.di.component.DaggerActivityComponent
import com.bhavya.weatherapp.viewmodel.WeatherViewModel
import kotlinx.coroutines.launch
import com.bhavya.weatherapp.ui.MainFragment
import javax.inject.Inject

class MainActivity : AppCompatActivity() , MainFragment.Callbacks {
    private val LOCATION_PERMISSION_REQUEST_CODE = 2000


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