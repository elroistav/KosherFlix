#include "../RecExecuter.h"
#include <fstream>
#include <filesystem>
#include <gtest/gtest.h>
#include <set>

using namespace std;
namespace fs = std::filesystem;


// getRelevantUsers tests
TEST(RecExecuter, getRelevantUsers_allOthers) {
    RecExecuter myRecExecuter(1, 1);

if (fs::exists("data/Users/user_1.txt")) {
    fs::remove("data/Users/user_1.txt"); 
}
if (fs::exists("data/Users/user_2.txt")) {
    fs::remove("data/Users/user_2.txt"); 
}
if (fs::exists("data/Users/user_3.txt")) {
    fs::remove("data/Users/user_3.txt"); 
}
if (fs::exists("data/Users/user_70.txt")) {
    fs::remove("data/Users/user_70.txt"); 
}



// create users file and insert movies IDs
filesystem::create_directories("data/Users");
ofstream file_1("data/Users/user_1.txt");
    ASSERT_TRUE(file_1.is_open())  <<"Failed to open file user_1.txt" ;
        file_1 << "20\n";
        file_1 << "21\n";
        file_1 << "22\n";
        file_1 << "60\n";

        file_1.close();

ofstream file_2("data/Users/user_2.txt");
    ASSERT_TRUE(file_2.is_open())  <<"Failed to open file user_2.txt" ;
        file_2 << "22\n";
        file_2 << "30\n";
        file_2 << "700\n";
        file_2 << "60\n";

        file_2.close();

        ofstream file_3("data/Users/user_3.txt");
    ASSERT_TRUE(file_3.is_open())  <<"Failed to open file user_3.txt" ;
        file_3 << "6\n";
        file_3 << "21\n";
        file_3 << "22\n";
        file_3 << "60\n";

        file_3.close();

ofstream file_70("data/Users/user_70.txt");
    ASSERT_TRUE(file_70.is_open())  <<"Failed to open file user_70.txt" ;
        file_70 << "50\n";
        file_70 << "30\n";
        file_70 << "40\n";
        file_70 << "60\n";

        file_70.close();

    set<int> relevantUsers = myRecExecuter.getRelevantUsers(60, 2);

    set<int> expectedUsers = {1, 3, 70};

    EXPECT_EQ(relevantUsers, expectedUsers);

    fs::remove("data/Users/user_1.txt");
    fs::remove("data/Users/user_2.txt");
    fs::remove("data/Users/user_3.txt");
    fs::remove("data/Users/user_70.txt");

}

TEST(RecExecuter, getRelevantUsers_oneOther) {
    RecExecuter myRecExecuter(1, 1);

if (fs::exists("data/Users/user_1.txt")) {
    fs::remove("data/Users/user_1.txt"); 
}
if (fs::exists("data/Users/user_2.txt")) {
    fs::remove("data/Users/user_2.txt"); 
}
if (fs::exists("data/Users/user_3.txt")) {
    fs::remove("data/Users/user_3.txt"); 
}
if (fs::exists("data/Users/user_70.txt")) {
    fs::remove("data/Users/user_70.txt"); 
}



// create users file and insert movies IDs
filesystem::create_directories("data/Users");
ofstream file_1("data/Users/user_1.txt");
    ASSERT_TRUE(file_1.is_open())  <<"Failed to open file user_1.txt" ;
        file_1 << "20\n";
        file_1 << "21\n";
        file_1 << "22\n";
        file_1 << "60\n";

        file_1.close();

ofstream file_2("data/Users/user_2.txt");
    ASSERT_TRUE(file_2.is_open())  <<"Failed to open file user_2.txt" ;
        file_2 << "22\n";
        file_2 << "30\n";
        file_2 << "700\n";
        file_2 << "60\n";

        file_2.close();

        ofstream file_3("data/Users/user_3.txt");
    ASSERT_TRUE(file_3.is_open())  <<"Failed to open file user_3.txt" ;
        file_3 << "6\n";
        file_3 << "21\n";
        file_3 << "22\n";
        file_3 << "60\n";

        file_3.close();

ofstream file_70("data/Users/user_70.txt");
    ASSERT_TRUE(file_70.is_open())  <<"Failed to open file user_70.txt" ;
        file_70 << "50\n";
        file_70 << "30\n";
        file_70 << "40\n";
        file_70 << "6\n";

        file_70.close();

    set<int> relevantUsers = myRecExecuter.getRelevantUsers(6, 70);

    set<int> expectedUsers = {3};

    EXPECT_EQ(relevantUsers, expectedUsers);

    fs::remove("data/Users/user_1.txt");
    fs::remove("data/Users/user_2.txt");
    fs::remove("data/Users/user_3.txt");
    fs::remove("data/Users/user_70.txt");

}

