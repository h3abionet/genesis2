/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.model;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.h3abionet.genesis.Genesis;

/**
 *
 * @author scott
 */
public class Project {

    // <! This a fam section: with all the variables needed store fam details >
    /* Hash map to store fam ids as keys and array of other fields as values
    *  Can be used to join the fam file with the phenotype file
     */
    static HashMap<String, String[]> famData; // [iid, [...]]

    /* Arraylist of arrays storing row values from the fam file
    *  Used to merge the fam file with the Q file
    *  Note: The Q file might contain ids that are not in the fam file
     */
    static List<String[]> famIDsList;

    // <! This a pheno section: with all the variables needed store details in the pheno file>
    // only one hashmap is required - but the pheno files provided in the examples had different formats: Find out??
    static HashMap<String, String[]> pcaPhenoData; // hashmap to store phenotype records. key = id; value = list of phenos.    
    static HashMap<String, String[]> admixturePhenoData; // used to map pheno details to fam details

    public static HashMap<String, String[]> individualPhenoDetails; // Hashmap to store individual pheno details for searching

    static int phenoColumnNumber; // store selected column with phenotype

    /**
     * required to calculate column constraints in the main controller
     * and other purposes
     */
    public static int numOfIndividuals;

    Object layout;
    ArrayList<Graph> graphs;

    private static Project project;

    private String projectName;
    private String phenoFileName;
    private String famFileName;

    private List<String> groupNames = new ArrayList<>();
    private HashMap groupColors = new HashMap(); // mkk -> #800000
    private HashMap groupIcons =  new HashMap();

    public Project(String proj_name, String pheno_fname_s) throws IOException {
        this.projectName = proj_name;
        this.phenoFileName = pheno_fname_s;
        layout = null;
        graphs = new ArrayList<>();
        project = this;
        readPhenotypeFile(pheno_fname_s);

    }

    /**
     * Create a new project
     *
     * @param proj_name
     * @throws java.io.FileNotFoundException
     */
    public Project(String proj_name, String fam_fname_s, String pheno_fname_s) throws IOException {
        this.projectName = proj_name;
        layout = null;
        graphs = new ArrayList<>();

        project = this;
        readPhenotypeFile(pheno_fname_s);
        readFamFile(fam_fname_s);
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

        BufferedReader r;
        r = Genesis.openFile(famFilePath);
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
        pcaPhenoData = new HashMap<>(); // key is FID:IID
        individualPhenoDetails = new HashMap<>(); // key is IID
        admixturePhenoData = new HashMap<>(); // key is "FID IID"

        BufferedReader r = Genesis.openFile(phenoFilePath);
        String line = r.readLine();
        String fields[];
        while (line != null) {
            fields = line.split("\\s+");
            String ids = fields[0] + " " + fields[1];
            String[] curr_phenos = Arrays.copyOfRange(fields, 2, fields.length);
            // used to map with evec file individual pcs
            pcaPhenoData.put(ids, curr_phenos);

            // provide details when individual is clicked or searched
            individualPhenoDetails.put(fields[1], fields);

            // used to map with the fam file
            admixturePhenoData.put(fields[0] + " " + fields[1], curr_phenos);
            line = r.readLine();

            // keep track of unique phenotypes
            if (!groupNames.contains(fields[phenoColumnNumber-1])) {
                groupNames.add(fields[phenoColumnNumber-1]);
            }
        }

        // set colors and icons for every phenotype
//        for (int i = 0; i < groupNames.size(); i++){
//            groupColors.put(groupNames.get(i),colors[i]);
//            groupIcons.put(groupNames.get(i),icons[i]);
//        }

        // print phenotype rows -- testing
//        for (int i = 0; i < listOfRows.size(); i++){
//            System.out.println(Arrays.asList(listOfRows.get(i)));
//        }
    }

    /**
     * set column with phenotype
     *
     * @param phenoColumnNumber
     */
    public static void setPhenoColumnNumber(int phenoColumnNumber) {
        Project.phenoColumnNumber = phenoColumnNumber;
    }

    // singleton pattern is used to limit creation of a class to only one object
    public static Project getProject() {
        return project;
    }

    public List<String> getGroupNames() {
        return groupNames;
    }

    /**
     * Given a new graph, add it to the project
     *
     * @param g
     */
    public void addGraph(Graph g) {
        boolean add = graphs.add(g);
    }

    public void addPCA(File file) {

    }

}
