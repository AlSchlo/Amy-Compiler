# CLP - Amy Compiler

## Presentation

This project was realized in the context of the Computer Language Processing (CLP) course at EPFL. It implements a compiler for Amy, a custom functional language whose *specifications* can be found in the eponymous folder of this repository. 

A complete report (along with presentation slides) describing in particular the implementation of the C backend can be found in the respective directories.

This work was carried out in collaboration with Adrien Nelson Rey, undergraduate student at EPFL (adrien.rey@epfl.ch).

## Use

The compiler generates either

- WebAssembly code (*by default*)

- C code (*by adding the -cmode flag*)

To generate C/Wasm source code from Amy files, execute the following command after launching a *sbt* session from the root of this repository.

Don't forget to add **all** required dependencies if needed. The pre-written `List`, `Option` and `Std` objects are located in the library directory. 

```sh
run [PATH_DEP_1] [PATH_DEP_2] ... [PATH_DEP_N] [PATH_AMY_MAIN_FILE] (-cmode)
```

Examples of Amy files can be found in the `examples` folder.

## Wasm Mode (or Default)

This mode generates a package of 4 files (HTML, JS, WASM, WAT) in the `/wasmout` newly-created directory. The produced code can simply be executed with *node* as follows.

```sh
node [PATH_JS_MAIN_FILE]
```

## C Mode (-cmode)

This mode generates a single C file in the `/cout` directory. Its name corresponds to the name of the Amy main module, not the Amy main file. The produced source code can for example be compiled with *gcc* and run as follows.
```sh
gcc [PATH_C_MAIN_MODULE] [PATH/cout/error.h] [PATH/cout/std.h]
./[PATH_OUT]
```

## Notes

-  The order of arguments is important.
- Sometimes gcc may issue warnings. These can safely be ignored.
- The generated C file has the name of the Amy main *module*, not the main file.