TEST(RecExecuter, getRelevantUsers_userDidntWatch) {
    RecExecuter myRecExecuter(1, 1);

if (fs::exists("data/Users/user_1.txt")) {
    fs::remove("data/Users/user_1.txt"); 
}
if (fs::exists("data/Users/user_2.txt")) {
    fs::remove("data/Users/user_2.txt"); 
}
if (fs::exists("data/Users/user_3.txt")) {
    fs::remove("data/Users/user_3.txt"); 
}
if (fs::exists("data/Users/user_70.txt")) {
    fs::remove("data/Users/user_70.txt"); 
}



// create users file and insert movies IDs
filesystem::create_directories("data/Users");
ofstream file_1("data/Users/user_1.txt");
    ASSERT_TRUE(file_1.is_open())  <<"Failed to open file user_1.txt" ;
        file_1 << "20\n";
        file_1 << "21\n";
        file_1 << "22\n";
        file_1 << "60\n";

        file_1.close();

ofstream file_2("data/Users/user_2.txt");
    ASSERT_TRUE(file_2.is_open())  <<"Failed to open file user_2.txt" ;
        file_2 << "22\n";
        file_2 << "30\n";
        file_2 << "700\n";
        file_2 << "60\n";

        file_2.close();

        ofstream file_3("data/Users/user_3.txt");
    ASSERT_TRUE(file_3.is_open())  <<"Failed to open file user_3.txt" ;
        file_3 << "6\n";
        file_3 << "21\n";
        file_3 << "50\n";
        file_3 << "60\n";

        file_3.close();

ofstream file_70("data/Users/user_70.txt");
    ASSERT_TRUE(file_70.is_open())  <<"Failed to open file user_70.txt" ;
        file_70 << "50\n";
        file_70 << "30\n";
        file_70 << "40\n";
        file_70 << "6\n";

        file_70.close();

    set<int> relevantUsers = myRecExecuter.getRelevantUsers(50, 1);

    set<int> expectedUsers = {3, 70};

    EXPECT_EQ(relevantUsers, expectedUsers);

    fs::remove("data/Users/user_1.txt");
    fs::remove("data/Users/user_2.txt");
    fs::remove("data/Users/user_3.txt");
    fs::remove("data/Users/user_70.txt");

}

TEST(RecExecuter, getRelevantUsers_noOtherUsers) {
    RecExecuter myRecExecuter(1, 1);

if (fs::exists("data/Users/user_1.txt")) {
    fs::remove("data/Users/user_1.txt"); 
}
if (fs::exists("data/Users/user_2.txt")) {
    fs::remove("data/Users/user_2.txt"); 
}
if (fs::exists("data/Users/user_3.txt")) {
    fs::remove("data/Users/user_3.txt"); 
}
if (fs::exists("data/Users/user_70.txt")) {
    fs::remove("data/Users/user_70.txt"); 
}



// create users file and insert movies IDs
filesystem::create_directories("data/Users");
ofstream file_1("data/Users/user_1.txt");
    ASSERT_TRUE(file_1.is_open())  <<"Failed to open file user_1.txt" ;
        file_1 << "20\n";
        file_1 << "21\n";
        file_1 << "22\n";
        file_1 << "60\n";

        file_1.close();

ofstream file_2("data/Users/user_2.txt");
    ASSERT_TRUE(file_2.is_open())  <<"Failed to open file user_2.txt" ;
        file_2 << "22\n";
        file_2 << "30\n";
        file_2 << "700\n";
        file_2 << "60\n";

        file_2.close();

        ofstream file_3("data/Users/user_3.txt");
    ASSERT_TRUE(file_3.is_open())  <<"Failed to open file user_3.txt" ;
        file_3 << "6\n";
        file_3 << "21\n";
        file_3 << "50\n";
        file_3 << "60\n";

        file_3.close();

ofstream file_70("data/Users/user_70.txt");
    ASSERT_TRUE(file_70.is_open())  <<"Failed to open file user_70.txt" ;
        file_70 << "50\n";
        file_70 << "30\n";
        file_70 << "40\n";
        file_70 << "6\n";

        file_70.close();

    set<int> relevantUsers = myRecExecuter.getRelevantUsers(100, 1);

    set<int> expectedUsers = {};

    EXPECT_EQ(relevantUsers, expectedUsers);

    fs::remove("data/Users/user_1.txt");
    fs::remove("data/Users/user_2.txt");
    fs::remove("data/Users/user_3.txt");
    fs::remove("data/Users/user_70.txt");

}

