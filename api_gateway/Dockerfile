# Use the official Node.js image as a base image
FROM node:22

# Set the working directory in the container
WORKDIR .

# Copy package.json and package-lock.json to the container
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application code to the container
COPY . .

# Expose the port the app will run on
EXPOSE 3000

# Run the application
CMD ["node", "server.js"]
