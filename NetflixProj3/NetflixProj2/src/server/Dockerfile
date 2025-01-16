FROM gcc:latest

# Install dependencies for building the server
RUN apt-get update && apt-get install -y cmake libgtest-dev

# Set the working directory to the project root (where CMakeLists.txt is located)
WORKDIR /usr/src/myapp

# Copy the entire project directory into the container
COPY . .

# Copy the src directory into the container
#COPY ../src /usr/src/myapp/src

# Build the server
RUN mkdir build && cd build && cmake .. && make

# Expose port 5555 for the server
EXPOSE 5555

# Default entry point allows passing arguments
ENTRYPOINT ["./build/runApp"]

# Set default port to 5555 if none is provided
CMD ["5555"]
