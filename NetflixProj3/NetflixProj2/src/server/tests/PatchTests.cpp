#include "../data_access/FileDataAccess.hpp"
#include <fstream>
#include <filesystem>
#include <gtest/gtest.h>
#include "../App.h"
#include "../Patch.h"
#include "../Post.h"
#include <set>
using namespace std;
namespace fs = std::filesystem;

// patch means the user already exists, else 404 error

TEST(PATCH, check_patch_if_user_exists) {
   // Create the users folder
    if (!fs::exists("data/Users")) {
        fs::create_directory("data/Users");
        ASSERT_TRUE(fs::exists("data/Users")) << "Failed to create data/Users directory";

    }

    // Create the user file if it doesn't exist using the user_1.txt file
    if (!fs::exists("data/Users/user_1.txt")) {
        ofstream userFile("data/Users/user_1.txt"); // Create the file
        userFile << "10"; // Add any initial content if needed
        userFile.close();
        ASSERT_TRUE(fs::exists("data/Users/user_1.txt")) << "Failed to create data/Users/user_1.txt file";
    }

    Patch patch1 = Patch(); // this is not meant to work, user doesn't exist

    EXPECT_THROW(patch1.execute(2, {10}), std::invalid_argument);

    Patch patch2 = Patch(); // this is meant to work, patching a user that already exists.
    
    string execute_meant_to_be_204 = patch2.execute(1, {10});
    EXPECT_EQ (execute_meant_to_be_204, "204 No Content");

    if (fs::exists("data/Users/user_1.txt")) {
        fs::remove("data/Users/user_1.txt");
    }
}

