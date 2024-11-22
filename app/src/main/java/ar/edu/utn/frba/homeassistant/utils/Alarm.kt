package ar.edu.utn.frba.homeassistant.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import ar.edu.utn.frba.homeassistant.data.model.ClockAutomation
import ar.edu.utn.frba.homeassistant.utils.receivers.AlarmReceiver
import java.util.Calendar

fun setAlarm(
    context: Context,
    id: Long,
    clockAutomation: ClockAutomation,
    deviceIds: LongArray
) {
    val intent = Intent(context, AlarmReceiver::class.java)
    intent.putExtra("automationId", id)
    intent.putExtra("deviceIds", deviceIds)
    intent.putExtra("shouldTurnOn", clockAutomation.shouldTurnOn)

    val alarmManager = context.getSystemService(AlarmManager::class.java)
    val time = clockAutomation.time
    val hour = time.split(":")[0].toInt()
    val minute = time.split(":")[1].toInt()

    val days = mapOf(
        "Monday" to clockAutomation.monday,
        "Tuesday" to clockAutomation.tuesday,
        "Wednesday" to clockAutomation.wednesday,
        "Thursday" to clockAutomation.thursday,
        "Friday" to clockAutomation.friday,
        "Saturday" to clockAutomation.saturday,
        "Sunday" to clockAutomation.sunday
    )

    for ((day, shouldExecute) in days) {
        if (shouldExecute) {
            val dayOfWeek = when (day) {
                "Monday" -> Calendar.MONDAY
                "Tuesday" -> Calendar.TUESDAY
                "Wednesday" -> Calendar.WEDNESDAY
                "Thursday" -> Calendar.THURSDAY
                "Friday" -> Calendar.FRIDAY
                "Saturday" -> Calendar.SATURDAY
                "Sunday" -> Calendar.SUNDAY
                else -> -1
            }
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            if (dayOfWeek != -1) {
                calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)
                println(calendar.time)
                println(calendar.timeInMillis)
                println(calendar.timeZone)
                if (calendar.timeInMillis < System.currentTimeMillis()) {
                    calendar.add(Calendar.WEEK_OF_YEAR, 1)
                    println("TEST")
                }
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    1000 * 60 * 60 * 24 * 7,
                    PendingIntent.getBroadcast(
                        context,
                        "0$dayOfWeek$id".toInt(),
                        intent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )
            }
        }
    }
}

fun cancelAlarm(context: Context, id: Long, clockAutomation: ClockAutomation, deviceIds: LongArray) {
    val alarmManager = context.getSystemService(AlarmManager::class.java)
    val intent = Intent(context, AlarmReceiver::class.java)
    intent.putExtra("automationId", id)
    intent.putExtra("deviceIds", deviceIds)
    intent.putExtra("shouldTurnOn", clockAutomation.shouldTurnOn)
    val days = mapOf(
        "Monday" to clockAutomation.monday,
        "Tuesday" to clockAutomation.tuesday,
        "Wednesday" to clockAutomation.wednesday,
        "Thursday" to clockAutomation.thursday,
        "Friday" to clockAutomation.friday,
        "Saturday" to clockAutomation.saturday,
        "Sunday" to clockAutomation.sunday
    )

    for ((day, shouldExecute) in days) {
        if (shouldExecute) {
            val dayOfWeek = when (day) {
                "Monday" -> Calendar.MONDAY
                "Tuesday" -> Calendar.TUESDAY
                "Wednesday" -> Calendar.WEDNESDAY
                "Thursday" -> Calendar.THURSDAY
                "Friday" -> Calendar.FRIDAY
                "Saturday" -> Calendar.SATURDAY
                "Sunday" -> Calendar.SUNDAY
                else -> -1
            }
            if (dayOfWeek != -1) {
                alarmManager.cancel(
                    PendingIntent.getBroadcast(
                        context,
                        "0$dayOfWeek$id".toInt(),
                        intent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )
            }
        }
    }
}