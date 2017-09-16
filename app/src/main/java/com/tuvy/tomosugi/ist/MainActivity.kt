package com.tuvy.tomosugi.ist

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.tuvy.tomosugi.ist.model.Coordinate
import com.tuvy.tomosugi.ist.model.Distance
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("Main", "start")
        val socket: Socket = IO.socket("http://153.126.157.25:5000")
        socket
                .on(Socket.EVENT_CONNECT, Emitter.Listener {
                    Log.d("Socket", "Connect")
                    val distance = Distance(test = 10.0)
                    val gson = Gson()
                    socket.emit("changeColor1", gson.toJson(distance))
                    Log.d("Socket", gson.toJson(distance))
                    socket.disconnect()
                })
                .on("event", Emitter.Listener {
                    Log.d("Socket", "Event")
                })
                .on(Socket.EVENT_DISCONNECT, Emitter.Listener {
                    Log.d("Socket", "Disconnect")
                })
        socket.connect()
        Log.d("Main", "finish")
    }

    fun targetCoordinate(): Coordinate {
        val target = Coordinate(
                x = 35.672693,
                y = 139.370088
        )
        return target
    }

//    fun distance(target: Coordinate, current: Coordinate): Double {
//        val distance: Double
//        distance =
//    }
}
