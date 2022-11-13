#include <stdint.h>
#include <stdlib.h>
#include <string.h>
#include "std.h"
#include "error.h"

char* Std_intToString(int32_t i);
char* Std_booleanToString(int32_t b);
int32_t Std_printBoolean(int32_t b);
int32_t SubsetSum_subsetSum(int32_t a, int32_t b, int32_t sum);

char* Std_intToString(int32_t i) {
  char* ite_1;
  if((i < 0)) {
    char* string_1 = malloc(strlen("-") + 1);
    strcpy(string_1, "-");
    char* left_0 = string_1;
    char* right_0 = Std_intToString((-i));
    char* string_0 = malloc(strlen(left_0) + strlen(right_0) + 1);
    strcpy(string_0, left_0);
    strcat(string_0, right_0);
    ite_1 = string_0;
  }
  else {
    int32_t rem_0 = (i % 10);
    int32_t div_0 = (i / 10);
    char* ite_0;
    if((div_0 == 0)) {
      ite_0 = Std_digitToString(rem_0);
    }
    else {
      char* left_1 = Std_intToString(div_0);
      char* right_1 = Std_digitToString(rem_0);
      char* string_2 = malloc(strlen(left_1) + strlen(right_1) + 1);
      strcpy(string_2, left_1);
      strcat(string_2, right_1);
      ite_0 = string_2;
    }
    ite_1 = ite_0;
  }
  return ite_1;
}

char* Std_booleanToString(int32_t b) {
  char* ite_2;
  if(b) {
    char* string_3 = malloc(strlen("true") + 1);
    strcpy(string_3, "true");
    ite_2 = string_3;
  }
  else {
    char* string_4 = malloc(strlen("false") + 1);
    strcpy(string_4, "false");
    ite_2 = string_4;
  }
  return ite_2;
}

int32_t Std_printBoolean(int32_t b) {
  return Std_printString(Std_booleanToString(b));
}

int32_t SubsetSum_subsetSum(int32_t a, int32_t b, int32_t sum) {
  int32_t ite_4;
  if(((b < a) || (sum < 0))) {
    ite_4 = 0;
  }
  else {
    int32_t ite_3;
    if((sum == 0)) {
      ite_3 = 1;
    }
    else {
      ite_3 = (SubsetSum_subsetSum(a, b, (sum - b)) + SubsetSum_subsetSum(a, (b - 1), sum));
    }
    ite_4 = ite_3;
  }
  return ite_4;
}

int main() {
  char* string_5 = malloc(strlen("Caution this problem is NP complete do not input too big number:") + 1);
  strcpy(string_5, "Caution this problem is NP complete do not input too big number:");
  Std_printString(string_5);
  char* string_6 = malloc(strlen("Enter the Sum :") + 1);
  strcpy(string_6, "Enter the Sum :");
  Std_printString(string_6);
  int32_t sum_0 = Std_readInt();
  char* string_7 = malloc(strlen("Enter lower bound of interval :") + 1);
  strcpy(string_7, "Enter lower bound of interval :");
  Std_printString(string_7);
  int32_t a_0 = Std_readInt();
  char* string_8 = malloc(strlen("Enter upper bound of interval :") + 1);
  strcpy(string_8, "Enter upper bound of interval :");
  Std_printString(string_8);
  int32_t b_0 = Std_readInt();
  return Std_printInt(SubsetSum_subsetSum(a_0, b_0, sum_0));
}