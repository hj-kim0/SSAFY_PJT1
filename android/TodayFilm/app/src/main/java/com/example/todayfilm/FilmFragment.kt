package com.example.todayfilm

import CustomDialogFragment
import android.R.attr.path
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
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.todayfilm.databinding.FragmentFilmBinding
import kotlinx.android.synthetic.main.fragment_film.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*


class FilmFragment : Fragment(), View.OnClickListener,PopupMenu.OnMenuItemClickListener{
    lateinit var binding: FragmentFilmBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.beginTransaction().add(R.id.fragment_content_film, FrameFragment()).commit()
        setOnClickListener()
    }

    private fun setOnClickListener() {

        binding.menu.setOnClickListener(this)
    }



    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.menu -> { showPopup(binding.menu)




            }
        }
    }
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
                        //취소버튼을 눌렀을 때 처리할 곳
                        Toast.makeText(context, "삭제가 완료되었습니다.", duration).show()
                    }

                    override fun onButton2Clicked() {
                        //확인버튼을 눌렀을 때 처리할 곳
                        Toast.makeText(context, "삭제가 취소되었습니다.", duration).show()
                    }
                })


            }

            R.id.share -> {

                dialog.show((activity as MainActivity).supportFragmentManager, "CustomDialog")

            }

            R.id.shareFile -> {

//                val bitmap = Bitmap.createBitmap(fragment_content_film.getWidth(), fragment_content_film.getHeight(), Bitmap.Config.ARGB_8888);
//                val canvas = Canvas(bitmap);
//                val bgDrawable = fragment_content_film.getBackground();
//                if (bgDrawable != null) {
//                    bgDrawable.draw(canvas);
//                } else {
//
//                    canvas.drawColor(Color.WHITE);
//                }
//                fragment_content_film.draw(canvas);
                val bitmap = getBitmap(fragment_content_film)

                // bitmap


                val path :Uri? = getImageUri(context,bitmap)


                // uri





                val sharingIntent = Intent(Intent.ACTION_SEND)
                val screenshotUri = Uri.parse(path.toString()) // android image path


                sharingIntent.type = "image/png"
                sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri)
                startActivity(Intent.createChooser(sharingIntent, "Share image using")) // 변경가능


            }

            R.id.save -> {
                val btn= arrayOf("네","아니오")
                normaldialog.arguments = bundleOf(

                    "bodyTitle" to "정말 저장하시겠습니까?",
                    "bodyContext" to "프레임이 갤러리에 저장됩니다.",
                    "btnData" to btn
                )

                normaldialog.setButtonClickListener(object :
                    NormalDialogFragment.OnButtonClickListener {
                    override fun onButton1Clicked() {



//                        val bitmap = Bitmap.createBitmap(fragment_content_film.getWidth(), fragment_content_film.getHeight(), Bitmap.Config.ARGB_8888);
//                        val canvas = Canvas(bitmap);
//                        val bgDrawable = fragment_content_film.getBackground();
//                        if (bgDrawable != null) {
//                            bgDrawable.draw(canvas);
//                        } else {
//
//                            canvas.drawColor(Color.WHITE);
//                        }
//                        fragment_content_film.draw(canvas);

                        val bitmap = getBitmap(fragment_content_film)



                        var fos: OutputStream? = null
                        var title = "이것은 당시 날짜이다."
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

    fun getImageUri(inContext: Context?, inImage: Bitmap?): Uri? {
        val bytes = ByteArrayOutputStream()
        if (inImage != null) {
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        }
        val path = MediaStore.Images.Media.insertImage(inContext?.getContentResolver(), inImage, "Title" + " - " + Calendar.getInstance().getTime(), null)
        return Uri.parse(path)
    }

    fun getBitmap(fragment: FrameLayout): Bitmap{

        val bitmap = Bitmap.createBitmap(fragment.getWidth(), fragment.getHeight(), Bitmap.Config.ARGB_8888);
        val canvas = Canvas(bitmap);
        val bgDrawable = fragment.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {

            canvas.drawColor(Color.WHITE);
        }
        fragment.draw(canvas);

        return bitmap
    }











}