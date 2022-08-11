package com.example.todayfilm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.todayfilm.data.*
import com.example.todayfilm.databinding.FragmentProfileBinding
import com.example.todayfilm.retrofit.NetWorkClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment(), View.OnClickListener {
    lateinit var binding: FragmentProfileBinding
    var userid = ""
    var isMyProfile = false
    var isFollow = false
    var search_userpid = ""
    var userpid = ""


        

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater,container,false)

        userpid = MyPreference.read(requireActivity(), "userpid")

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        search_userpid = arguments?.getString("search_userpid").toString()


        if (userpid == search_userpid) {
            isMyProfile = true
        }

        if (isMyProfile) {
            // 본인 프로필
            binding.profileBtn.text = "회원정보 수정"
            binding.profileToSettings.visibility = View.VISIBLE
        } else {
            // 다른 사용자 프로필
            binding.profileToSettings.visibility = View.INVISIBLE
            searchProfile(true)

        }
        // 사용자 정보 조회
        getProfile()
        // 사용자 게시글 조회
        getArticle()

        setOnClickListener()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.profileToSettings.setOnClickListener(this)
        binding.profileBtn.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.profile_to_settings -> {
                val intent = Intent(activity, SettingsActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }

            R.id.profile_btn -> {
                // 본인 프로필인 경우 change profile 액티비티로 이동
                if (isMyProfile) {
                    val intent = Intent(activity, ChangeProfileActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)


                } else {
                    searchProfile(false)
                    // 팔로우 중인지 확인 후 서버로 팔로우 / 언팔로우 요청 보내기

                }
            }
        }
    }

    private fun initArticleRecycler(articledatas: ArrayList<ArticleResponse>) {
        val articleAdapter = ProfileArticleAdapter(requireActivity())
        binding.profileArticle.adapter = articleAdapter

        articleAdapter.setItemClickListener(object: ProfileArticleAdapter.ItemClickListener {
            override fun onClick(view: View, articleidx: String, articlecreatedate: String, article_userpid: String, likey: String, hashstring: String) {
                (activity as MainActivity).changeFragment(3, articleidx, articlecreatedate, article_userpid, likey, hashstring)
            }
        })

        articleAdapter.datas = articledatas
    }


    private fun unfollow(){
        val deleteFollowR = FollowRequest()
        deleteFollowR.subfrom = search_userpid
        deleteFollowR.subto = userpid

        val calldeletefollow = NetWorkClient.GetNetwork.deletefollow(deleteFollowR)
        calldeletefollow.enqueue( object : Callback<noRseponse> {
            override fun onResponse(
                call: Call<noRseponse>,
                response: Response<noRseponse>
            ) {
                val result: noRseponse? = response.body()
                Log.d("체크석세스",result?.success.toString())
                if (result?.success!!) {
                    binding.profileBtn.text = "팔로우"
                    Toast.makeText(requireActivity(), "언팔로우에 성공했습니다", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(requireActivity(), "언팔로우에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<noRseponse>, t: Throwable) {
            }
        })
    }

    private fun follow(){
        val addFollowR = FollowRequest()
        addFollowR.subfrom = search_userpid
        addFollowR.subto = userpid

        val calldeletefollow = NetWorkClient.GetNetwork.createfollow(addFollowR)
        calldeletefollow.enqueue( object : Callback<noRseponse> {
            override fun onResponse(
                call: Call<noRseponse>,
                response: Response<noRseponse>
            ) {

                val result: noRseponse? = response.body()
                if (result?.success!!) {
                    binding.profileBtn.text = "언팔로우"
                    Toast.makeText(requireActivity(), "팔로우에 성공했습니다", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(requireActivity(), "팔로우에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }

            }



            override fun onFailure(call: Call<noRseponse>, t: Throwable) {
            }
        })
    }

    private fun getArticle(){
        val article = GetArticle()
        article.userpid = userpid
        article.search_userpid = search_userpid

        val callArticle = NetWorkClient.GetNetwork.showarticle(article)
        callArticle.enqueue( object : Callback<List<ArticleResponse>>{
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
                Log.d("사용자 게시글 조회 실패", t.message.toString())
            }
        })
    }
    private fun getProfile(){
        val profile = GetProfile()
        profile.userpid = search_userpid

        val callUser = NetWorkClient.GetNetwork.getprofile(profile)
        callUser.enqueue(object : Callback<CompleteProfile>{
            override fun onResponse(
                call: Call<CompleteProfile>,
                response: Response<CompleteProfile>
            ) {
                val result = response.body()
                val imgview = binding.profileImage
                Glide.with(requireActivity()).load("http://i7c207.p.ssafy.io:8080/harufilm/upload/profile/" + result?.userimg).into(imgview)
                binding.profileId.text = result?.userid
                binding.profileUsername.text = result?.username
                binding.profileDescription.text = result?.userdesc
            }

            override fun onFailure(call: Call<CompleteProfile>, t: Throwable) {
                Log.d("사용자 정보 조회 실패", t.message.toString())
            }
        })
    }

    private fun searchProfile(switch:Boolean){
        val searchFollowr = FollowRequest()
        searchFollowr.subfrom = search_userpid
        searchFollowr.subto = userpid

        val searchFollow = NetWorkClient.GetNetwork.followsearch(searchFollowr)
        searchFollow.enqueue(object : Callback<FollowBoolean>{
            override fun onResponse(call: Call<FollowBoolean>, response: Response<FollowBoolean>) {
                var isFollow = response.body()?.followBoolean

                if (isFollow!!) {
                    if (switch){
                        binding.profileBtn.text = "언팔로우"
                    }else{
                        unfollow()
                    }

                } else {
                    if (switch){
                        binding.profileBtn.text = "팔로우"
                    }else{
                        follow()
                    }
                }
            }

            override fun onFailure(call: Call<FollowBoolean>, t: Throwable) {

            }
        })

    }
}