package com.example.todayfilm

import android.content.ContentValues
import android.content.Context
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
import android.widget.FrameLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.todayfilm.data.ArticleDeleteRequest
import com.example.todayfilm.data.ArticleDeleteResponse
import com.example.todayfilm.data.CompleteProfile
import com.example.todayfilm.data.GetProfile
import com.example.todayfilm.databinding.FragmentFilmBinding
import com.example.todayfilm.retrofit.NetWorkClient
import kotlinx.android.synthetic.main.fragment_film.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

class FilmFragment : Fragment(), View.OnClickListener, PopupMenu.OnMenuItemClickListener{
    lateinit var binding: FragmentFilmBinding
    var article_userpid: String? = null
    var articlecreatedate: String? = null
    var articleidx: String? = null
    var likey: String? = null
    var hashstring: String? = null
    var userpid: String? = null

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
                Log.d("사용자 정보 조회 실패", t.message.toString())
            }
        })

        // 본인이면 filmMenu 보여주기
        if (userpid == article_userpid) {
            binding.filmMenu.visibility = View.VISIBLE
        } else {
            binding.filmMenu.visibility = View.GONE
        }

        childFragmentManager.beginTransaction().add(R.id.fragment_content_film, frameFragment).commit()
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.filmMenu.setOnClickListener(this)
        binding.filmPlayBtn.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.film_menu -> {
                showPopup(binding.filmMenu)
            }

            R.id.film_play_btn -> {
                val sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
                sharedViewModel.setIsPlay(true)
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
        val duration = Toast.LENGTH_SHORT

        when (p0?.itemId) { // 메뉴 아이템에 따라 동작 다르게 하기
            R.id.delete -> {
                val btn= arrayOf("네","아니오")
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
                        Log.d("게시글 번호", articleidx.toString())
                        var articledelete = ArticleDeleteRequest()
                        articledelete.articleidx = articleidx!!.toInt()
                        Log.d("게시글번호2", articledelete.articleidx.toString())
                        val call = NetWorkClient.GetNetwork.articledelete(articledelete)
                        call.enqueue(object : Callback<ArticleDeleteResponse> {
                            override fun onResponse(
                                call: Call<ArticleDeleteResponse>,
                                response: Response<ArticleDeleteResponse>
                            ) {
                                val result: ArticleDeleteResponse? = response.body()

                                Log.d("성공", result?.message.toString() + " " + result?.success.toString())
                                Toast.makeText(context, "삭제가 완료되었습니다.", duration).show()
                                moveToMain()
                            }

                            override fun onFailure(call: Call<ArticleDeleteResponse>, t: Throwable) {
                                Log.d("실패:", t.message.toString())
                            }
                        })
                        Toast.makeText(context, "삭제가 완료되었습니다.", duration).show()
                    }

                    override fun onButton2Clicked() {
                        //취소버튼을 눌렀을 때 처리할 곳
                        Toast.makeText(context, "삭제가 취소되었습니다.", duration).show()
                    }
                })
            }

            R.id.share -> {
                val sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
                sharedViewModel.setArticleIdx(articleidx ?: "")
                dialog.show((activity as MainActivity).supportFragmentManager, "CustomDialog")
            }

            R.id.shareFile -> {
                val bitmap = getBitmap(fragment_content_film)

                val path :Uri = getImageUri(context,bitmap)

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
                        val title = articlecreatedate
                        // 3
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            // 4
                            context?.contentResolver?.also { resolver ->

                                // 5
                                val contentValues = ContentValues().apply {
                                    put(MediaStore.MediaColumns.DISPLAY_NAME, "$title.png")
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
                            val image = File(imagesDir, "$title.png")
                            fos = FileOutputStream(image)
                        }

                        fos?.use {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                            Toast.makeText(context, "저장이 완료되었습니다.", duration).show()
                        }
                    }

                    override fun onButton2Clicked() {
                        //확인버튼을 눌렀을 때 처리할 곳
                        Toast.makeText(context, "저장이 취소되었습니다.", duration).show()
                    }
                })
                normaldialog.show((activity as MainActivity).supportFragmentManager, "CustomDialog")
            }
        }
        return p0 != null
    }

    fun getImageUri(inContext: Context?, inImage: Bitmap): Uri {
        val storage = File(requireContext().cacheDir, "images")
        val fileName: String = "cache"+ ".jpg"
        val tempFile = File(storage, fileName)

        try {
            // 자동으로 빈 파일을 생성합니다.
            tempFile.createNewFile()

            // 파일을 쓸 수 있는 스트림을 준비합니다.
            val out = FileOutputStream(tempFile)

            // compress 함수를 사용해 스트림에 비트맵을 저장합니다.
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, out)

            // 스트림 사용후 닫아줍니다.
            out.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val uri = FileProvider.getUriForFile(requireActivity(),"com.example.todayfilm.Fileprovider",tempFile)

        return uri
    }

    fun getBitmap(fragment: FrameLayout): Bitmap{
        val bitmap = Bitmap.createBitmap(fragment.getWidth(), fragment.getHeight(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgDrawable = fragment.getBackground()
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        fragment.draw(canvas)

        return bitmap
    }
    private fun moveToMain() {
        // complete 액티비티로 이동
        val intent = Intent(activity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}