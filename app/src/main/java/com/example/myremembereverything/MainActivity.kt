package com.example.myremembereverything

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kakao.sdk.user.UserApiClient
import com.yanzhenjie.permission.AndPermission
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.nav_header.view.*
import java.security.Permission

class MainActivity : AppCompatActivity() {
    var TAG = "kakaologin"
    var email = ""
    private var database = FirebaseDatabase.getInstance()
    private var myFavShopRef = database.getReference("favShop")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar.title="Remember Everything"
        setSupportActionBar(toolbar)

        email = intent.getStringExtra("email").toString()

        var nav_header_view = navigationView.getHeaderView(0)
        nav_header_view.clientEmail.text = email + "님"

        with(supportFragmentManager.beginTransaction()) {
            val fragment1 = FragmentA()
            toolbar.title="Remember Everything"
            replace(R.id.container, fragment1)
            commit()
        }
        bottom_navigation.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.tab1 -> { // 하단 왼쪽 메뉴 탭 눌렀을 때
                    with(supportFragmentManager.beginTransaction()) {
                        val fragment1 = FragmentA()
                        toolbar.title="Remember Everything"
                        replace(R.id.container, fragment1)
                        commit()
                    }
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.tab2 -> { // 하단 가운데 메뉴 탭 눌렀을 때
                    with(supportFragmentManager.beginTransaction()) {
                        val fragment2 = FragmentB()
                        toolbar.title="Remember Everything"
                        replace(R.id.container, fragment2)
                        commit()
                    }
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.tab3 -> { // 하단 오른쪽 메뉴 탭 눌렀을 때
                    with(supportFragmentManager.beginTransaction()) {
                        val fragment3 = FragmentC()
                        toolbar.title="Remember Everything"
                        replace(R.id.container, fragment3)
                        commit()
                    }
                    return@setOnNavigationItemSelectedListener true
                }
            }
            return@setOnNavigationItemSelectedListener false
        }

        navigationView.setNavigationItemSelectedListener { //네비게이션뷰 이벤트리스너
            when(it.itemId) {
                R.id.item1 -> { // 제일 첫번째 메뉴
                    with(supportFragmentManager.beginTransaction()) {
                        val fragment4 = FragmentD()
                        toolbar.title="사용자 단골 가게 목록"
                        replace(R.id.container, fragment4)
                        commit() // 바꾼거 실행
                    }
                }
                R.id.item2 -> { // 두번째 메뉴
                    with(supportFragmentManager.beginTransaction()) {
                        val fragment5 = FragmentE()
                        toolbar.title="캘린더 장보기 메모장"
                        replace(R.id.container, fragment5)
                        commit() // 바꾼거 실행
                    }
                }
                R.id.item4 -> { // 세번째 메뉴
                    with(supportFragmentManager.beginTransaction()) {
                        val fragment6 = FragmentF()
                        toolbar.title="날씨 및 미세먼지 기반 추천"
                        replace(R.id.container, fragment6)
                        commit() // 바꾼거 실행
                    }
                }
                R.id.item3 -> { // 네번째 메뉴(로그아웃)
                    UserApiClient.instance.logout { error ->
                        if (error != null) {
                            Log.e(TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                        }
                        else {
                            Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
                        }
                    }
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            return@setNavigationItemSelectedListener true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { // 단골 가게 목록 삭제하기 누르고 삭제가 성공했을 때 실행됨
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            with(supportFragmentManager.beginTransaction()) {
                val fragment1 = FragmentA()
                toolbar.title="Remember Everything"
                replace(R.id.container, fragment1)
                commit()
            }
        }
    }

    override fun onBackPressed() { // 이전키가 눌려졌을 때 자동 호출
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}