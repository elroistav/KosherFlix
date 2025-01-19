//#include <data_access/files.hpp>
#include "../data_access/FileDataAccess.hpp"
#include <fstream>
#include <filesystem>
#include <gtest/gtest.h>
#include <set>


using namespace std;
namespace fs = std::filesystem;
FileDataAccess fda;
TEST(FileDataAccess, storeMovies_simple) {
    int myUser = 1;
    set<int> myMovies = {10};

    // if (!fs::exists(Users)) {
    //     fs::create_directory(Users);
    // }
    if (fs::exists("data/Users/user_1.txt")) {
        fs::remove("data/Users/user_1.txt");
    }

    // run the func
    FileDataAccess::storeMovies(myUser, myMovies);

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
    fs::remove("data/Users/user_1.txt");

}


TEST(FileDataAccess, storeMovies_multipleMovies) {
    int myUser = 70;
    set<int> myMovies = {10, 1, 23, 5, 6, 12, 500};

    // if (!fs::exists(Users)) {
    //     fs::create_directory(Users);
    // }
    if (fs::exists("data/Users/user_70.txt")) {
        fs::remove("data/Users/user_70.txt");
    }

    // run the func
    FileDataAccess::storeMovies(myUser, myMovies);

    // check if file creates
    ASSERT_TRUE(fs::exists("data/Users/user_70.txt")) << "File user_70.txt was not created: ";

    // check file content
    ifstream file("data/Users/user_70.txt");
    ASSERT_TRUE(file.is_open()) << "Failed to open file user_70.txt: ";

    string out;
    string line;
    while (getline(file, line)) {
        out.append(line);
        out.append("\n");
    }
    ASSERT_EQ(out, "1\n5\n6\n10\n12\n23\n500\n") << "expected: 1\n5\n6\n10\n12\n23\n500\n, got: "<< out;

    // end
    file.close();
    fs::remove("data/Users/user_70.txt");

}

TEST(FileDataAccess, storeMovies_2Users) {
    int myUser_1 = 5;
    set<int> myMovies_1 = {50, 51, 52};
    int myUser_2 = 10;
    set<int> myMovies_2 = {101, 102, 103};

    // if (!fs::exists(Users)) {
    //     fs::create_directory(Users);
    // }
    if (fs::exists("data/Users/user_5.txt")) {
        fs::remove("data/Users/user_5.txt");
    }
    if (fs::exists("data/Users/user_10.txt")) {
        fs::remove("data/Users/user_10.txt");
    }

    // run the func
    FileDataAccess::storeMovies(myUser_1, myMovies_1);
    FileDataAccess::storeMovies(myUser_2, myMovies_2);


    // check if file creates
    ASSERT_TRUE(fs::exists("data/Users/user_5.txt")) << "File user_5.txt was not created: ";
    ASSERT_TRUE(fs::exists("data/Users/user_10.txt")) << "File user_10.txt was not created: ";


    // check file content
    ifstream file_1("data/Users/user_5.txt");
    ASSERT_TRUE(file_1.is_open()) << "Failed to open file user_5.txt: ";
    ifstream file_2("data/Users/user_10.txt");
    ASSERT_TRUE(file_2.is_open()) << "Failed to open file user_10.txt: ";

    string out1;
    string line1;
    while (getline(file_1, line1)) {
        out1.append(line1);
        out1.append("\n");
    }
    ASSERT_EQ(out1, "50\n51\n52\n") << "expected: 50\n51\n52\n, got: "<< out1;
    string out;
    string line;
    while (getline(file_2, line)) {
        out.append(line);
        out.append("\n");
    }
    ASSERT_EQ(out, "101\n102\n103\n") << "expected: 101\n102\n103\n, got: "<< out;

    // end
    file_1.close();
    file_2.close();

    fs::remove("data/Users/user_5.txt");
    fs::remove("data/Users/user_10.txt");


}

