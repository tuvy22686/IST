package com.tuvy.tomosugi.ist

import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.DownloadListener
import android.widget.LinearLayout
import android.widget.SimpleAdapter
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import com.google.gson.Gson
import com.tuvy.tomosugi.ist.model.Coordinate
import com.tuvy.tomosugi.ist.model.Distance
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import java.util.*


class MainActivity : AppCompatActivity() {

    var timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val target = targetCoordinate()
        val start = startCoordinate()
        val gapLevel = 10
        val gapCoordinate = Coordinate(
                x = Math.abs(target.x - start.x)/gapLevel,
                y = Math.abs(target.y - start.y)/gapLevel)
        val dataset: List<Coordinate>
                = List(10,
                { index ->
                    Coordinate(x = start.x + index*gapCoordinate.x,
                            y = start.y + index*gapCoordinate.y) })


        var distanceView = findViewById(R.id.distance) as TextView
        var cnt = 0
        Log.d("DataSet", "Gap: " + gapCoordinate.x.toString() + ", " + gapCoordinate.y.toString())
        Log.d("DataSet", "Stt: " + start.x.toString() + ", " + start.y.toString())
        while (cnt < dataset.size) {
//            Log.d("DataSet", "Log: " + dataset[cnt].x.toString() + ", " + dataset[cnt].y.toString())
            Log.d("DataSet", "Log: " + getDistance(target, dataset[cnt]).first().toString())
            distanceView.text = getDistance(target, dataset[cnt]).first().toString()
            cnt++
        }
        Log.d("DataSet", "End: " + target.x.toString() + ", " + target.y.toString())


        Log.d("Main", "start")
        val socket: Socket = IO.socket("http://153.126.157.25:5000")
        socket
                .on(Socket.EVENT_CONNECT, Emitter.Listener {
                    Log.d("Socket", "Connect")
//                    while (cnt < dataset.size) {
//                        val distance = Distance(getDistance(target, dataset[cnt]).first().toDouble())
//                        val gson = Gson()
//                        socket.emit("changeColor1", gson.toJson(distance))
//                        cnt++
//                    }
                    val distance = Distance(diff = getDistance(target, dataset[0]).first().toDouble())
                    val gson = Gson()
                    socket.emit("changeColor1", gson.toJson(distance))
                    Log.d("Socket", gson.toJson(distance))
                    socket.disconnect()
                })
                .on(Socket.EVENT_DISCONNECT, Emitter.Listener {
                    Log.d("Socket", "Disconnect")
                })
        socket.connect()
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
