package ar.edu.utn.frba.homeassistant.utils.Receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import ar.edu.utn.frba.homeassistant.GLOBAL_TAG
import ar.edu.utn.frba.homeassistant.R
import ar.edu.utn.frba.homeassistant.network.UdpService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val ALARM_RECEIVER_TAG = "$GLOBAL_TAG#ALARM_BROADCAST_RECEIVER"

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(ALARM_RECEIVER_TAG, "[onReceive]: Geofence broadcast received")
        if (context == null || intent == null){
            Log.e(ALARM_RECEIVER_TAG, "[onReceive]: Context or intent is null")
            return
        }
        val id = intent.getLongExtra("automationId", -1)
        Log.d(ALARM_RECEIVER_TAG, "[onReceive]: Alarm broadcast received for automation with id: $id")
//        context.let { ctx ->
//            val notificationManager =
//                ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            val builder = NotificationCompat.Builder(ctx, "ALARM_AUTOMATIONS")
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                .setContentTitle("Home Assistant triggered an automation!")
//                .setContentText("Automation with id $id was triggered")
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//            notificationManager.notify(1, builder.build())


        //}

//        CoroutineScope(Dispatchers.IO).launch {
//            UdpService.sendUdpMessage(it, "toggle:off")
//        }

    }


}