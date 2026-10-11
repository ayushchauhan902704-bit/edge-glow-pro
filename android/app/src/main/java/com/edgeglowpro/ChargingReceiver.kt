package com.edgeglowpro
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
class ChargingReceiver : BroadcastReceiver() {
    override fun onReceive(c: Context?, i: Intent?) {
        if(i?.action==Intent.ACTION_POWER_CONNECTED){ val s=Intent(c, EdgeLightingService::class.java); s.putExtra("DURATION", 5000L); c?.startService(s) }
    }
}
