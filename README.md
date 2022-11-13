To generate the C source code of the Amy examples, just execute the following command:

--> run [PATH_DEP_1] [PATH_DEP_2] ... [PATH_DEP_N] [PATH_AMY_MAIN_FILE_EXAMPLE]

NOTES: 

1. The order of arguments is important.
2. The C file will be generated in cout.
3. No automated tests have been coded.
4. When compiling in C to test the behaviour don't forget the std.h and error.h provided libraries in cout.
5. Sometimes the C compiler will complain with warnings, just ignore them.
6. The C file generated has the name of the Amy main module (and not the name of the Amy main file).