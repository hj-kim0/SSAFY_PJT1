package com.example.todayfilm

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.example.todayfilm.data.*
import com.example.todayfilm.databinding.FragmentProfileBinding
import com.example.todayfilm.retrofit.NetWorkClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

class ProfileFragment : Fragment(), View.OnClickListener, SensorEventListener {
    lateinit var binding: FragmentProfileBinding
    var userid = ""
    private var isMyProfile = false
    private var search_userpid = ""
    var parent = ""
    var userpid = ""
    var followedNumber = 0
    var followNumber = 0
    private var isShake = true
    val datas = arrayListOf<ArticleResponse>()

    private var accel: Float = 0.0f //초기
    private var accelCurrent: Float = 0.0f //이동하는 치수
    private var accelLast: Float = 0.0f
    private lateinit var sensorManager: SensorManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater,container,false)

        userpid = MyPreference.read(requireActivity(), "userpid")
        search_userpid = arguments?.getString("search_userpid").toString()
        parent = arguments?.getString("parent").toString()

        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accel = 10f
        accelCurrent = SensorManager.GRAVITY_EARTH //지구 중력값 주기
        accelLast = SensorManager.GRAVITY_EARTH

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
    }

    override fun onResume() {
        super.onResume()
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

        getProfile()
        getArticle()
        getFollowNumber()
        getFollowingNumber()
        setOnClickListener()

        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun setOnClickListener() {
        binding.profileToSettings.setOnClickListener(this)
        binding.profileBtn.setOnClickListener(this)
        binding.profileFollowing.setOnClickListener(this)
        binding.profileFollower.setOnClickListener(this)
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
                    getFollowNumber()
                    getFollowingNumber()
                }
            }

            R.id.profile_following -> {
                (activity as MainActivity).changeFragment(2, "following", search_userpid)
            }

            R.id.profile_follower -> {
                (activity as MainActivity).changeFragment(2, "follower", search_userpid)
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
        deleteFollowR.subfrom = userpid
        deleteFollowR.subto = search_userpid

        val calldeletefollow = NetWorkClient.GetNetwork.deletefollow(deleteFollowR)
        calldeletefollow.enqueue( object : Callback<noRseponse> {
            override fun onResponse(
                call: Call<noRseponse>,
                response: Response<noRseponse>
            ) {
                val result: noRseponse? = response.body()
                if (result?.success!!) {
                    binding.profileBtn.text = "팔로우"

                    getFollowNumber()
                    getFollowingNumber()
                } else {
                    Toast.makeText(requireActivity(), "언팔로우 요청이 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<noRseponse>, t: Throwable) {
                Toast.makeText(requireActivity(), "언팔로우 요청이 실패했습니다.", Toast.LENGTH_SHORT).show()
                Log.e("팔로우 요청 실패", t.message.toString())
            }
        })
    }

    private fun follow(){
        val addFollowR = FollowRequest()
        addFollowR.subfrom = userpid
        addFollowR.subto = search_userpid

        val callcreatefollow = NetWorkClient.GetNetwork.createfollow(addFollowR)
        callcreatefollow.enqueue( object : Callback<noRseponse> {
            override fun onResponse(
                call: Call<noRseponse>,
                response: Response<noRseponse>
            ) {
                val result: noRseponse? = response.body()
                if (result?.success!!) {
                    binding.profileBtn.text = "언팔로우"

                    getFollowNumber()
                    getFollowingNumber()
                } else {
                    Toast.makeText(requireActivity(), "팔로우 요청이 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<noRseponse>, t: Throwable) {
                Toast.makeText(requireActivity(), "팔로우 요청이 실패했습니다.", Toast.LENGTH_SHORT).show()
                Log.e("팔로우 요청 실패", t.message.toString())
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

                if (result != null) {
                    for (r in result) {
                        datas.add(r)
                    }
                }

                datas.reverse()

                if (parent == "complete") {
                    val todayarticle = datas[0]
                    var hashstring = ""

                    for (hashtag in todayarticle.hash) {
                        hashstring += "#$hashtag "
                    }

                    MyPreference.write(requireActivity(), "todayarticleidx", todayarticle.article.articleidx)
                    (activity as MainActivity).changeFragment(3, todayarticle.article.articleidx, todayarticle.article.articlecreatedate, todayarticle.article.userpid, todayarticle.likey, hashstring)
                }

                initArticleRecycler(datas)
            }

            override fun onFailure(call: Call<List<ArticleResponse>>, t: Throwable) {
                Toast.makeText(requireActivity(), "사용자 게시글 조회에 실패했습니다.", Toast.LENGTH_SHORT).show()
                Log.e("사용자 게시글 조회 실패", t.message.toString())
            }
        })
    }

    private fun getProfile(){
        val profile = GetProfile()
        profile.userpid = search_userpid

        val callUser = NetWorkClient.GetNetwork.getprofile(profile)
        callUser.enqueue(object : Callback<CompleteProfile> {
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
                Toast.makeText(requireActivity(), "사용자 정보 조회에 실패했습니다.", Toast.LENGTH_SHORT).show()
                Log.e("사용자 정보 조회 실패", t.message.toString())
            }
        })
    }

    private fun searchProfile(switch:Boolean){
        val searchFollowr = FollowRequest()
        searchFollowr.subfrom = userpid
        searchFollowr.subto = search_userpid

        val searchFollow = NetWorkClient.GetNetwork.followsearch(searchFollowr)
        searchFollow.enqueue(object : Callback<FollowBoolean>{
            override fun onResponse(call: Call<FollowBoolean>, response: Response<FollowBoolean>) {
                val isFollow = response.body()?.followBoolean

                if (isFollow!!) {
                    if (switch) {
                        binding.profileBtn.text = "언팔로우"
                    } else {
                        unfollow()
                    }

                } else {
                    if (switch) {
                        binding.profileBtn.text = "팔로우"
                    } else {
                        follow()
                    }
                }
            }

            override fun onFailure(call: Call<FollowBoolean>, t: Throwable) {
                Toast.makeText(requireActivity(), "팔로우에 실패했습니다.", Toast.LENGTH_SHORT).show()
                Log.e("팔로우 실패", t.message.toString())
            }
        })
    }

    private fun getFollowNumber() {
        val getFollow = GetProfile()
        getFollow.userpid = search_userpid
        val callFollowUser = NetWorkClient.GetNetwork.followed(getFollow)
        callFollowUser.enqueue(object : Callback<FollowList>{
            override fun onResponse(call: Call<FollowList>, response: Response<FollowList>) {
                followNumber = response.body()?.list!!.size
                binding.profileFollowerCnt.text = followNumber.toString()
            }

            override fun onFailure(call: Call<FollowList>, t: Throwable) {
                Toast.makeText(requireActivity(), "팔로우 정보 조회에 실패했습니다.", Toast.LENGTH_SHORT).show()
                Log.e("팔로우 정보 조회 실패", t.message.toString())
            }
        })
    }

    private fun getFollowingNumber(){
        val getFollow = GetProfile()
        getFollow.userpid = search_userpid
        val callFollowingUser = NetWorkClient.GetNetwork.following(getFollow)
        callFollowingUser.enqueue(object : Callback<FollowList>{
            override fun onResponse(call: Call<FollowList>, response: Response<FollowList>) {
                followedNumber = response.body()?.list!!.size
                binding.profileFollowingCnt.text = followedNumber.toString()
            }

            override fun onFailure(call: Call<FollowList>, t: Throwable) {
                Toast.makeText(requireActivity(), "팔로잉 정보 조회에 실패했습니다.", Toast.LENGTH_SHORT).show()
                Log.e("팔로잉 정보 조회 실패", t.message.toString())
            }
        })
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        val x:Float = p0?.values?.get(0) as Float
        val y:Float = p0.values?.get(1) as Float
        val z:Float = p0.values?.get(2) as Float

        accelLast = accelCurrent
        accelCurrent = kotlin.math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()

        val delta: Float = accelCurrent - accelLast

        accel = accel * 0.9f + delta

        isShake = PreferenceManager.getDefaultSharedPreferences(requireActivity()).getBoolean("shake", true)
        // 액셀 치수가 30보다 크고, 흔들기 설정이 켜져있고, 본인 프로필이고, 필름이 있다면 랜덤 필름 감상
        if (accel > 30 && isShake && userpid == search_userpid && datas.isNotEmpty()) {
            val random = Random.nextInt(datas.size)
            val randomdata = datas[random]
            var hashstring = ""

            for (hashtag in randomdata.hash) {
                hashstring += "#$hashtag "
            }

            (activity as MainActivity).changeFragment(3, randomdata.article.articleidx, randomdata.article.articlecreatedate, randomdata.article.userpid, randomdata.likey, hashstring)
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        Log.d("확인", "정확도 변경")
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        datas.clear()
    }
}