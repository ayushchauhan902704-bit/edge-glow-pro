package com.edgeglowpro
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.SweepGradient
import android.graphics.Canvas
import android.os.IBinder
import android.view.View
import android.view.WindowManager
import android.animation.ValueAnimator

class EdgeLightingService : Service() {
    private var windowManager: WindowManager? = null
    private var overlayView: EdgeView? = null
    override fun onBind(intent: Intent?): IBinder? = null

    private val colorsList = listOf(
        intArrayOf(Color.MAGENTA, Color.YELLOW, Color.CYAN),
        intArrayOf(Color.BLUE, Color.CYAN),
        intArrayOf(Color.RED, Color.YELLOW),
        intArrayOf(Color.BLUE, Color.GREEN),
        intArrayOf(Color.RED, Color.parseColor("#FF8500")),
        intArrayOf(Color.WHITE, Color.CYAN),
        intArrayOf(Color.GREEN, Color.YELLOW),
        intArrayOf(Color.GRAY, Color.BLUE)
    )

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val idx = intent?.getIntExtra("template", 0) ?: 0
        val cols = colorsList[idx % colorsList.size]
        val ch = NotificationChannel("edge", "Edge Glow", NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(ch)
        val n = android.app.Notification.Builder(this, "edge").setContentTitle("Edge Active").setSmallIcon(android.R.drawable.ic_menu_day).build()
        startForeground(1, n)
        show(cols)
        return START_STICKY
    }

    private fun show(cols: IntArray) {
        try { overlayView?.let { windowManager?.removeView(it) } } catch(e: Exception) {}
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        overlayView = EdgeView(this, cols)
        val p = WindowManager.LayoutParams(-1, -1, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, 8 or 16 or 256, PixelFormat.TRANSLUCENT)
        windowManager?.addView(overlayView, p)
    }

    override fun onDestroy() {
        try { overlayView?.let { windowManager?.removeView(it) } } catch(e: Exception) {}
        super.onDestroy()
    }

    class EdgeView(c: android.content.Context, private val colors: IntArray) : View(c) {
        private val paint = Paint(1).apply { style = Paint.Style.STROKE; strokeWidth = 6f }
        private var ang = 0f
        init {
            ValueAnimator.ofFloat(0f, 360f).apply { duration = 2000; repeatCount = -1; addUpdateListener { ang = it.animatedValue as Float; invalidate() }; start() }
        }
        override fun onDraw(canvas: Canvas) {
            val s = SweepGradient(width/2f, height/2f, colors, null)
            val m = Matrix()
            m.setRotate(ang, width/2f, height/2f)
            s.setLocalMatrix(m)
            paint.shader = s
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        }
    }
}