TEST(RecExecuter, getRelevantUsers_noOtherUsers2) {
    RecExecuter myRecExecuter(1, 1);

if (fs::exists("data/Users/user_1.txt")) {
    fs::remove("data/Users/user_1.txt"); 
}
if (fs::exists("data/Users/user_2.txt")) {
    fs::remove("data/Users/user_2.txt"); 
}
if (fs::exists("data/Users/user_3.txt")) {
    fs::remove("data/Users/user_3.txt"); 
}
if (fs::exists("data/Users/user_70.txt")) {
    fs::remove("data/Users/user_70.txt"); 
}



// create users file and insert movies IDs
filesystem::create_directories("data/Users");
ofstream file_1("data/Users/user_1.txt");
    ASSERT_TRUE(file_1.is_open())  <<"Failed to open file user_1.txt" ;
        file_1 << "20\n";
        file_1 << "21\n";
        file_1 << "22\n";
        file_1 << "60\n";

        file_1.close();
        
    set<int> relevantUsers = myRecExecuter.getRelevantUsers(20, 1);

    set<int> expectedUsers = {};

    EXPECT_EQ(relevantUsers, expectedUsers);

    fs::remove("data/Users/user_1.txt");
    
}

TEST(RecExecuter, getRelevantUsers_noOtherUsers3) {
    RecExecuter myRecExecuter(1, 1);

if (fs::exists("data/Users/user_1.txt")) {
    fs::remove("data/Users/user_1.txt"); 
}
if (fs::exists("data/Users/user_2.txt")) {
    fs::remove("data/Users/user_2.txt"); 
}
if (fs::exists("data/Users/user_3.txt")) {
    fs::remove("data/Users/user_3.txt"); 
}
if (fs::exists("data/Users/user_70.txt")) {
    fs::remove("data/Users/user_70.txt"); 
}



// create users file and insert movies IDs
    filesystem::create_directories("data/Users");
    ofstream file_1("data/Users/user_1.txt");
    ASSERT_TRUE(file_1.is_open()) << "Failed to open file user_1.txt" ;
        file_1 << "25\n";
        file_1 << "21\n";
        file_1 << "22\n";
        file_1 << "60\n";

        file_1.close();

    ofstream file_2("data/Users/user_2.txt");

    set<int> relevantUsers = myRecExecuter.getRelevantUsers(20, 2);

    set<int> expectedUsers = {};

    EXPECT_EQ(relevantUsers, expectedUsers);

    fs::remove("data/Users/user_1.txt");
    fs::remove("data/Users/user_2.txt");
    
}

//////////////////////////// findMatchLevel tests

