import 'dotenv/config';
import { createUsers } from './userManager.js';
import { logLatencyToCSV } from './util/latencyLogger.js';
import { TOTAL_USERS, ALLOW_FAST_SEND } from './util/config.js';

let users = await createUsers(fetchUserData);
console.log(`${users.length} users were created`);

users.forEach((user) => {
    user.sendLocation();

    let lastSwipeTime = Date.now();

    user.ws.on('message', (message) => {
        const data = JSON.parse(message);

        if (data.type === 'recommendation') {
            const currentTime = Date.now();
            const timeSinceLastSwipe = currentTime - lastSwipeTime;

            if (user.id === 1) {
                console.log("System latency: " + timeSinceLastSwipe);
                logLatencyToCSV(user.id, timeSinceLastSwipe);
            }

            if (timeSinceLastSwipe >= 1000 || ALLOW_FAST_SEND) {
                user.swipe(data.content);
                lastSwipeTime = currentTime;
            } else {
                const waitTime = 1000 - timeSinceLastSwipe;
                setTimeout(() => {
                    user.swipe(data.content);
                    lastSwipeTime = Date.now();
                }, waitTime);
                console.log(`User ${user.id} waiting for ${waitTime}ms before next swipe.`);
            }
        }
    });
});

async function fetchUserData() {
    try {
        const response = await fetch(`http://${process.env.API_HOST}:3000/users?count=${TOTAL_USERS}`);
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error("Failed to fetch users:", error);
        throw error;
    }
}
