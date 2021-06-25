package com.example.myremembereverything

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.myremembereverything.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_cancel.*

class CancelActivity : AppCompatActivity() {
    private var database = FirebaseDatabase.getInstance()
    private var myfavShopRef = database.getReference("favShop")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cancel)

        var intent = intent
        var shopname = intent.getStringExtra("shopname")
        var email = intent.getStringExtra("email")
        var e = email!!.substring(0, email!!.lastIndexOf("@"))
        var key = intent.getStringExtra("key")

        deleteshopname.text = shopname

        deleteBtn.setOnClickListener { // 사용자 단골 가게 목록에서 삭제하기
            myfavShopRef.child(e!!).child(key!!).removeValue() // 사용자의 단골 가게 내용중 키값에 해당하는 데이터 db에서 삭제
            Toast.makeText(applicationContext, "삭제를 완료하였습니다.", Toast.LENGTH_SHORT).show()
            val resultIntent = Intent()
            resultIntent.putExtra("result", "333")
            setResult(Activity.RESULT_OK, resultIntent)
            finish() // 액티비티 종료
        }

        noBtn.setOnClickListener {
            finish()
        }
    }
}