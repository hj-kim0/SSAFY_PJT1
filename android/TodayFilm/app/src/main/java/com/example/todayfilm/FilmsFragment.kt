package com.example.todayfilm

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.example.todayfilm.databinding.FragmentFilmsBinding
import kotlinx.android.synthetic.main.fragment_films.*


class FilmsFragment : Fragment() {
    lateinit var binding: FragmentFilmsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?



    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFilmsBinding.inflate(inflater,container,false)
        return inflater.inflate(R.layout.fragment_films, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        go_settings.setOnClickListener{
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }

    }


}

