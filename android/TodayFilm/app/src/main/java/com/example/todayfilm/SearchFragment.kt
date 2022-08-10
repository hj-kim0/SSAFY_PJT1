package com.example.todayfilm

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.example.todayfilm.data.SearchUserRequest
import com.example.todayfilm.data.SearchUserResponse
import com.example.todayfilm.data.SearchUser
import com.example.todayfilm.databinding.FragmentSearchBinding
import com.example.todayfilm.retrofit.NetWorkClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment(),View.OnClickListener {
    lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnclickListener()
    }

    private fun setOnclickListener(){
        binding.goProfileList.setOnClickListener(this)
        binding.searchKeyword.setOnEditorActionListener { view, i, event ->
            var handled = false
            if (i == EditorInfo.IME_ACTION_DONE) {
                // 검색 수행
                requestSearch()
                handled = true
            }
            handled
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id){
            R.id.go_profile_list -> {
                (activity as MainActivity).changeFragment(2)
            }
        }
    }

    private fun requestSearch() {
        val keyword = binding.searchKeyword.text.toString()

        val search = SearchUserRequest()
        search.keyword = keyword

        // 사용자 검색
        val call = NetWorkClient.GetNetwork.searchuser(search)
        call.enqueue(object : Callback<SearchUserResponse> {
            override fun onResponse(call: Call<SearchUserResponse>, response: Response<SearchUserResponse>) {
                val result: SearchUserResponse? = response.body()

                // 검색 결과로 받는 사용자 리스트를 userdatas 변수에 넣고 보여주기
                val userdatas = result?.userlist
                initUserRecycler(userdatas as ArrayList<SearchUser>)
            }

            override fun onFailure(call: Call<SearchUserResponse>, t: Throwable) {
                Log.d("", "실패"+t.message.toString())
            }
        })

        // 게시글 검색
    }

    private fun initUserRecycler(userdatas: ArrayList<SearchUser>) {
        val userAdapter = SearchUserAdapter(requireActivity())
        binding.searchResultUser.adapter = userAdapter

        userAdapter.setItemClickListener(object: SearchUserAdapter.ItemClickListener {
            override fun onClick(view: View, userpid: String) {
                (activity as MainActivity).changeFragment(4, userpid)
            }
        })

        userAdapter.datas = userdatas
    }
}


