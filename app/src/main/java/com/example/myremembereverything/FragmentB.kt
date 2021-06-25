package com.example.myremembereverything

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_b.view.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.StringReader
import java.net.URL
import java.util.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentB.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentB : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var email=""

    lateinit var mContext : Context

    private lateinit var databaseRef: DatabaseReference

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
        val view = inflater.inflate(R.layout.fragment_b, container, false)
        databaseRef = FirebaseDatabase.getInstance().reference

        email = (activity as MainActivity).email

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.listviewB)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val serviceUrl = "https://api.odcloud.kr/api/15048906/v1/uddi:76d78696-e2d6-42e2-9a6f-fd855beba945_201909261643?page=1000&perPage=10&returnType=XML&serviceKey="
        val serviceKey = "2rMAtbDqvunHNC8%2FtkQPyHaLW%2F8IJ%2F9QzZtVGcoySujQOUzTDqB5sj0U57VyAQMDqnNl40eXn4C7GhLSojyLYw%3D%3D"

        val requestUrl = serviceUrl + serviceKey
        fetchXML(requestUrl , view)
    }

    fun fetchXML(myURL : String, view : View) { // 공공 데이터 포털에서 인천광역시 음식점 정보 api 사용하기
        lateinit var page : String

        if(databaseRef.child("rShop") != null) { // 매번 api 호출 다시해서 db 갱신(이미 바뀌어서 잘못되어버린 정보를 제공하는 것을 막기 위함)
            databaseRef.child("rShop").removeValue()
        }

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
                var i = 0
                val day = arrayOf("월", "화", "수", "목", "금", "토", "일")
                var itemList: ArrayList<ShopItem> = arrayListOf()
                var bSet1 = false
                var bSet2 = false
                var bSet3 = false
                var ck1 = false
                var ck2 = false
                var ck3 = false
                lateinit var rNm : String
                lateinit var rAd : String
                lateinit var rInfo : String
                var factory = XmlPullParserFactory.newInstance()
                factory.setNamespaceAware(true)
                val xpp = factory.newPullParser() // XML 파서
                xpp.setInput(StringReader(page))
                var eventType = xpp.eventType
                while(eventType != XmlPullParser.END_DOCUMENT) {
                    var key : String? = databaseRef.child("rShop").push().getKey()
                    if (eventType == XmlPullParser.START_DOCUMENT) {}
                    else if (eventType == XmlPullParser.START_TAG) {
                        //var tag_name = xpp.name
                        var attr_name = ""
                        if(xpp.name == "col") {
                            attr_name = xpp.getAttributeValue(null, "name")
                        }
                        if(attr_name.equals("업태")) bSet1 = true
                        else if(attr_name.equals("소재지")) bSet2 = true
                        else if(attr_name.equals("업소명")) bSet3 = true
                    }
                    if (eventType == XmlPullParser.TEXT) {
                        if(bSet1) {
                            rInfo = xpp.text
                            bSet1 = false
                            ck1 = true
                        }
                        else if(bSet2) {
                            rAd = xpp.text
                            bSet2 = false
                            ck2 = true
                        }
                        else if(bSet3) {
                            rNm = xpp.text
                            bSet3 = false
                            ck3 = true
                        }
                        if(ck1 && ck2 && ck3) {
                            val d = day.get(i)
                            Log.d("ddd", d)
                            if(i == 6) {
                                i = 0
                            } else {
                                i += 1
                            }
                            var item = ShopItem(key!!, "", rNm, d, rAd, rInfo, "")
                            val itemValues: HashMap<String, Any> = item.toMap()
                            val itemUpdates: MutableMap<String, Any> = HashMap()
                            itemUpdates["/rShop/$key"] = itemValues
                            databaseRef.updateChildren(itemUpdates) // 음식정 정보 db에 오픈 api에서 가져온 내용 저장
                            itemList.add(item)
                            ck1 = false
                            ck2 = false
                            ck3 = false
                        }
                    }
                    if (eventType == XmlPullParser.END_TAG) {}
                    eventType = xpp.next()
                }
                view.listviewB.adapter = RShopItemAdapter(itemList,email) // 어댑터 연결
            }
        }
        getDangerGrade().execute()
    }

    override fun onAttach(activity: Activity) { // 프래그먼트가 메인액티비티에 붙을때실행
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
         * @return A new instance of fragment FragmentB.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                FragmentB().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}