TEST(RecExecuter, findMatchLevel_fewOthers) {
        RecExecuter myRecExecuter(1, 1);

    if (fs::exists("data/Users/user_1.txt")) {
    fs::remove("data/Users/user_1.txt"); 
}
if (fs::exists("data/Users/user_2.txt")) {
    fs::remove("data/Users/user_2.txt"); 
}
if (fs::exists("data/Users/user_3.txt")) {
    fs::remove("data/Users/user_3.txt"); 
}
if (fs::exists("data/Users/user_70.txt")) {
    fs::remove("data/Users/user_70.txt"); 
}



// create users file and insert movies IDs
filesystem::create_directories("data/Users");
ofstream file_1("data/Users/user_1.txt");
    ASSERT_TRUE(file_1.is_open())  <<"Failed to open file user_1.txt" ;
        file_1 << "20\n";
        file_1 << "21\n";
        file_1 << "22\n";
        file_1 << "60\n";

        file_1.close();

ofstream file_2("data/Users/user_2.txt");
    ASSERT_TRUE(file_2.is_open())  <<"Failed to open file user_2.txt" ;
        file_2 << "22\n";
        file_2 << "30\n";
        file_2 << "700\n";
        file_2 << "60\n";

        file_2.close();

        ofstream file_3("data/Users/user_3.txt");
    ASSERT_TRUE(file_3.is_open())  <<"Failed to open file user_3.txt" ;
        file_3 << "6\n";
        file_3 << "21\n";
        file_3 << "22\n";
        file_3 << "61\n";

        file_3.close();

        ofstream file_4("data/Users/user_4.txt");
    ASSERT_TRUE(file_4.is_open())  <<"Failed to open file user_4.txt" ;
        file_4 << "2000\n";
        file_4 << "210\n";
        file_4 << "2200\n";
        file_4 << "600\n";

        file_4.close();

        ofstream file_5("data/Users/user_5.txt");
    ASSERT_TRUE(file_5.is_open())  <<"Failed to open file user_5.txt" ;
        file_5 << "2022\n";
        file_5 << "2122\n";
        file_5 << "2222\n";
        file_5 << "6022\n";

        file_5.close();

ofstream file_70("data/Users/user_70.txt");
    ASSERT_TRUE(file_70.is_open())  <<"Failed to open file user_70.txt" ;
        file_70 << "50\n";
        file_70 << "30\n";
        file_70 << "40\n";
        file_70 << "60\n";

        file_70.close();

    map<int, int> userMatchLevel = myRecExecuter.findMatchLevel(1, {2, 70});

    map<int, int> expectedMatchLevel = {{2, 2}, {70, 1}};

    EXPECT_EQ(userMatchLevel, expectedMatchLevel);

    fs::remove("data/Users/user_1.txt");
    fs::remove("data/Users/user_2.txt");
    fs::remove("data/Users/user_3.txt");
    fs::remove("data/Users/user_4.txt");
    fs::remove("data/Users/user_5.txt");
    fs::remove("data/Users/user_70.txt");
}

TEST(RecExecuter, findMatchLevel_noOthers) {
    if (fs::exists("data/Users/user_1.txt")) {
    fs::remove("data/Users/user_1.txt"); 
}
if (fs::exists("data/Users/user_2.txt")) {
    fs::remove("data/Users/user_2.txt"); 
}
if (fs::exists("data/Users/user_3.txt")) {
    fs::remove("data/Users/user_3.txt"); 
}
if (fs::exists("data/Users/user_70.txt")) {
    fs::remove("data/Users/user_70.txt"); 
}



// create users file and insert movies IDs

filesystem::create_directories("data/Users");
RecExecuter myRecExecuter(1, 1);

ofstream file_1("data/Users/user_1.txt");
    ASSERT_TRUE(file_1.is_open())  <<"Failed to open file user_1.txt" ;
        file_1 << "20\n";
        file_1 << "21\n";
        file_1 << "22\n";
        file_1 << "60\n";

        file_1.close();

    map<int, int> userMatchLevel = myRecExecuter.findMatchLevel(1, {});

    map<int, int> expectedMatchLevel = {};

    EXPECT_EQ(userMatchLevel, expectedMatchLevel);

    fs::remove("data/Users/user_1.txt");
}

