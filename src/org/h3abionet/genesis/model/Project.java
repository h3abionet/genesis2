/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.model;

import org.h3abionet.genesis.Genesis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author scott
 */
public class Project implements java.io.Serializable {

    private static final long serialVersionUID = 2L;

    private String[] colors = new String[]{"#800000", "#000080", "#808000", "#FFFF00", "#860061", "#ff8000", "#008000", "#800080", "#004C4C", "#ff00ff"};
    private String[] icons = new String[]{"M 0.0 10.0 L 3.0 3.0 L 10.0 0.0 L 3.0 -3.0 L 0.0 -10.0 L -3.0 -3.0 L -10.0 0.0 L -3.0 3.0 Z",
            "M0 -3.5 v7 l 4 -3.5z",
            "M5,0 L10,9 L5,18 L0,9 Z",
            "M2,0 L5,4 L8,0 L10,0 L10,2 L6,5 L10,8 L10,10 L8,10 L5,6 L2,10 L0,10 L0,8 L4,5 L0,2 L0,0 Z",
            "M 20.0 20.0  v24.0 h 10.0  v-24   Z",
            "M0,4 L2,4 L4,8 L7,0 L9,0 L4,11 Z",
            "M 2 2 L 6 2 L 4 6 z",
            "M 10 10 H 90 V 90 H 10 L 10 10",
            "M0 -3.5 v7 l 4 -3.5z", // repeated
            "M5,0 L10,9 L5,18 L0,9 Z", // repeated
            "M2,0 L5,4 L8,0 L10,0 L10,2 L6,5 L10,8 L10,10 L8,10 L5,6 L2,10 L0,10 L0,8 L4,5 L0,2 L0,0 Z" // repeated
    };

    private boolean projIsImported;

    // creation of project variables
    private static Project project;
    private String projectName;
    private String phenoFileName;
    private String famFileName;
    private boolean phenoFileProvided;
    private PCAGraph pcaGraph;
    private int phenoColumnNumber; // column with phenotype
    private int currentTabIndex; // set index of the current tab
    private List<String> groupNames = new ArrayList<>();

    private int defaultIconSize = 10; //default icon size
    private HashMap groupColors = new HashMap(); // default group colors e.g. mkk -> #800000
    private HashMap groupIcons =  new HashMap();  // default group icons e.g. mkk -> "M 2 2 L 6 2 L 4 6 z"

    private HashMap iconTypes;
    private ArrayList<int []> selectedPCs = new ArrayList<>(); // for each graph, keep selected pcs

    private ArrayList<String> hiddenPoints = new ArrayList<>(); // store hidden ids of every pc graph in a separate list
    private ArrayList<Subject> pcGraphSubjects; // list of every subject object created
    private ArrayList<ArrayList<Subject>> pcGraphSubjectsList =  new ArrayList<>(); // every graph has it

    private int numOfIndividuals;
    private AdmixtureGraph admixtureGraph;
    private ArrayList<Integer> importedKs = new ArrayList<>(); // store Ks e.g {1, 2, 3, ...}
    private ArrayList<String> iidsList = new ArrayList<>();
    private HashMap<String, ArrayList<String>> famOrder = new HashMap<>();

    public Project(String proj_name, String fam_fname_s) {
        this.projectName = proj_name;
        this.famFileName = fam_fname_s;
        this.phenoFileProvided = false;

        project = this;
        pcGraphSubjects = new ArrayList<>();
        try {
            readFamFile(fam_fname_s);
            setIconTypes();
        }catch (Exception e){
            Genesis.throwInformationException("Wrong fam file provided");
        }
    }

    /**
     * Create a new project
     * @param proj_name
     */
    public Project(String proj_name, String fam_fname_s, String pheno_fname_s, int phenoColumnNumber) {
        this.projectName = proj_name;
        this.famFileName = fam_fname_s;
        this.phenoFileName = pheno_fname_s;
        this.phenoColumnNumber = phenoColumnNumber;
        this.phenoFileProvided = true;
        pcGraphSubjects = new ArrayList<>();

        project = this;
        try {
            readFamFile(fam_fname_s);
            readPhenotypeFile(pheno_fname_s);
            setIconTypes();
        } catch (Exception e){
            Genesis.throwInformationException("Wrong files provided");
        }
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

        BufferedReader r = Genesis.openFile(famFilePath);
        String l = r.readLine();
        String fields[];
        while (l != null) {
            fields = l.split("\\s+");
            fid = fields[0];
            iid = fields[1];
            pat = fields[2];
            mat = fields[3];
            sex = fields[4];
            phe = fields[5];

            // set subjects
            pcGraphSubjects.add(new Subject(fid, iid, pat, mat, sex, phe, colors[0], icons[0], defaultIconSize, false));
            iidsList.add(iid); // keep order of iids

            l = r.readLine();
        }

        numOfIndividuals = pcGraphSubjects.size();

        // set group name, icons and color if only fam file is provided
        groupNames.add("All"); // if no pheno column, name the group All
        famOrder.put("All", iidsList);
        groupColors.put(groupNames.get(0), colors[0]);  // mkk -> #800000
        groupIcons.put(groupNames.get(0), icons[0]); // mkk -> "M0 -3.5 v7 l 4 -3.5z"
    }

