/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.model;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import org.h3abionet.genesis.Genesis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.scene.control.Alert.AlertType;

/**
 *
 * @author scott
 */
public class Project implements Serializable {

    private static final long serialVersionUID = 2L;

    IconsAndColors iconsAndColors = new IconsAndColors();

    private String[] colors = iconsAndColors.getListOfColors();
    private String[] icons = iconsAndColors.getListOfIcons();

    private boolean projIsImported;

    // creation of project variables
    private Project project;
    private String projectName;
    private String phenoFileName;
    private String famFileName;
    private boolean phenoFileProvided;
    private PCAGraph pcaGraph;
    private int phenoColumnNumber; // column with phenotype
    private transient int phenoColumnCount,
            famColCount;
    private int currentTabIndex; // set index of the current tab
    private ArrayList<String> groupNames = new ArrayList<>();
    private ArrayList<String> pcaLegendItems = new ArrayList<>();
    private ArrayList<String> orderOfLegendItems = new ArrayList<>();

    private int defaultIconSize = 15; //default icon size
    private HashMap pcaGroupColors = new HashMap(); // default group colors e.g. mkk -> #800000
    private HashMap pcaGroupIcons = new HashMap();  // default group icons e.g. mkk -> "M 2 2 L 6 2 L 4 6 z"
    private List<ArrayList<String>> admixtureAncestryColor = new ArrayList<>();

    private HashMap iconTypes;
    private ArrayList<int[]> selectedPCs = new ArrayList<>(); // for each graph, keep selected pcs

    private ArrayList<String> hiddenPoints = new ArrayList<>(); // store hidden ids of every pc graph in a separate list
    private ArrayList<Subject> subjectsList; // list of every subject object created
    private ArrayList<ArrayList<Subject>> pcGraphSubjectsList = new ArrayList<>(); // every graph has it
    private ArrayList<ArrayList<Annotation>> pcGraphAnnotationsList = new ArrayList<>(); // every graph has it

    private int numOfIndividuals;
    private AdmixtureGraph admixtureGraph;
    private ArrayList<Integer> importedKs = new ArrayList<>(); // store Ks e.g {1, 2, 3, ...}
    private ArrayList<String> iidsList = new ArrayList<>();
    private HashMap<String, ArrayList<String>> famOrder = new HashMap<>();
    private ArrayList<PCAGraphLayout> pcaGraphLayoutList = new ArrayList<>();
    private boolean famCreated;
    private boolean phenoCorrect,
            famCorrect;
    private ArrayList<String> hiddenGroups = new ArrayList<>(); // [CEU, MKK, ...]

    private double stageWidth;
    private double stageHeight;

    // chart groups with subjects
    HashMap<String, ArrayList<Subject>> subjectGroups = new HashMap<>();
    private String pcaFileName;
    private ArrayList<Annotation> admixtureAnnotationList = new ArrayList<>();
    private boolean isAdmixtureVertical;
    private boolean isAdmixtureHorizontal;

    public Project(String proj_name, String fam_fname_s) {
        System.out.println("In constructor Project(String proj_name, String fam_fname_s)");
        this.projectName = proj_name;
        this.famFileName = fam_fname_s;
        this.phenoFileProvided = false;
        
        phenoCorrect = false;
        famCorrect = false;
        
        System.out.println("In constructor Project(String proj_name, String fam_fname_s)");

        setProject(this);
        subjectsList = new ArrayList<>();
        try {
            readFamFile(fam_fname_s);
            if (!famCorrect) {
                fam_fname_s="";
                return;
            }
            setIconTypes();
            createGroups();
        } catch (Exception e) {
            Genesis.reportInformationException("Wrong fam file provided");
        }

        Genesis.getMainStage().widthProperty().addListener((obs, oldVal, newVal) -> {
            stageWidth = (double) newVal;
        });

        Genesis.getMainStage().heightProperty().addListener((obs, oldVal, newVal) -> {
            stageHeight = (double) newVal;
        });

        addResizeEventToStage();
    }

