package com.example.myremembereverything

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentD.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentD : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var email=""

    private var database = FirebaseDatabase.getInstance()
    private var myFavShopRef = database.getReference("favShop")
    private lateinit var recyclerView:RecyclerView
    var ad:FavShopItemAdapter?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view : View = inflater.inflate(R.layout.fragment_d, container, false)
        email = (activity as MainActivity).email
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById<RecyclerView>(R.id.listviewD)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        var e = email!!.substring(0, email!!.lastIndexOf("@"))

        var itemList : ArrayList<ShopItem> = arrayListOf()
        myFavShopRef.addListenerForSingleValueEvent(object: ValueEventListener { // 사용자 단골 가게 목록 db에서 정보 가져오기(이메일로 구분)
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) { // 사용자 이메일에 해당하는 자식 노드만 가져와서 사용자별 구분 가능
                for(ds in snapshot.children) {
                    if(ds.key.toString() == e) { // 사용자 이메일 주소랑 동일한 노드일 때
                        for(ks in ds.children) {
                            var key = ks.child("objectID").value as String
                            var sname = ks.child("name").value as String
                            var day = ks.child("close").value as String
                            var info = ks.child("info").value as String
                            var address = ks.child("address").value as String
                            itemList.add(ShopItem(key, "", sname, day, address, info, "")) // db 정보 리스트에 추가해서 리사이클러뷰로 출력할 수 있게
                            Log.d("itemList", itemList.toString())
                        }
                    }

                }
                recyclerView.adapter = FavShopItemAdapter(itemList, email) // 어댑터 연결
            }

        })
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentD.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                FragmentD().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}