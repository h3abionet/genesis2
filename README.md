[![biotools:genesis2](https://img.shields.io/badge/biotools-genesis2-blue)](https://bio.tools/genesis2)
[![fair-software.eu](https://img.shields.io/badge/fair--software.eu-%E2%97%8F%20%20%E2%97%8F%20%20%E2%97%8B%20%20%E2%97%8F%20%20%E2%97%8B-orange)](https://fair-software.eu)
# Genesis 2


### Execution instructions

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


## User manual

A detailed user manual can be found on our [GitHub pages documentation](https://h3abionet.github.io/genesis2#user_manual)


## Other builds

Documentation on building on other platforms can be found in our [GitHub pages documentation](https://h3abionet.github.io/genesis2#documentation_for_building_on_other_platforms)

## Technical manual

For those wanting to fork or contribute our [Technical manual](docs/Genesis_2_documentation_V1_1_1.pdf) may be useful

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
