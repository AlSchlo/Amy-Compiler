/**
 * @file error.h
 * @brief Error utility function implementation and signature.
 * Header file that gets prepended at compilation time.
 */

#include <assert.h>
#include <stdio.h>
#include <stdlib.h>

/**
 * @brief Throws an assert error with a given message. 
 * @param str The message of the error.
 */
int32_t Error_error(const char* str) {
    fprintf(stderr, "%s\n", str);
    exit(-1);
}