    private void readPhenotypeFile(String phenoFilePath) throws FileNotFoundException, IOException {

        // get phenotype groups and assign colors and icons
        setPhenotypeGroups(Genesis.openFile(phenoFilePath));

        BufferedReader secBuf = Genesis.openFile(phenoFilePath);
        String line = secBuf.readLine();
        String fields[];
        while (line != null) {
            fields = line.split("\\s+");

            String fid = fields[0];
            String iid = fields[1];

            // get color and icon for selected pheno group or column
            String chosenPheno = fields[phenoColumnNumber-1];
            String color = (String) groupColors.get(chosenPheno);
            String icon = (String) groupIcons.get(chosenPheno);

            // add pheno details to every subject
            for(Subject sub : pcGraphSubjects){
                if (sub.getFid().equals(fid) && sub.getIid().equals(iid)){
                    sub.setPhenos(fields);
                    sub.setColor(color);
                    sub.setIcon(icon);
                }
            }

            line = secBuf.readLine();
        }

        // categorize fam iids according to phenotype column
        for (Subject subject: pcGraphSubjects){
            String phenoGroupName = subject.getPhenos()[phenoColumnNumber-1];
            if(famOrder.containsKey(phenoGroupName)){
                famOrder.get(phenoGroupName).add(subject.getIid());
            }else {
                ArrayList<String> ls = new ArrayList<>(); // define new list
                ls.add(subject.getIid());
                famOrder.put(phenoGroupName, ls);
            }
        }
    }

    /**
     * create phenotype groups and assign colors and icons
     * @param r
     * @throws IOException
     */
    private void setPhenotypeGroups(BufferedReader r) throws IOException {

        // only used when the pheno file is not provided
        groupNames.clear();
        groupIcons.clear();
        groupColors.clear();
        famOrder.clear();

        String row = r.readLine();
        String rowValues[];
        while (row != null) {
            rowValues = row.split("\\s+");
            // keep track of unique phenotypes
            if (!groupNames.contains(rowValues[phenoColumnNumber-1])) {
                groupNames.add(rowValues[phenoColumnNumber-1]);
            }
            row = r.readLine();
        }

        // set colors and icons for every phenotype
        for (int i = 0; i < groupNames.size(); i++){
            groupColors.put(groupNames.get(i), colors[i]);  // mkk -> #800000
            groupIcons.put(groupNames.get(i), icons[i]); // mkk -> "M0 -3.5 v7 l 4 -3.5z"
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

    public int getPhenoColumnNumber() {
        return phenoColumnNumber;
    }

    // singleton pattern is used to limit creation of a class to only one object
    public static Project getProject() {
        return project;
    }

    public List<String> getGroupNames() {
        return groupNames;
    }

    public int getNumOfIndividuals() {
        return numOfIndividuals;
    }

    public HashMap getGroupColors() {
        return groupColors;
    }

    public HashMap getGroupIcons() {
        return groupIcons;
    }

    public ArrayList<Subject> getPcGraphSubjects() {
        return pcGraphSubjects;
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
        iconTypes = new HashMap<String, String>();
        iconTypes.put("M 0.0 10.0 L 3.0 3.0 L 10.0 0.0 L 3.0 -3.0 L 0.0 -10.0 L -3.0 -3.0 L -10.0 0.0 L -3.0 3.0 Z", "star");
        iconTypes.put("M0 -3.5 v7 l 4 -3.5z", "arrow");
        iconTypes.put("M5,0 L10,9 L5,18 L0,9 Z", "kite");
        iconTypes.put("M2,0 L5,4 L8,0 L10,0 L10,2 L6,5 L10,8 L10,10 L8,10 L5,6 L2,10 L0,10 L0,8 L4,5 L0,2 L0,0 Z", "cross");
        iconTypes.put("M 20.0 20.0  v24.0 h 10.0  v-24   Z", "rectangle");
        iconTypes.put("M0,4 L2,4 L4,8 L7,0 L9,0 L4,11 Z", "tick");
        iconTypes.put("M 2 2 L 6 2 L 4 6 z", "triangle");
        iconTypes.put("M 10 10 H 90 V 90 H 10 L 10 10", "square");
    }

    public HashMap getIconTypes() {
        return iconTypes;
    }

    public int getDefaultIconSize() {
        return defaultIconSize;
    }

    public ArrayList<int []> getSelectedPCs() {
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
}
