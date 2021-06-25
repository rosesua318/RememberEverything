package com.example.myremembereverything

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.favshopitem.view.*

class FavShopItemAdapter (var items:ArrayList<ShopItem>, var email:String) : RecyclerView.Adapter<FavShopItemAdapter.ViewHolder>() { // 사용자 단골 가게 목록 항목 리사이클러뷰로 보여주기 위함
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavShopItemAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.favshopitem,parent,false)
        return ViewHolder(itemView)
    }

    override fun getItemCount()= items.count()

    override fun onBindViewHolder(holder: FavShopItemAdapter.ViewHolder, position: Int) {
        val item = items[position]
        holder.setItem(item)
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        fun setItem(item:ShopItem) {
            itemView.fshopname.text = item.name
            itemView.fshopclose.text = "매주" + item.close + "요일"
            itemView.fshopaddress.text = item.address
            itemView.fshopinfo.text = item.info

            itemView.setOnClickListener {
                val intent = Intent(itemView?.context, CancelActivity::class.java)
                intent.putExtra("shopname", item.name)
                intent.putExtra("email", email)
                intent.putExtra("key", item.objectId)
                startActivityForResult(itemView.context as Activity, intent, 101, null) // 데이터 삭제 됐을 경우 메인으로 돌아가기 위함
            }
        }
    }
}