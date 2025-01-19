//
// Created by Yoni Trachtenberg on 11/29/24.
//
#include <gtest/gtest.h>
#include <sstream>
#include <fstream>
#include "../InputValidator.h"
#include "../HelpDisplay.h"
#include "../App.h"
#include <filesystem>
#include <stdexcept>
#include <iostream>

using namespace std;
namespace fs = std::filesystem;

TEST(HelpCommandTest, validCommandWithString) {
    CommandUtils::initCommandUtils();
    string inputStr = "help me";
    InputValidator validator;

    string command = validator.getCommand(inputStr);

    EXPECT_EQ(command, "help");
    EXPECT_EQ(inputStr, "me");
}

TEST(HelpCommandTest, validCommand) {
    CommandUtils::initCommandUtils();
    string inputStr = "help";
    InputValidator validator;

    string command = validator.getCommand(inputStr);

    EXPECT_EQ(command, "help");
}

TEST(HelpCommandTests, validInputCheckOutput) {
    CommandUtils::initCommandUtils();
    // Create an instance of HelpDisplay
    HelpDisplay helpMethod = HelpDisplay();
    string output = helpMethod.execute(-1, {});

    // Define expected output
    string expectedOutput = "200 Ok\n\n"
                            "DELETE, arguments: [userid] [movieid1] [movieid2] ...\n"
                            "GET, arguments: [userid] [movieid]\n"
                            "PATCH, arguments: [userid] [movieid1] [movieid2] ...\n"
                            "POST, arguments: [userid] [movieid1] [movieid2] ...\n"
                            "help\n";

    // Check the output
    EXPECT_EQ(output, expectedOutput);
}

TEST(HelpCommandTests, CheckAllInvalidInputs) {
    CommandUtils::initCommandUtils();
    // Create an instance of HelpDisplay
    HelpDisplay helpMethod = HelpDisplay();
    // Check the output
    EXPECT_THROW(helpMethod.execute(-1, {2}), std::runtime_error) << "Help command doesnt take any movie ids";
    EXPECT_THROW(helpMethod.execute(-1, {2, 3, 4, 5}), std::runtime_error) << "Help command takes 0 movie ids";
    EXPECT_THROW(helpMethod.execute(0, {}), std::runtime_error) << "Help command doesnt take any userId";
    EXPECT_THROW(helpMethod.execute(0, {1, 2, 3}), std::runtime_error) << "Help command takes neither userIds nor moviesIds";
    EXPECT_THROW(helpMethod.execute(-1, {2, 3, 4, 5}), std::runtime_error) << "Help command takes 0 movie ids";
}