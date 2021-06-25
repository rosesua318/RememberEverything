package com.example.myremembereverything

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_a.*
import kotlinx.android.synthetic.main.fragment_a.view.*
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private lateinit var databaseRef: DatabaseReference

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentA.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentA : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var mContext : Context

    private var database = FirebaseDatabase.getInstance()
    private var myShopRef = database.getReference("shop")

    private var email=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view : View = inflater.inflate(R.layout.fragment_a, container, false)
        databaseRef = FirebaseDatabase.getInstance().reference
        email = (activity as MainActivity).email
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.listviewA)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        doTask("https://itour.incheon.go.kr/NTK_30000/NTK_30100/List.jsp")
    }

    fun doTask(myurl : String) { // 인천광역시 홈페이지에서 대형쇼핑몰 관련 정보 웹크롤링
        if(databaseRef.child("shop") != null) { // 매번 웹크롤링 다시해서 db 갱신(이미 바뀌어서 잘못되어버린 정보를 제공하는 것을 막기 위함)
            databaseRef.child("shop").removeValue()
        }
        var i = 0
        val day = arrayOf("월", "화", "수", "목", "금", "토", "일")
        var documentTitle : String = ""
        var itemList : ArrayList<ShopItem> = arrayListOf()
        Single.fromCallable {
            try {
                val doc = Jsoup.connect(myurl).get()

                val elems : Elements = doc.select("ul.t_list li")
                run elemsLoop@ {
                    elems.forEachIndexed{ index, elem ->
                        var key : String? = databaseRef.child("shop").push().getKey()

                        var imgsrc = elem.select("div.t_list_img img").attr("src")
                        var title = elem.select("div.t_list_cont p").text()
                        var address = elem.select("span.adress").text()
                        var info = elem.select("span.info").text()
                        var homep = elem.select("span.homep a").text()
                        val d = day.get(i)
                        if(i == 6) {
                            i = 0
                        } else {
                            i += 1
                        }
                        var item = ShopItem(key!!, imgsrc, title, d, address, info, homep) // 크롤링한 내용 데이터 클래스로 생성
                        val itemValues: HashMap<String, Any> = item.toMap()
                        val itemUpdates: MutableMap<String, Any> = HashMap()
                        itemUpdates["/shop/$key"] = itemValues
                        databaseRef.updateChildren(itemUpdates) // 대형쇼핑몰 정보 db에 저장
                        itemList.add(item)
                        if(index == 9) {
                            return@elemsLoop
                        }
                    }
                }
                documentTitle = doc.title()
            } catch(e : Exception) {
                e.printStackTrace()
            }
            return@fromCallable documentTitle
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ text -> // 후반작업
                    listviewA.adapter = ShopItemAdapter(itemList, email) // 어댑터 연결
                },{
                    it.printStackTrace()
                })
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
         * @return A new instance of fragment FragmentA.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                FragmentA().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}