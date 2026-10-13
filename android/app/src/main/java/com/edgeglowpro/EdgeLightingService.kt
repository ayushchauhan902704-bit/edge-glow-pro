package com.edgeglowpro
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.*
import android.os.IBinder
import android.view.WindowManager
import android.view.View
import android.animation.ValueAnimator

class EdgeLightingService : Service() {
    private var windowManager: WindowManager? = null
    private var overlayView: EdgeView? = null
    override fun onBind(intent: Intent?): IBinder? = null

    // Tere 16 templates ke colors
    private val colorsList = listOf(
        intArrayOf(Color.MAGENTA, Color.YELLOW, Color.CYAN),
        intArrayOf(Color.BLUE, Color.CYAN),
        intArrayOf(Color.RED, Color.YELLOW),
        intArrayOf(Color.BLUE, Color.GREEN),
        intArrayOf(Color.parseColor("#FF6B6B"), Color.parseColor("#FFE66D")),
        intArrayOf(Color.parseColor("#9D4EDD"), Color.parseColor("#00F5D4")),
        intArrayOf(Color.RED, Color.parseColor("#FF8500")),
        intArrayOf(Color.WHITE, Color.CYAN),
        intArrayOf(Color.GREEN, Color.parseColor("#90BE6D")),
        intArrayOf(Color.MAGENTA,
