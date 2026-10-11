package com.edgeglowpro
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
class CallReceiver : BroadcastReceiver() {
    override fun onReceive(c: Context?, i: Intent?) {
        val state = i?.getStringExtra(TelephonyManager.EXTRA_STATE)
        if(state==TelephonyManager.EXTRA_STATE_RINGING){ val s=Intent(c, EdgeLightingService::class.java); s.putExtra("DURATION", 30000L); c?.startService(s) }
        if(state==TelephonyManager.EXTRA_STATE_IDLE){ val s=Intent(c, EdgeLightingService::class.java); s.putExtra("DURATION", 5000L); c?.startService(s) }
    }
}
