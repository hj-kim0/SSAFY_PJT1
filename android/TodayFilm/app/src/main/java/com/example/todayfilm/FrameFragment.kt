package com.example.todayfilm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.todayfilm.databinding.FragmentFrameBinding

class FrameFragment : Fragment() {
    lateinit var binding: FragmentFrameBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFrameBinding.inflate(inflater, container, false)

        return binding.root
    }
}