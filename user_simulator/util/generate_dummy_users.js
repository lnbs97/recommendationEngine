import 'dotenv/config';
import { fakerDE as faker } from '@faker-js/faker';
import pkg from 'pg';
import {categories} from "../data/categories.js";

const {Client} = pkg;

const client = new Client({
    host: process.env.POSTGRES_HOST,
    user: process.env.POSTGRES_USER,
    database: process.env.POSTGRES_DB,
    password: process.env.POSTGRES_PASSWORD,
    port: 5432,
});

(async () => {
    try {
        // Connect to PostgreSQL
        await client.connect();

        const randomInterests = [];
        // Generate 10 user archetypes
        for (let i = 0; i < 10; i++){
            randomInterests[i] = getRandomInterests()
        }

        // Generate and insert 1000 fake users
        for (let i = 0; i < 100000; i++) {
            const firstName = faker.person.firstName();
            const lastName = faker.person.lastName();
            const interests = randomInterests[i%10]

            await client.query(
                `INSERT INTO users (first_name, last_name, interests) VALUES ($1, $2, $3)`,
                [firstName, lastName, interests]
            );
        }

        console.log('Fake users inserted successfully!');

    } catch (err) {
        console.error('Error inserting data:', err.stack);
    } finally {
        await client.end();
    }
})();

function getRandomInterests() {
    let interests = {};
    categories.forEach(category => {
        interests[category] = Math.random().toFixed(2);
    });
    return interests;
}