package ar.edu.utn.frba.homeassistant.utils.Receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationCompat
import ar.edu.utn.frba.homeassistant.R

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val id = intent.getLongExtra("automationId", -1)
            println("Automation with id: $id")
            //Toast.makeText(context.applicationContext, "Automation with id: $id", Toast.LENGTH_SHORT).show()
            context.let { ctx ->
                val notificationManager =
                    ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val builder = NotificationCompat.Builder(ctx, "ALARM_AUTOMATIONS")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Home Assistant triggered an automation!")
                    .setContentText("Automation with id $id was triggered")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                notificationManager.notify(1, builder.build())
            }
        }

    }


}