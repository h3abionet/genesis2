
# Genesis 2


### Execution instructions

Note that the following instructions are a work in progress.

It is built using Maven on NetBeans 18 and uses OpenJavaFX libraries. The Linux build used NetBeans 19 so there is some evidence that other NetBeans versions should work but this is not tested. 

There are two builds: the one including dependences will only run on the platform for which it is built and the other can be run from the command line by adding the dependences at launch.

The latest release includes builds for:

* an ARM Mac (Genesis2JavaFX-2.1.1a-Mac-ARM.jar -- built on macOS Sonoma 14.0)
* an Intel Mac (Genesis2JavaFX-2.1.1a-Mac-Intel.jar -- built on macOS Monterey 12.6.8)
* Linux (Genesis2JavaFX-2.1.1a-Ubuntu.jar -- built on Ubuntu 22.04.3 LTS)
* Windows (Genesis2JavaFX-2.1.1a-Windows.jar -- built on Windows 11)

and should be possible to run by double-clicking on the build in the file browser (Finder in the Mac). On Ubuntu you may need to add execute permissions. Either look at Properties in the file manager where you can set execute permission -- easy in GNOME -- or on the command line:

    chmod +x pathtojarfile/Genesis2JavaFX-2.1.1a-Ubuntu.jar

If you wish to run from the command line, the following should work

    java  -jar Genesis2JavaFX-2.1.1a-Mac-ARM.jar

(Use the JAR file for your environment)

## Other builds
    

A build on another platform should work, subject to checking that the right dependences for JavaFX libraries are used. The way the project is set up, Maven should automatically find them.

To run the version that does not include dependences on the command line, you need to download a version of the [JavaFX SDK](https://gluonhq.com/products/javafx/).

Assuming the JavaFX library is in directory named in shell variable `$JAVAFX` and your JAR file is in `$JARF`, you can invoke it as follows:

    java --module-path $JAVAFX --add-modules javafx.controls,javafx.fxml -jar $JARF

*Note*: the library path should go to the actual contents so if e.g. the path is `/usr/local/lib/JavaFX/lib` then include the trailing `/lib`. In my examples, the libraries are not in another layer of `lib` directory as this is implied by the rest of the path.

There is a Bash script `genesis.sh` in this repository (in `scripts`) that can run the above. To invoke, create environment variables that the script will use (it has defaults if you don’t do this). To the the above effect (modifying the paths to suit your install):

    export JAVAFX=/usr/local/lib/JavaFX-21
    export JARF=$HOME/Applications/Genesis2JavaFX-2.1.1a.jar

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

Latest fix: rescaling window contents when resizing a window works, though non-uniform rescaling distorts shapes (e.g. a circle becomes an oval).

Tested on a Mac: if you hold SHIFT while resizingf a window, it rescales uniformly (i.e. maintains the aspect ratio).

Last commit shows a lot of other fixes in the README; deleted from here for brevity.

1. If a new project is started (using **New**), you cannot change to another project in the same session: both **New** and **Import** are no longer available.

2. If you open a project (**Load**), you can no longer create a new project, though you can load another project.

3. There is no way to close a project once it is opened. You can close all the individual panes but when the last closes, it asks you to save, without the load to open or create another project.

4. Saving saves everything that is currently visible; this possibly is what you want, though it can be confusing as to what name to use to save.

5. Annotations are mostly completely correct. But note that if you resize the window, that is a zoomed in or out view; that does not change how it is saved.  


6. The **Show/Hide** feature works partially (see below). **The label is changed to _Show hidden_.**

7. Changing the legend does not save.
8. Hiding a group via the legend doesn't save and reopen properly; unhide then save also breaks.
9. Hiding a group or an individual vial secondary-click on part of the chart does not save correctly (exception thrown when loading the file).
  * _For now, I have disabled saving remove group and removing an individual doesn't actually do anything though the interface is there; in the original code before my changes, removing individuals did work but removing groups also threw an exception when opening the saved file. This is the exeption thrown<br>_`java.io.WriteAbortedException: writing aborted;`<br> `java.io.NotSerializable`<br>`Exception:<br>javafx.scene.chart.XYChart$Series`

8. A few exceptions get thrown, but not consistently.
	*  _Some arose from user interface features not completely or not correctly implemented: much less abn issue now._

9. If you see a message like this when launchingfrom the command line, it could mean you need a version of the JavaFX library (there is a compatibility issue with macOS 14.x; as tested it does not cause any other error):

`May 02, 2024 8:48:49 AM`
`com.sun.glass.ui.mac.MacApplication`
`lambda$waitForReactivation$6`


* _Fixed by installing JFX libraries v 21.0.3 to replace 21._


The main difficulty in correcting the annotations issues is that the way annotations are implemented is clumsy. There is a single `Annotations` class that represents every variation and the different annotation classes each embed this class; the proposed fix: a top-level `Annotation` class that only contains the common properties of all annotations and derived classes that implement functions specific to that annotation type. This will make it easier to record the state of the annotation before edits and restore it if the edit is cancelled. It is also weird that the Annotation class is in package Model while the uses of it are in Controller. You could argue that the contents of an annotation are part of the data but why are methods to manipulate it split between the model and controller?

* _Rather than do this, I now focus on transferring state between JavaFX view and model classes in *X*options.java files where *X* is a particular annotation type._

Another issue is the confusing way a project is implemented; some thought needs to go into re-architecting this.
     

Funded by the NIH, Grant U24HG006941
