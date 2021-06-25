package com.example.myremembereverything

import android.app.Activity
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentC.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentC : Fragment(), OnMapReadyCallback {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var mMap: GoogleMap
    lateinit var mContext : Context

    var locationClient: FusedLocationProviderClient? = null

    private var database = FirebaseDatabase.getInstance()
    private var myShopRef = database.getReference("shop")
    private var myRShopRef = database.getReference("rShop")

    var sLoc = arrayListOf<LocationItem>()
    var rsLoc = arrayListOf<LocationItem>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_c, container, false)

        // 구글맵에 표시하기 위한 사전작업
        sLoc.add(LocationItem(37.385141900041624, 126.65862710998648)) // 현대 프리미엄 아울렛
        sLoc.add(LocationItem(37.447188417607705, 126.65080008300603)) // 홈플러스 인하점
        sLoc.add(LocationItem(37.39829262333249, 126.63563329835037)) // NC CUBE 커넬워크
        sLoc.add(LocationItem(37.49774613223398, 126.67075467137069)) // 홈플러스 가좌점
        sLoc.add(LocationItem(37.49278338192014, 126.49125889835248)) // 롯데마트 영종도점
        sLoc.add(LocationItem(37.40048826446957, 126.72550735416858)) // 뉴코아 논현점
        sLoc.add(LocationItem(37.41525267239141, 126.67520058485972)) // 롯데마트 연수점
        sLoc.add(LocationItem(37.44260206416122, 126.7012972406787)) // 롯데백화점 인천터미널점
        sLoc.add(LocationItem(37.41993726013156, 126.66917692718722)) // 인천 LF스퀘어 인천점
        sLoc.add(LocationItem(37.441068756896456, 126.70147964253317)) // 롯데백화점 인천터미널점

        rsLoc.add(LocationItem(37.41094007458786, 126.66679709151467)) // 지니쿡 커피상점
        rsLoc.add(LocationItem(37.42541752282435, 126.64409701369645)) // 코코도쿄
        rsLoc.add(LocationItem(37.41741562436247, 126.65714907136902)) // 해오름
        rsLoc.add(LocationItem(37.426250781349665, 126.67882645602376)) // 토스투어
        rsLoc.add(LocationItem(37.377227976854385, 126.6522631059539)) // 1989 고깃집
        rsLoc.add(LocationItem(37.406071116641094, 126.68355831765959)) // 계절밥상 연수스퀘어원점
        rsLoc.add(LocationItem(37.37721849226303, 126.65414659872893)) // 맘스터치 송도글로벌캠퍼스점
        rsLoc.add(LocationItem(37.39284507897856, 126.64635891369574)) // 피제리아 일피노
        rsLoc.add(LocationItem(37.392205699659975, 126.63706004643507)) // 수라
        rsLoc.add(LocationItem(37.397404713922626, 126.61981432237958)) // 멕시카나치킨 송도신도시점

        AndPermission.with(activity)
                .runtime()
                .permission(Permission.Group.LOCATION)
                .onGranted { permissions ->
                    Log.d("Main", "허용된 권한 갯수 : ${permissions.size}")
                }
                .onDenied { permissions ->
                    Log.d("Main", "거부된 권한 갯수 : ${permissions.size}")
                }
                .start()

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync  {
            mMap = it
            try {
                mMap.isMyLocationEnabled = true
            } catch(e: SecurityException) {
                e.printStackTrace()
            }
        }
        requestLocation()
        nearShop()
        return view
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE // 위성사진으로 보이게
        mMap.uiSettings.isZoomControlsEnabled = true // 줌 컨트롤 버튼
    }

    private fun requestLocation() { // 현재 위치 찾아서 구글맵에서 마커로 표시하기
        locationClient = LocationServices.getFusedLocationProviderClient(mContext)
        try {
            locationClient?.lastLocation?.addOnSuccessListener { location ->
                if(location == null) {
                    Log.d("location", "최근 위치 확인 실패")
                }
                else {
                    Log.d("location", "최근 위치 확인 성공 : ${location.latitude}, ${location.longitude}")
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
                            Log.d("location", "내 위치 : ${location.latitude}, ${location.longitude}")
                        }
                        val curPoint = LatLng(it.locations[0].latitude, it.locations[0].longitude)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15f))
                        val myMarker = MarkerOptions()
                        myMarker.position(curPoint)
                        myMarker.title("● 내 위치\n")
                        myMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                        mMap.addMarker(myMarker)
                    }
                }
            }
            locationClient?.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        }
        catch (e : SecurityException) {
            e.printStackTrace()
        }
    }

    private fun nearShop() { // db에 있는 상점들 중 휴무일 아닌 상점만 마커로 표시
        val currentTime = Calendar.getInstance().getTime()
        val weekdayFormat = SimpleDateFormat("EE", Locale("ko", "KR"))
        val weekDay = weekdayFormat.format(currentTime)
        Log.d("weekDay", weekDay)
        var cksLoc = arrayListOf<LocationItem>()
        var ckrsLoc = arrayListOf<LocationItem>()
        var shopList = arrayListOf<String>()
        var rshopList = arrayListOf<String>()
        myShopRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var i = 0
                for(ds in snapshot.children) {
                    val day = ds.child("close").value as String

                    if(!day.equals(weekDay)) {
                        shopList.add(ds.child("name").value as String)
                        cksLoc.add(sLoc[i])
                    }
                    i+=1
                }
                //Log.d("shopList", shopList.toString())
                var j = 0
                for(loc in cksLoc) {
                    var mLat = loc.latitude
                    var mLoc = loc.longtitude
                    val myMarker = MarkerOptions()
                    val curPoint = LatLng(mLat, mLoc)
                    myMarker.position(curPoint)
                    myMarker.title(shopList[j])
                    myMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                    mMap.addMarker(myMarker)
                    j+=1
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        myRShopRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var i = 0
                for(ds in snapshot.children) {
                    val day = ds.child("close").value as String

                    if(!day.equals(weekDay)) {
                        rshopList.add(ds.child("name").value as String)
                        ckrsLoc.add(rsLoc[i])
                    }
                    i+=1
                }
                var j = 0
                Log.d("rshop", rshopList.toString())
                for(loc in ckrsLoc) {
                    var mLat = loc.latitude
                    var mLoc = loc.longtitude
                    val myMarker = MarkerOptions()
                    val curPoint = LatLng(mLat, mLoc)
                    myMarker.position(curPoint)
                    myMarker.title(rshopList[j])
                    myMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                    mMap.addMarker(myMarker)
                    j+=1
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
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
         * @return A new instance of fragment FragmentC.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentC().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}