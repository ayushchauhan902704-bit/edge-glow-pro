package com.edgeglowpro
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
class NotificationListener : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val i = Intent(this, EdgeLightingService::class.java); i.putExtra("DURATION", 5000L); startService(i)
    }
}
