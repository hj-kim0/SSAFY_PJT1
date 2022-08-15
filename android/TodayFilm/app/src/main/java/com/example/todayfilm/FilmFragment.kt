package com.example.todayfilm

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.BounceInterpolator
import android.view.animation.ScaleAnimation
import android.widget.FrameLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.todayfilm.data.*
import com.example.todayfilm.databinding.FragmentFilmBinding
import com.example.todayfilm.retrofit.NetWorkClient
import kotlinx.android.synthetic.main.fragment_film.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class FilmFragment : Fragment(), View.OnClickListener, PopupMenu.OnMenuItemClickListener{
    lateinit var binding: FragmentFilmBinding
    private var article_userpid: String? = null
    private var articlecreatedate: String? = null
    private var articleidx: String? = null
    private var likey: String? = null
    private var hashstring: String? = null
    private var userpid: String? = null
    private lateinit var sharedViewModel: SharedViewModel
    private val duration = Toast.LENGTH_SHORT

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilmBinding.inflate(inflater, container, false)

        articleidx = arguments?.getString("articleidx")
        articlecreatedate = arguments?.getString("articlecreatedate")
        article_userpid = arguments?.getString("article_userpid")
        likey = arguments?.getString("likey")
        hashstring = arguments?.getString("hashstring")
        userpid = MyPreference.read(requireActivity(), "userpid")

        binding.filmLikey.text = likey

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 데이터 넣어서 frame 프래그먼트 호출
        val frameFragment = FrameFragment()
        val bundle = Bundle()
        bundle.putString("parent", "film")
        bundle.putString("articleidx", articleidx)
        bundle.putString("articlecreatedate", articlecreatedate)
        bundle.putString("article_userpid", article_userpid)
        frameFragment.arguments = bundle
        binding.filmHashtags.text = hashstring

        // 작성자 정보 조회
        val profile = GetProfile()
        profile.userpid = article_userpid.toString()

        val callUser = NetWorkClient.GetNetwork.getprofile(profile)
        callUser.enqueue(object : Callback<CompleteProfile> {
            override fun onResponse(
                call: Call<CompleteProfile>,
                response: Response<CompleteProfile>
            ) {
                val result = response.body()
                val imgview = binding.filmUserimg
                Glide.with(requireActivity()).load("http://i7c207.p.ssafy.io:8080/harufilm/upload/profile/" + result?.userimg).into(imgview)
                binding.filmUsername.text = result?.username
            }

            override fun onFailure(call: Call<CompleteProfile>, t: Throwable) {
                Toast.makeText(requireActivity(), "사용자 정보 조회에 실패했습니다.", Toast.LENGTH_SHORT).show()
                Log.e("사용자 정보 조회 실패", t.message.toString())
            }
        })

        // 본인이면 filmMenu 보여주기
        if (userpid == article_userpid) {
            binding.filmMenu.visibility = View.VISIBLE
        } else {
            binding.filmMenu.visibility = View.GONE
        }

        childFragmentManager.beginTransaction().add(R.id.fragment_content_film, frameFragment).commit()
        likecheckFun(true)

        setOnClickListener()
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        sharedViewModel.getIsPlay().observe(requireActivity()) {
            binding.filmPlayBtn.isClickable = !it
        }
    }

    private fun setOnClickListener() {
        binding.filmMenu.setOnClickListener(this)
        binding.filmPlayBtn.setOnClickListener(this)
        binding.filmLikeBtn.setOnClickListener(this)
        binding.filmUser.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.film_menu -> {
                showPopup(binding.filmMenu)
            }

            R.id.film_play_btn -> {
                sharedViewModel.setIsPlay(true)
            }

            R.id.film_like_btn ->{
                // 체크
                likecheckFun(false)
            }

            R.id.film_user -> {
                (activity as MainActivity).changeFragment(4, article_userpid)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private fun showPopup(v: View) {
        val popup = PopupMenu(context,v,  Gravity.TOP, 0, R.style.popup) // PopupMenu 객체 선언
        popup.menuInflater.inflate(R.menu.popup, popup.menu) // 메뉴 레이아웃 inflate
        popup.setOnMenuItemClickListener(this)
        popup.show() // 팝업 보여주기
    }

    override fun onMenuItemClick(p0: MenuItem?): Boolean {
        val dialog = CustomDialogFragment()
        val normaldialog = NormalDialogFragment()

        when (p0?.itemId) { // 메뉴 아이템에 따라 동작 다르게 하기
            R.id.delete -> {
                val today = SimpleDateFormat("yyyyMMdd", Locale.KOREA).format(System.currentTimeMillis())

                if (articlecreatedate == today) {
                    Toast.makeText(requireActivity(), "오늘의 필름은 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    val btn = arrayOf("네", "아니오")
                    normaldialog.arguments = bundleOf(
                        "bodyContext" to "삭제하면 복구되지 않습니다",
                        "bodyTitle" to "정말 삭제하시겠습니까?",
                        "btnData" to btn
                    )
                    normaldialog.show((activity as MainActivity).supportFragmentManager, "CustomDialog")

                    normaldialog.setButtonClickListener(object :
                        NormalDialogFragment.OnButtonClickListener {
                        override fun onButton1Clicked() {
                            //확인버튼을 눌렀을 때 처리할 곳
                            val articledelete = ArticleDeleteRequest()
                            articledelete.articleidx = articleidx!!.toInt()
                            val call = NetWorkClient.GetNetwork.articledelete(articledelete)
                            call.enqueue(object : Callback<ArticleDeleteResponse> {
                                override fun onResponse(
                                    call: Call<ArticleDeleteResponse>,
                                    response: Response<ArticleDeleteResponse>
                                ) {
                                    Toast.makeText(context, "게시글이 삭제되었습니다.", duration).show()
                                    (activity as MainActivity).myProfile()
                                }

                                override fun onFailure(call: Call<ArticleDeleteResponse>, t: Throwable) {
                                    Toast.makeText(context, "게시글 삭제에 실패했습니다.", duration).show()
                                    Log.e("게시글 삭제 실패", t.message.toString())
                                }
                            })
                        }

                        override fun onButton2Clicked() {}
                    })
                }
            }

            R.id.share -> {
                val sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
                sharedViewModel.setArticleIdx(articleidx ?: "")
                dialog.show((activity as MainActivity).supportFragmentManager, "CustomDialog")
            }

            R.id.shareFile -> {
                val bitmap = getBitmap(fragment_content_film)

                val path :Uri = getImageUri(bitmap)

                val shareIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, path)
                    type = "image/jpeg"
                }
                startActivity(Intent.createChooser(shareIntent, "esources.getText(R.string.send_to"))
            }

            R.id.save -> {
                val btn= arrayOf("네", "아니오")
                normaldialog.arguments = bundleOf(
                    "bodyTitle" to "프레임을 저장하시겠습니까?",
                    "bodyContext" to "갤러리에 저장됩니다.",
                    "btnData" to btn
                )

                normaldialog.setButtonClickListener(object :
                    NormalDialogFragment.OnButtonClickListener {
                    override fun onButton1Clicked() {
                        val bitmap = getBitmap(fragment_content_film)

                        var fos: OutputStream? = null
                        // 3
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            // 4
                            context?.contentResolver?.also { resolver ->

                                // 5
                                val contentValues = ContentValues().apply {
                                    put(MediaStore.MediaColumns.DISPLAY_NAME, "$articlecreatedate.jpg")
                                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                                }

                                // 6
                                val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                                // 7
                                fos = imageUri?.let { resolver.openOutputStream(it) }
                            }
                        } else {
                            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                            val image = File(imagesDir, "$articlecreatedate.jpg")
                            fos = FileOutputStream(image)
                        }

                        fos?.use {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                            Toast.makeText(context, "저장이 완료되었습니다.", duration).show()
                        }
                    }

                    override fun onButton2Clicked() {
                        //확인버튼을 눌렀을 때 처리할 곳
                    }
                })
                normaldialog.show((activity as MainActivity).supportFragmentManager, "CustomDialog")
            }
        }
        return p0 != null
    }

    private fun getImageUri(inImage: Bitmap): Uri {
        val destPath = requireActivity().externalCacheDir.toString() + "/cache_file.jpg"
        val tempFile = File(destPath)
        tempFile.createNewFile()
        val out =FileOutputStream(tempFile)
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, out)

        return FileProvider.getUriForFile(requireActivity(),"com.example.todayfilm.Fileprovider",tempFile)
    }

    fun getBitmap(fragment: FrameLayout): Bitmap{
        val bitmap = Bitmap.createBitmap(fragment.width, fragment.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgDrawable = fragment.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        fragment.draw(canvas)

        return bitmap
    }

    private fun likecheckFun(tf :Boolean){
        val check = LikeCheck()
        check.userpid = userpid.toString()
        check.articleidx = articleidx.toString()

        val callCheck = NetWorkClient.GetNetwork.likedcheck(check)
        callCheck.enqueue( object : Callback<LikeBoolean>{
            override fun onResponse (
                call: Call<LikeBoolean>,
                response: Response<LikeBoolean>
            ) {
                val isLiked = response.body()?.success
                if (isLiked!!) {
                    if (tf) {
                        binding.filmLikeBtn.isChecked = true
                    } else {
                        // 좋아요 취소
                        val likeDelete = LikeRequest()
                        likeDelete.likeyfrom = userpid.toString()
                        likeDelete.likeyto = articleidx.toString()
                        val callDelete = NetWorkClient.GetNetwork.likeddelete(likeDelete)
                        callDelete.enqueue(object : Callback<noRseponse> {
                            override fun onResponse (
                                call: Call<noRseponse>,
                                response: Response<noRseponse>
                            ) {
                                binding.filmLikeBtn.isChecked = false
                                countLikey()
                            }

                            override fun onFailure(call: Call<noRseponse>, t: Throwable) {
                                Toast.makeText(context, "좋아요 취소에 실패했습니다.", duration).show()
                                Log.e("좋아요 취소 실패", t.message.toString())
                            }
                        })
                    }
                } else {
                    if (tf) {
                        binding.filmLikeBtn.isChecked = false
                    } else {
                        // 좋아요
                        val likeAdd = LikeRequest()
                        likeAdd.likeyfrom = userpid.toString()
                        likeAdd.likeyto = articleidx.toString()
                        val callAdd = NetWorkClient.GetNetwork.likedcreate(likeAdd)
                        callAdd.enqueue(object  :Callback<noRseponse>{

                            override fun onResponse(
                                call: Call<noRseponse>,
                                response: Response<noRseponse>
                            ) {
                                animation()
                                countLikey()
                            }

                            override fun onFailure(call: Call<noRseponse>, t: Throwable) {
                                Toast.makeText(context, "좋아요에 실패했습니다.", duration).show()
                                Log.e("좋아요 실패", t.message.toString())
                            }
                        })
                    }
                }
            }

            override fun onFailure(call: Call<LikeBoolean>, t: Throwable) {
                Toast.makeText(context, "좋아요 확인에 실패했습니다.", duration).show()
                Log.e("좋아요 확인 실패", t.message.toString())
            }
        })
    }

    private fun countLikey(){
        val getArticleNumber = getArticleRequest()
        getArticleNumber.articleidx = articleidx.toString()
        val callLikeyNumber =  NetWorkClient.GetNetwork.getarticle(getArticleNumber)
        callLikeyNumber.enqueue(object : Callback<getArticleResponse>{
            override fun onResponse(
                call: Call<getArticleResponse>,
                response: Response<getArticleResponse>
            ) {
                binding.filmLikey.text = response.body()?.likey.toString()
            }

            override fun onFailure(call: Call<getArticleResponse>, t: Throwable) {
                Toast.makeText(context, "좋아요 조회에 실패했습니다.", duration).show()
                Log.e("좋아요 조회 실패", t.message.toString())
            }
        })
    }

    private fun animation(){
        val scaleAnimation = ScaleAnimation(
            0.7f,
            1.0f,
            0.7f,
            1.0f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        val bounceInterpolator = BounceInterpolator()
        scaleAnimation.duration = 500
        scaleAnimation.interpolator = bounceInterpolator
        binding.filmLikeBtn.startAnimation(scaleAnimation)
    }
}