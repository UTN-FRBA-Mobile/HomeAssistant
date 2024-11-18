package ar.edu.utn.frba.homeassistant

import android.app.Application
import ar.edu.utn.frba.homeassistant.network.UdpService
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application(){
    @Inject
    lateinit var udpService: UdpService // Injected just to start the udp server on app start
}