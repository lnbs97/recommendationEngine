import {createServer} from 'http';
import app from './rest.js';
import setupWebSocket from './websocket.js';
import {stopProducer} from "./kafkaProducer.js";
import {stopConsumer} from "./kafkaConsumer.js";

const server = createServer(app);
await setupWebSocket(server);

server.listen(3000, () => {
    console.log('HTTP & WebSocket server running on http://localhost:3000');
});

process.on('SIGTERM', async () => {
    await stopProducer();
    await stopConsumer();
    process.exit(0);
});