TEST(RecExecuter, findMatchLevel_allOthers) {
    RecExecuter myRecExecuter(1, 1);

    if (fs::exists("data/Users/user_1.txt")) {
    fs::remove("data/Users/user_1.txt"); 
}
if (fs::exists("data/Users/user_2.txt")) {
    fs::remove("data/Users/user_2.txt"); 
}
if (fs::exists("data/Users/user_3.txt")) {
    fs::remove("data/Users/user_3.txt"); 
}
if (fs::exists("data/Users/user_70.txt")) {
    fs::remove("data/Users/user_70.txt"); 
}



// create users file and insert movies IDs

filesystem::create_directories("data/Users");
ofstream file_1("data/Users/user_1.txt");
    ASSERT_TRUE(file_1.is_open())  <<"Failed to open file user_1.txt" ;
        file_1 << "20\n";
        file_1 << "21\n";
        file_1 << "22\n";
        file_1 << "60\n";

        file_1.close();

ofstream file_2("data/Users/user_2.txt");
    ASSERT_TRUE(file_2.is_open())  <<"Failed to open file user_2.txt" ;
        file_2 << "22\n";
        file_2 << "30\n";
        file_2 << "700\n";
        file_2 << "60\n";

        file_2.close();

        ofstream file_3("data/Users/user_3.txt");
    ASSERT_TRUE(file_3.is_open())  <<"Failed to open file user_3.txt" ;
        file_3 << "20\n";
        file_3 << "21\n";
        file_3 << "25\n";
        file_3 << "60\n";

        file_3.close();

ofstream file_70("data/Users/user_70.txt");
    ASSERT_TRUE(file_70.is_open())  <<"Failed to open file user_70.txt" ;
        file_70 << "50\n";
        file_70 << "700\n";
        file_70 << "30\n";
        file_70 << "60\n";

        file_70.close();

    map<int, int> userMatchLevel = myRecExecuter.findMatchLevel(1, {2, 3, 70});

    map<int, int> expectedMatchLevel = {{2, 2}, {3, 3}, {70, 1}};

    EXPECT_EQ(userMatchLevel, expectedMatchLevel);

    fs::remove("data/Users/user_1.txt");
    fs::remove("data/Users/user_2.txt");
    fs::remove("data/Users/user_3.txt");
    fs::remove("data/Users/user_70.txt");

}

TEST(RecExecuter, findMatchLevel_noMatch) {
    RecExecuter myRecExecuter(1, 1);

    if (fs::exists("data/Users/user_1.txt")) {
    fs::remove("data/Users/user_1.txt"); 
}
if (fs::exists("data/Users/user_2.txt")) {
    fs::remove("data/Users/user_2.txt"); 
}
if (fs::exists("data/Users/user_3.txt")) {
    fs::remove("data/Users/user_3.txt"); 
}
if (fs::exists("data/Users/user_70.txt")) {
    fs::remove("data/Users/user_70.txt"); 
}



// create users file and insert movies IDs

filesystem::create_directories("data/Users");
ofstream file_1("data/Users/user_1.txt");
    ASSERT_TRUE(file_1.is_open())  <<"Failed to open file user_1.txt" ;
        file_1 << "20\n";
        file_1 << "21\n";
        file_1 << "22\n";
        file_1 << "60\n";

        file_1.close();

ofstream file_2("data/Users/user_2.txt");
    ASSERT_TRUE(file_2.is_open())  <<"Failed to open file user_2.txt" ;
        file_2 << "220\n";
        file_2 << "350\n";
        file_2 << "7000\n";
        file_2 << "600\n";

        file_2.close();

        ofstream file_3("data/Users/user_3.txt");
    ASSERT_TRUE(file_3.is_open())  <<"Failed to open file user_3.txt" ;
        file_3 << "20\n";
        file_3 << "21\n";
        file_3 << "25\n";
        file_3 << "60\n";

        file_3.close();

ofstream file_70("data/Users/user_70.txt");
    ASSERT_TRUE(file_70.is_open())  <<"Failed to open file user_70.txt" ;
        file_70 << "50\n";
        file_70 << "700\n";
        file_70 << "30\n";
        file_70 << "60\n";

        file_70.close();

    map<int, int> userMatchLevel = myRecExecuter.findMatchLevel(2, {1, 3, 70});

    map<int, int> expectedMatchLevel = {{1, 0}, {3, 0}, {70, 0}};

    EXPECT_EQ(userMatchLevel, expectedMatchLevel);

    fs::remove("data/Users/user_1.txt");
    fs::remove("data/Users/user_2.txt");
    fs::remove("data/Users/user_3.txt");
    fs::remove("data/Users/user_70.txt");

}

