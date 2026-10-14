package com.edgeglowpro
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.*
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.animation.ValueAnimator

class EdgeLightingService : Service() {
    private var windowManager: WindowManager? = null
    private var overlayView: EdgeView? = null
    private val handler = Handler(Looper.getMainLooper())
    private var isShowing = false

    // 5 sec on / 1 min off cycle
    private val cycleRunnable = object : Runnable {
        override fun run() {
            overlayView?.let {
                if(isShowing){
                    it.visibility = View.GONE
                    isShowing = false
                    handler.postDelayed(this, 60000) // 1 min band
                } else {
                    it.visibility = View.VISIBLE
                    isShowing = true
                    handler.postDelayed(this, 5000) // 5 sec jale
                }
            }
        }
    }

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

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val idx = intent?.getIntExtra("template", 0) ?: 0
        val cols = colorsList[idx % colorsList.size]
        
        val ch = NotificationChannel("edge", "Edge Glow", NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(ch)
        val n = android.app.Notification.Builder(this, "edge")
            .setContentTitle("Edge Glow Active - 5s on / 1m off")
            .setSmallIcon(android.R.drawable.ic_menu_day).build()
        startForeground(1, n)
        
        show(cols)
        return START_STICKY
    }

    private fun show(cols: IntArray) {
        try { overlayView?.let { windowManager?.removeView(it) } } catch(e: Exception) {}
        handler.removeCallbacks(cycleRunnable)

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        overlayView = EdgeView(this, cols)

        // MATCH_PARENT se pure kinare cover honge
        val p = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or 
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or 
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )
        windowManager?.addView(overlayView, p)
        
        // Cycle start
        isShowing = false
        handler.post(cycleRunnable)
    }

    override fun onDestroy() {
        handler.removeCallbacks(cycleRunnable)
        try { overlayView?.let { windowManager?.removeView(it) } } catch(e: Exception) {}
        super.onDestroy()
    }

    class EdgeView(c: android.content.Context, private val colors: IntArray) : View(c) {
        // FIX: Moti light + Gol corner
        private val thickness = 38f // pehle 6f thi
        private val cornerRadius = 75f // phone ka gol kinara follow karegi
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { 
            style = Paint.Style.STROKE
            strokeWidth = thickness
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }
        private var ang = 0f
        
        init {
            ValueAnimator.ofFloat(0f, 360f).apply { 
                duration = 2000
                repeatCount = -1
                addUpdateListener { 
                    ang = it.animatedValue as Float
                    invalidate()
                }
                start()
            }
        }

        override fun onDraw(canvas: Canvas) {
            val sweep = SweepGradient(width/2f, height/2f, colors, null)
            val m = Matrix()
            m.setRotate(ang, width/2f, height/2f)
            sweep.setLocalMatrix(m)
            paint.shader = sweep

            // Pure kinare pe, gol shape me
            val rect = RectF(thickness/2, thickness/2, width - thickness/2, height - thickness/2)
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
        }
    }
}
