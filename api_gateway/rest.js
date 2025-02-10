import express from 'express';
import client from './db.js';

const app = express();
app.use(express.json());

app.get("/health", (req, res) => {
    res.status(200).json({ status: "ok" });
});

app.get("/users", async (req, res) => {
    try {
        const count = parseInt(req.query.count, 10) || 777;
        const query = `SELECT * FROM users ORDER BY id ASC LIMIT $1`;
        const usersResult = await client.query(query, [count]);
        res.json(usersResult.rows);
    } catch (error) {
        console.error("Error fetching users:", error);
        res.status(500).json({ error: "Internal Server Error" });
    }
});

export default app;
