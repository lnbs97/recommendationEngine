import crypto from "crypto";
import {cities} from "./data/cities.js";
import {SWIPES_PER_USER, SWIPES_PER_CITY, TOTAL_CITIES} from './util/config.js';


class User {
    constructor(id, ws, cityIndex, interests) {
        this.id = id; // Unique user ID
        this.ws = ws; // WebSocket connection object
        this.location = cities[cityIndex]; // Current location of the user
        this.swipeCount = 0; // Count of swipes performed by the user
        this.interests = interests;
        this.cityIndex = cityIndex;
    }

    sendLocation() {
        const locationData = {
            eventTime: Date.now(),
            eventId: crypto.randomUUID(),
            userId: this.id,
            lat: this.location.latitude,
            lon: this.location.longitude
        };
        this.ws.send(JSON.stringify({
            type: 'location',
            content: locationData
        }));
    }

    swipe(recommendation) {
        const swipeData = {
            eventTime: Date.now(),
            eventId: crypto.randomUUID(),
            userId: this.id,
            attractionId: recommendation.id,
            attractionTags: recommendation.tags,
            swipeType: this.getSwipeType(recommendation),
        };
        this.ws.send(JSON.stringify({
            type: "swipe",
            content: swipeData
        }))
        this.swipeCount++;
        console.log(`User ${this.id} swiped (${swipeData.swipeType}) on recommendation: ${recommendation}`);

        // Check if user should exit
        if (this.swipeCount >= SWIPES_PER_USER) {
            this.disconnect();
            return;
        }

        // Check if user should change location
        if (this.swipeCount % SWIPES_PER_CITY === 0) {
            this.changeLocation();
        }
    }

    getSwipeType(attraction) {
        // Extract the attraction tag
        const [key, value] = Object.entries(attraction.tags)[0];
        const tag = `${key}:${value}`;

        // Get the probability for the matching tag
        const probability = parseFloat(this.interests[tag]) || 0.5; // Default to 0.5 if tag is not found

        // Flip a coin
        const randomValue = Math.random(); // Random number between 0 and 1
        const liked = randomValue <= probability;

        return liked ? "like" : "dislike";
    }

    changeLocation() {
        this.cityIndex = (this.cityIndex + 1) % TOTAL_CITIES;
        this.location = cities[this.cityIndex];
        console.log(`User ${this.id} changed location to: ${JSON.stringify(this.location)}`);
        this.sendLocation();
    }

    disconnect() {
        if (this.ws.readyState === this.ws.OPEN) {
            this.ws.close();
            console.log(`User ${this.id} disconnected after ${this.swipeCount} swipes.`);
        }
    }
}

export default User;
