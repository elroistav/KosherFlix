#include "../data_access/FileDataAccess.hpp"
#include <fstream>
#include <filesystem>
#include <gtest/gtest.h>
#include "../App.h"
#include <set>
# include "../Post.h"
using namespace std;
namespace fs = std::filesystem;

// post means the user doesn't exist yet, else 404 error

TEST(POST, check_post_if_user_exists) {
    // Create the users folder
    if (!fs::exists("data/Users")) {
        fs::create_directory("data/Users");
        ASSERT_TRUE(fs::exists("data/Users")) << "Failed to create data/Users directory";

    }

    // Create the user file if it doesn't exist using the user_1.txt file
    if (!fs::exists("data/Users/user_2.txt")) {
        ofstream userFile("data/Users/user_2.txt"); // Create the file
        userFile << "10"; // Add any initial content if needed
        userFile.close();
        ASSERT_TRUE(fs::exists("data/Users/user_2.txt")) << "Failed to create data/Users/user_2.txt file";
    }
    if (fs::exists("data/Users/user_1.txt")) {
        fs::remove("data/Users/user_1.txt");
    }

    Post post1 = Post(); // this is not meant to work, user already exists

    EXPECT_THROW(post1.execute(2, {10}), std::invalid_argument);



    Post post2 = Post(); // this is meant to work, post a user that doesn't exist yet.

    string execute_meant_to_be_204 = post2.execute(1, {10});
    EXPECT_EQ (execute_meant_to_be_204, "201 Created");

    // check if file creates
    ASSERT_TRUE(fs::exists("data/Users/user_1.txt")) << "File user_1.txt was not created: ";

    // check file content
    ifstream file("data/Users/user_1.txt");
    ASSERT_TRUE(file.is_open()) << "Failed to open file user_1.txt: ";

    string out;
    string line;
    while (getline(file, line)) {
    out.append(line);
    out.append("\n");
    }
    ASSERT_EQ(out, "10\n") << "expected: 10\n, got: "<< out;

    // end
    file.close();
    if (fs::exists("data/Users/user_1.txt")) {
        fs::remove("data/Users/user_1.txt");
    }
    if (fs::exists("data/Users/user_2.txt")) {
        fs::remove("data/Users/user_2.txt");
    }
}

