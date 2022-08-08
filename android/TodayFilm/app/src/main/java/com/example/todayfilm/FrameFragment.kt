package com.example.todayfilm

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings.System.DATE_FORMAT
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.example.todayfilm.data.Imgvid
import com.example.todayfilm.databinding.FragmentFrameBinding
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.NonDisposableHandle.parent
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FrameFragment : Fragment(), View.OnClickListener {
    lateinit var binding: FragmentFrameBinding
    var parent: String? = null
    var imgnumber = 0
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFrameBinding.inflate(inflater, container, false)
        parent = arguments?.getString("parent")

        sharedPreferences = requireActivity().getSharedPreferences(MyPreference.sp_name, Context.MODE_PRIVATE)

        // 부모가 home이나 complete라면 내부 저장소의 데이터 가져와서 보여주기
        if (parent == "home" || parent == "complete") {
            // 내부 저장소에 저장된 사진 정보 확인
            val prev = MyPreference.read(requireActivity(), "imgvids") // 내부 저장소에 저장된 string(json)
            val gson = GsonBuilder().create()
            val groupListType: Type = object: TypeToken<ArrayList<Imgvid?>?>() {}.type // json을 객체로 바꿀 때 타입 추론을 위함
            val tempArray = ArrayList<Imgvid>()

            // 데이터가 비어있지 않다면 객체 수 확인해서 imgnumber값 지정하고 tempArray에 기존 데이터 추가
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

            // 설정에서 반복하기로 한 경우
            val isRepeat = PreferenceManager.getDefaultSharedPreferences(requireActivity()).getBoolean("repeat", true)
            if (parent == "complete" && isRepeat && imgnumber < 4) {
                when (imgnumber) {
                    1 -> {
                        binding.imgvid1 = tempArray[0]
                        binding.imgvid2 = tempArray[0]
                        binding.imgvid3 = tempArray[0]
                        binding.imgvid4 = tempArray[0]
                    }

                    2 -> {
                        binding.imgvid1 = tempArray[0]
                        binding.imgvid2 = tempArray[1]
                        binding.imgvid3 = tempArray[0]
                        binding.imgvid4 = tempArray[1]
                    }

                     3 -> {
                         binding.imgvid1 = tempArray[0]
                         binding.imgvid2 = tempArray[1]
                         binding.imgvid3 = tempArray[2]
                         binding.imgvid4 = tempArray[0]
                     }
                }
            }

            binding.date = MyPreference.read(requireActivity(), "date")

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
        if (imgnumber > 0) {
            binding.image1Section.setOnClickListener(this)
        }
        if (imgnumber > 1) {
            binding.image2Section.setOnClickListener(this)
        }
        if (imgnumber > 2) {
            binding.image3Section.setOnClickListener(this)
        }
        if (imgnumber > 3) {
            binding.image4Section.setOnClickListener(this)
        }
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

    // 설정 변경 이벤트 처리
    val prefListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences: SharedPreferences?, key: String? ->
        when (key) {
            "date" -> {
                binding.imgvid1 = null
                binding.imgvid2 = null
                binding.imgvid3 = null
                binding.imgvid4 = null
                binding.date = sharedPreferences!!.getString("date", "")
            }
        }
    }

    // 리스너 등록
    override fun onResume() {
        super.onResume()
        if (parent == "home" || parent == "complete") {
            sharedPreferences.registerOnSharedPreferenceChangeListener(prefListener)
        }
    }

    // 리스너 해제
    override fun onPause() {
        super.onPause()
        if (parent == "home" || parent == "complete") {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(prefListener)
        }
    }
}