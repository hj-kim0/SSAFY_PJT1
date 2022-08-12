package com.example.todayfilm

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.todayfilm.data.*
import com.example.todayfilm.databinding.FragmentProfileListBinding
import com.example.todayfilm.retrofit.NetWorkClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileListFragment : Fragment(),View.OnClickListener {
    lateinit var binding: FragmentProfileListBinding
    var title: String? = ""
    var search_userpid: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        title = arguments?.getString("title")
        search_userpid = arguments?.getString("search_userpid")

        val profile = GetProfile()
        profile.userpid = search_userpid.toString()

        var callUser = NetWorkClient.GetNetwork.getfollowing(profile)

        if (title == "following") {
            binding.profileListTitle.text = "팔로잉"
        } else if (title == "follower") {
            binding.profileListTitle.text = "팔로워"
            callUser = NetWorkClient.GetNetwork.getfollower(profile)
        }

        callUser.enqueue(object : Callback<ProfileListResponse> {
            override fun onResponse(
                call: Call<ProfileListResponse>,
                response: Response<ProfileListResponse>
            ) {
                val result = response.body()
                val datas = result?.list
                initProfileListRecycler(datas as ArrayList<SearchUser>)
            }

            override fun onFailure(call: Call<ProfileListResponse>, t: Throwable) {
                Toast.makeText(requireActivity(), "${title} 목록 조회에 실패했습니다.", Toast.LENGTH_SHORT).show()
                Log.d("사용자 정보 조회 실패", t.message.toString())
            }
        })

        setOnclickListener()
    }

    private fun setOnclickListener(){
        binding.profileListBack.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id){
            R.id.profileList_back -> {
                requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    private fun initProfileListRecycler(datas: ArrayList<SearchUser>) {
        val profileListAdapter = ProfileListAdapter(requireActivity())
        binding.profileListRecycler.adapter = profileListAdapter

        profileListAdapter.setItemClickListener(object: ProfileListAdapter.ItemClickListener {
            override fun onClick(view: View, userpid: String) {
                (activity as MainActivity).changeFragment(4, userpid)
            }
        })

        profileListAdapter.datas = datas
    }
}


