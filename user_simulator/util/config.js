import 'dotenv/config';

// Connection parameters
export const API_HOST = process.env.API_HOST

// Simulation parameters
export const TOTAL_USERS = process.env.TOTAL_USERS;
export const TOTAL_CITIES = process.env.TOTAL_CITIES;

export const SWIPES_PER_USER = process.env.SWIPES_PER_USER;
export const SWIPES_PER_CITY = process.env.SWIPES_PER_CITY;

export const ALLOW_FAST_SEND = process.env.ALLOW_FAST_SEND === "true";