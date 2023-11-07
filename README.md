
*Genesis 2*

Note that the following instructions are a work in progress.

There are two builds: the one including dependences will only run on the platform for which it is built and the other can be run from the command line by adding the dependences at launch.

The latest build should work on an ARM Mac (aarch) and should be possible to run by double-clicking on the build that includes dependences.

It is not tested on other platforms but a buld on another platform should work, subject to checking that the right dependences for JavaFX libraries are used.

It is built using Maven on Netebeans 18 and uses OpenJavaFX libraries.

To run the version that does not include dependences on the command line, you need to [download a version ofthe JavaFX SDK](https://gluonhq.com/products/javafx/) -- see more detail beliow; assuming that is in directory named in shell variable `$JAVAFX` and your JAR file is in `$JARF`, you can invoke it as follows:

`java --module-path $JAVAFX/lib --add-modules javafx.controls,javafx.fxml -jar $JARF`

Note that the instructions assume a specific release -- they will be updated.

## Installing for different environemts


* [Download the SDK](https://gluonhq.com/products/javafx/) for the version of JavaFX (21 is used in the current build but earlier versions may work if your OS and Java install don't support 21)  and the architecture and oeprating system  you require
     * NB: note that for macOS there is a choice between x64 and aarch64. x64 is for Macs with Intel based chips, aarch64 is for Macs with Apple's ARM-based CPU. If you're not sure which you have, choose "About Mac". It it says says Mx where x is 1, 2 or 3...,  you need aarch64.
     * Unzip the file. Inside the unzipped directory is a `lib` directory. Move that to a convenient place on your system such as `/usr/local/lib/` and *rename* it to something meaningful (e.g. `JavaFX-21`)
     * Download the `genesis2.jar` (this name will change with a new release) file and move it to a suitable place on your system
     * Edit the `genesis` Bash script file in this repo and replace
          * the definition of `JARF` with the full path and name of the genesis.jar (e.g. `${HOME}/Appllications/genesis2.jar` if you installed it in your own `Applications` folder on a Mac)
	  * the definition of `LIB` with the location of the SDK `lib` file (e.g. `${HOME}/lib/javafx-18-aarch-lib`)
	  * the MODS variable specifying the modules to load should not change

On Winbdows you will need to modify these instructions to fit; on a Unix platform the steps should be similar.
     

Funded by the NIH, Grant U24HG006941