TEST(RecExecuter, findMatchLevel_noMatch2) {
    RecExecuter myRecExecuter(1, 1);

    if (fs::exists("data/Users/user_1.txt")) {
    fs::remove("data/Users/user_1.txt"); 
}
if (fs::exists("data/Users/user_2.txt")) {
    fs::remove("data/Users/user_2.txt"); 
}
if (fs::exists("data/Users/user_3.txt")) {
    fs::remove("data/Users/user_3.txt"); 
}
if (fs::exists("data/Users/user_70.txt")) {
    fs::remove("data/Users/user_70.txt"); 
}



// create users file and insert movies IDs

filesystem::create_directories("data/Users");
ofstream file_1("data/Users/user_1.txt");
    ASSERT_TRUE(file_1.is_open())  <<"Failed to open file user_1.txt" ;
        file_1 << "20\n";
        file_1 << "21\n";
        file_1 << "22\n";
        file_1 << "60\n";

        file_1.close();

ofstream file_2("data/Users/user_2.txt");
    ASSERT_TRUE(file_2.is_open())  <<"Failed to open file user_2.txt" ;

        file_2.close();

        ofstream file_3("data/Users/user_3.txt");
    ASSERT_TRUE(file_3.is_open())  <<"Failed to open file user_3.txt" ;
        file_3 << "20\n";
        file_3 << "21\n";
        file_3 << "25\n";
        file_3 << "60\n";

        file_3.close();

ofstream file_70("data/Users/user_70.txt");
    ASSERT_TRUE(file_70.is_open())  <<"Failed to open file user_70.txt" ;
        file_70 << "50\n";
        file_70 << "700\n";
        file_70 << "30\n";
        file_70 << "60\n";

        file_70.close();

    map<int, int> userMatchLevel = myRecExecuter.findMatchLevel(2, {1, 3, 70});

    map<int, int> expectedMatchLevel = {{1, 0}, {3, 0}, {70, 0}};

    EXPECT_EQ(userMatchLevel, expectedMatchLevel);

    fs::remove("data/Users/user_1.txt");
    fs::remove("data/Users/user_2.txt");
    fs::remove("data/Users/user_3.txt");
    fs::remove("data/Users/user_70.txt");

}


//map<int, int> movieRating = getMovieRating(userMatchLevel);

// getMovieRating tests

TEST(RecExecuter, getMovieRating_allUsers) {
    RecExecuter myRecExecuter(1, 1);

if (fs::exists("data/Users/user_1.txt")) {
    fs::remove("data/Users/user_1.txt"); 
}
if (fs::exists("data/Users/user_2.txt")) {
    fs::remove("data/Users/user_2.txt"); 
}
if (fs::exists("data/Users/user_3.txt")) {
    fs::remove("data/Users/user_3.txt"); 
}
if (fs::exists("data/Users/user_70.txt")) {
    fs::remove("data/Users/user_70.txt"); 
}



// create users file and insert movies IDs
filesystem::create_directories("data/Users");
ofstream file_1("data/Users/user_1.txt");
    ASSERT_TRUE(file_1.is_open())  <<"Failed to open file user_1.txt" ;
        file_1 << "20\n";
        file_1 << "21\n";
        file_1 << "22\n";
        file_1 << "60\n";

        file_1.close();

ofstream file_2("data/Users/user_2.txt");
    ASSERT_TRUE(file_2.is_open())  <<"Failed to open file user_2.txt" ;
        file_2 << "22\n";
        file_2 << "30\n";
        file_2 << "700\n";
        file_2 << "60\n";

        file_2.close();

        ofstream file_3("data/Users/user_3.txt");
    ASSERT_TRUE(file_3.is_open())  <<"Failed to open file user_3.txt" ;
        file_3 << "6\n";
        file_3 << "21\n";
        file_3 << "22\n";
        file_3 << "60\n";
        file_3 << "110\n";
        file_3 << "120\n";
        file_3 << "701\n";

        file_3.close();

ofstream file_70("data/Users/user_70.txt");
    ASSERT_TRUE(file_70.is_open())  <<"Failed to open file user_70.txt" ;
        file_70 << "20\n";
        file_70 << "30\n";
        file_70 << "40\n";
        file_70 << "60\n";
        file_70 << "701\n";
        file_70 << "702\n";
        file_70 << "710\n";

        file_70.close();

    map<int, int> userMatchLevel = {{2, 2}, {3, 3}, {70, 2}};
    map<int, int> expectedRating = {{6, 3}, {30, 4}, {40, 2}, {110, 3}, {120, 3},{700, 2}, {701, 5}, {702, 2}, {710, 2}};

    map<int, int> movieRating = myRecExecuter.getMovieRating(userMatchLevel, 60, 1);
    EXPECT_EQ(movieRating, expectedRating);

    fs::remove("data/Users/user_1.txt");
    fs::remove("data/Users/user_2.txt");
    fs::remove("data/Users/user_3.txt");
    fs::remove("data/Users/user_70.txt");

}