// existing user - expcted to override existing content wuth the new set
TEST(FileDataAccess, storeMovies_existingUser) {
    int myUser = 1;
    set<int> myMovies = {10, 20, 30};

    // if (!fs::exists(Users)) {
    //     fs::create_directory(Users);
    // }
    if (fs::exists("data/Users/user_1.txt")) {
        fs::remove("data/Users/user_1.txt");
    }

    // run the func
    FileDataAccess::storeMovies(myUser, myMovies);

    // check if file creates
    ASSERT_TRUE(fs::exists("data/Users/user_1.txt")) << "File user_1.txt was not created: ";

    // check file content
    ifstream file1("data/Users/user_1.txt");
    ASSERT_TRUE(file1.is_open()) << "Failed to open file user_1.txt: ";

    string line1;
    getline(file1, line1);
    ASSERT_EQ(line1, "10") << "expected: 10, got: "<< line1;

    // overriding existing user
    myMovies = {20, 15, 40};

    // run the func
    FileDataAccess::storeMovies(myUser, myMovies);

    // check if file exist
    ASSERT_TRUE(fs::exists("data/Users/user_1.txt")) << "File user_1.txt isnt exist anymore: ";

    // check file content
    ifstream file("data/Users/user_1.txt");
    ASSERT_TRUE(file.is_open()) << "Failed to open file user_1.txt: ";

    string out;
    string line;
    while (getline(file, line)) {
        out.append(line);
        out.append("\n");
    }
    ASSERT_EQ(out, "15\n20\n40\n") << "expected: 15\n20\n40\n, got: "<< out;

    // end
    file.close();
    fs::remove("data/Users/user_1.txt");

}

// getMoviesForUser tests

TEST(FileDataAccess, getMoviesForUser_simple) {
    int myUserId = 1;

    // if (!fs::exists(Users)) {
    //     fs::create_directory(Users);
    // }
    if (fs::exists("data/Users/user_1.txt")) {
        fs::remove("data/Users/user_1.txt");
    }

    // create user file and inserte movies IDs
    ofstream file("data/Users/user_1.txt");
        ASSERT_TRUE(file.is_open())  <<"Failed to open file user_1.txt" ;
            file << "20\n";
            file << "21\n";
            file << "22\n";

            file.close();

    // create movies set (expected)
    set<int> expctedMovies = {20, 21, 22};

    // get user movies set
    set<int> userMovies = fda.getMoviesForUser(myUserId);

    // compare expected and actual
    EXPECT_EQ(expctedMovies, userMovies);

    if (fs::exists("data/Users/user_1.txt")) {
        fs::remove("data/Users/user_1.txt");
    }
}

TEST(FileDataAccess, getMoviesForUser_noUser) {
int myUserId = 1;

// if (!fs::exists(Users)) {
//     fs::create_directory(Users); 
// }
if (fs::exists("data/Users/user_1.txt")) {
    fs::remove("data/Users/user_1.txt"); 
}

// create empty set (expected)
set<int> expctedMovies;

// get user movies set
set<int> userMovies = fda.getMoviesForUser(myUserId);

// compare expected and actual
EXPECT_EQ(expctedMovies, userMovies);


}

TEST(FileDataAccess, getMoviesForUser_noMovies) {
int myUserId = 1;

// if (!fs::exists(Users)) {
//     fs::create_directory(Users); 
// }
if (fs::exists("data/Users/user_1.txt")) {
    fs::remove("data/Users/user_1.txt"); 
}

// create user file 
filesystem::create_directories("data/Users");
ofstream file("data/Users/user_1.txt");
    ASSERT_TRUE(file.is_open())  <<"Failed to open file user_1.txt" ;
        file.close();

// create empty set (expected)
set<int> expctedMovies;

// get user movies set
set<int> userMovies = fda.getMoviesForUser(myUserId);

// compare expected and actual
EXPECT_EQ(expctedMovies, userMovies);

if (fs::exists("data/Users/user_1.txt")) {
fs::remove("data/Users/user_1.txt");
}
}


// getUsersForMovie tests

TEST(FileDataAccess, getUsersForMovie_simple1) {
    if (!fs::exists("data/Users")) {
            fs::create_directories("data/Users");
                ASSERT_TRUE(fs::exists("data/Users")) << "Failed to create data/Users directory";

        }
    if (fs::exists("data/Users/user_1.txt")) {
        fs::remove("data/Users/user_1.txt");
    }
    if (fs::exists("data/Users/user_2.txt")) {
        fs::remove("data/Users/user_2.txt");
    }

    // create users file and insert movies IDs
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
            file_2 << "40\n";
            file_2 << "60\n";

            file_2.close();


    // create movies sets (expected)
    set<int> user_1_only = {1};
    set<int> user_2_only = {2};
    set<int> users_1_2 = {1, 2};

    // compare expected and actual
    EXPECT_EQ(fda.getUsersForMovie(22), users_1_2);
    EXPECT_EQ(fda.getUsersForMovie(20), user_1_only);
    EXPECT_EQ(fda.getUsersForMovie(40), user_2_only);
    EXPECT_EQ(fda.getUsersForMovie(60), users_1_2);



    // Cleanup
        if (fs::exists("data/Users/user_1.txt")) {
            fs::remove("data/Users/user_1.txt");
        }
        if (fs::exists("data/Users/user_2.txt")) {
            fs::remove("data/Users/user_2.txt");
        }
}

