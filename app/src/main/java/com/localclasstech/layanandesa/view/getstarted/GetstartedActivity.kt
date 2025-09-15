package com.localclasstech.layanandesa.view.getstarted

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.auth.view.LoginActivity
import com.localclasstech.layanandesa.databinding.ActivityGetstartedBinding
import com.localclasstech.layanandesa.view.MainActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GetstartedActivity : AppCompatActivity() {
    private val viewModel: GetStartedViewModel by viewModels {
        GetStartedViewModelFactory(applicationContext)
    }
    private lateinit var binding: ActivityGetstartedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGetstartedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        MainScope().launch {
            delay(2000)
            viewModel.checkInternetConnection()
            setupInternetAndNavigate()
            finish()
        }

    }

    private fun setupInternetAndNavigate() {
        viewModel.isOnline.observe(this) { online ->
            if (online) {
                startActivity(Intent(this, LoginActivity::class.java))
//                Toast.makeText(this, "Online Mode", Toast.LENGTH_SHORT).show()
                finish()
            } else {
//                Toast.makeText(this, "Offline Mode", Toast.LENGTH_SHORT).show()
                viewModel.setupOfflineMode() // Setup SQLite atau logika offline lainnya
                startActivity(Intent(this, InternetlostActivity::class.java))
                finish()
            }
        }
    }
}