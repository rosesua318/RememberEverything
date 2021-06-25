package com.example.myremembereverything

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.shopitem.view.*

class RShopItemAdapter (var items:ArrayList<ShopItem>, var email:String) : RecyclerView.Adapter<RShopItemAdapter.ViewHolder>() { // 음식점 정보 리사이클러뷰로 보여주기 위함
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RShopItemAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.shopitem,parent,false)
        return ViewHolder(itemView)
    }

    override fun getItemCount()= items.count()

    override fun onBindViewHolder(holder: RShopItemAdapter.ViewHolder, position: Int) {
        val item = items[position]
        holder.setItem(item)
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        fun setItem(item:ShopItem) {
            itemView.shopname.text = item.name
            itemView.shopclose.text = "매주" + item.close + "요일"
            itemView.shopaddress.text = "주소: "+item.address

            itemView.setOnClickListener { // 상세정보 보여주기

                val intent = Intent(itemView?.context, CheckActivity::class.java)
                intent.putExtra("shopname", item.name)
                intent.putExtra("day", item.close)
                intent.putExtra("info", item.info)
                intent.putExtra("address", item.address)
                intent.putExtra("email", email)
                intent.putExtra("key", item.objectId)
                ContextCompat.startActivity(itemView.context, intent, null)

            }
        }
    }
}