package com.example.myremembereverything

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_check.*
import java.util.HashMap
import kotlin.properties.Delegates

class CheckActivity : AppCompatActivity() {
    private var database = FirebaseDatabase.getInstance()
    private lateinit var databaseRef: DatabaseReference
    private var myShopRef = database.getReference("shop")
    private var myrShopRef = database.getReference("rShop")
    private var myfavShopRef = database.getReference("favShop")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check)

        var intent = intent
        var shopname = intent.getStringExtra("shopname")
        var email = intent.getStringExtra("email")
        var e = email!!.substring(0, email!!.lastIndexOf("@"))
        var key = intent.getStringExtra("key")

        databaseRef = FirebaseDatabase.getInstance().reference

        lateinit var day: String
        lateinit var info: String
        lateinit var address: String

        myShopRef.addValueEventListener(object: ValueEventListener { // 대형쇼핑몰 정보 db에서 가져오기
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children) {
                    val sname = ds.child("name").value as String
                    if(shopname == sname) { // 사용자가 클릭한 상점과 db의 상점 이름이 똑같을 때
                        day = ds.child("close").value as String
                        info = ds.child("info").value as String
                        address = ds.child("address").value as String

                        // db 정보들로 상세 정보란 채워넣기
                        detailshopname.text = sname
                        detailshopclose.text = "휴무일: "+ day
                        detailshopinfo.text = info
                        detailshopaddress.text = address

                        break
                    }
                }
            }

        })
        myrShopRef.addValueEventListener(object: ValueEventListener { // 음식점 정보 db에서 가져오기
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children) {
                    val sname = ds.child("name").value as String
                    if(shopname == sname) { // 사용자가 클릭한 상점과 db의 상점 이름이 똑같을 때
                        day = ds.child("close").value as String
                        info = ds.child("info").value as String
                        address = ds.child("address").value as String

                        // db 정보들로 상세 정보란 채워넣기
                        detailshopname.text = "음식점 이름: "+sname
                        detailshopclose.text = "휴무일: "+day
                        detailshopinfo.text = "정보: "+info
                        detailshopaddress.text = "주소: "+address

                        break
                    }
                }
            }

        })
        callBtn.setOnClickListener { // 전화하기 버튼 눌렀을 때
            var str = detailshopinfo.text.toString() // 상세정보에서 전화번호 가져오기
            if(str.length < 12) { // 전화번호가 없는 경우
                Toast.makeText(applicationContext, "전화번호 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            } else { // 전화번호가 있는 경우
                var phone = str.substring(10)
                var tel = "tel:"+phone
                var uri = Uri.parse(tel)
                var intent = Intent(Intent.ACTION_DIAL, uri)
                startActivity(intent) // 다이얼로 이동
            }
        }
        addBtn.setOnClickListener { // 단골 가게 목록에 추가하기 버튼 눌렀을 때
            if(day.isNotBlank()) { // 상점 db에 해당 상점의 정보가 있는 경우
                myfavShopRef.addListenerForSingleValueEvent(object: ValueEventListener { // 사용자 단골 가게 목록 정보 db에서 가져오기
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) { // 사용자 이메일에 해당하는 자식 노드만 가져와서 사용자별 구분 가능
                        var flag = true
                        for(ds in snapshot.children) {
                            if(ds.key.toString() == e) { // 사용자 이메일 주소랑 동일한 노드일 때
                                for(ks in ds.children) {
                                    var sname = ks.child("name").value as String
                                    if(shopname == sname) {
                                        flag = false
                                        Toast.makeText(applicationContext, "이미 단골 가게 목록에 저장된 가게입니다.", Toast.LENGTH_SHORT).show()
                                        finish() // db에 추가하지 않고 종료
                                        return
                                    }
                                }
                            }

                        }

                        if(flag) { // 사용자가 딘골 가게 목록에 추가하고자 하는 상점이 db에 없을 때 -> 추가
                            var key : String? = databaseRef.child("favShop").child(e!!).push().getKey()
                            var item = ShopItem(key!!, "", shopname!!, day, address, info, "")
                            val itemValues: HashMap<String, Any> = item.toMap()
                            val itemUpdates: MutableMap<String, Any> = HashMap()
                            itemUpdates["/favShop/$e/$key"] = itemValues
                            databaseRef.updateChildren(itemUpdates)
                            Toast.makeText(application, "단골 가게 목록에 저장하였습니다.", Toast.LENGTH_SHORT).show()
                            finish()
                            return
                        }
                    }

                })
            } else {
                Toast.makeText(applicationContext, "저장에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        cancelBtn.setOnClickListener {
            finish()
        }
    }
}
