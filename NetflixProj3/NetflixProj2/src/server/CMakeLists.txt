# CMake minimum required version
cmake_minimum_required(VERSION 3.11)

# Define the project name
project(Proj1YT_YD_ES)

# Set C++17 standard for the project
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED YES)

# Include directories
include_directories(../..)
include_directories(${CMAKE_CURRENT_LIST_DIR}/../src/data_access)

# For Debug: Print current directory
message(STATUS "CMake Current List Directory: ${CMAKE_CURRENT_LIST_DIR}")

# --- Main Application Executable ---
add_executable(runApp
        App.cpp
        ICommand.h
        Patch.cpp
        InputValidator.cpp
        Commands.h
        CommandUtils.cpp
        Get.cpp
        Delete.cpp
        HelpDisplay.cpp
        data_access/DataAccess.hpp
        data_access/FileDataAccess.cpp
        RecExecuter.cpp
        MultiThreadsManager.cpp
        ThreadpoolManager.cpp
        IThreadsManager.h
        ClientHandler.cpp
        Post.cpp
        server.cpp
        ServerLogic.cpp
)

# Link pthread to the main executable
target_link_libraries(runApp pthread)

# --- Testing Executable ---
enable_testing()
find_package(GTest REQUIRED)


# Create a test executable
add_executable(runTests
        tests/FileDataAccess_tests.cpp
        tests/inputValidator_tests.cpp
        tests/HelpCommandTests.cpp
        tests/DELETCommandTests.cpp
        tests/App2_tests.cpp
        tests/PatchTests.cpp
        tests/PostTests.cpp
        tests/RecExecuter_tests.cpp
        tests/Server_tests.cpp
        Patch.cpp
        InputValidator.cpp
        Delete.cpp
        CommandUtils.cpp
        Get.cpp
        HelpDisplay.cpp
        data_access/DataAccess.hpp
        data_access/FileDataAccess.cpp
        RecExecuter.cpp
        MultiThreadsManager.cpp
        ThreadpoolManager.cpp
        IThreadsManager.h
        ClientHandler.cpp
        Post.cpp
        ServerLogic.cpp
)

# Link Google Test and pthread to the test executable
target_link_libraries(runTests
        GTest::GTest
        GTest::Main
        pthread
)

# Add a test for CTest
add_test(NAME RunTests COMMAND runTests)