    /**
     * Create a new project
     *
     * @param proj_name
     */
    public Project(String proj_name, String fam_fname_s, String pheno_fname_s, int phenoColumnNumber) throws IOException {
        System.out.println("In constructor Project(String proj_name, String fam_fname_s, String pheno_fname_s, int phenoColumnNumber)");
        this.projectName = proj_name;
        this.famFileName = fam_fname_s;
        this.phenoFileName = pheno_fname_s;
        this.phenoColumnNumber = phenoColumnNumber;
        this.phenoFileProvided = true;
        subjectsList = new ArrayList<>();

        phenoCorrect = false;
        famCorrect = false;

        setProject(this);
        readFamFile(fam_fname_s);
        if (!famCorrect) {
//            fam_fname_s = "";
            return;
        }
        readPhenotypeFile(pheno_fname_s);

        if (phenoCorrect && famCorrect) {
            setIconTypes();
            createGroups();

            addResizeEventToStage();
        }

        // FIXME: starting poiint for maintaining aspect ratio when resizing
        double aspectRatio = Genesis.getMainStage().widthProperty().getValue()
                / Genesis.getMainStage().heightProperty().getValue();

//        System.out.println("Aspect ratio " + aspectRatio);

    }

    public void addResizeEventToStage() {

        Genesis.getMainStage().widthProperty().addListener((obs, oldVal, newVal) -> {
            stageWidth = (double) newVal;
        });

        Genesis.getMainStage().heightProperty().addListener((obs, oldVal, newVal) -> {
            stageHeight = (double) newVal;
        });
    }

    public void setProject(Project project) {
        this.project = project;
    }

    private void createGroups() {
        // add groups to the map
        for (String group : groupNames) {
            subjectGroups.put(group, new ArrayList<>());
        }
        // add subject to various groups
        for (Subject sub : project.getSubjectsList()) {
            int phenoPosition = project.getPhenoColumnNumber() - 1;
            if (sub.getPhenos() != null) {
                subjectGroups.get(sub.getPhenos()[phenoPosition]).add(sub);
            }
        }
    }

    public HashMap<String, ArrayList<Subject>> getSubjectGroups() {
        return subjectGroups;
    }

    public boolean isPhenoFileProvided() {
        return phenoFileProvided;
    }

