package com.example.myremembereverything

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.MODE_NO_LOCALIZED_COLLATORS
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_e.*
import kotlinx.android.synthetic.main.fragment_e.view.*
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentE.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentE : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var fileName : String
    lateinit var mContext : Context
    private var str : String = ""

    private var email=""
    private var e = ""
    private var key = ""

    private var database = FirebaseDatabase.getInstance()
    private var myMemoRef = database.getReference("memoList")
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
        var view = inflater.inflate(R.layout.fragment_e, container, false)

        email = (activity as MainActivity).email
        e = email!!.substring(0, email!!.lastIndexOf("@"))

        databaseRef = FirebaseDatabase.getInstance().reference


        view.calendarView.setOnDateChangeListener { it, year, monthOfYear, dayOfMonth -> // 캘린더뷰에서 날짜 클릭했을 때
            diaryTextView.visibility = View.VISIBLE
            saveBtn.visibility = View.VISIBLE
            contextEditText.visibility = View.VISIBLE
            textView.visibility = View.INVISIBLE
            changeBtn.visibility = View.INVISIBLE
            delBtn.visibility = View.INVISIBLE
            diaryTextView.text = String.format("%d년 %d월 %d일", year, monthOfYear+1, dayOfMonth)
            contextEditText.setText("")
            checkDay(year, monthOfYear, dayOfMonth)
        }

        view.saveBtn.setOnClickListener { // 저장버튼 눌렀을 때
            removeDiary(fileName) // 해당날짜에 메모 내용 있는지 확인하고 있으면 삭제하는 함수 호출
            saveDiary(fileName) // 메모 DB에 저장하는 함수 호출
            str=contextEditText.text.toString()
            textView.text=str
            saveBtn.visibility = View.INVISIBLE
            changeBtn.visibility = View.VISIBLE
            delBtn.visibility = View.VISIBLE
            contextEditText.visibility = View.INVISIBLE
            textView.visibility = View.VISIBLE
        }

        return view
    }

    fun checkDay(cYear: Int, cMonth: Int, cDay: Int) {
        fileName = (Integer.toString(cYear)+Integer.toString(cMonth+1)+Integer.toString(cDay))
        str=""

        myMemoRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children) {
                    if(ds.key.toString() == e) { // 사용자 이메일 주소랑 동일한 노드일 때
                        for(ks in ds.children) {
                            var file = ks.child("fileName").value as String
                            if(fileName == file) { // 캘린더뷰에서 세팅한 날짜에 메모 내용이 DB에 있을 때
                                var text = ks.child("content").value as String
                                key = ks.child("objectID").value as String
                                str = text // DB의 메모 내용으로 내용 보여지게
                                break
                            }
                        }
                    }

                }
                contextEditText.visibility = View.INVISIBLE
                textView.visibility = View.VISIBLE
                textView.text = str

                saveBtn.visibility = View.INVISIBLE
                changeBtn.visibility = View.VISIBLE
                delBtn.visibility = View.VISIBLE

                changeBtn.setOnClickListener { // 수정 버튼 이벤트 리스너
                    contextEditText.visibility = View.VISIBLE
                    textView.visibility = View.INVISIBLE
                    contextEditText.setText(str)

                    saveBtn.visibility = View.VISIBLE
                    changeBtn.visibility = View.INVISIBLE
                    delBtn.visibility = View.INVISIBLE
                    textView.setText(contextEditText.getText())
                }

                if(textView.text == "") { // 해당 날짜에 DB에 저장된 메모 내용 없을 때
                    textView.visibility = View.INVISIBLE
                    diaryTextView.visibility = View.VISIBLE
                    saveBtn.visibility = View.VISIBLE // 새로 내용 쓸 수 있게 저장버튼만 활성화
                    changeBtn.visibility = View.INVISIBLE
                    delBtn.visibility = View.INVISIBLE
                    contextEditText.visibility = View.VISIBLE
                }
            }
        })
        delBtn.setOnClickListener { // 삭제 버튼 이벤트 리스너
            textView.visibility = View.INVISIBLE
            contextEditText.setText("")
            contextEditText.visibility = View.VISIBLE
            saveBtn.visibility = View.VISIBLE
            changeBtn.visibility = View.INVISIBLE
            delBtn.visibility = View.INVISIBLE
            removeDiary(fileName)
        }
    }

    fun removeDiary(readDay: String) {
        var flag = true
        myMemoRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children) {
                    if(ds.key.toString() == e) { // 사용자 이메일 주소랑 동일한 노드일 때
                        for(ks in ds.children) {
                            var file = ks.child("fileName").value as String
                            if(readDay == file) { // 해당 날짜에 메모 내용이 DB에 있을 때
                                key = ks.child("objectID").value as String
                                flag = false // 플래그를 false로 바꿔줌
                                break
                            }
                        }
                    }

                }
                if(!flag) { // flag가 false일 때 즉, 해당 날짜에 메모 내용이 DB에 있을 때
                    myMemoRef.child(e!!).child(key!!).removeValue() // 메모 내용 삭제해줌
                }
            }
        })
    }

    fun saveDiary(readDay: String) {
        myMemoRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                var flag = true
                for(ds in snapshot.children) {
                    if(ds.key.toString() == e) { // 사용자 이메일 주소랑 동일한 노드일 때
                        for(ks in ds.children) {
                            var file = ks.child("fileName").value as String
                            if(readDay == file) {  // 해당 날짜에 메모 내용이 DB에 있을 때
                                key = ks.child("fileName").value as String
                                myMemoRef.child(e!!).child(key!!).removeValue() // 해당 날짜 메모 DB에서 삭제해줌
                                break
                            }
                        }

                    }

                }
                if(flag) {
                    var k : String? = databaseRef.child("memoList").child(e!!).push().getKey()
                    var item = MemoItem(k!!, readDay, contextEditText.text.toString())
                    key = k.toString()
                    val itemValues: HashMap<String, Any> = item.toMap()
                    val itemUpdates: MutableMap<String, Any> = HashMap()
                    itemUpdates["/memoList/$e/$k"] = itemValues
                    databaseRef.updateChildren(itemUpdates) // 사용자가 입력한 내용 DB에 저장
                    Toast.makeText(mContext, "메모를 저장하였습니다.", Toast.LENGTH_SHORT).show()
                }
            }

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
         * @return A new instance of fragment FragmentE.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                FragmentE().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}