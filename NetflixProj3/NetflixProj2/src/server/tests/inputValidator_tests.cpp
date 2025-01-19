#include <gtest/gtest.h>
#include <sstream>
#include "../InputValidator.h"

using namespace std;

TEST(InputValidatorTest, ValidCommandWithParameters) {
    string inputStr = "POST 4 2 1";
    InputValidator validator;

    string command = validator.getCommand(inputStr);
    
    EXPECT_EQ(command, "POST");
    EXPECT_EQ(inputStr, "4 2 1");
}

TEST(InputValidatorTest, ValidCommandWithParametersAndSpaces) {
    string inputStr = "POST   4 2 1  5   8   ";
    InputValidator validator;

    string command = validator.getCommand(inputStr);
    
    EXPECT_EQ(command, "POST");
    EXPECT_EQ(inputStr, "  4 2 1  5   8   ");
}

TEST(InputValidatorTest, ValidCommandWithSingleParameter) {
    string inputStr = "POST 46 ";
    InputValidator validator;

    string command = validator.getCommand(inputStr);
    
    EXPECT_EQ(command, "POST");
    EXPECT_EQ(inputStr, "46 ");
}

TEST(InputValidatorTest, ValidCommandNoParameter) {
    string inputStr = "POST";
    InputValidator validator;

    string command = validator.getCommand(inputStr);
    
    EXPECT_EQ(command, "POST");
    EXPECT_EQ(inputStr, "");
}

TEST(InputValidatorTest, ValidCommand_CommandWrong_1) {
    string input = "pOST 1 2 3";
    InputValidator validator;
    
    EXPECT_THROW(validator.getCommand(input), runtime_error);
}

TEST(InputValidatorTest, ValidCommand_CommandWrong_2) {
    string input = "POSTd 1 2 3";
    InputValidator validator;
    
    EXPECT_THROW(validator.getCommand(input), runtime_error);
}

TEST(InputValidatorTest, ValidCommand_CommandWrong_3) {
    string input = "POST1 5 2 3";
    InputValidator validator;
    
    EXPECT_THROW(validator.getCommand(input), runtime_error);
}

TEST(InputValidatorTest, ValidCommandNoCommand) {
    string input = "";
    InputValidator validator;
    
    EXPECT_THROW(validator.getCommand(input), runtime_error);
}

TEST(InputValidatorTest, ValidCommand_SpaceBeforeCommand) {
    string input = " POST 1 10";
    InputValidator validator;
    
    EXPECT_THROW(validator.getCommand(input), runtime_error);
}

TEST(InputValidatorTest, getUserId_simple) {
    string inputStr = "1 2 3 10 20 30";
    InputValidator validator;

    int UID = validator.getUserId(inputStr);
    
    EXPECT_EQ(UID, 1);
    EXPECT_EQ(inputStr, "2 3 10 20 30");
}

TEST(InputValidatorTest, getUserId_spaces) {
    string inputStr = "  6  2    3 10   20 30 ";
    InputValidator validator;

    int UID = validator.getUserId(inputStr);
    
    EXPECT_EQ(UID, 6);
    EXPECT_EQ(inputStr, "2    3 10   20 30 ");
}

TEST(InputValidatorTest, getUserId_InvaliSstringsArgs) {
    string inputStr = "  6  2  3a  3 b 10   20 30 ";
    InputValidator validator;

    EXPECT_EQ(validator.getUserId(inputStr), 6);
    EXPECT_EQ(inputStr, "2  3a  3 b 10   20 30 ");
}

TEST(InputValidatorTest, ValidCommand_inValidStringUID) {
    string input = "a1  1 10";
    InputValidator validator;
    
    EXPECT_THROW(validator.getUserId(input), runtime_error);
}

TEST(InputValidatorTest, ValidCommand_inValidString) {
    string input = "";
    InputValidator validator;

    EXPECT_EQ(validator.getUserId(input), -1);
}

