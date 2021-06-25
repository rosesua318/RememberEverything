package com.example.myremembereverything

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class SplashActivity : AppCompatActivity() { // 스플래쉬 화면
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val backgroundExecutor : ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        val mainExecutor : Executor = ContextCompat.getMainExecutor(this) // splashactivity 실행하겠다는 뜻

        backgroundExecutor.schedule({
            mainExecutor.execute{
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 2, TimeUnit.SECONDS) // 1초 동안 spalsh화면 보여주고 다음 액티비티로 넘어가게함

    }
}