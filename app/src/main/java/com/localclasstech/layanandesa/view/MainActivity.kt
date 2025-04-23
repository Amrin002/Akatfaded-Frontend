package com.localclasstech.layanandesa.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.auth.view.LoginActivity
import com.localclasstech.layanandesa.databinding.ActivityMainBinding
import com.localclasstech.layanandesa.feature.beranda.view.BerandaFragment
import com.localclasstech.layanandesa.feature.berita.view.BeritaFragment
import com.localclasstech.layanandesa.feature.layanan.view.LayananFragment
import com.localclasstech.layanandesa.feature.pengaturan.view.PengaturanFragment
import com.localclasstech.layanandesa.view.getstarted.GetstartedActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val loginMode = sharedPreferences.getString("login_mode", null)
        if (loginMode == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }


        val bottomNavigation = binding.bottomNavigation
        val activeIndicator = binding.activeIndicator

        // Gunakan ViewTreeObserver untuk memastikan ukuran sudah final
        bottomNavigation.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                bottomNavigation.viewTreeObserver.removeOnGlobalLayoutListener(this)
                animateIndicator(activeIndicator, 0) // Atur indikator di posisi tab pertama (index 0)
            }
        })

        loadFragment(BerandaFragment())

        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            val selectedIndex = when (item.itemId) {
                R.id.beranda -> {
                    loadFragment(BerandaFragment())
                    0
                }
                R.id.layanan -> {
                    loadFragment(LayananFragment())
                    1
                }
                R.id.berita -> {
                    loadFragment(BeritaFragment())
                    2
                }
                R.id.pengaturan -> {
                    loadFragment(PengaturanFragment())
                    3
                }
                else -> {
                    loadFragment(BerandaFragment())
                    0
                }
            }
            animateIndicator(activeIndicator, selectedIndex)
            true
        }
    }
    fun animateIndicator(indicator: View, index: Int) {
        val bottomNavigation =binding.bottomNavigation
        val menuView = bottomNavigation.getChildAt(0) as ViewGroup
        val itemWidth = menuView.getChildAt(index).width
        val translationX = (menuView.getChildAt(index).left + itemWidth / 1.20) - (indicator.width / 1.20)

        indicator.visibility = View.VISIBLE
        indicator.animate().translationX(translationX.toFloat()).setDuration(200).start()
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun loadFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentView, fragment)
            .commit()
    }

}
