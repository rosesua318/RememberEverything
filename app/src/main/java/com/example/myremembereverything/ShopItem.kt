package com.example.myremembereverything

import com.google.firebase.database.Exclude

data class ShopItem( // 상점 정보 데이터 클래스
    var objectId: String,
    val imgsrc:String,
    val name:String,
    val close:String,
    val address:String,
    val info:String,
    val homep:String
) {
    @Exclude
    fun toMap() : HashMap<String, Any> {
        val result: HashMap<String, Any> = HashMap()
        result["objectID"] = objectId
        result["imgsrc"] = imgsrc
        result["name"] = name
        result["close"] = close
        result["address"] = address
        result["info"] = info
        result["homep"] = homep
        return result
    }
}