package com.example.todayfilm

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.todayfilm.data.*
import com.example.todayfilm.retrofit.NetWorkClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ArticleAdapter(private val context: Context) : RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {
    var datas = mutableListOf<ArticleResponse>()

    // ViewHolder 객체 생성 후 리턴
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_search_article, parent, false)
        return ViewHolder(view)
    }

    // 전체 데이터 개수 조회
    override fun getItemCount(): Int = datas.size

    // ViewHolder 재활용 시 사용
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val user: ConstraintLayout = itemView.findViewById(R.id.recycler_article_user)
        private val userid: TextView = itemView.findViewById(R.id.recycler_article_userid)
        private val userimg: ImageView = itemView.findViewById(R.id.recycler_article_userimg)
        private val articlethumbnail: ImageView = itemView.findViewById(R.id.recycler_article_image)
        private val articlecreatedate: TextView = itemView.findViewById(R.id.recycler_article_date)

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(item: ArticleResponse) {
            // 작성자 정보 조회
            val profile = GetProfile()
            profile.userpid = item.article.userpid

            val callUser = NetWorkClient.GetNetwork.getprofile(profile)
            callUser.enqueue(object : Callback<CompleteProfile> {
                override fun onResponse(
                    call: Call<CompleteProfile>,
                    response: Response<CompleteProfile>
                ) {
                    val result = response.body()
                    Glide.with(itemView).load("http://i7c207.p.ssafy.io:8080/harufilm/upload/profile/" + result?.userimg).into(userimg)
                    userid.text = result?.userid
                }

                override fun onFailure(call: Call<CompleteProfile>, t: Throwable) {
                    Log.e("작성자 정보 조회 실패", t.message.toString())
                }
            })

            Glide.with(itemView).load("http://i7c207.p.ssafy.io:8080/harufilm/upload/article/${item.article.userpid}/${item.article.articlecreatedate}/${item.article.articlethumbnail}.jpg").into(articlethumbnail)

            val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
            val temp = LocalDate.parse(item.article.articlecreatedate, formatter)
            val changed = temp.format(DateTimeFormatter.ofPattern("yyyy/MM/dd (E)"))
            articlecreatedate.text = changed

            user.setOnClickListener {
                itemClickListener.onClick(it, item.article.userpid)
            }

            articlethumbnail.setOnClickListener {
                itemClickListener.onClick(it, item.article.articleidx, item.article.articlecreatedate, item.article.userpid, item.likey, item.hash as ArrayList)
            }
        }
    }

    interface ItemClickListener {
        fun onClick(view: View, articleidx: String, articlecreatedate: String, article_userpid: String, likey: String, hash: ArrayList<String>)

        fun onClick(view: View, userpid: String)
    }

    private lateinit var itemClickListener: ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }
}