    /**
     *
     * @param famFilePath
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void readFamFile(String famFilePath) throws FileNotFoundException, IOException {
        String fid;
        String iid;
        String pat;
        String mat;
        String sex;
        String phe;
        int rownumber = 0;
        
        famCorrect = false;

        if (famFilePath != null) { // is fam file provided?
            try {
                BufferedReader r = Genesis.openFile(famFilePath);
                String l = r.readLine();
                rownumber++;
                String[] fields;
                famColCount = l.split("\\s+").length;

                while (l != null) {
                    fields = l.split("\\s+");

                    if (famColCount != fields.length) {
                        Genesis.reportInformationException(
                                "Reading Fam file: row number "
                                + rownumber + ": `"
                                + fields
                                + "' should have " + famColCount
                                + " columns but has " + fields.length
                                + ". Canceling file read");
                        famCorrect = false;
                        return;
                    }
                    
                    fid = fields[0];
                    iid = fields[1];
                    pat = fields[2];
                    mat = fields[3];
                    sex = fields[4];
                    phe = fields[5];

                    // set subjects
                    subjectsList.add(new Subject(fid, iid, pat, mat, sex, phe, colors[0], icons[0], defaultIconSize, false));

                    iidsList.add(iid); // keep order of iids

                    l = r.readLine();
                    rownumber++;
                }

                famCreated = true; // fam file successfully imported

                numOfIndividuals = subjectsList.size();

                // set group name, icons and color if only fam file is provided
                groupNames.add("All"); // if no pheno column, name the group All
                pcaLegendItems.add("All"); // only one legend item
                famOrder.put("All", iidsList);
                pcaGroupColors.put(groupNames.get(0), colors[0]);  // mkk -> #800000
                pcaGroupIcons.put(groupNames.get(0), icons[0]); // mkk -> "M0 -3.5 v7 l 4 -3.5z"
            } catch (Exception e) {
                famCreated = false;
                String famError = "There was a problem in reading the fam file. "
                        + "Make sure the file is in this format \"RYCS149 WITS149 1 2 1 1\" - starting with individual iids in the first line";
                Alert dialog = new Alert(Alert.AlertType.ERROR, famError, ButtonType.OK);
                dialog.show();
            }

            famCorrect = true;
        } else {
            famCorrect = true; // if the fam file name is not provided, do nothing
        }
    }

    private void readPhenotypeFile(String phenoFilePath) throws IOException {
        int rownumber = 0;
        try {

        if (famCreated == false) {
            BufferedReader reader = Genesis.openFile(phenoFilePath);
            String row = reader.readLine();
            phenoColumnCount = row.split("\\s+").length;
            String[] fields;
            rownumber = 1;

            // create the subjects
            while (row != null) {
                fields = row.split("\\s+");
                if (phenoColumnCount != fields.length) {
                    Genesis.reportInformationException(
                            "Reading Pheno, no Fam file: row number "
                            +rownumber+ ": `" 
                            + row
                            + "' should have " + phenoColumnCount
                            + " columns but has " + fields.length
                            + ". Canceling file read");
                    phenoCorrect = false;
                    return;
                }
                String fid = fields[0];
                String iid = fields[1];
                // set subjects
                subjectsList.add(new Subject(fid, iid, null, null, null, null, colors[0], icons[0], defaultIconSize, false));
                // set iids
                iidsList.add(iid); // keep order of iids
                row = reader.readLine();
                rownumber++;
            }
        }

            // get phenotype groups and assign colors and icons
            setPhenotypeGroups(Genesis.openFile(phenoFilePath));
            if (!phenoCorrect)
                return;

            BufferedReader secBuf = Genesis.openFile(phenoFilePath);
            String line = secBuf.readLine();
            rownumber = 1;
            phenoColumnCount = line.split("\\s+").length;
            String[] fields;
            while (line != null) {
                fields = line.split("\\s+");

                String fid = fields[0];
                String iid = fields[1];

                // get color and icon for selected pheno group or column
                if (phenoColumnCount != fields.length) {
                    Genesis.reportInformationException(
                            "Reading Pheno: row number "
                            + rownumber + ": `"
                            + line
                            + "' should have " + phenoColumnCount
                            + " columns but has " + fields.length
                            + ". Canceling file read");
                    phenoCorrect = false;
                    return;
                }

                String chosenPheno = fields[phenoColumnNumber - 1];
                String color = (String) pcaGroupColors.get(chosenPheno);
                String icon = (String) pcaGroupIcons.get(chosenPheno);

                // add pheno details to every subject
                for (Subject sub : subjectsList) {
                    if (sub.getFid().equals(fid) && sub.getIid().equals(iid) && sub.getPhenos() == null) {
                        sub.setPhenos(fields);
                        sub.setColor(color);
                        sub.setIcon(icon);
                        break;
                    }
                }

                line = secBuf.readLine();
                rownumber++;
            }

            // remove all the subjects with no phenos
            ArrayList<Subject> deleteSubsList = new ArrayList<>();
            for (Subject sub : subjectsList) {
                if (sub.getPhenos() == null) {
                    deleteSubsList.add(sub);
                }
            }
            subjectsList.removeAll(deleteSubsList);

            // change number of individuals
            numOfIndividuals = subjectsList.size();

            phenoCorrect = true; // phenotype file successfully imported

            // categorize fam iids according to phenotype column
            for (Subject subject : subjectsList) {
                String phenoGroupName = subject.getPhenos()[phenoColumnNumber - 1];
                if (famOrder.containsKey(phenoGroupName)) {
                    famOrder.get(phenoGroupName).add(subject.getIid());
                } else {
                    ArrayList<String> ls = new ArrayList<>(); // define new list
                    ls.add(subject.getIid());
                    famOrder.put(phenoGroupName, ls);
                }
            }

        } catch (Exception e) {
            phenoCorrect = false;
            Genesis.reportInformationException(e.getMessage());
        }
    }

    /**
     * create phenotype groups and assign colors and icons
     *
     * @param r
     * @throws IOException
     */
    private void setPhenotypeGroups(BufferedReader r) throws IOException {

        // only used when the pheno file is not provided
        pcaLegendItems.clear();
        groupNames.clear();
        pcaGroupIcons.clear();
        pcaGroupColors.clear();
        famOrder.clear();
        int rownumber = 1;

        String row = r.readLine();
        String[] rowValues;
        while (row != null) {
            rowValues = row.split("\\s+");
            phenoColumnCount = rowValues.length;
            if (phenoColumnCount != rowValues.length) {
                Genesis.reportInformationException(
                        "setPhenotypeGroups: row number "
                        + rownumber + ": `"
                        + row
                        + "' should have " + phenoColumnCount
                        + " columns but has " + rowValues.length
                        + ". Canceling file read");
                phenoCorrect = false;
                return;
            }
            // keep track of unique phenotypes
            if (!groupNames.contains(rowValues[phenoColumnNumber - 1])) {
                groupNames.add(rowValues[phenoColumnNumber - 1]);
                pcaLegendItems.add(rowValues[phenoColumnNumber - 1]);
            }
            row = r.readLine();
            rownumber++;

            // set colors and icons for every phenotype
            for (int i = 0; i < groupNames.size(); i++) {
                pcaGroupColors.put(groupNames.get(i), colors[i]);  // mkk -> #800000
                pcaGroupIcons.put(groupNames.get(i), icons[i]); // mkk -> "M0 -3.5 v7 l 4 -3.5z"
            }
            phenoCorrect = true;
        }
    }

