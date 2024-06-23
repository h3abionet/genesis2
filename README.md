[![biotools:genesis2](https://img.shields.io/badge/biotools-genesis2-blue)](https://bio.tools/genesis2)
[![fair-software.eu](https://img.shields.io/badge/fair--software.eu%E2%97%8F%20%20%E2%97%8F%20%20%E2%97%8B%20%20%E2%97%8F%20%20%E2%97%8B-orange)](https://fair-software.eu)
# Genesis 2


### Execution instructions

Note that the following instructions are a work in progress.

It is built using Maven on NetBeans 18 and uses OpenJavaFX libraries. The Linux build used NetBeans 19 so there is some evidence that other NetBeans versions should work but this is not tested. 

There are two builds: the one including dependences will only run on the platform for which it is built and the other can be run from the command line by adding the dependences at launch.

The latest release (2.4b, the first beta release candidate for final release) includes builds for:

* an ARM Mac (`Genesis2-2.4b-macArm64.jar` -- built on macOS Sonoma 14.4.1)
* an Intel Mac (`Genesis2-2.4b-macX64.jar` -- built on macOS Monterey 12.6.8)
* Linux (`Genesis2-2.4b-ubuntuX64.jar` -- built on Ubuntu 22.04.3 LTS)
* Windows (`Genesis2-2.4b-winX64.jar` -- built on Windows 11)
* Generic: requires a command-line that attaches the local JavaFX librariews (`Genesis2-2.4b-Generic.jar`)

and should be possible to run by double-clicking on the build in the file browser (Finder in the Mac). On Ubuntu you may need to add execute permissions. Either look at Properties in the file manager where you can set execute permission -- easy in GNOME -- or on the command line:

    chmod +x Genesis2-2.4b-ubuntuX64.jar

If you wish to run from the command line, the following should work (adjusted to the JAR file for your environment):

    java  -jar Genesis2-2.4b-ubuntuX64.jar
    
You will find more information in the docs directory; the PDF file `Genesis_2_documentation_V1_1_1.pdf` documents design choices and how far along the project is towards a robust, sustainable build.


## Other builds
    

A build on another platform should work, subject to checking that the right dependences for JavaFX libraries are used. The way the project is set up, Maven should automatically find them.

To run the version that does not include dependences on the command line, you need to download a version of the [JavaFX SDK](https://gluonhq.com/products/javafx/).

Assuming the JavaFX library is in directory named in shell variable `$JAVAFX` and your JAR file is in `$JARF`, you can invoke it as follows:

    java --module-path $JAVAFX --add-modules javafx.controls,javafx.fxml,javafx.swing -jar $JARF

*Note*: the library path should go to the actual contents so if e.g. the path is `/usr/local/lib/JavaFX/lib` then include the trailing part of the path after `/lib`. In my examples, the libraries are not in another layer of `lib` directory as this is implied by the rest of the path.

There is a Bash script `genesis.sh` in this repository (in `scripts`) that can run the above. To invoke, create environment variables that the script will use (it has defaults if you don’t do this). To the the above effect (modifying the paths to suit your install):

    export JAVAFX=/usr/local/lib/JavaFX-21
    export JARF=$HOME/Applications/Genesis2-2.4b-Generic.jar

Note: in Unix shell scripting, you can create a shell variable wuthout using the word `EXPORT` but the value will not be visible to a child process, i.e., the script will not see it. The JavaFX path should contain the actual library files -- if they are in a directory called `lib` append that to the path.

## Building on different environemts


* Install [git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git) if you don't have it already; there are versions for all Unix platforms (including Mac) and Windows
* On the command line (assuming you want this branch), run `git clone -b Philip https://github.com/h3abionet/genesis2.git`
* Install [JDK](https://www.oracle.com/java/technologies/downloads/) (21 is tested; earlier versions may work) -- make sure you get the right version for your platform
  * For a Mac, you want the DMG installer for ARM (if your machine is an M1, M2, M3, etc, model, x64 otherwise)
  * For Linux, whether you want a DEB or RPM depends on the Linux distribution (also there are options for both ARM and x86; for Linux x86 is more common)
  * For Windows, either the .exe or the MSI installer should work
* Install [NetBeans](https://netbeans.apache.org/front/main/download/index.html) ([version 18](https://netbeans.apache.org/front/main/download/nb18/) includes a Mac installer, though 19 can also run on all platforms)
* Install [SceneBuilder](https://gluonhq.com/products/scene-builder/) if you want to edit the user interface
* Open Netbeans, select Open Project and navigate to the place you downloaded using `git` -- the top-level directory (folder) should be called `genesis` and NetBeans will recognize this as a project
* Look for **Clean and Build** under the **Run** menu, or the clean and bulid icon (hammer and broom) and select either of them.
  * Look in the `target` directory (folder); you should find two JAR files:
     * the one with the name ending with `with-dependencies.jar` should be possible to launch from the file browser (Finder on a Mac)
     * the other JAR file does not include the JavaFX libraries and should be possible to launch from the command line using the  `genesis.sh` script (or a variation suitable for Windows)

## Test data
Data to test functionality is available in the repository in directory `agm-demo/agm-genesis/sample-data/` (Windows users can rotate the slashes to suit).

Data file types available to open are:

* `.g2f` -- a Genesis2 project (the first thing to open, unless you want to start from scratch); you can then use
  * `.eigenvec` -- PCA file
  * `.Q` -- Admixture file
    * when opening one of these, you are not restricted as to file type as there is no standard for admixture files
    * this will add another pane to graph the admixture plot
    * you can open more than one admixture file and there will be an additional plot for each
* If you start a new project instead, you need to add the following file types:
  * `.fam` -- a [FAM](https://www.cog-genomics.org/plink/1.9/formats#fam) file
  * `.phe` -- phenotype file
  * You will also need to import a PCA file (`.eigenvec`) before anything is graphed

## Known issues

Latest fixes: 

* changing order of admix charts, deleting and added works across save-quit-load; I also worked on the aspect ratio of saved PDFs from the admix pane, which were quite far off (circles looekd oval)
* if a `fam` or `phe` file has any rows (lines) with a differing number of columns (fields) from the first row, an error is thrown, reporting the first erroneous line
* saving and restoring hidden groups or items is fixed and should work, including being able to unhide before or after saving; this change qualifies for a version change; the newest release is 2.4b
* rescaling window contents when resizing a window works, though non-uniform rescaling distorts shapes (e.g. a circle becomes an oval). Showing and hiding individual features works and works across save-quit-load.

Tested on a Mac: if you hold SHIFT while resizingf a window, it rescales uniformly (i.e. maintains the aspect ratio).

When you save as a different name, the project now remembers the new name so when you save again, it offers the new name not its original file name.

Earlier commits shows a lot of other fixes in the README; deleted from here for brevity.

New issues now at the top.

1. On Ubuntu, saving an image file does not set the suffix (extension) to the file type yo select; you must type it yourself. If it is a valid file type, it will be saved as that type (otherwise nothing happens).<br>**I would like to fix this so I did not for now put in a warning if the file does not save.**
2. On Ubuntu, the code for opening a PDF after saving it breaks so I took it out for now.
3. If a `.fam` or phenotype file (`.phe`) has any lines not the correct length, a warning is issued and setting up the new project ends; the first line that is the wrong length is given. The number of fields (columns) is set by the first line (row).
4. Sometimes a dialog (e.g. save file) opens behind the main window; the fix for now is to move the main window to see the dialog.
5. If a new project is started (using **New**), you cannot change to another project in the same session: both **New** and **Import** are no longer available.
6. If you open a project (**Load**), you can no longer create a new project, though you can load another project (or another instance of the same one -- why you would want this is not clear).
7. There is no way to close a project once it is opened. You can close all the individual panes but when the last closes, it asks you to save, without the load to open or create another project. This is not consistently done with all close modes nor is track kept of whether to save. Saving should be different from saving as a new name.
8. Saving saves everything that is currently visible; this possibly is what you want, including highlighting if the mouse was over an object (minus the problems of saving a hidden group, see below).
9. Annotations are mostly completely correct. But note that if you resize the window, that is a zoomed in or out view; that does not change how the graphics content is internally represented saved.<br>**The aspect ratio is not necessarily maintained when you resize a window (e.g. circle becomes oval) but is correct as saved and with exporting the image.**
10. The **Show Hidden** feature mostly works except that changing the legend format does not save.
11. A few exceptions get thrown, but not consistently.<br>**_Some arose from user interface features not completely or not correctly implemented: much less an issue now._**
12. If you see a message like this when launching from the command line, it could mean you need a version of the JavaFX library (there is a compatibility issue with macOS 14.x; as tested it does not cause any other error):<br>`May 02, 2024 8:48:49 AM`<br>`com.sun.glass.ui.mac.MacApplication`<br>`lambda$waitForReactivation$6`<br>**_Fixed by installing JFX libraries v 21.0.3 to replace 21. If you change the library version, you also need to update it in `pom.xml`_**


The main difficulty in correcting annotations issues is that the way annotations are implemented is clumsy. There is a single `Annotations` class that represents every variation and the different annotation classes each embed this class; the proposed fix: a top-level `Annotation` class that only contains the common properties of all annotations and derived classes that implement functions specific to that annotation type. This will make it easier to record the state of the annotation before edits and restore it if the edit is cancelled. It is also weird that the Annotation class is in package Model while the uses of it are in Controller. You could argue that the contents of an annotation are part of the data but why are methods to manipulate it split between the model and controller?
<br><br>
**_Rather than do this, I now focus on transferring state between JavaFX view and model classes in *X*options.java files where *X* is a particular annotation type._**

Another issue is the confusing way a project is implemented; some thought needs to go into re-architecting this.
     

Funded by the NIH, Grant U24HG006941