TEST(RecExecuter, getMovieRating_feaOtherUsers) {
    RecExecuter myRecExecuter(1, 1);

if (fs::exists("data/Users/user_1.txt")) {
    fs::remove("data/Users/user_1.txt"); 
}
if (fs::exists("data/Users/user_2.txt")) {
    fs::remove("data/Users/user_2.txt"); 
}
if (fs::exists("data/Users/user_3.txt")) {
    fs::remove("data/Users/user_3.txt"); 
}
if (fs::exists("data/Users/user_70.txt")) {
    fs::remove("data/Users/user_70.txt"); 
}
if (fs::exists("data/Users/user_60.txt")) {
    fs::remove("data/Users/user_60.txt"); 
}


// create users file and insert movies IDs
filesystem::create_directories("data/Users");
ofstream file_1("data/Users/user_1.txt");
    ASSERT_TRUE(file_1.is_open())  <<"Failed to open file user_1.txt" ;
        file_1 << "20\n";
        file_1 << "21\n";
        file_1 << "22\n";
        file_1 << "60\n";

        file_1.close();

ofstream file_2("data/Users/user_2.txt");
    ASSERT_TRUE(file_2.is_open())  <<"Failed to open file user_2.txt" ;
        file_2 << "22\n";
        file_2 << "30\n";
        file_2 << "700\n";
        file_2 << "60\n";

        file_2.close();

        ofstream file_3("data/Users/user_3.txt");
    ASSERT_TRUE(file_3.is_open())  <<"Failed to open file user_3.txt" ;
        file_3 << "6\n";
        file_3 << "21\n";
        file_3 << "22\n";
        file_3 << "60\n";
        file_3 << "110\n";
        file_3 << "120\n";
        file_3 << "701\n";

        file_3.close();

ofstream file_70("data/Users/user_70.txt");
    ASSERT_TRUE(file_70.is_open())  <<"Failed to open file user_70.txt" ;
        file_70 << "20\n";
        file_70 << "30\n";
        file_70 << "40\n";
        file_70 << "60\n";
        file_70 << "701\n";
        file_70 << "702\n";
        file_70 << "710\n";

        file_70.close();

        ofstream file_60("data/Users/user_60.txt");
    ASSERT_TRUE(file_60.is_open())  <<"Failed to open file user_60.txt" ;
        file_60 << "6333\n";
        file_60 << "2133\n";
        file_60 << "2233\n";
        

        file_60.close();

    map<int, int> userMatchLevel = {{2, 2}, {3, 3}, {70, 2}};
    map<int, int> expectedRating = {{6, 3}, {30, 4}, {40, 2}, {110, 3}, {120, 3},{700, 2}, {701, 5}, {702, 2}, {710, 2}};

    map<int, int> movieRating = myRecExecuter.getMovieRating(userMatchLevel, 60, 1);
    EXPECT_EQ(movieRating, expectedRating);

    fs::remove("data/Users/user_1.txt");
    fs::remove("data/Users/user_2.txt");
    fs::remove("data/Users/user_3.txt");
    fs::remove("data/Users/user_60.txt");
    fs::remove("data/Users/user_70.txt");

}



