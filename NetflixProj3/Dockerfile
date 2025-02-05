FROM node:18

WORKDIR /usr/src/app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application code
COPY . .

# Create and set permissions for uploads directory
RUN mkdir -p /usr/src/app/uploads && \
    chmod 777 /usr/src/app/uploads

# Expose the port
EXPOSE 4000

# Serve uploads as a static directory
VOLUME ["/usr/src/app/uploads"]

# Run the app
CMD ["npm", "run", "startDocker"]