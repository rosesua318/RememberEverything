package com.example.myremembereverything

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // kakao SDK 초기화
        KakaoSdk.init(this, "38e1affac5329105a8441a495eb9d705")
    }
}