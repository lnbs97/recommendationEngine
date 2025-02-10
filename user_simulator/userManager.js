import { WebSocket } from 'ws';
import User from './user.js';
import {TOTAL_USERS, TOTAL_CITIES, API_HOST} from './util/config.js';

export const createUsers = async (getUserData) => {
    const users = [];
    const userData = await getUserData();

    await Promise.all(
        userData.slice(0, TOTAL_USERS).map(async (userData, i) => {
            const userId = userData.id;
            const interests = userData.interests;

            const ws = new WebSocket(`ws://${API_HOST}:3000`);
            await new Promise((resolve, reject) => {
                ws.on('open', () => {
                    console.log(`User ${userId} connected`);
                    ws.send(JSON.stringify({ type: 'init', id: userId }));
                    resolve();
                });
                ws.on('error', (error) => {
                    console.error(`WebSocket error for user ${userId}:`, error);
                    reject(error);
                });
            });

            const cityIndex = Math.floor((i / TOTAL_USERS) * TOTAL_CITIES);
            const user = new User(userId, ws, cityIndex, interests);
            users.push(user);
        })
    );
    return users;
};
