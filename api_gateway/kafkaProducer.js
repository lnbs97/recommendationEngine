import 'dotenv/config';
import {Kafka} from 'kafkajs'

const kafka = new Kafka({
    clientId: 'express',
    brokers: [process.env.KAFKA_BROKER],
})

const producer = kafka.producer()

export async function sendMessageToKafka(topic, message) {
    try {
        await producer.send({
            topic,
            messages: [
                {value: JSON.stringify(message)},
            ],
        });
    } catch (error) {
        console.error("Error sending message to Kafka:", error)
    }
}

export async function startProducer() {
    await producer.connect()
    console.log("Kafka Producer connected")
}

export async function stopProducer() {
    await producer.disconnect()
    console.log("Kafka Producer disconnected")
}