TEST(FileDataAccess, getUsersForMovie_simple2) {
    fs::path usersPath = "data/Users";
    fs::path user1Path = usersPath / "user_1.txt";
    fs::path user2Path = usersPath / "user_2.txt";


    // Attempt to create Users directory with verbose error handling
    try {
        if (!fs::exists(usersPath)) {
            fs::create_directories(usersPath);
        }
        ASSERT_TRUE(fs::exists(usersPath)) << "Failed to create data/Users directory";

        // DEBUGGING: Verify directory permissions after creation
        auto dirPerms = fs::status(usersPath).permissions();
        
    } catch (const fs::filesystem_error& e) {
        std::cerr << "Filesystem error during directory creation: " << e.what() << std::endl;
        FAIL() << "Could not create Users directory";
    }

    // Remove existing files with verbose logging
    try {
        if (fs::exists(user1Path)) {
            fs::remove(user1Path);
        }
        if (fs::exists(user2Path)) {
            fs::remove(user2Path);
        }
    } catch (const fs::filesystem_error& e) {
        std::cerr << "Error removing existing files: " << e.what() << std::endl;
    }

    // File creation with extensive error checking
    try {
        std::ofstream file_1(user1Path);
        if (!file_1.is_open()) {
            std::cerr << "Failed to open user_1.txt. Error: " << strerror(errno) << std::endl;
            ASSERT_TRUE(file_1.is_open()) << "Failed to open file user_1.txt";
        }
        file_1 << "20\n";
        file_1 << "21\n";
        file_1 << "22\n";
        file_1 << "60\n";
        file_1.close();

        std::ofstream file_2(user2Path);
        if (!file_2.is_open()) {
            std::cerr << "Failed to open user_2.txt. Error: " << strerror(errno) << std::endl;
            ASSERT_TRUE(file_2.is_open()) << "Failed to open file user_2.txt";
        }
        file_2 << "22\n";
        file_2 << "30\n";
        file_2 << "40\n";
        file_2 << "60\n";
        file_2.close();

    } catch (const std::exception& e) {
        std::cerr << "Exception during file creation: " << e.what() << std::endl;
        FAIL() << "Could not create user files";
    }

    // Rest of the test remains the same
    // create movies sets (expected)
    set<int> user_1_only = {1};
    set<int> user_2_only = {2};
    set<int> users_1_2 = {1, 2};

    // compare expected and actual
    EXPECT_EQ(fda.getUsersForMovie(22), users_1_2);
    EXPECT_EQ(fda.getUsersForMovie(20), user_1_only);
    EXPECT_EQ(fda.getUsersForMovie(40), user_2_only);
    EXPECT_EQ(fda.getUsersForMovie(60), users_1_2);

    // Cleanup with verbose logging
    try {
        if (fs::exists(user1Path)) {
            fs::remove(user1Path);
        }
        if (fs::exists(user2Path)) {
            fs::remove(user2Path);
        }

        if (fs::exists(usersPath) && fs::is_empty(usersPath)) {
            fs::remove(usersPath);
        }
    } catch (const fs::filesystem_error& e) {
        std::cerr << "Error during cleanup: " << e.what() << std::endl;
    }
}


