package com.example.todayfilm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.todayfilm.databinding.FragmentFrameBinding

class FrameFragment : Fragment(), View.OnClickListener {
    lateinit var binding: FragmentFrameBinding
    var parent: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFrameBinding.inflate(inflater, container, false)
        parent = arguments?.getString("parent")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (parent == "complete") {
            setOnClickListener()
        }
    }

    private fun setOnClickListener() {
        binding.image1Section.setOnClickListener(this)
        binding.image2Section.setOnClickListener(this)
        binding.image3Section.setOnClickListener(this)
        binding.image4Section.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.image1_section -> {
                binding.image1Section.foreground = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_check_yellow)
                binding.image2Section.foreground = null
                binding.image3Section.foreground = null
                binding.image4Section.foreground = null
                MyPreference.writeInt(requireActivity(), "mainImage", 1)
            }
            R.id.image2_section -> {
                binding.image1Section.foreground = null
                binding.image2Section.foreground = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_check_yellow)
                binding.image3Section.foreground = null
                binding.image4Section.foreground = null
                MyPreference.writeInt(requireActivity(), "mainImage", 2)
            }
            R.id.image3_section -> {
                binding.image1Section.foreground = null
                binding.image2Section.foreground = null
                binding.image3Section.foreground = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_check_yellow)
                binding.image4Section.foreground = null
                MyPreference.writeInt(requireActivity(), "mainImage", 3)
            }
            R.id.image4_section -> {
                binding.image1Section.foreground = null
                binding.image2Section.foreground = null
                binding.image3Section.foreground = null
                binding.image4Section.foreground = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_check_yellow)
                MyPreference.writeInt(requireActivity(), "mainImage", 4)
            }
        }
    }
}