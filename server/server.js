const dgram = require('dgram');
const express = require('express');
const http = require('http');
const WebSocket = require('ws');

const app = express();
const server = http.createServer(app);
const wss = new WebSocket.Server({ server });
const udpServer = dgram.createSocket('udp4');

const devices = [
  { id: 111111, name: 'Kitchen Air Conditioner', type: 'Air Conditioner', state: false, image: 'air-conditioner.png' },
  { id: 222222, name: 'Kitchen Light', type: 'Light', state: false, image: 'light-bulb.png' },
  { id: 333333, name: 'Bedroom Television', type: 'TV', state: false, image: 'television.png' },
];

const PORT = 54321;
const LISTEN_PORT = 12345;
const LOCALHOST_IP = '127.0.0.1';

// Serve static files, including images and HTML
app.use(express.static(__dirname));

// Endpoint to fetch devices
app.get('/devices', (req, res) => {
  res.json(devices);
});

// WebSocket for real-time updates from FE
wss.on('connection', (ws) => {
  ws.on('message', (message) => {
    const { deviceId, state } = JSON.parse(message);
    const device = devices.find((d) => d.id === parseInt(deviceId));
    if (device) {
      device.state = state;

      // Send UDP packet
      const udpMessage = Buffer.from(`${deviceId}:state:${state ? 'on' : 'off'}`);
      udpServer.send(udpMessage, PORT, LOCALHOST_IP, (err) => {
        if (err) console.error('UDP error:', err);
      });

      // Broadcast the updated state to all clients
      wss.clients.forEach((client) => {
        if (client.readyState === WebSocket.OPEN) {
          client.send(JSON.stringify({ deviceId, state }));
        }
      });
    }
  });
});

// Listen for UDP messages
udpServer.on('message', (msg) => {
  const [deviceId, action, state] = msg.toString().split(':');
  if (action === 'getState'){
    devices.forEach(device => {
      const udpMessage = Buffer.from(`${device.id}:state:${device.state ? 'on' : 'off'}`);
      udpServer.send(udpMessage, PORT, LOCALHOST_IP, (err) => {
        if (err) console.error('UDP error:', err);
      });
    })
  }
  if (action === 'toggle') {
    const device = devices.find((d) => d.id === parseInt(deviceId));
    if (device) {
      device.state = state === 'on';

      // Broadcast the updated state to all clients
      wss.clients.forEach((client) => {
        if (client.readyState === WebSocket.OPEN) {
          client.send(JSON.stringify({ deviceId, state: device.state }));
        }
      });
    }
  }
});

udpServer.bind(LISTEN_PORT);

// Start the server
server.listen(3000, () => {
  console.log('Server running at http://localhost:3000');
});