TEST(InputValidatorTest, ValidCommand_inValidNEgativeUID) {
    string input = "-1 8 3 5";
    InputValidator validator;
    
    EXPECT_THROW(validator.getUserId(input), runtime_error);
}

TEST(InputValidatorTest, ValidCommand_inValidNEgativeArgs) {
    string input = "1 8 -3 5";
    InputValidator validator;
    
    EXPECT_EQ(validator.getUserId(input), 1);
    EXPECT_EQ(input, "8 -3 5");
}

TEST(InputValidatorTest, ExtractNumbers_inValidNEgativeArgs) {
string input = "1 8 -3 5";
InputValidator validator;

EXPECT_THROW(validator.extractNumbers(input), runtime_error);
}






/*

TEST(ValidatorTest, SimpleValidatorAdd) {
int i = commandProcess("add 1 10");

EXPECT_EQ(i, 1);

}

TEST(ValidatorTest, ValidatorAddMultipyleNums) {
int i = commandProcess("add 9 8 7 6 5 4 3 2 1");

EXPECT_EQ(i, 1);

}

TEST(ValidatorTest, ValidatorAddSpaces) {
int i = commandProcess("add  7  3 1   89");

EXPECT_EQ(i, 1);

}

TEST(ValidatorTest, ValidatorAddLessArgs_1) {
int i = commandProcess("add  7");

EXPECT_EQ(i, 0);

}

TEST(ValidatorTest, ValidatorAddLessArgs_2) {
int i = commandProcess("add ");

EXPECT_EQ(i, 0);

}

TEST(ValidatorTest, ValidatorAddWrongCommand_1) {
int i = commandProcess("Add 1 10 ");

EXPECT_EQ(i, 0);

}

TEST(ValidatorTest, ValidatorAddWrongCommand_2) {
int i = commandProcess("addd 1 10 ");

EXPECT_EQ(i, 0);

}

TEST(ValidatorTest, ValidatorAddWrongString_1) {
int i = commandProcess("add 1 10 aa ");

EXPECT_EQ(i, 0);

}

TEST(ValidatorTest, ValidatorAddWrongMinus) {
int i = commandProcess("add 1 10 -5 6 ");

EXPECT_EQ(i, 0);

}

TEST(ValidatorTest, ValidatorAddWrongString_2) {
int i = commandProcess("add add 1 10 2");

EXPECT_EQ(i, 0);

}

TEST(ValidatorTest, ValidatorAddWrongUser_1) {
int i = commandProcess("add 0 1 10 2");

EXPECT_EQ(i, 0);

}

TEST(ValidatorTest, ValidatorAddWrongUser_2) {
int i = commandProcess("add -4 1 10 2");

EXPECT_EQ(i, 0);

}

TEST(ValidatorTest, SimpleValidatorRec) {
int i = commandProcess("recommend 1 2");

EXPECT_EQ(i, 2);

}

TEST(ValidatorTest, ValidatorRecLessArgs_1) {
int i = commandProcess("recommend 1 ");

EXPECT_EQ(i, 0);

}

TEST(ValidatorTest, ValidatorRecLessArgs_2) {
int i = commandProcess("recommend 1 ");

EXPECT_EQ(i, 0);

}

TEST(ValidatorTest, ValidatorRecMoreArgs_1) {
int i = commandProcess("recommend 1 1 2 3");

EXPECT_EQ(i, 0);

}

TEST(ValidatorTest, ValidatorRecMoreArgs_2) {
int i = commandProcess("recommend 1 1 0");

EXPECT_EQ(i, 0);

}

TEST(ValidatorTest, ValidatorRecWrongUser_1) {
int i = commandProcess("recommend 0 1");

EXPECT_EQ(i, 0);

}

TEST(ValidatorTest, ValidatorRecWrongUser_2) {
int i = commandProcess("recommend -1 1 10 2");

EXPECT_EQ(i, 0);

}

*/