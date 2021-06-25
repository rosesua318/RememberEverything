package com.example.myremembereverything

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_shop.*
import java.util.*

class ShopActivity : AppCompatActivity() {
    private val rList = arrayListOf<String>("한식", "통닭(치킨)")
    private val pList = arrayListOf<String>("식육(숯불구이)", "경양식")
    private val nList = arrayListOf<String>("기타", "일식", "패스트푸드")
    private var database = FirebaseDatabase.getInstance()
    private var shops = database.getReference("rShop")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

        var intent = intent
        var weather = intent.getStringExtra("weather")
        var tmp = intent.getStringExtra("tmp")
        var pm10v = intent.getStringExtra("pm10v")
        var pm25v = intent.getStringExtra("pm25v")

        var s = 0

        shops.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var nameList = arrayListOf<String>()
                val shopList = arrayListOf<ShopItem>()
                val shList = arrayListOf<ShopItem>()
                for(ds in snapshot.children) {
                    val name = ds.child("name").value as String
                    val address = ds.child("address").value as String
                    val info = ds.child("info").value as String
                    shopList.add(ShopItem("", "", name, "", address, info, "")) // db에서 주변음식점 정보 얻어와서 저장
                }
                //Log.d("shop", shopList.toString())
                if(weather != "0" && (pm10v!!.toInt() > 80 || pm25v!!.toInt() > 35)) { // 비오고 미세먼지 안 좋을 때
                    nameList.addAll(rList)
                    nameList.addAll(pList)
                    for(sh in shopList) {
                        for(n in nameList) {
                            if(sh.info == n) {
                                shList.add(sh)
                                break
                            }
                        }
                    }
                    shopplus.text = "날씨도 좋지 않고 미세먼지가 많은 지금은 "
                    s = shList.size
                } else if(weather != "0" && (pm10v!!.toInt() <= 80 && pm25v!!.toInt() <= 35)) { // 비오는데 미세먼지 좋을 때
                    nameList.addAll(rList)
                    for(sh in shopList) {
                        for(n in nameList) {
                            if(sh.info == n) {
                                shList.add(sh)
                                Log.d("shList", shList.toString())
                                break
                            }
                        }
                    }
                    s = shList.size
                    shopplus.text = "날씨가 좋지 않은 지금은 "
                } else if(weather == "0" && (pm10v!!.toInt() > 80 || pm25v!!.toInt() > 35)) { // 화창한데 미세먼지 안 좋을 때
                    nameList.addAll(pList)
                    for(sh in shopList) {
                        for(n in nameList) {
                            if(sh.info == n) {
                                shList.add(sh)
                                break
                            }
                        }
                    }
                    s = shList.size
                    shopplus.text = "미세먼지가 많은 지금은 "
                } else if(weather == "0" && (pm10v!!.toInt() <= 80 && pm25v!!.toInt() <= 35)) { // 화창하고 미세먼지 없을 때
                    nameList.addAll(nList)
                    for(sh in shopList) {
                        for(n in nameList) {
                            if(sh.info == n) {
                                shList.add(sh)
                                break
                            }
                        }
                    }
                    s = shList.size
                    shopplus.text = "날씨도 좋고 미세먼지도 없는 지금은 "
                }

                val random = Random()
                val i = random.nextInt(s-1)
                val item = shList.get(i) // 랜덤 음식점

                foodshopname.text = item.name // 랜덤 음식점에 해당하는 식당 이름 적용
                shopaddress.text = item.address // 랜덤 음식점에 해당하는 식당 주소 적용
                var ps = shopplus.text.toString()
                ps += item.info
                shopplus.text = ps + " 식당 추천!" // 랜덤 음식점에 해당하는 식당 정보 적용
            }

        })

        retryShopBtn.setOnClickListener { //재추천하기 눌렀을 때
            shops.addValueEventListener(object: ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    var nameList = arrayListOf<String>()
                    val shopList = arrayListOf<ShopItem>()
                    val shList = arrayListOf<ShopItem>()
                    for(ds in snapshot.children) {
                        val name = ds.child("name").value as String
                        val address = ds.child("address").value as String
                        val info = ds.child("info").value as String
                        shopList.add(ShopItem("", "", name, "", address, info, ""))
                    }
                    //Log.d("shop", shopList.toString())
                    if(weather != "0" && (pm10v!!.toInt() > 80 || pm25v!!.toInt() > 35)) { // 비오고 미세먼지 안 좋을 때
                        nameList.addAll(rList)
                        nameList.addAll(pList)
                        for(sh in shopList) {
                            for(n in nameList) {
                                if(sh.info == n) {
                                    shList.add(sh)
                                    break
                                }
                            }
                        }
                        shopplus.text = "날씨도 좋지 않고 미세먼지가 많은 지금은 "
                        s = shList.size
                    } else if(weather != "0" && (pm10v!!.toInt() <= 80 && pm25v!!.toInt() <= 35)) { // 비오는데 미세먼지 좋을 때
                        nameList.addAll(rList)
                        for(sh in shopList) {
                            for(n in nameList) {
                                if(sh.info == n) {
                                    shList.add(sh)
                                    Log.d("shList", shList.toString())
                                    break
                                }
                            }
                        }
                        s = shList.size
                        shopplus.text = "날씨가 좋지 않은 지금은 "
                    } else if(weather == "0" && (pm10v!!.toInt() > 80 || pm25v!!.toInt() > 35)) { // 화창한데 미세먼지 안 좋을 때
                        nameList.addAll(pList)
                        for(sh in shopList) {
                            for(n in nameList) {
                                if(sh.info == n) {
                                    shList.add(sh)
                                    break
                                }
                            }
                        }
                        s = shList.size
                        shopplus.text = "미세먼지가 많은 지금은 "
                    } else if(weather == "0" && (pm10v!!.toInt() <= 80 && pm25v!!.toInt() <= 35)) { // 화창하고 미세먼지 없을 때
                        nameList.addAll(nList)
                        for(sh in shopList) {
                            for(n in nameList) {
                                if(sh.info == n) {
                                    shList.add(sh)
                                    break
                                }
                            }
                        }
                        s = shList.size
                        shopplus.text = "날씨도 좋고 미세먼지도 없는 지금은 "
                    }

                    val random = Random()
                    val i = random.nextInt(s-1)
                    val item = shList.get(i)

                    foodshopname.text = item.name
                    shopaddress.text = item.address
                    var ps = shopplus.text.toString()
                    ps += item.info
                    shopplus.text = ps + " 식당 추천!"
                }

            })
        }

        backShopBtn.setOnClickListener { // 돌아가기 눌렀을 때
            finish()
        }
    }
}