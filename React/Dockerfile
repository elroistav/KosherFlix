# Step 1: Use an official Node.js image as the base
FROM node:16-alpine

# Step 2: Set the working directory inside the container
WORKDIR /usr/src/frontend

# Step 3: Copy the package.json and package-lock.json to the container
COPY ./NetflixProj4/package.json ./NetflixProj4/package-lock.json ./

# Step 4: Install dependencies
RUN npm install --legacy-peer-deps

# Step 5: Copy the entire project to the container
COPY ./NetflixProj4 .

# Step 6: Build the React app (optional for production)
RUN npm run build

# Step 7: Expose port 3000 for the React app
EXPOSE 3000

# Step 8: Set the default command to start the React app
CMD ["npm", "start"]
