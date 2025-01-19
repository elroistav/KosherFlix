//
// Created by Yoni Trachtenberg on 12/10/24.
//

#include <gtest/gtest.h>
#include <sstream>
#include <fstream>
#include "../InputValidator.h"
#include "../App.h"
#include "../Delete.h"
#include <filesystem>
#include <string>
using namespace std;
namespace fs = std::filesystem;

TEST(DeleteTest, DeleteOneMovieID) {
    if (fs::exists("data/Users/user_1.txt")) {
    fs::remove("data/Users/user_1.txt");
    }

    // create user file and inserte movies IDs
    ofstream file("data/Users/user_1.txt");
    ASSERT_TRUE(file.is_open())  <<"Failed to open file user_1.txt" ;
    file << "1\n";
    file << "2\n";
    file << "3\n";

    file.close();

// Create an instance of HelpDisplay
    Delete deleteMethod = Delete();
    string output = deleteMethod.execute(1, {1});

    // Define expected output
    string expectedOutput = "204 No Content";

    // Check the output
    EXPECT_EQ(output, expectedOutput);
    EXPECT_TRUE(fs::exists("data/Users/user_1.txt")) << "File should still exist after deletion";

    // Read the file to verify its contents
    std::ifstream updatedFile("data/Users/user_1.txt");
    ASSERT_TRUE(updatedFile.is_open()) << "Failed to reopen file user_1.txt";

    std::string line;
    std::vector<std::string> fileContents;
    while (std::getline(updatedFile, line)) {
    fileContents.push_back(line);
    }
    updatedFile.close();

    // Expected contents after removing movie ID 1
    std::vector<std::string> expectedContents = {"2", "3"};

    // Assert the contents match
    EXPECT_EQ(fileContents, expectedContents);
    if (fs::exists("data/Users/user_1.txt")) {
        fs::remove("data/Users/user_1.txt");
    }
}

TEST(DeleteTest, DeleteAllMovieIDs) {
    // Setup: Ensure the file doesn't exist before starting
    if (fs::exists("data/Users/user_1.txt")) {
    fs::remove("data/Users/user_1.txt");
    }

    // Create user file and insert movie IDs
    std::ofstream file("data/Users/user_1.txt");
    ASSERT_TRUE(file.is_open()) << "Failed to open file user_1.txt";
    file << "1\n";
    file << "2\n";
    file << "3\n";
    file.close();

    // Call the delete command to remove all IDs
    Delete deleteMethod = Delete();
    std::string output = deleteMethod.execute(1, {1, 2, 3});

    // Define expected output
    std::string expectedOutput = "204 No Content";

    // Assert the output of the method
    EXPECT_EQ(output, expectedOutput);

    // Assert the file still exists
    EXPECT_TRUE(fs::exists("data/Users/user_1.txt")) << "File should still exist after deletion";

    // Read the file to verify it is empty
    std::ifstream updatedFile("data/Users/user_1.txt");
    ASSERT_TRUE(updatedFile.is_open()) << "Failed to reopen file user_1.txt";

    std::string line;
    std::vector<std::string> fileContents;
    while (std::getline(updatedFile, line)) {
    fileContents.push_back(line);
    }
    updatedFile.close();

    // Assert the file is empty
    EXPECT_TRUE(fileContents.empty()) << "File should be empty after all IDs are removed";

    if (fs::exists("data/Users/user_1.txt")) {
        fs::remove("data/Users/user_1.txt");
    }
}

TEST(DeleteTest, DeleteNonExistentMovieIDThrowsException) {
    // Setup: Ensure the file doesn't exist before starting
    if (fs::exists("data/Users/user_1.txt")) {
    fs::remove("data/Users/user_1.txt");
    }

    // Create user file and insert movie IDs
    std::ofstream file("data/Users/user_1.txt");
    ASSERT_TRUE(file.is_open()) << "Failed to open file user_1.txt";
    file << "1\n";
    file << "2\n";
    file << "3\n";
    file.close();

    // Create an instance of Delete
    Delete deleteMethod = Delete();

    // Attempt to delete a non-existent movie ID and expect an exception
    EXPECT_THROW({
    deleteMethod.execute(1, {4}); // Movie ID 4 does not exist
    }, std::invalid_argument);

    // Verify the file is unchanged
    std::ifstream updatedFile("data/Users/user_1.txt");
    ASSERT_TRUE(updatedFile.is_open()) << "Failed to reopen file user_1.txt";

    std::string line;
    std::vector<std::string> fileContents;
    while (std::getline(updatedFile, line)) {
    fileContents.push_back(line);
    }
    updatedFile.close();

    // Expected contents (unchanged)
    std::vector<std::string> expectedContents = {"1", "2", "3"};

    // Assert the contents match
    EXPECT_EQ(fileContents, expectedContents) << "File contents should remain unchanged when trying to delete a non-existent ID";

    if (fs::exists("data/Users/user_1.txt")) {
        fs::remove("data/Users/user_1.txt");
    }
}

TEST(DeleteTest, DeleteFromNonExistentUserThrowsException) {
    // Setup: Ensure the file does not exist
    if (fs::exists("data/Users/user_999.txt")) {
    fs::remove("data/Users/user_999.txt");
    }

    // Create an instance of Delete
    Delete deleteMethod = Delete();

    // Attempt to delete from a non-existent user and expect an exception
    EXPECT_THROW({
    deleteMethod.execute(999, {1}); // User ID 999 does not exist
    }, std::invalid_argument);

    // Ensure no file was created inadvertently
    EXPECT_FALSE(fs::exists("data/Users/user_999.txt")) << "File for non-existent user should not exist";
}