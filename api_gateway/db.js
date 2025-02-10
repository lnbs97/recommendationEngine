import 'dotenv/config';
import pkg from 'pg';
const { Client } = pkg;

const client = new Client({
    host: process.env.POSTGRES_HOST,
    user: process.env.POSTGRES_USER,
    database: process.env.POSTGRES_DB,
    password: process.env.POSTGRES_PASSWORD,
    port: 5432,
});

await client.connect();
export default client;
