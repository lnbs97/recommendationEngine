import 'dotenv/config';
import {Kafka} from 'kafkajs';

const kafka = new Kafka({
    clientId: 'express',
    brokers: [process.env.KAFKA_BROKER],
});

// console.log('Kafka Broker:', process.env.KAFKA_BROKER);

const consumer = kafka.consumer({groupId: 'express-group'});

export async function startConsumer(topic, onMessage) {
    try {
        await consumer.connect();
        console.log('Kafka Consumer connected');

        await consumer.subscribe({topic, fromBeginning: true});
        console.log(`Subscribed to topic ${topic}`);

        await consumer.run({
            eachMessage: async ({topic, partition, message}) => {
                const key = message.key ? message.key.toString() : null;
                const value = message.value ? message.value.toString() : null;
                const offset = message.offset;

                onMessage({topic, partition, key, value, offset});
            },
        });
    } catch (error) {
        console.error('Error starting Kafka Consumer:', error);
    }
}

export async function stopConsumer() {
    try {
        await consumer.disconnect();
        console.log('Kafka Consumer disconnected');
    } catch (error) {
        console.error('Error stopping Kafka Consumer:', error);
    }
}