TEST(RecExecuter, getMovieRating_noUsers) {
    RecExecuter myRecExecuter(1, 1);

    map<int, int> userMatchLevel = {};
    map<int, int> expectedRating = {};

    map<int, int> movieRating = myRecExecuter.getMovieRating(userMatchLevel, 2, 2);
    EXPECT_EQ(movieRating, expectedRating);

}

TEST(RecExecuter, getMovieRating_noUsers2) {
    RecExecuter myRecExecuter(1, 1);

    map<int, int> userMatchLevel = {{2, 0}, {3, 0}, {70, 0}};
    map<int, int> expectedRating = {};

    map<int, int> movieRating = myRecExecuter.getMovieRating(userMatchLevel, 60, 1);
    EXPECT_EQ(movieRating, expectedRating);

}


//std::vector<int> RecExecuter::getTop10Movies(const std::map<int, int>& movieRating) 

//////////////////////////////////////////getTop10Movies tests

TEST(RecExecuter, getTop10Movies_simple) {
    RecExecuter myRecExecuter(1, 1);

    map<int, int> movieRating = {{10, 1}, {9, 2}, {8, 3}, {7, 4}, {6, 5}, {5, 6}, {4, 7}, {3, 8}, {2, 9},{1, 10}};
    vector<int> expectedTop10Movies = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

    vector<int> top10Movies = myRecExecuter.getTop10Movies(movieRating);
    EXPECT_EQ(top10Movies, expectedTop10Movies);

}

TEST(RecExecuter, getTop10Movies_sameMatch) {
    RecExecuter myRecExecuter(1, 1);

    map<int, int> movieRating = {{10, 1}, {9, 1}, {8, 1}, {7, 2}, {6, 2}, {5, 2}, {4, 2}, {3, 8}, {2, 9},{1, 10}};
    vector<int> expectedTop10Movies = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

    vector<int> top10Movies = myRecExecuter.getTop10Movies(movieRating);
    EXPECT_EQ(top10Movies, expectedTop10Movies);

}

TEST(RecExecuter, getTop10Movies_moreThan10) {
    RecExecuter myRecExecuter(1, 1);

    map<int, int> movieRating = {{10, 1}, {9, 1}, {8, 1}, {7, 2}, {6, 2}, {5, 8}, {4, 2}, {3, 8}, {2, 9},{1, 10}, {11, 1}, {12, 0}};
    vector<int> expectedTop10Movies = {1, 2, 3, 5, 4, 6, 7, 8, 9, 10};

    vector<int> top10Movies = myRecExecuter.getTop10Movies(movieRating);
    EXPECT_EQ(top10Movies, expectedTop10Movies);

}

TEST(RecExecuter, getTop10Movies_moreThan10_2) {
    RecExecuter myRecExecuter(1, 1);

    map<int, int> movieRating = {{80, 30}, {50, 40}, {60,0}, {10, 1}, {9, 1}, {8, 1}, {7, 5}, {6, 2}, {5, 2}, {4, 2}, {3, 8}, {2, 9},{1, 10}};
    vector<int> expectedTop10Movies = {50, 80, 1, 2, 3, 7, 4, 5, 6, 8};

    vector<int> top10Movies = myRecExecuter.getTop10Movies(movieRating);
    EXPECT_EQ(top10Movies, expectedTop10Movies);

}

TEST(RecExecuter, getTop10Movies_lessThan10) {
    RecExecuter myRecExecuter(1, 1);

    map<int, int> movieRating = {{5, 8}, {4, 0}, {3, 8}, {2, 9},{1, 10}};
    vector<int> expectedTop10Movies = {1, 2, 3, 5, 4};

    vector<int> top10Movies = myRecExecuter.getTop10Movies(movieRating);
    EXPECT_EQ(top10Movies, expectedTop10Movies);

}






