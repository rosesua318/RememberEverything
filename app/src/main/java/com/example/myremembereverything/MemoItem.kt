package com.example.myremembereverything

import com.google.firebase.database.Exclude

class MemoItem ( // 메모 정보 데이터 클래스
        var objectId: String,
        val fileName:String,
        val content:String
) {
    @Exclude
    fun toMap() : HashMap<String, Any> {
        val result: HashMap<String, Any> = HashMap()
        result["objectID"] = objectId
        result["fileName"] = fileName
        result["content"] = content
        return result
    }
}