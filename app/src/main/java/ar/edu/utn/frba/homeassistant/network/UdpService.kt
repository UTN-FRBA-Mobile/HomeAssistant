package ar.edu.utn.frba.homeassistant.network

import ar.edu.utn.frba.homeassistant.data.repository.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import javax.inject.Inject
import javax.inject.Singleton


private const val PORT = 12345
private const val LISTEN_PORT = 54321

@Singleton
class UdpService @Inject constructor(private val repository: AppRepository) {

    init {
        startListening()
        // This will tell the server to send the current devices state
        CoroutineScope(Dispatchers.IO).launch {
            sendUdpMessage(-1, "getState")
        }
    }

    fun sendUdpMessage(deviceId: Long, message: String) {
        val socket = DatagramSocket()
        socket.use {
            val broadcastIP =
                InetAddress.getByName("10.0.2.2") // 10.0.2.2 is an alias to your host loopback interface https://developer.android.com/studio/run/emulator-networking#networkaddresses
            val data = "$deviceId:$message".toByteArray()
            val packet = DatagramPacket(data, data.size, broadcastIP, PORT)
            socket.send(packet)
        }
    }

    private fun startListening() {
        // To make this work with the emulator you have to open a terminal and configure the redirection to the emulator
        // https://developer.android.com/studio/run/emulator-networking#redirection
        // nc localhost 5554 / telnet localhost 5554 -> input credentials -> redir add udp:54321:54321
        CoroutineScope(Dispatchers.IO).launch {
            val socket = DatagramSocket(LISTEN_PORT)
            val buffer = ByteArray(1024)
            val packet = DatagramPacket(buffer, buffer.size)

            while (true) {
                socket.receive(packet)
                val message = String(packet.data, 0, packet.length)
                processMessage(message)
            }
        }
    }

    private suspend fun processMessage(message: String) {
        val (deviceId, action, payload) = message.split(":")
        if (action == "state") {
            val device = repository.getDeviceById(deviceId.toLong())
            if (device != null) {
                repository.updateDevice(device.copy(isOn = payload == "on"))
            }
        }
    }
}