TEST(FileDataAccess, getUsersForMovie_noUser) {

// if (!fs::exists(Users)) {
//     fs::create_directory(Users); 
// }
if (fs::exists("data/Users/user_1.txt")) {
    fs::remove("data/Users/user_1.txt"); 
}
if (fs::exists("data/Users/user_2.txt")) {
    fs::remove("data/Users/user_2.txt"); 
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
        file_2 << "40\n";
        file_2 << "60\n";


        file_2.close();


// create movies sets (expected)
set<int> user_1_only = {1};
set<int> user_2_only = {2};
set<int> users_1_2 = {1, 2};
set<int> noUser;


// compare expected and actual
EXPECT_EQ(fda.getUsersForMovie(19), noUser);
EXPECT_EQ(fda.getUsersForMovie(5), noUser);
EXPECT_EQ(fda.getUsersForMovie(100), noUser);


if (fs::exists("data/Users/user_1.txt")) {
fs::remove("data/Users/user_1.txt");
}
if (fs::exists("data/Users/user_2.txt")) {
fs::remove("data/Users/user_2.txt");
}
}

// addMovies tests

TEST(FileDataAccess, addMovies_simpleNewUser) {

set<int> myMovies = {10};

// if (!fs::exists(Users)) {
//     fs::create_directory(Users); 
// }
if (fs::exists("data/Users/user_1.txt")) {
    fs::remove("data/Users/user_1.txt"); 
}

// run the func
fda.addMovies(1, myMovies);

// check if file creates
ASSERT_TRUE(fs::exists("data/Users/user_1.txt")) << "File user_1.txt was not created: ";

// check file content
ifstream file("data/Users/user_1.txt");
ASSERT_TRUE(file.is_open()) << "Failed to open file user_1.txt: ";

string line;
getline(file, line);
ASSERT_EQ(line, "10") << "expected: 10\n, got: "<< line;

// end
file.close();

if (fs::exists("data/Users/user_1.txt")) {
fs::remove("data/Users/user_1.txt");
}

}

TEST(FileDataAccess, addMovies_NewUserMultipyleMovies) {
int myUser = 70;
set<int> myMovies = {10, 1, 23, 5, 6, 12, 500};

// if (!fs::exists(Users)) {
//     fs::create_directory(Users); 
// }
if (fs::exists("data/Users/user_70.txt")) {
    fs::remove("data/Users/user_70.txt"); 
}

// run the func
fda.addMovies(myUser, myMovies);

// check if file creates
ASSERT_TRUE(fs::exists("data/Users/user_70.txt")) << "File user_70.txt was not created: ";

// check file content
ifstream file("data/Users/user_70.txt");
ASSERT_TRUE(file.is_open()) << "Failed to open file user_70.txt: ";

string out;
string line;
while (getline(file, line)) {
        out.append(line);
        out.append("\n");
    }
ASSERT_EQ(out, "1\n5\n6\n10\n12\n23\n500\n") << "expected: 1\n5\n6\n10\n12\n23\n500\n, got: "<< out;

// end
file.close();
if (fs::exists("data/Users/user_70.txt")) {
fs::remove("data/Users/user_70.txt");
}

}

TEST(FileDataAccess, addMovies_NewUserSameMovies) {
int myUser = 70;
set<int> myMovies = {10, 1, 23, 5, 6, 12, 500, 10, 23, 10};

// if (!fs::exists(Users)) {
//     fs::create_directory(Users); 
// }
if (fs::exists("data/Users/user_70.txt")) {
    fs::remove("data/Users/user_70.txt"); 
}

// run the func
fda.addMovies(myUser, myMovies);

// check if file creates
ASSERT_TRUE(fs::exists("data/Users/user_70.txt")) << "File user_70.txt was not created: ";

// check file content
ifstream file("data/Users/user_70.txt");
ASSERT_TRUE(file.is_open()) << "Failed to open file user_70.txt: ";

string out;
string line;
while (getline(file, line)) {
        out.append(line);
        out.append("\n");
    }
ASSERT_EQ(out, "1\n5\n6\n10\n12\n23\n500\n") << "expected: 1\n5\n6\n10\n12\n23\n500\n, got: "<< line;

// end
file.close();
if (fs::exists("data/Users/user_70.txt")) {
fs::remove("data/Users/user_70.txt");
}

}

TEST(FileDataAccess, addMovies_addingToExistUser_newIds) {
// first level - creating new user and movies Ids

    int myUser = 70;
    set<int> myMovies = {10, 1, 23, 5, 6, 12, 500};

//    if (!fs::exists(Users)) {
//        fs::create_directory(Users);
//    }
    if (fs::exists("data/Users/user_70.txt")) {
        fs::remove("data/Users/user_70.txt"); 
    }

    // run the func
    fda.addMovies(myUser, myMovies);

    // check if file creates
    ASSERT_TRUE(fs::exists("data/Users/user_70.txt")) << "File user_70.txt was not created: ";

    // check file content
    ifstream file("data/Users/user_70.txt");
    ASSERT_TRUE(file.is_open()) << "Failed to open file user_70.txt: ";

    string out;
    string line;
while (getline(file, line)) {
        out.append(line);
        out.append("\n");
    }
ASSERT_EQ(out, "1\n5\n6\n10\n12\n23\n500\n") << "expected: 1\n5\n6\n10\n12\n23\n500\n, got: "<< out;

    

// second level -adding new movies to exist user

    myMovies = {100, 200, 300};

    // run the func
    fda.addMovies(myUser, myMovies);

    // check is file exist
    ASSERT_TRUE(fs::exists("data/Users/user_70.txt")) << "File user_70.txt was not created: ";

    // check file content
    ifstream file2("data/Users/user_70.txt");
    ASSERT_TRUE(file2.is_open()) << "Failed to open file user_70.txt: ";

    string out2;
    string line2;
while (getline(file2, line2)) {
        out2.append(line2);
        out2.append("\n");
    }
ASSERT_EQ(out2, "1\n5\n6\n10\n12\n23\n100\n200\n300\n500\n") << "expected: 1\n5\n6\n10\n12\n23\n100\n200\n300\n500\n, got: "<< line2;


// end
    file.close();
if (fs::exists("data/Users/user_70.txt")) {
fs::remove("data/Users/user_70.txt");
}

}

TEST(FileDataAccess, addMovies_addingToExistUser_exist_new_Ids) {
// first level - creating new user and movies Ids

    int myUser = 70;
    set<int> myMovies = {10, 1, 23, 5, 6, 12, 500};

//    if (!fs::exists(Users)) {
//        fs::create_directory(Users);
//    }
    if (fs::exists("data/Users/user_70.txt")) {
        fs::remove("data/Users/user_70.txt"); 
    }

    // run the func
    fda.addMovies(myUser, myMovies);

    // check if file creates
    ASSERT_TRUE(fs::exists("data/Users/user_70.txt")) << "File user_70.txt was not created: ";

    // check file content
    ifstream file("data/Users/user_70.txt");
    ASSERT_TRUE(file.is_open()) << "Failed to open file user_70.txt: ";

    string out;
    string line;
    while (getline(file, line)) {
        out.append(line);
        out.append("\n");
    }
    ASSERT_EQ(out, "1\n5\n6\n10\n12\n23\n500\n") << "expected: 1\n5\n6\n10\n12\n23\n500\n, got: "<< out;

    

// second level -adding new movies to exist user

    myMovies = {23, 5, 6, 100, 200, 300};

    // run the func
    fda.addMovies(myUser, myMovies);

    // check is file exist
    ASSERT_TRUE(fs::exists("data/Users/user_70.txt")) << "File user_70.txt was not created: ";

    // check file content
    ifstream file2("data/Users/user_70.txt");
    ASSERT_TRUE(file2.is_open()) << "Failed to open file user_70.txt: ";

    string out2;
    string line2;
while (getline(file2, line2)) {
        out2.append(line2);
        out2.append("\n");
    }
    ASSERT_EQ(out2, "1\n5\n6\n10\n12\n23\n100\n200\n300\n500\n") << "expected: 1\n5\n6\n10\n12\n23\n100\n200\n300\n500\n, got: "<< line2;


// end
    file.close();
if (fs::exists("data/Users/user_70.txt")) {
fs::remove("data/Users/user_70.txt");
}

}

TEST(FileDataAccess, addMovies_addingToExistUser_emptyAdd) {
// first level - creating new user and movies Ids

    int myUser = 70;
    set<int> myMovies = {10, 1, 23, 5, 6, 12, 500};

//    if (!fs::exists(Users)) {
//        fs::create_directory(Users);
//    }
    if (fs::exists("data/Users/user_70.txt")) {
        fs::remove("data/Users/user_70.txt"); 
    }

    // run the func
    fda.addMovies(myUser, myMovies);

    // check if file creates
    ASSERT_TRUE(fs::exists("data/Users/user_70.txt")) << "File user_70.txt was not created: ";

    // check file content
    ifstream file("data/Users/user_70.txt");
    ASSERT_TRUE(file.is_open()) << "Failed to open file user_70.txt: ";

    string out;
    string line;
    while (getline(file, line)) {
        out.append(line);
        out.append("\n");
    }
    ASSERT_EQ(out, "1\n5\n6\n10\n12\n23\n500\n") << "expected: 1\n5\n6\n10\n12\n23\n500\n, got: "<< line;

    

// second level -adding empty movies to exist user

    set<int> emptyMovies;

    // run the func
    fda.addMovies(myUser, emptyMovies);

    // check is file exist
    ASSERT_TRUE(fs::exists("data/Users/user_70.txt")) << "File user_70.txt was not created: ";

    // check file content
    ifstream file2("data/Users/user_70.txt");
    ASSERT_TRUE(file2.is_open()) << "Failed to open file user_70.txt: ";

    string out2;
    string line2;
while (getline(file2, line2)) {
        out2.append(line2);
        out2.append("\n");
    }
    ASSERT_EQ(out2, "1\n5\n6\n10\n12\n23\n500\n") << "expected: 1\n5\n6\n10\n12\n23\n500\n, got: "<< out2;


// end
    file.close();
if (fs::exists("data/Users/user_70.txt")) {
fs::remove("data/Users/user_70.txt");
}

}











