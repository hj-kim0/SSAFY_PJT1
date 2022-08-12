package com.example.todayfilm

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.todayfilm.data.*
import com.example.todayfilm.databinding.FragmentFeedBinding
import com.example.todayfilm.retrofit.NetWorkClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedFragment : Fragment(),View.OnClickListener {
    lateinit var binding: FragmentFeedBinding
    var userpid = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedBinding.inflate(inflater,container,false)

        userpid = MyPreference.read(requireActivity(), "userpid")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
    }

    override fun onResume() {
        super.onResume()
        // 피드 조회
        val profile = GetProfile()
        profile.userpid = userpid

        val callArticle = NetWorkClient.GetNetwork.showsubarticle(profile)
        callArticle.enqueue(object : Callback<List<ArticleResponse>> {
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

                initFeedRecycler(datas)
            }

            override fun onFailure(call: Call<List<ArticleResponse>>, t: Throwable) {
                Toast.makeText(requireActivity(), "피드 조회에 실패했습니다.", Toast.LENGTH_SHORT).show()
                Log.d("피드 조회 실패", t.message.toString())
            }
        })
    }

    private fun setOnClickListener() {
        binding.goSearch.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id){
            R.id.go_search -> {
                (activity as MainActivity).changeFragment(1)
            }
        }
    }

    private fun initFeedRecycler(articledatas: ArrayList<ArticleResponse>) {
        val articleAdapter = ArticleAdapter(requireActivity())
        binding.feedArticle.adapter = articleAdapter

        articleAdapter.setItemClickListener(object: ArticleAdapter.ItemClickListener {
            override fun onClick(view: View, articleidx: String, articlecreatedate: String, article_userpid: String, likey: String, hashstring: String) {
                (activity as MainActivity).changeFragment(3, articleidx, articlecreatedate, article_userpid, likey, hashstring)
            }

            override fun onClick(view: View, userpid: String) {
                (activity as MainActivity).changeFragment(4, userpid)
            }
        })

        articleAdapter.datas = articledatas
    }
}