package com.example.todayfilm

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil.setContentView
import com.example.todayfilm.data.*
import com.example.todayfilm.databinding.FragmentFeedBinding
import com.example.todayfilm.retrofit.NetWorkClient
import kotlinx.android.synthetic.main.fragment_feed.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedFragment : Fragment(),View.OnClickListener {
    lateinit var binding: FragmentFeedBinding
    var userpid = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedBinding.inflate(inflater,container,false)

        userpid = MyPreference.read(requireActivity(), "userpid")

        binding.noFriend.isSelected = true

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
                super.onCreate(savedInstanceState);


        val anim =  AlphaAnimation(0.0f,1.0f);
        anim.setDuration(100);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        binding.noFriend.startAnimation(anim)





    }

    override fun onResume() {
        super.onResume()
        // 피드 조회
        val profile = GetProfile()
        profile.userpid = userpid

        val callArticle = NetWorkClient.GetNetwork.showsubarticle(profile)
        callArticle.enqueue(object : Callback<ArticleResponseLike>{
            override fun onResponse(
                call: Call<ArticleResponseLike>,
                response: Response<ArticleResponseLike>
            ) {

                val result = response.body()
                val datas = arrayListOf<ArticleResponse>()
                Log.d("체크",datas.size.toString())
                if(response.body()!!.check){
                    binding.noFriend.visibility = View.VISIBLE
                }
                else{
                    binding.noFriend.visibility = View.GONE
                }


                for (r in response.body()!!.list) {
                    datas.add(r)
                }


                datas.reverse()

                initFeedRecycler(datas)

            }

            override fun onFailure(call: Call<ArticleResponseLike>, t: Throwable) {
                Toast.makeText(requireActivity(), "피드 조회에 실패했습니다.", Toast.LENGTH_SHORT).show()
                Log.e("피드 조회 실패", t.message.toString())
            }
        })

//        callArticle.enqueue(object : Callback<ArticleResponseLike> {
//            override fun onResponse(
//                call: Call<List<ArticleResponse>>,
//                response: Response<ArticleResponseLike>
//            ) {
//                val result = response.body()
//                val datas = arrayListOf<ArticleResponse>()
//                Log.d("체크",datas.size.toString())
//                if(datas.size == 0){
//                    binding.noFriend.visibility = View.VISIBLE
//                }
//
//
//                if (result != null) {
//                    for (r in result) {
//                        datas.add(r)
//                    }
//                }
//
//                datas.reverse()
//
//                initFeedRecycler(datas)
//            }
//
//            override fun onFailure(call: Call<ArticleResponseLike> , t: Throwable) {
//                Toast.makeText(requireActivity(), "피드 조회에 실패했습니다.", Toast.LENGTH_SHORT).show()
//                Log.e("피드 조회 실패", t.message.toString())
//            }
//        })
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
            override fun onClick(view: View, articleidx: String, articlecreatedate: String, article_userpid: String, likey: String, hash: ArrayList<String>) {
                (activity as MainActivity).changeFragment(3, articleidx, articlecreatedate, article_userpid, likey, hash)
            }

            override fun onClick(view: View, userpid: String) {
                (activity as MainActivity).changeFragment(4, userpid)
            }
        })

        articleAdapter.datas = articledatas
    }
}