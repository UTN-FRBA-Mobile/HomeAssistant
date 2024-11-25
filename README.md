# HomeAssistant

Create a more organized and personalized smart home with HomeAssistant.

Set up and control your Lights, TVs, Air Conditioners, and more compatible devices, all from the HomeAssistant app.

## Screenshots

### Devices
<img src="assets/devices.png" alt="devices" width="200"/> <img src="assets/add-device.png" alt="add-device" width="200"/>

### Scenes
<img src="assets/scenes.png" alt="scenes" width="200"/> <img src="assets/add-scene.png" alt="add-scene" width="200"/>

### Automations
<img src="assets/automations.png" alt="automations" width="200"/> <img src="assets/add-clock-automation.png" alt="clock-automation" width="200"/> <img src="assets/add-geolocation-automation.png" alt="geolocation-automation" width="200"/> <img src="assets/add-shake-automation.png" alt="shake-automation" width="200"/>

## How to run locally
1. Prerequisites
    - Install NodeJS

2. Run local server

```bash
cd server
```
```bash
npm i
```
```bash
node server.js
```

<img src="assets/run-local-server.png" alt="run-local-server"/>

3. Configure port redirection in android emulator (Android emulator must be already open)

```bash
nc localhost 5554 ## mac
```
or
```bash
telnet localhost 5554 ## windows
```

```bash
auth {{credentials}} ## the console will tell you where your credentials are stored
```

```bash
redir add udp:54321:54321
```

<img src="assets/configure-port-redirection.png" alt="configure-port-redirection"/>

More information about port redirection [here](https://developer.android.com/studio/run/emulator-networking#redirection).