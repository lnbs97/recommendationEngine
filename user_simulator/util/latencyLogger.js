import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const logFilePath = path.join(__dirname, 'latency_logs.csv');

if (!fs.existsSync(logFilePath)) {
    fs.writeFileSync(logFilePath, 'userId,latency,timestamp\n');
}

export const logLatencyToCSV = (userId, latency) => {
    const logEntry = `${userId},${latency},${new Date().toISOString()}\n`;
    fs.appendFile(logFilePath, logEntry, (err) => {
        if (err) {
            console.error('Error writing latency to CSV:', err);
        } else {
            console.log(`Logged latency for user ${userId}: ${latency}ms`);
        }
    });
};
