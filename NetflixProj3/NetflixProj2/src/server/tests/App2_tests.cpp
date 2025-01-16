#include <gtest/gtest.h>
#include <sstream>
#include <iostream>
#include <fstream>
#include <filesystem>
#include "../App.h"
#include "../data_access/FileDataAccess.hpp"
#include "../InputValidator.h"
#include <thread>
#include <chrono>
using namespace std;
namespace fs = std::filesystem;
using namespace std;


// testing post with a few movies
TEST(app, POST_addMultipleIds_OneUser) {
    CommandUtils::initCommandUtils();

    if (!fs::exists("data/Users")) {
        fs::create_directory("data/Users");
        ASSERT_TRUE(fs::exists("data/Users")) << "Failed to create data/Users directory";
    }
    if (fs::exists("data/Users/user_1.txt")) {
        fs::remove("data/Users/user_1.txt");
    }

    InputValidator validator;

    // define input for POST command
    string inputStr = "POST 1 10 20 30 40 50 60";
    string command = validator.getCommand(inputStr);
    int userId = validator.getUserId(inputStr);

    ASSERT_EQ(command, "POST") << "Command should be 'POST'";
    ASSERT_EQ(userId, 1) << "User ID should be 1";

    // check if the input is valid
    EXPECT_EQ(InputValidator::checkInputValidity("POST 1 10 20 30 40 50 60"), "201 Created");


    // check file content
    ifstream file("data/Users/user_1.txt");
    ASSERT_TRUE(file.is_open()) << "Failed to open file user_1.txt: ";

    string out;
    string line;
    while (getline(file, line)) {
        out.append(line);
        out.append("\n");
    }
    //end
    file.close();
    fs::remove("data/Users/user_1.txt");
    ASSERT_EQ(out, "10\n20\n30\n40\n50\n60\n") << "expected: 10\n20\n30\n40\n50\n60\n, got: "<< out;

    if (fs::exists("data/Users/user_1.txt")) {
        fs::remove("data/Users/user_1.txt");
    }
}

// testing post with multiple movies and whitespaces in the input
TEST(app, POST_addMultipleIds_OneUser_WithWhitespaces) {
    CommandUtils::initCommandUtils();

    if (!fs::exists("data/Users")) {
        fs::create_directory("data/Users");
        ASSERT_TRUE(fs::exists("data/Users")) << "Failed to create data/Users directory";
    }
    if (fs::exists("data/Users/user_1.txt")) {
        fs::remove("data/Users/user_1.txt");
    }

    
    InputValidator validator;

    // define input for POST command
    string inputStr = "POST 1       10 20   30 40    50 60";
    string command = validator.getCommand(inputStr);
    int userId = validator.getUserId(inputStr);

    ASSERT_EQ(command, "POST") << "Command should be 'POST'";
    ASSERT_EQ(userId, 1) << "User ID should be 1";
    
    EXPECT_EQ(InputValidator::checkInputValidity("POST 1       10 20   30 40    50 60"), "201 Created");


    // check file content
    ifstream file("data/Users/user_1.txt");
    ASSERT_TRUE(file.is_open()) << "Failed to open file user_1.txt: ";

    string out;
    string line;
    while (getline(file, line)) {
        out.append(line);
        out.append("\n");
    }
    //end
    file.close();
    fs::remove("data/Users/user_1.txt");
    ASSERT_EQ(out, "10\n20\n30\n40\n50\n60\n") << "expected: 10\n20\n30\n40\n50\n60\n, got: "<< out;

    if (fs::exists("data/Users/user_1.txt")) {
        fs::remove("data/Users/user_1.txt");
    }
}


// testing patching a movie that the user already has
TEST(app, PATCH_addMultipleIds_OneUser) {
    CommandUtils::initCommandUtils();

    if (!fs::exists("data/Users")) {
        fs::create_directory("data/Users");
        ASSERT_TRUE(fs::exists("data/Users")) << "Failed to create data/Users directory";
    }
    if (!fs::exists("data/Users/user_1.txt")) {
        ofstream file("data/Users/user_1.txt");
        file << "10\n20\n30\n";
        file.close();
    }
    
    InputValidator validator;

    // define input for PATCH command
    string inputStr = "PATCH 1 10 20 30 40 50 60";
    string command = validator.getCommand(inputStr);
    int userId = validator.getUserId(inputStr);

    ASSERT_EQ(command, "PATCH") << "Command should be 'PATCH'";
    ASSERT_EQ(userId, 1) << "User ID should be 1";
    
    EXPECT_EQ(InputValidator::checkInputValidity("PATCH 1 10 20 30 40 50 60"), "204 No Content");

    // check file content
    ifstream file("data/Users/user_1.txt");
    ASSERT_TRUE(file.is_open()) << "Failed to open file user_1.txt: ";

    string out;
    string line;
    while (getline(file, line)) {
        out.append(line);
        out.append("\n");
    }
    //end
    file.close();
    fs::remove("data/Users/user_1.txt");
    ASSERT_EQ(out, "10\n20\n30\n40\n50\n60\n") << "expected: 10\n20\n30\n40\n50\n60\n, got: "<< out;

    if (fs::exists("data/Users/user_1.txt")) {
        fs::remove("data/Users/user_1.txt");
    }
}

