package com.tuvy.tomosugi.ist

import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.DownloadListener
import android.widget.*
import com.google.gson.Gson
import com.tuvy.tomosugi.ist.model.Coordinate
import com.tuvy.tomosugi.ist.model.Distance
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import java.util.*
import android.widget.LinearLayout




class MainActivity : AppCompatActivity() {

    var timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // サンプルのデータセットの作成
        val target = targetCoordinate()
        val start = startCoordinate()
        val gapLevel = 10
        val gapCoordinate = Coordinate(
                x = Math.abs(target.x - start.x)/gapLevel,
                y = Math.abs(target.y - start.y)/gapLevel)
        val dataset: List<Coordinate>
                = List(gapLevel,
                { index ->
                    Coordinate(x = start.x + index*gapCoordinate.x,
                            y = start.y + index*gapCoordinate.y)
                })
        // データセット確認用
        var index = 0
        Log.d("DataSet", "Gap: " + gapCoordinate.x.toString() + ", " + gapCoordinate.y.toString())
        Log.d("DataSet", "Stt: " + start.x.toString() + ", " + start.y.toString())
        while (index < dataset.size) {
//            Log.d("DataSet", "Log: " + dataset[index].x.toString() + ", " + dataset[index].y.toString())
            Log.d("DataSet", "Log: " + getDistance(target, dataset[index]).first().toString())
            index++
        }
        Log.d("DataSet", "End: " + target.x.toString() + ", " + target.y.toString())


        var distanceLayout = findViewById(R.id.parentDistance) as LinearLayout
        var distanceText = findViewById(R.id.distance) as TextView
        var submitButton = findViewById(R.id.submitLocation) as Button
        var connectButton = findViewById(R.id.connect) as Button

        var cnt = 0

        Log.d("Main", "start")
        // Web Socket初期化
        val socket: Socket = IO.socket("http://153.126.157.25:5000")
        socket
                .on(Socket.EVENT_CONNECT, Emitter.Listener {
                    Log.d("Socket", "Connect")
                })
                .on(Socket.EVENT_CONNECT_ERROR, Emitter.Listener {
                    Log.d("Socket", "ConnectError")
                })
                .on(Socket.EVENT_ERROR, Emitter.Listener {
                    Log.d("Socket", "Error")
                })
                .on(Socket.EVENT_DISCONNECT, Emitter.Listener {
                    Log.d("Socket", "Disconnect")
                })

        connectButton.setOnClickListener {
            socket.connect()
            submitButton.isEnabled = true
            connectButton.isEnabled = false
        }

        submitButton.isEnabled = false
        submitButton.setOnClickListener {
            val gson = Gson()
            if (cnt >= dataset.size) {
                socket.emit("changeColor1", gson.toJson(Distance(0.1)))
                socket.disconnect()
                submitButton.isEnabled = false
                distanceLayout.removeAllViews()
                layoutInflater.inflate(R.layout.arrive_view, distanceLayout)
            }
            else {
                Log.d("Socket", "Emit")
                val distance = Distance(getDistance(target, dataset[cnt]).first().toDouble())
                socket.emit("changeColor1", gson.toJson(distance))
                distanceText.text = getDistance(target, dataset[cnt]).first().toInt().toString()
            }
            cnt++
        }

        Log.d("Main", "finish")
    }

    fun targetCoordinate(): Coordinate {
        // コニカミノルタ八王子SKT
        val target = Coordinate(
                x = 35.672693,
                y = 139.370088
        )
        return target
    }

    fun startCoordinate(): Coordinate {
        // 北八王子駅
        val start = Coordinate(
                x = 35.669374,
                y = 139.363394
        )
        return start
    }

    fun getDistance(target: Coordinate, current: Coordinate): FloatArray {
        var results: FloatArray = kotlin.FloatArray(3)
//        distance = Math.sqrt(getSquare(target.x - current.x) - getSquare(target.y - current.y))
        Location.distanceBetween(target.x, target.y, current.x, current.y, results)
        return results
    }

    fun getSquare(td: Double): Double {
        return (td * td)
    }
}