    public void setAdmixtureGraph(AdmixtureGraph admixtureGraph) {
        this.admixtureGraph = admixtureGraph;
    }

    public AdmixtureGraph getAdmixtureGraph() {
        return admixtureGraph;
    }

    public PCAGraph getPcaGraph() {
        return pcaGraph;
    }

    public void setPcaGraph(PCAGraph pcaGraph) {
        this.pcaGraph = pcaGraph;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String newName) {
        projectName = newName;
    }

    public int getPhenoColumnNumber() {
        return phenoColumnNumber;
    }

    // singleton pattern is used to limit creation of a class to only one object
    public Project getProject() {
        return project;
    }

    public ArrayList<String> getGroupNames() {
        return groupNames;
    }

    public int getNumOfIndividuals() {
        return numOfIndividuals;
    }

    public HashMap getPcaGroupColors() {
        return pcaGroupColors;
    }

    public HashMap getPcaGroupIcons() {
        return pcaGroupIcons;
    }

    public ArrayList<Subject> getSubjectsList() {
        return subjectsList;
    }

    public ArrayList<ArrayList<Subject>> getPcGraphSubjectsList() {
        return pcGraphSubjectsList;
    }

    public void setCurrentTabIndex(int tabIndex) {
        this.currentTabIndex = tabIndex;
    }

    public int getCurrentTabIndex() {
        return currentTabIndex;
    }

    public ArrayList<String> getHiddenPoints() {
        return hiddenPoints;
    }

    public void setIconTypes() {
        // name the shapes
        iconTypes = iconsAndColors.getIconTypes(); // in a HashMap
    }

    public HashMap getIconTypes() {
        return iconTypes;
    }

    public int getDefaultIconSize() {
        return defaultIconSize;
    }

    public ArrayList<int[]> getSelectedPCs() {
        return selectedPCs;
    }

