package com.example.todayfilm

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.todayfilm.data.Imgvid
import com.example.todayfilm.databinding.FragmentFrameBinding
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FrameFragment : Fragment(), View.OnClickListener {
    lateinit var binding: FragmentFrameBinding
    var parent: String? = null
    private val DATE_FORMAT = "yyyy/MM/dd (E)"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFrameBinding.inflate(inflater, container, false)
        parent = arguments?.getString("parent")

        // 부모가 home이나 complete라면 내부 저장소의 데이터 가져와서 보여주기
        if (parent == "home" || parent == "complete") {
            // 내부 저장소에 저장된 사진 정보 확인
            val prev = MyPreference.read(requireActivity(), "imgvids") // 내부 저장소에 저장된 string(json)
            val gson = GsonBuilder().create()
            val groupListType: Type = object: TypeToken<ArrayList<Imgvid?>?>() {}.type // json을 객체로 바꿀 때 타입 추론을 위함
            val tempArray = ArrayList<Imgvid>()

            // 데이터가 비어있지 않다면 객체 수 확인해서 imgnumber값 지정하고 tempArray에 기존 데이터 추가
            var imgnumber = 0
            if (prev != "none" && prev != "" && prev != "[]") {
                tempArray.addAll(gson.fromJson(prev, groupListType))
                imgnumber += tempArray.size
            }

            // 데이터 바인딩
            if (imgnumber > 0) {
                binding.imgvid1 = tempArray[0]
            }
            if (imgnumber > 1) {
                binding.imgvid2 = tempArray[1]
            }
            if (imgnumber > 2) {
                binding.imgvid3 = tempArray[2]
            }
            if (imgnumber > 3) {
                binding.imgvid4 = tempArray[3]
            }

            val date = SimpleDateFormat(DATE_FORMAT, Locale.KOREA)
                .format(System.currentTimeMillis())
            binding.date = date

        } else {
            // 부모가 film이라면 넘겨받은 정보로 서버 데이터 보여주기
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 부모가 complete면 대표 이미지 지정할 수 있게 함
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