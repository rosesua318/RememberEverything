package com.example.myremembereverything

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color.blue
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import kotlinx.android.synthetic.main.fragment_f.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.StringReader
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentF.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentF : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var email=""
    lateinit var mContext : Context
    private var locationClient: FusedLocationProviderClient? = null
    private var lat=0.0
    private var lon=0.0
    private var weather : String=""
    private var tmp : String = ""
    private var pm10v : String=""
    private var pm25v : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_f, container, false)
        email = (activity as MainActivity).email

        AndPermission.with(activity)
            .runtime()
            .permission(Permission.Group.LOCATION)
            .onGranted { permissions ->
                Log.d("Main", "????????? ?????? ?????? : ${permissions.size}")
            }
            .onDenied { permissions ->
                Log.d("Main", "????????? ?????? ?????? : ${permissions.size}")
            }
            .start()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // ?????? ?????? api ???????????? ?????? ??????
        val serviceUrl1 = "http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst?serviceKey="
        val serviceKey = "2rMAtbDqvunHNC8%2FtkQPyHaLW%2F8IJ%2F9QzZtVGcoySujQOUzTDqB5sj0U57VyAQMDqnNl40eXn4C7GhLSojyLYw%3D%3D"
        val serviceUrl2 = "&numOfRows=10&pageNo=1&base_date="
        val current = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        var formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        var formatted = current.format(formatter) // ?????? ?????? ex)20210616
        val formatter2 = DateTimeFormatter.ofPattern("h")
        val formatted2 = current.format(formatter2) // ????????????
        var serviceUrl3 = ""
        if(formatted2.toInt() < 8) { // 0???~8???????????? ?????? 23??? ?????? ?????? ??????
            var calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            var TimeToDate = calendar.time
            var f = SimpleDateFormat("yyyyMMdd")
            var finalResultDate = f.format(TimeToDate)
            formatted = finalResultDate
            serviceUrl3 = "&base_time=2300&nx="
        } else if(formatted2.toInt() >= 8) { // ?????? 8??? ??????????????? ?????? ?????? 8??? ?????? ??????
            serviceUrl3 = "&base_time=0800&nx="
        }

        menuBtn.setOnClickListener { // ???????????? ?????? ????????? ???
            val intent = Intent(context, MenuActivity::class.java)
            intent.putExtra("weather", weather)
            intent.putExtra("tmp", tmp)
            intent.putExtra("pm10v", pm10v)
            intent.putExtra("pm25v", pm25v)
            intent.putExtra("email", email)
            ContextCompat.startActivity(view.context, intent, null)
        }

        shopBtn.setOnClickListener { // ????????? ?????? ?????? ????????? ???
            val intent = Intent(context, ShopActivity::class.java)
            intent.putExtra("weather", weather)
            intent.putExtra("tmp", tmp)
            intent.putExtra("pm10v", pm10v)
            intent.putExtra("pm25v", pm25v)
            intent.putExtra("email", email)
            ContextCompat.startActivity(view.context, intent, null)
        }

        locationClient = LocationServices.getFusedLocationProviderClient(mContext)
        try {
            locationClient?.lastLocation?.addOnSuccessListener { location ->
                if(location == null) {
                    //Log.d("location", "?????? ?????? ?????? ??????")
                }
                else {
                    //Log.d("location", "?????? ?????? ?????? ?????? : ${location.latitude}, ${location.longitude}")
                    lat = location.latitude
                    lon = location.longitude
                    val serviceUrl4 = "&ny="
                    val requestUrl = serviceUrl1+serviceKey+serviceUrl2+formatted+serviceUrl3+lat.toInt().toString()+serviceUrl4+lon.toInt().toString()
                    Log.d("requestUrl", requestUrl)
                    findWeather(requestUrl, view) // ?????? ????????? ?????? ????????? ????????? ?????? ?????? ????????? ?????? ??????db?????? ???????????? ?????? ??????

                }
            }
                ?.addOnFailureListener {
                    it.printStackTrace()
                }
            val locationRequest = LocationRequest.create()
            locationRequest.run {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                interval = 60 * 1000
            }
            val locationCallback = object: LocationCallback() {
                override fun onLocationResult(p0: LocationResult?) {
                    p0?.let {
                        for((i, location) in it.locations.withIndex()) {
                            //Log.d("location", "??? ?????? : ${location.latitude}, ${location.longitude}")
                            lat = location.latitude
                            lon = location.longitude
                        }
                    }
                }
            }
            locationClient?.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
            val dserviceUrl = "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty?sidoName=??????&pageNo=1&numOfRows=1&returnType=xml&serviceKey="
            val dserviceKey = "2rMAtbDqvunHNC8%2FtkQPyHaLW%2F8IJ%2F9QzZtVGcoySujQOUzTDqB5sj0U57VyAQMDqnNl40eXn4C7GhLSojyLYw%3D%3D"
            val dserviceUrl2 = "&ver=1.0"
            val drequestUrl = dserviceUrl + dserviceKey + dserviceUrl2
            findDust(drequestUrl, view) // ?????? ???????????? ?????? ??????db?????? ???????????? ?????? ??????
        }
        catch (e : SecurityException) {
            e.printStackTrace()
        }

    }

    fun findWeather(myURL : String, view : View) { // ?????? ?????? api ??????
        lateinit var page : String
        class getDangerGrade : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                val stream = URL(myURL).openStream()
                val bufreader = BufferedReader(InputStreamReader(stream, "UTF-8"))
                var line = bufreader.readLine()
                page = ""
                while(line != null) {
                    page += line
                    line = bufreader.readLine()
                }
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                var bSet1 = false
                var bSet2 = false
                var bSet3 = false
                var bSet4 = false
                var factory = XmlPullParserFactory.newInstance()
                factory.setNamespaceAware(true)
                val xpp = factory.newPullParser() // XML ??????
                xpp.setInput(StringReader(page))
                var eventType = xpp.eventType
                while(eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {}
                    else if (eventType == XmlPullParser.START_TAG) {
                        var tag_name = xpp.name
                        if(tag_name == "category") {
                            bSet1 = true
                        }
                        else if(tag_name == "fcstValue") {
                            bSet2 = true
                        }
                    }
                    if (eventType == XmlPullParser.TEXT) {
                        if(bSet1) {
                            if(xpp.text == "T3H") {
                                bSet3 = true
                            } else if(xpp.text == "PTY") {
                                bSet4 = true
                            }
                            bSet1 = false
                        }
                        else if(bSet2) {
                            if(bSet3) {
                                //temp.text = xpp.text
                                tmp = xpp.text
                            } else if(bSet4) {
                                weather = xpp.text
                            }
                            bSet2 = false
                            bSet3 = false
                            bSet4 = false
                        }

                    }
                    if (eventType == XmlPullParser.END_TAG) {}
                    eventType = xpp.next()
                }
                temp.text = "??????: " + tmp + "??C" // ?????? ??????
                if(weather == "1") { // ????????? ???
                    wimg.setImageResource(R.drawable.rain)
                } else if(weather == "2") { // ????????? ???
                    wimg.setImageResource(R.drawable.rain)
                } else if(weather == "3") { // ????????? ???
                    wimg.setImageResource(R.drawable.snow)
                } else if(weather == "4") { // ????????? ???
                    wimg.setImageResource(R.drawable.rain)
                } else if(weather == "0"){ // ????????? ??????
                    wimg.setImageResource(R.drawable.sunny)
                } else if(weather == "5"){ // ????????? ???
                    wimg.setImageResource(R.drawable.rain)
                } else if(weather == "6") { // ????????? ???
                    wimg.setImageResource(R.drawable.rain)
                } else if(weather == "7") { // ????????? ???
                    wimg.setImageResource(R.drawable.snow)
                } else {// ??? ?????? ????????? ??????
                    wimg.setImageResource(R.drawable.sunny)
                }
                Log.d("weather", weather)
            }
        }
        getDangerGrade().execute()
    }

    fun findDust(myURL : String, view : View) { // ???????????? ?????? api
        lateinit var page : String
        class getDangerGrade : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg p0: Void?): Void? {
                val stream = URL(myURL).openStream()
                val bufreader = BufferedReader(InputStreamReader(stream, "UTF-8"))
                var line = bufreader.readLine()
                page = ""
                while(line != null) {
                    page += line
                    line = bufreader.readLine()
                }
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                var bSet1 = false
                var bSet2 = false
                var factory = XmlPullParserFactory.newInstance()
                factory.setNamespaceAware(true)
                val xpp = factory.newPullParser() // XML ??????
                xpp.setInput(StringReader(page))
                var eventType = xpp.eventType
                while(eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {}
                    else if (eventType == XmlPullParser.START_TAG) {
                        var tag_name = xpp.name
                        if(tag_name == "pm10Value") {
                            bSet1 = true
                        }
                        else if(tag_name == "pm25Value") {
                            bSet2 = true
                        }
                    }
                    if (eventType == XmlPullParser.TEXT) {
                        if(bSet1) {
                            pm10v = xpp.text
                            bSet1 = false
                        }
                        else if(bSet2) {
                            pm25v = xpp.text
                            bSet2 = false
                        }

                    }
                    if (eventType == XmlPullParser.END_TAG) {}
                    eventType = xpp.next()
                }
                if(pm10v.toInt() <= 30) { // ??????????????? 30????????? ?????? ?????????
                    pm10.setTextColor(ContextCompat.getColor(mContext!!, R.color.blue))
                    pm10.text = "????????????: " + pm10v + "(??????)"
                } else if (pm10v.toInt() <= 80) { // ??????????????? 80????????? ?????? ?????? ???
                    pm10.setTextColor(ContextCompat.getColor(mContext!!, R.color.green))
                    pm10.text = "????????????: " + pm10v + "(??????)"
                } else if (pm10v.toInt() <= 150) { // ??????????????? 150????????? ?????? ?????? ???
                    pm10.setTextColor(ContextCompat.getColor(mContext!!, R.color.orange))
                    pm10.text = "????????????: " + pm10v + "(??????)"
                } else { // ??????????????? 150 ?????? ??????
                    pm10.setTextColor(ContextCompat.getColor(mContext!!, R.color.red))
                    pm10.text = "????????????: " + pm10v + "(????????????)"
                }
                if(pm25v.toInt() <= 15) { // ?????????????????? 15????????? ?????? ?????? ???
                    pm25.setTextColor(ContextCompat.getColor(mContext!!, R.color.blue))
                    pm25.text = "???????????????: " + pm25v + "(??????)"
                } else if(pm25v.toInt() <= 35) { // ?????????????????? 35????????? ?????? ?????? ???
                    pm25.setTextColor(ContextCompat.getColor(mContext!!, R.color.green))
                    pm25.text = "???????????????: " + pm25v + "(??????)"
                } else if(pm25v.toInt() <= 75) { // ?????????????????? 75????????? ?????? ?????? ???
                    pm25.setTextColor(ContextCompat.getColor(mContext!!, R.color.orange))
                    pm25.text = "???????????????: " + pm25v + "(??????)"
                } else { // ?????????????????? 76?????? ??????
                    pm25.setTextColor(ContextCompat.getColor(mContext!!, R.color.red))
                    pm25.text = "???????????????: " + pm25v + "(????????????)"
                }
            }
        }
        getDangerGrade().execute()
    }

    override fun onAttach(activity: Activity) { // ?????????????????? ????????????????????? ???????????????
        super.onAttach(activity)
        if (context is MainActivity) {
            mContext = context as MainActivity
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentF.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                FragmentF().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}