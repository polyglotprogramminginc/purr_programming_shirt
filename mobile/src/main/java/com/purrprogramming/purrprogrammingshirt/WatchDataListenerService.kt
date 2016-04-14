package com.purrprogramming.purrprogrammingshirt

import android.content.Intent
import android.util.Log

import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class WatchDataListenerService : WearableListenerService() {
    val SERVICE_NOTIFICATION = "com.polyglotprogramminginc.purrprogramming.SERVICE_NOTIFICATION"

    override fun onMessageReceived(messageEvent: MessageEvent?) {
        Log.i("message received", messageEvent!!.path)
        super.onMessageReceived(messageEvent)
        val i: Intent = Intent(SERVICE_NOTIFICATION)
        i.putExtra("command", messageEvent!!.path)
        sendBroadcast(i)
    }

}
