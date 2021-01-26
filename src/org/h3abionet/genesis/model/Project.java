/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.model;

import java.io.BufferedReader;
import java.util.*;
import java.io.FileNotFoundException;
import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.h3abionet.genesis.Genesis;

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

    // creation of project variables
    private static Project project;
    private String projectName;
    private String phenoFileName;
    private String famFileName;
    private PCAGraph pcaGraph;
    private int phenoColumnNumber; // column with phenotype
    private int currentTabIndex; // set index of the current tab
    private List<String> groupNames = new ArrayList<>();

    private int defaultIconSize = 10; //default icon size
    private HashMap groupColors = new HashMap(); // default group colors e.g. mkk -> #800000
    private ArrayList<HashMap> listOfGraphsGroupColors = new ArrayList<>(); // for every pc graph, create its group colors
    private HashMap groupIcons =  new HashMap();  // default group icons e.g. mkk -> "M 2 2 L 6 2 L 4 6 z"
    private ArrayList<HashMap> listOfGraphsGroupIcons = new ArrayList<>(); // for every pc graph, create its group icons

    private HashMap iconTypes;
    private ArrayList<String> selectedPCs = new ArrayList<>(); // for each graph, keep selected pcs

    private ArrayList<ArrayList<String>> hiddenPoints = new ArrayList<>(); // store hidden ids of every pc graph in a separate list
    private ArrayList<Subject> pcGraphSubjects; // list of every subject object created
    private ArrayList<ArrayList<Subject> > pcGraphSubjectsList =  new ArrayList<>(); // every graph has it

    // TODO Admiture Plot section to be modified
    static HashMap<String, String[]> famData; // [iid, [...]]
    static List<String[]> famIDsList;
    static HashMap<String, String[]> admixturePhenoData; // used to map pheno details to fam details
    public static HashMap<String, String[]> individualPhenoDetails; // Hashmap to store individual pheno details for searching
    private int numOfIndividuals;

    public Project(String proj_name, String pheno_fname_s, int phenoColumnNumber) throws IOException {
        this.projectName = proj_name;
        this.phenoFileName = pheno_fname_s;
        this.phenoColumnNumber = phenoColumnNumber;

        project = this;
        pcGraphSubjects = new ArrayList<Subject>();
        readPhenotypeFile(pheno_fname_s);
        setIconTypes();
    }

    /**
     * Create a new project
     *
     * @param proj_name
     * @throws java.io.FileNotFoundException
     */
    public Project(String proj_name, String fam_fname_s, String pheno_fname_s, int phenoColumnNumber) throws IOException {
        this.projectName = proj_name;
        this.famFileName = fam_fname_s;
        this.phenoFileName = pheno_fname_s;
        this.phenoColumnNumber = phenoColumnNumber;
        pcGraphSubjects = new ArrayList<>();

        project = this;
        readPhenotypeFile(pheno_fname_s);
        readFamFile(fam_fname_s);
        setIconTypes();
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
        famData = new HashMap<>();
        famIDsList = new ArrayList<>();
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

            // add fam details to subjects
            for(Subject sub : pcGraphSubjects){
                if (sub.getFid().equals(fid) && sub.getIid().equals(iid)){
                    sub.setPat(pat);
                    sub.setMat(mat);
                    sub.setSex(sex);
                    sub.setPhen(phe);
                }
            }

            String ids = fid + " " + iid;
            String[] idsList = {fid, iid};
            famIDsList.add(idsList); // store ids of the farm file to map with the admixture values
            String other_cols[] = {pat, mat, sex, phe};
            famData.put(ids, other_cols);
            l = r.readLine();
        }

        numOfIndividuals = famData.size();
        // test if the fam file is imported -- comment this section otherwise it prints in the results in the terminal
//        for (int i = 0; i < famIDsList.size(); i++){
//            System.out.println(Arrays.asList(famIDsList.get(i)));
//        }
    }

    private void readPhenotypeFile(String phenoFilePath) throws FileNotFoundException, IOException {
        individualPhenoDetails = new HashMap<>(); // key is IID
        admixturePhenoData = new HashMap<>(); // key is "FID IID"

        BufferedReader r = Genesis.openFile(phenoFilePath);

        // get phenotype groups and assign colors and icons
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

        BufferedReader secBuf = Genesis.openFile(phenoFilePath);
        String line = secBuf.readLine();
        String fields[];
        while (line != null) {
            fields = line.split("\\s+");

            String fid = fields[0];
            String iid = fields[1];
            String phenotypeA = fields[2];
            String phenotypeB = fields[3];

            // get color and icon for selected pheno group or column
            String chosenPheno = fields[phenoColumnNumber-1];
            String color = (String) groupColors.get(chosenPheno);
            String icon = (String) groupIcons.get(chosenPheno);

            // store this individual
            pcGraphSubjects.add(new Subject(fid, iid, phenotypeA, phenotypeB, color, icon, defaultIconSize, false));

            // provide details when individual is clicked or searched
            individualPhenoDetails.put(fields[1], fields);

            // used to map with the fam file
            admixturePhenoData.put(fields[0] + " " + fields[1], Arrays.copyOfRange(fields, 2, fields.length));
            line = secBuf.readLine();

        }
        // print phenotype rows -- testing
//        for (int i = 0; i < listOfRows.size(); i++){
//            System.out.println(Arrays.asList(listOfRows.get(i)));
//        }
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

    public ArrayList<HashMap> getListOfGraphsGroupColors() {
        return listOfGraphsGroupColors;
    }

    public ArrayList<HashMap> getListOfGraphsGroupIcons() {
        return listOfGraphsGroupIcons;
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

    public ObservableList<String> getHiddenIndvsOfCurrentGraph(){
        ObservableList<String> hiddenIndividualsList = FXCollections.observableArrayList();
        for(Subject s: pcGraphSubjectsList.get(currentTabIndex)){
            if(s.isHidden()){
                String fid_iid = s.getFid()+" "+s.getIid();

                // check if a list of hidden points exists
                if (currentTabIndex >= 0 && currentTabIndex < hiddenPoints.size() && hiddenPoints.get(currentTabIndex).contains(fid_iid)) {
                    // An entry exists; add ids to the obsList
                    hiddenIndividualsList.add(fid_iid);
                }
            }
        }
        return hiddenIndividualsList;
    }

    public ArrayList<ArrayList<String>> getHiddenPoints() {
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

    public ArrayList<String> getSelectedPCs() {
        return selectedPCs;
    }
}
