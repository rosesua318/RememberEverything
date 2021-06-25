package com.example.myremembereverything

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.coroutines.*
import java.util.*

class MenuActivity : AppCompatActivity() {

    private lateinit var databaseRef: DatabaseReference
    private var database = FirebaseDatabase.getInstance()
    private var foods = database.getReference("foods")
    var fList = arrayListOf<FoodItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        databaseRef = FirebaseDatabase.getInstance().reference

        var intent = intent
        var weather = intent.getStringExtra("weather") // 날씨
        var tmp = intent.getStringExtra("tmp")
        var pm10v = intent.getStringExtra("pm10v") // 미세먼지
        var pm25v = intent.getStringExtra("pm25v") // 초미세먼지
        var email = intent.getStringExtra("email")

        var s = 0

        foods.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var showList = arrayListOf<FoodItem>()
                var shList = arrayListOf<FoodItem>()
                for(ds in snapshot.children) {
                    val name = ds.child("name").value as String
                    val src = ds.child("imgsrc").value as String
                    val type = ds.child("type").value as String
                    showList.add(FoodItem("", name, type, src)) // db에서 음식메뉴 정보 모두 저장
                }
                if(weather != "0" && (pm10v!!.toInt() > 80 || pm25v!!.toInt() > 35)) { // 비오고 미세먼지 안 좋을 때
                    for(sh in showList) {
                        if(sh.type == "2" || sh.type == "3") {
                            shList.add(sh)
                        }
                    }
                    plus.text = "날씨도 좋지 않고 미세먼지가 많은 지금 추천해요!"
                    s = shList.size
                } else if(weather != "0" && (pm10v!!.toInt() <= 80 && pm25v!!.toInt() <= 35)) { // 비오는데 미세먼지 좋을 때
                    for(sh in showList) {
                        if(sh.type == "3") {
                            shList.add(sh)
                        }
                    }
                    s = shList.size
                    plus.text = "날씨가 좋지 않은 지금 추천해요!"
                } else if(weather == "0" && (pm10v!!.toInt() > 80 || pm25v!!.toInt() > 35)) { // 화창한데 미세먼지 안 좋을 때
                    for(sh in showList) {
                        if(sh.type == "2") {
                            shList.add(sh)
                        }
                    }
                    s = shList.size
                    plus.text = "미세먼지가 많은 지금 추천해요!"
                } else if(weather == "0" && (pm10v!!.toInt() <= 80 && pm25v!!.toInt() <= 35)) { // 화창하고 미세먼지 없을 때
                    for(sh in showList) {
                        if(sh.type == "1") {
                            shList.add(sh)
                        }
                    }
                    s = shList.size
                    plus.text = "날씨도 좋고 미세먼지도 없는 지금 추천해요!"
                }

                val random = Random()
                val i = random.nextInt(s-1)
                val item = shList.get(i) // 랜덤 메뉴

                val imageView: ImageView = findViewById(R.id.simg)
                Glide.with(applicationContext)
                    .load(item.imgsrc) // 랜덤 메뉴에 해당하는 이미지 이미지뷰에 적용
                    .into(imageView)
                foodname.text = item.name // 랜덤 메뉴에 해당하는 메뉴이름 텍스트뷰에 적용

            }

        })

        retryBtn.setOnClickListener { // 재추천하기
            foods.addValueEventListener(object: ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    var showList = arrayListOf<FoodItem>()
                    var shList = arrayListOf<FoodItem>()
                    for(ds in snapshot.children) {
                        val name = ds.child("name").value as String
                        val src = ds.child("imgsrc").value as String
                        val type = ds.child("type").value as String
                        showList.add(FoodItem("", name, type, src))
                    }
                    if(weather != "0" && (pm10v!!.toInt() > 80 || pm25v!!.toInt() > 35)) { // 비오고 미세먼지 안 좋을 때
                        for(sh in showList) {
                            if(sh.type == "2" || sh.type == "3") {
                                shList.add(sh)
                            }
                        }
                        plus.text = "날씨도 좋지 않고 미세먼지가 많은 지금 추천해요!"
                        s = shList.size
                    } else if(weather != "0" && (pm10v!!.toInt() <= 80 && pm25v!!.toInt() <= 35)) { // 비오는데 미세먼지 좋을 때
                        for(sh in showList) {
                            if(sh.type == "3") {
                                shList.add(sh)
                            }
                        }
                        s = shList.size
                        plus.text = "날씨가 좋지 않은 지금 추천해요!"
                    } else if(weather == "0" && (pm10v!!.toInt() > 80 || pm25v!!.toInt() > 35)) { // 화창한데 미세먼지 안 좋을 때
                        for(sh in showList) {
                            if(sh.type == "2") {
                                shList.add(sh)
                            }
                        }
                        s = shList.size
                        plus.text = "미세먼지가 많은 지금 추천해요!"
                    } else if(weather == "0" && (pm10v!!.toInt() <= 80 && pm25v!!.toInt() <= 35)) { // 화창하고 미세먼지 없을 때
                        for(sh in showList) {
                            if(sh.type == "1") {
                                shList.add(sh)
                            }
                        }
                        s = shList.size
                        plus.text = "날씨도 좋고 미세먼지도 없는 지금 추천해요!"
                    }

                    val random = Random()
                    val i = random.nextInt(s-1)
                    val item = shList.get(i)

                    val imageView: ImageView = findViewById(R.id.simg)
                    Glide.with(applicationContext)
                        .load(item.imgsrc)
                        .into(imageView)
                    foodname.text = item.name

                }

            })
        }

        backBtn.setOnClickListener { // 돌아가기 눌렀을 때
            finish()
        }
    }
}