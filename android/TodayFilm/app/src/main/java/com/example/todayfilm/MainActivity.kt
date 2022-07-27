package com.example.todayfilm


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.todayfilm.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_films.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragmentManager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        val fragment = HomeFragment()
        transaction.add(R.id.fragment_content_main, fragment)
        transaction.commit()
        // 얘로 불러옴

        nav_search.setOnClickListener{

            supportFragmentManager.beginTransaction().remove(fragment).commit()
            val fragment = SearchFragment()
            supportFragmentManager.beginTransaction().replace(R.id.fragment_content_main,fragment).commit()


        }

        nav_album.setOnClickListener{
            supportFragmentManager.beginTransaction().remove(fragment).commit()
            val fragment = FilmsFragment()
            supportFragmentManager.beginTransaction().replace(R.id.fragment_content_main,fragment).commit()

        }

        nav_home.setOnClickListener{
            supportFragmentManager.beginTransaction().remove(fragment).commit()
            val fragment = HomeFragment()
            supportFragmentManager.beginTransaction().replace(R.id.fragment_content_main,fragment).commit()

        }















    }
}