
*Genesis 2*

Note that the following instructions are a work in progress.

The latest build should work on an ARM Mac (aarch) and should be possible to run by double-clicking the build including dependences.

It is built using Maven on Netebeans 18 and uses OpenJavaFX libraries.

The following refers to earlier builds and will be updated.


** Installing for different environemts **

* Go to https://gluonhq.com/products/javafx/

* Download the SDK for the version of JavaFX (required 17 or 18)  and the architecture and oeprating system  you require
     * NB: note that for macOS there is a choice between x64 and aarch64. x64 is for Macs with Intel based chips, aarch64 is for Macs with new Silicon chip. If you're not sure which you have, choose "About Mac". It it says says M1 or M2  you need the aarch64.
     * Unzip the file. Inside the unzipped directory is a `lib` directory. Move that to a convenient place on your system and *rename* it to something meaningful (e.g. `javafx-17-aarch-lib`)
     * Download the `genesis2.jar` file and move it to a suitable place on your system
     * Edit the `genesis` file in this repo and replace
          * the definition of `JARF` with the full path and name of the genesis.jar (e.g. `${HOME}/lib/genesis2.jar`)
	  * the definition of `LIB` with the location of the SDK `lib` file (e.g. `${HOME}/lib/javafx-18-aarch-lib`)
     

Funded by the NIH, Grant U24HG006941
