package com.example.todayfilm

import android.content.Context
import android.os.Build
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

class HashtagAdapter(private val context: Context) : RecyclerView.Adapter<HashtagAdapter.ViewHolder>() {
    var datas = mutableListOf<String>()

    // ViewHolder 객체 생성 후 리턴
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recyler_hashtag, parent, false)
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
        private val hashtag: TextView = itemView.findViewById(R.id.recycler_hashtag)

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(item: String) {
            hashtag.text = "#$item"

            itemView.setOnClickListener {
                itemClickListener.onClick(it, item)
            }
        }
    }

    interface ItemClickListener {
        fun onClick(view: View, hashtag: String)
    }

    private lateinit var itemClickListener: ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }
}