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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.todayfilm.data.ArticleResponse
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ProfileArticleAdapter(private val context: Context) : RecyclerView.Adapter<ProfileArticleAdapter.ViewHolder>() {
    val userpid = MyPreference.read(context, "userpid")
    var datas = mutableListOf<ArticleResponse>()

    // ViewHolder 객체 생성 후 리턴
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_profile_article, parent, false)
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
        private val likes: TextView = itemView.findViewById(R.id.recycler_profile_article_likes)
        private val articlecreatedate: TextView = itemView.findViewById(R.id.recycler_profile_article_date)
        private val articlethumbnail: ImageView = itemView.findViewById(R.id.recycler_profile_article_image)

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(item: ArticleResponse) {
            var hashstring = ""

            for (hashtag in item.hash) {
                hashstring += "#$hashtag "
            }

            val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
            val temp = LocalDate.parse(item.article.articlecreatedate, formatter)
            val changed = temp.format(DateTimeFormatter.ofPattern("yyyy/MM/dd (E)"))

            articlecreatedate.text = changed
            likes.text = item.likey
            Glide.with(itemView).load("http://i7c207.p.ssafy.io:8080/harufilm/upload/article/${item.article.userpid}/${item.article.articlecreatedate}/${item.article.articlethumbnail}.png").into(articlethumbnail)

            itemView.setOnClickListener {
                itemClickListener.onClick(it, item.article.articleidx, item.article.articlecreatedate, item.article.userpid, item.likey, hashstring)
            }
        }
    }

    interface ItemClickListener {
        fun onClick(view: View, articleidx: String, articlecreatedate: String, article_userpid: String, likey: String, hashstring: String)
    }

    private lateinit var itemClickListener: ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }
}