#!/bin/bash

# Run an instance of Genesis without JavaFX libraries in the build.
# Should work on any Unix-like platform with Bash installed, including
# Linux and macOS.
#
# To set the JAR file name and location, on the command line:
#
# EXPORT JARF=path/jarfilename.jar
#
# to set the JavaFX library on the command line:
#
# EXPORT LIB=path/libraryname
#
# See defaults in this file for examples of how this is done.
# If you set the defaults correctly, you will not need to set
# the command-line variables.

MODS=javafx.controls,javafx.fxml

# Edit the following line to change the default for where the JAR file is and its name
if [ -z ${JARF+${HOME}/Appllications/genesis2.jar} ]
  then echo "JARF is set to default '$JARF'"
  else echo "JARF is set to your '$JARF'"
fi

# Edit the following line to change the default for where the JavaFX library is installed
if [ -z ${LIB+/usr/local/lib/JavaFX-21} ]
  then echo "LIB is set to default '$LIB'"
  else echo "LIB is set to your '$LIB'"
fi


java --module-path $LIB --add-modules $MODS -jar $JARF