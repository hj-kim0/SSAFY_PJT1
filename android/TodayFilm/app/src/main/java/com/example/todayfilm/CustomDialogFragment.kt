package com.example.todayfilm

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.todayfilm.data.ChangeArticleShareRequest
import com.example.todayfilm.data.ChangeUserDetailResponse
import com.example.todayfilm.data.LoginData
import com.example.todayfilm.databinding.FragmentCustomDialogBinding
import com.example.todayfilm.retrofit.NetWorkClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomDialogFragment: DialogFragment() {
    private var _binding: FragmentCustomDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var articleidx: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCustomDialogBinding.inflate(inflater, container, false)
        val view = binding.root
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        val sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedViewModel.getArticleIdx().observe(
            requireActivity(),
        ) {
            articleidx = it
        }

        initDialog()
        return view
    }

    fun initDialog() {
        val duration = Toast.LENGTH_SHORT
        var share: String

        binding.completeDialogShowAll.setOnClickListener {
            // 서버 요청
            share = "0"
            val changeArticleShareRequest = ChangeArticleShareRequest()
            changeArticleShareRequest.articleidx = articleidx
            changeArticleShareRequest.sharenum = share

            val call = NetWorkClient.GetNetwork.changearticleshare(changeArticleShareRequest)
            call.enqueue(object : Callback<ChangeUserDetailResponse> {
                override fun onResponse(call: Call<ChangeUserDetailResponse>, response: Response<ChangeUserDetailResponse>) {
                    Toast.makeText(context, "전체 공개로 변경되었습니다.", duration).show()
                    dismiss()
                }

                override fun onFailure(call: Call<ChangeUserDetailResponse>, t: Throwable) {
                    Toast.makeText(context, "공개 여부 변경이 실패했습니다.", duration).show()
                    dismiss()
                }
            })
        }

        binding.completeDialogShowFollowers.setOnClickListener {
            // 서버 요청
            share = "1"

            val changeArticleShareRequest = ChangeArticleShareRequest()
            changeArticleShareRequest.articleidx = articleidx
            changeArticleShareRequest.sharenum = share

            val call = NetWorkClient.GetNetwork.changearticleshare(changeArticleShareRequest)
            call.enqueue(object : Callback<ChangeUserDetailResponse> {
                override fun onResponse(call: Call<ChangeUserDetailResponse>, response: Response<ChangeUserDetailResponse>) {
                    Toast.makeText(context, "팔로워에게만 공개로 변경되었습니다.", duration).show()
                    dismiss()
                }

                override fun onFailure(call: Call<ChangeUserDetailResponse>, t: Throwable) {
                    Toast.makeText(context, "공개 여부 변경이 실패했습니다.", duration).show()
                    dismiss()
                }
            })
        }

        binding.completeDialogShowNobody.setOnClickListener {
            // 서버 요청
            share = "2"

            val changeArticleShareRequest = ChangeArticleShareRequest()
            changeArticleShareRequest.articleidx = articleidx
            changeArticleShareRequest.sharenum = share

            val call = NetWorkClient.GetNetwork.changearticleshare(changeArticleShareRequest)
            call.enqueue(object : Callback<ChangeUserDetailResponse> {
                override fun onResponse(call: Call<ChangeUserDetailResponse>, response: Response<ChangeUserDetailResponse>) {
                    Toast.makeText(context, "비공개로 변경되었습니다.", duration).show()
                    dismiss()
                }

                override fun onFailure(call: Call<ChangeUserDetailResponse>, t: Throwable) {
                    Toast.makeText(context, "공개 여부 변경이 실패했습니다.", duration).show()
                    dismiss()
                }
            })
        }
    }
}
