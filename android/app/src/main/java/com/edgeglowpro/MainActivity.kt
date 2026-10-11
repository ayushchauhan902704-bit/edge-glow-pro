package com.edgeglowpro

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat

class MainActivity : AppCompatActivity() {
    private lateinit var prefs: SharedPreferences
    private val CHANNEL_ID = "perm_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prefs = getSharedPreferences("edge_prefs", MODE_PRIVATE)
        createChannel()

        // Template SET button
        try {
            findViewById<Button>(R.id.btnSet1).setOnClickListener {
                prefs.edit().putInt("selected_template", 1).apply()
                Toast.makeText(this, "Rainbow Glow SET!", Toast.LENGTH_SHORT).show()
                testLight(20000L)
            }
        } catch(e: Exception) {}

        // Permission & Test button
        findViewById<Button>(R.id.btnEnable).setOnClickListener {
            checkAndNotify()
        }

        checkAndNotify()
    }

    private fun createChannel() {
        val channel = NotificationChannel(CHANNEL_ID, "Permission Alert", NotificationManager.IMPORTANCE_HIGH)
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(channel)
    }

    private fun checkAndNotify() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            val pending = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("Edge Glow Pro - Permission Do")
                .setContentText("Display over other apps ON karo, tabhi har app pe light jalegi")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pending)
                .build()
            val nm = getSystemService(NotificationManager::class.java)
            nm.notify(101, notification)
            Toast.makeText(this, "Notification pe click karke permission ON karo", Toast.LENGTH_LONG).show()
            startActivity(intent)
        } else {
            Toast.makeText(this, "Permission ON! Ab har app pe chalegi", Toast.LENGTH_SHORT).show()
            testLight(20000L)
        }
    }

    private fun testLight(duration: Long) {
        val i = Intent(this, EdgeLightingService::class.java)
        i.putExtra("DURATION", duration)
        startService(i)
    }
}
