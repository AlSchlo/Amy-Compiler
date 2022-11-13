/**
 * @file std.h
 * @brief Std utility functions implementations and signatures.
 * Header file that gets prepended at compilation time.
 */

#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>


/**
 * @brief Prints a given int32. 
 * @param n Int32 to print
 */
int32_t Std_printInt(int32_t n) {
    printf("%d\n", n);
    return 0;
}

/**
 * @brief Prints a given string. 
 * @param str String to print
 * @warning No security checks are done on the string
 */
int32_t Std_printString(char* str){
    printf("%s\n", str);
    return 0;
}


/**
 * @brief Reads an int32 on stdin.
 * @warning No format checks are done on the int
 * @return The read int32
 */
int32_t Std_readInt() {
    int n = 0; 
    scanf("%d", &n);
    return n;
}

/**
 * @brief Reads a string on stdin.
 * @warning No format checks are done on the string
 * @return The read string
 */
char* Std_readString() {
    char* str = (char*)calloc(1024, sizeof(char));
    scanf("%1023s", str);
    return str;
}

/**
 * @brief Transforms an int to a string.
 * @param digit Int to be transformed
 * @return The transormed digit in the form of a string
 */
char* Std_digitToString(int32_t digit){
    char* str = (char*)calloc(11, sizeof(char));
    sprintf(str, "%d", digit);
    return str;
}