// testing patch with multiple movies and whitespaces in the input
TEST(app, PATCH_addMultipleIds_OneUser_WithWhitespaces) {
    CommandUtils::initCommandUtils();

    if (!fs::exists("data/Users")) {
        fs::create_directory("data/Users");
        ASSERT_TRUE(fs::exists("data/Users")) << "Failed to create data/Users directory";
    }
    if (!fs::exists("data/Users/user_1.txt")) {
        ofstream file("data/Users/user_1.txt");
        file << "10\n20\n30\n";
        file.close();
    }
    
    InputValidator validator;

    // define input for POST command
    string inputStr = "PATCH 1 10      20 30 40 50    60";
    string command = validator.getCommand(inputStr);
    int userId = validator.getUserId(inputStr);

    ASSERT_EQ(command, "PATCH") << "Command should be 'POST'";
    ASSERT_EQ(userId, 1) << "User ID should be 1";
    EXPECT_EQ(InputValidator::checkInputValidity("PATCH 1 10      20 30 40 50    60"), "204 No Content");

    // check file content
    ifstream file("data/Users/user_1.txt");
    ASSERT_TRUE(file.is_open()) << "Failed to open file user_1.txt: ";

    string out;
    string line;
    while (getline(file, line)) {
        out.append(line);
        out.append("\n");
    }
    //end
    file.close();
    fs::remove("data/Users/user_1.txt");
    ASSERT_EQ(out, "10\n20\n30\n40\n50\n60\n") << "expected: 10\n20\n30\n40\n50\n60\n, got: "<< out;

    if (fs::exists("data/Users/user_1.txt")) {
        fs::remove("data/Users/user_1.txt");
    }
}

// testing patching a movie that the user already has
TEST(app, PATCH_addMultipleIds_OneUser2) {
    CommandUtils::initCommandUtils();

    if (!fs::exists("data/Users")) {
        fs::create_directory("data/Users");
        ASSERT_TRUE(fs::exists("data/Users")) << "Failed to create data/Users directory";
    }
    if (!fs::exists("data/Users/user_1.txt")) {
        ofstream file("data/Users/user_1.txt");
        file << "10\n20\n30\n";
        file.close();
    }
    
    InputValidator validator;

    // define input for POST command
    string inputStr = "PATCH 1 10 20";
    string command = validator.getCommand(inputStr);
    int userId = validator.getUserId(inputStr);

    ASSERT_EQ(command, "PATCH") << "Command should be 'PATCH'";
    ASSERT_EQ(userId, 1) << "User ID should be 1";
    
    EXPECT_EQ(InputValidator::checkInputValidity("PATCH 1 10 20"), "204 No Content");


    // check file content
    ifstream file("data/Users/user_1.txt");
    ASSERT_TRUE(file.is_open()) << "Failed to open file user_1.txt: ";

    string out;
    string line;
    while (getline(file, line)) {
        out.append(line);
        out.append("\n");
    }
    //end
    file.close();
    fs::remove("data/Users/user_1.txt");
    ASSERT_EQ(out, "10\n20\n30\n") << "expected: 10\n20\n30\n, got: "<< out;

    if (fs::exists("data/Users/user_1.txt")) {
        fs::remove("data/Users/user_1.txt");
    }
}

// testing inputs that should result in 400 error

// testing posting the same user twice in a row - should result in 400 error
TEST(app, POST_illegalInput1) {

    if (!fs::exists("data/Users")) {
        fs::create_directory("data/Users");
        ASSERT_TRUE(fs::exists("data/Users")) << "Failed to create data/Users directory";
    }
    if (fs::exists("data/Users/user_1.txt")) {
        fs::remove("data/Users/user_1.txt");
    }

    // define input for POST command
    string inputStr = "POST 1 10 20 30 40 50 60\nPOST 1 10 20 30 40 50 60\n";
    EXPECT_EQ(InputValidator::checkInputValidity(inputStr), "400 Bad Request");
}

// testing posting an illegal argument - should result in 400 error
TEST(app, POST_illegalInput2) {

    if (!fs::exists("data/Users")) {
        fs::create_directory("data/Users");
        ASSERT_TRUE(fs::exists("data/Users")) << "Failed to create data/Users directory";
    }
    if (fs::exists("data/Users/user_1.txt")) {
        fs::remove("data/Users/user_1.txt");
    }
    // define input for POST command
    string inputStr = "POST 1 10 20 30 40 -30";
    EXPECT_EQ(InputValidator::checkInputValidity(inputStr), "400 Bad Request");

}


// testing posting an illegal argument - should result in 400 error
TEST(app, POST_BEFORE_PATCH_illegalInput3) {

    if (!fs::exists("data/Users")) {
        fs::create_directory("data/Users");
        ASSERT_TRUE(fs::exists("data/Users")) << "Failed to create data/Users directory";
    }
    if (fs::exists("data/Users/user_1.txt")) {
        fs::remove("data/Users/user_1.txt");
    }
    // define input for POST command
    string inputStr = "POST 1 10 20 30 a40";
    EXPECT_EQ(InputValidator::checkInputValidity(inputStr), "400 Bad Request");
}







