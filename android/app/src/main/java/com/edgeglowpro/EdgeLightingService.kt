package com.edgeglowpro
import android.animation.ValueAnimator
import android.app.Service
import android.content.Intent
import android.graphics.*
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.WindowManager
import android.view.View

class EdgeLightingService : Service() {
    private var windowManager: WindowManager? = null
    private var overlayView: EdgeView? = null
    private val handler = Handler(Looper.getMainLooper())
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, f: Int, s: Int): Int {
        val d = intent?.getLongExtra("DURATION", 5000L) ?: 5000L
        show(); handler.postDelayed({ remove(); stopSelf() }, d)
        return START_NOT_STICKY
    }
    private fun show() {
        remove(); windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        overlayView = EdgeView(this)
        val p = WindowManager.LayoutParams(-1, -1, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, PixelFormat.TRANSLUCENT)
        windowManager?.addView(overlayView, p)
    }
    private fun remove() { try{ overlayView?.let{ windowManager?.removeView(it) } } catch(e:Exception){}; overlayView=null }
    class EdgeView(c: android.content.Context) : View(c) {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply{ style=Paint.Style.STROKE; strokeWidth=18f }
        private var angle=0f
        init{ ValueAnimator.ofFloat(0f,360f).apply{ duration=2000; repeatCount=ValueAnimator.INFINITE; addUpdateListener{ angle=it.animatedValue as Float; invalidate() }; start() } }
        override fun onDraw(canvas: Canvas) {
            val shader = SweepGradient(width/2f, height/2f, intArrayOf(Color.MAGENTA, Color.CYAN, Color.YELLOW, Color.MAGENTA), null)
            val m = Matrix(); m.setRotate(angle, width/2f, height/2f); shader.setLocalMatrix(m); paint.shader=shader
            canvas.drawRoundRect(10f,10f,width-10f.toFloat(),height-10f.toFloat(),40f,40f,paint)
        }
    }
}
