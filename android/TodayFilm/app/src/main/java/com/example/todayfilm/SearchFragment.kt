package com.example.todayfilm

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.example.todayfilm.data.*
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

        val search = SearchRequest()
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
                Toast.makeText(requireActivity(), "사용자 검색에 실패했습니다.", Toast.LENGTH_SHORT).show()
                Log.d("사용자 검색 실패", t.message.toString())
            }
        })

        // 게시글 검색
        val callArticle = NetWorkClient.GetNetwork.searcharticle(search)
        callArticle.enqueue(object : Callback<List<ArticleResponse>>{
            override fun onResponse(
                call: Call<List<ArticleResponse>>,
                response: Response<List<ArticleResponse>>
            ) {
                val result = response.body()
                val datas = arrayListOf<ArticleResponse>()

                if (result != null) {
                    for (r in result) {
                        datas.add(r)
                    }
                }

                initArticleRecycler(datas)
            }

            override fun onFailure(call: Call<List<ArticleResponse>>, t: Throwable) {
                Toast.makeText(requireActivity(), "게시글 검색에 실패했습니다.", Toast.LENGTH_SHORT).show()
                Log.d("게시글 검색 실패", t.message.toString())
            }
        })
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

    private fun initArticleRecycler(articledatas: ArrayList<ArticleResponse>) {
        val articleAdapter = ArticleAdapter(requireActivity())
        binding.searchResultArticle.adapter = articleAdapter

        articleAdapter.setItemClickListener(object: ArticleAdapter.ItemClickListener {
            override fun onClick(view: View, articleidx: String, articlecreatedate: String, article_userpid: String, likey: String, hashstring: String) {
                (activity as MainActivity).changeFragment(3, articleidx, articlecreatedate, article_userpid, likey, hashstring)
            }
        })

        articleAdapter.datas = articledatas
    }
}