    public ArrayList<Integer> getImportedKs() {
        return importedKs;
    }

    public void setImportedKs(Integer kValue) {
        importedKs.add(kValue);
    }

    public HashMap<String, ArrayList<String>> getFamOrder() {
        return famOrder;
    }

    public void setProjIsImported(boolean projIsImported) {
        this.projIsImported = projIsImported;
    }

    public boolean isProjIsImported() {
        return projIsImported;
    }

    public ArrayList<PCAGraphLayout> getPCAGraphLayouts() {
        return pcaGraphLayoutList;
    }

    public boolean isFamCreated() {
        return famCreated;
    }

    public boolean isPhenoCorrect() {
        return phenoCorrect;
    }

    private String getExtension(String filePath) {
        String extension = "";

        int i = filePath.lastIndexOf('.');
        if (i > 0) {
            extension = filePath.substring(i + 1);
        }
        return extension;
    }

    public ArrayList<String> getHiddenGroups() {
        return hiddenGroups;
    }

    /**
     * save group names every time uses modifies them
     *
     * @param oldGroupName
     * @param newGroupName
     */
    public void renameGroupName(String oldGroupName, String newGroupName) {
        // change the group name in project
        int index = groupNames.indexOf(oldGroupName);
        project.getGroupNames().set(index, newGroupName);

        // change subject pheno
        int phenoIndex = phenoColumnNumber - 1;
        for (Subject sub : subjectsList) {
            if (sub.getPhenos()[phenoIndex].equals(oldGroupName)) {
                sub.getPhenos()[phenoIndex] = newGroupName;
            }
        }

        // change the key for the icons and colors
        pcaGroupIcons.put(newGroupName, pcaGroupIcons.remove(oldGroupName));
        pcaGroupColors.put(newGroupName, pcaGroupColors.remove(oldGroupName));
    }

    public ArrayList<String> getPcaLegendItems() {
        return pcaLegendItems;
    }

    public ArrayList<String> getOrderOfLegendItems() {
        return orderOfLegendItems;
    }

    public void setPcaFileName(String pcaFileName) {
        this.pcaFileName = pcaFileName;
    }

    public String getPcaFileName() {
        return pcaFileName;
    }

    public ArrayList<ArrayList<Annotation>> getPcGraphAnnotationsList() {
        return pcGraphAnnotationsList;
    }

    public ArrayList<Annotation> getAdmixtureAnnotationsList() {
        return admixtureAnnotationList;
    }

    public void removeAnnotation(Tab selectedTab, Annotation annotationType) {
        if (selectedTab.getId().contains("admix")) {
            project.getAdmixtureAnnotationsList().remove(annotationType);
        }
        // if pca tab - remove annotation
        if (selectedTab.getId().contains("tab")) {
            String[] s = selectedTab.getId().split(" "); // [pca, 0] or [pca, 11]
            int tabIndex = Integer.valueOf(s[1]);
            project.getPcGraphAnnotationsList().get(tabIndex).remove(annotationType);
        }
    }

    public boolean isAdmixtureVertical() {
        return isAdmixtureVertical;
    }

    public void setAdmixtureVertical(boolean admixtureVertical) {
        isAdmixtureVertical = admixtureVertical;
    }

    public boolean isAdmixtureHorizontal() {
        return isAdmixtureHorizontal;
    }

    public void setAdmixtureHorizontal(boolean admixtureHorizontal) {
        isAdmixtureHorizontal = admixtureHorizontal;
    }

    public List<ArrayList<String>> getAdmixtureAncestryColor() {
        return admixtureAncestryColor;
    }

    public double getStageWidth() {
        return stageWidth;
    }

    public void setStageWidth(double stageWidth) {
        this.stageWidth = stageWidth;
    }

    public double getStageHeight() {
        return stageHeight;
    }

    public void setStageHeight(double stageHeight) {
        this.stageHeight = stageHeight;
    }
}
