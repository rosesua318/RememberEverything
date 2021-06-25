package com.example.myremembereverything

import com.google.firebase.database.Exclude

data class FoodItem ( // 음식메뉴 정보 데이터 클래스
    var objectId: String,
    val name:String,
    val type:String,
    val imgsrc:String
) {
    @Exclude
    fun toMap(): HashMap<String, Any> {
        val result: HashMap<String, Any> = HashMap()
        result["objectID"] = objectId
        result["name"] = name
        result["type"] = type
        result["imgsrc"] = imgsrc
        return result
    }
}