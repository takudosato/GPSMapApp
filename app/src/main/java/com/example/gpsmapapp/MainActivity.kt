package com.example.gpsmapapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.app.ActivityCompat
import java.net.URLEncoder

/**
 * メインのAcrivityクラス
 *
 */
class MainActivity : AppCompatActivity() {

    //緯度フィールド
    private var _latitude = 0.0
    //経度フィールド
    private var _longitude = 0.0


    /**
     * 起動時の処理
     *パーミッションを確認する
     *
     * @param savedInstanceState　
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //LocationManagerオブジェクトを取得
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //位置情報が更新された際のリスナオブジェクトを生成
        val locationLister = GPSLocationListnerer()
        //位置情報の追跡を開始
        //ACCESS_FINE_LOCATIONの許可がおりていないならば、
        if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //許可を求めるダイアログを表示する
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(this@MainActivity, permissions, 1000)

            //OnCreateを終了
            return
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationLister)
    }

    /**
     *
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //ACCESS_FINE_LOCATIONに対するパーミッションダイアログで許可を選択したばあい
        if(requestCode == 1000 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //LocationManagerオブジェクトを取得
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            //位置情報が更新された際のリスナオブジェクトを生成
            val locationLister = GPSLocationListnerer()
            //再度アクセスチェック
            if(ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationLister)
        }

    }

    /**
     *
     *
     */
    private inner class GPSLocationListnerer : LocationListener {
        override fun onLocationChanged(location: Location) {
            //locationから緯度・緯度を取得
            _latitude = location.latitude
            _longitude = location.longitude

            //取得した緯度・経度を表示
            val tvLatitude = findViewById<TextView>(R.id.tvLatitude)
            tvLatitude.text = _latitude.toString()
            val tvLongitude = findViewById<TextView>(R.id.tvLongitude)
            tvLongitude.text = _longitude.toString()
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    /**
     * 指定した文字列の場所の地図を起動する
     *
     * @param view
     */
    fun onMapSearchButtonClick(view: View) {

        //入力欄に入力されたキーワード文字列を取得
        val etSearchWord = findViewById<EditText>(R.id.etSearchWord)
        var searchWord = etSearchWord.text.toString()

        //入力されたキーワードをURLエンコード
        searchWord = URLEncoder.encode(searchWord, "UTF-8")
        //マップアプリと連携するURI文字列を生成
        val uriStr = "geo:0,0?q=${searchWord}"

        val uri = Uri.parse(uriStr)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    /**
     * 指定した緯度と経度の場所の地図を表示する
     *
     * @param view　使わない
     */
    fun onMapShowCurrentButtonClick(view: View) {

        //フィールドの緯度と経度の値を元にマップアプリと連携するURI文字列を生成
        val uriStr = "geo:${_latitude},${_longitude}"
        //URI文字列からURIオブジェクトを生成
        val uri = Uri.parse(uriStr)
        //Intentオブジェクトを生成
        val intent = Intent(Intent.ACTION_VIEW, uri)

        startActivity(intent)
    }
}