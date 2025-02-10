import { WebSocketServer } from 'ws';
import {startConsumer} from "./kafkaConsumer.js";
import {sendMessageToKafka, startProducer} from "./kafkaProducer.js";

const wsClients = new Map();

async function setupWebSocket(server) {
    await startProducer();

    const wss = new WebSocketServer({ server });

    startConsumer("recommendations", ({ key, value }) => {
        try {
            const message = JSON.parse(value);
            const { userId, attractionData } = message;

            let wsClient = wsClients.get(userId);
            if (wsClient) {
                wsClient.send(JSON.stringify({
                    type: "recommendation",
                    content: attractionData
                }));
            }
        } catch (error) {
            console.error("Error processing message:", error);
        }
    });

    wss.on('connection', (ws) => {
        let userId;

        ws.on('message', (message) => {
            const data = JSON.parse(message);

            if (data.type === 'init') {
                userId = data.id;
                wsClients.set(userId, ws);
                console.log(`Client ${userId} connected`);
            } else if (data.type === 'swipe') {
                sendMessageToKafka("swipes", data.content);
            } else if (data.type === 'location') {
                sendMessageToKafka("locations", data.content);
            }
        });

        ws.on('close', () => {
            if (userId) {
                wsClients.delete(userId);
                console.log(`Client ${userId} disconnected`);
            }
        });

        ws.on('error', (error) => {
            console.error(`Error for user ${userId}: ${error.message}`);
        });
    });

    return wss;
}

export default setupWebSocket;
