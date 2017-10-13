package me.rohanjahagirdar.notificationsstatistics

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.IntentFilter
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intentFilter = IntentFilter()
        intentFilter.addAction("me.rohanjahagirdar.notificationsstatistics")


    }
}
