package ar.edu.utn.frba.homeassistant.utils.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import ar.edu.utn.frba.homeassistant.GLOBAL_TAG
import ar.edu.utn.frba.homeassistant.data.repository.AppRepository
import ar.edu.utn.frba.homeassistant.network.UdpService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

const val ALARM_RECEIVER_TAG = "$GLOBAL_TAG#ALARM_BROADCAST_RECEIVER"

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var appRepository: AppRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(ALARM_RECEIVER_TAG, "[onReceive]: Alarm broadcast received")
        if (context == null || intent == null){
            Log.e(ALARM_RECEIVER_TAG, "[onReceive]: Context or intent is null")
            return
        }
        val id = intent.getLongExtra("automationId", -1)
        val shouldTurnOn = intent.getBooleanExtra("shouldTurnOn", true)
        val deviceIds = intent.getLongArrayExtra("deviceIds")
        Log.d(ALARM_RECEIVER_TAG, "[onReceive]: Alarm broadcast received for automation with id: $id")
        deviceIds?.forEach {
            CoroutineScope(Dispatchers.IO).launch {
                if(shouldTurnOn){
                    Log.d(ALARM_RECEIVER_TAG, "[onReceive]: Sending UDP message to device $it - toggle:on")
                    UdpService.sendUdpMessage(it, "toggle:on")
                    appRepository.updateIsOn(it, true)
                } else {
                    Log.d(ALARM_RECEIVER_TAG, "[onReceive]: Sending UDP message to device $it - toggle:off")
                    UdpService.sendUdpMessage(it, "toggle:off")
                    appRepository.updateIsOn(it, false)
                }
            }
        }

    }


}