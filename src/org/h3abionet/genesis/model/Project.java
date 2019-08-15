/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.model;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


/**
 * @author scott
 */
public class Project {
//

    Fam fam[];
    /* for every individual their phenotype */
    HashMap<String, String[]> pheno;
    String pheno_cols[];
    Object layout;
    ArrayList<Graph> graphs;
    private int num_inds;
    private String proj_name;

     HashMap<> group[]; //  group[i] is a hashmap describing who belongs to each group found in column i
	// e.g. group[3].get("YRI")  which individuals have a YRI in column 3

    public Project() {

    }

    /**
     * Create a new project, initialised by fam fi
     *
     * @param fam_name
     */
    public Project(String proj_name, String fam_name, String pheno_name) throws FileNotFoundException, IOException {
        this.proj_name = proj_name;
        setFam(fam_name);
        setPheno(pheno_name);
        layout = null;
        graphs = new ArrayList<>();
    }
    
   public void setPCA(File f) {
        
   } 
   
   void setFam (String fam_name) throws FileNotFoundException, IOException {

        BufferedReader r = openFile(fam_name);
        num_inds = 0;
        while (r.readLine() != null) num_inds++;
        fam = new Fam[num_inds];
        r = openFile(fam_name);
        int i = 0;
        String line = r.readLine();
        while (line != null) {
            fam[i] = new Fam(line);
            line = r.readLine();
            i++;
        }
    }


    void setPheno(String pheno_name) throws FileNotFoundException, IOException {
        pheno = new HashMap<>();  // for each individual what their phenotypes are
	
        BufferedReader r = openFile(pheno_name);
        String line = r.readLine();
        String fields[] = line.split("\\s+");
        int num_phenos = fields.length - 2;
        System.out.println("We have " + num_phenos + " phenotypes");
        pheno_cols = new String[num_phenos];
        boolean header = fields[0].equals("FID") && fields[1].equals("IID");
        for (int i = 0; i < num_phenos; i++)
            if (header)
                pheno_cols[i] = fields[i + 2];
            else
                pheno_cols[i] = Integer.toString(i);
        if (header) line = r.readLine();
<<<<<<< HEAD:src/org/h3abionet/genesis/model/Project.java
	group = new HashMap<>[num_phenos];    
        while (line !=null) {
            fields  = line.split("\\s+");
            String id = fields[0]+":"+fields[1];
            curr_phenos = Arrays.copyOfRange(fields,2,2+num_phenos);
	    for (column = 0; column< curr_phenos.lenth; column++) {
		 p = curr_phenos[column];
		 // if p is currently a key in group[column] then add 
		 // id to group[column].get(p)
		 // otherwise add a new entry to group[column]
	    }
            pheno.put(id, curr_phenos);
=======
        while (line != null) {
            fields = line.split("\\s+");
            String id = fields[0] + ":" + fields[1];
            pheno.put(id, Arrays.copyOfRange(fields, 2, 2 + num_phenos));
>>>>>>> ayyoub-branch:src/org/h3abionet/genesis/model/Project.java
            String p[] = pheno.get(id);
            if (p.length != 4) {
                System.out.println(id + " " + p.length);
            }
            line = r.readLine();
        }

    }


    /**
     * Given a new graph, add it to the project
     *
     * @param g
     */
    public void addGraph(Graph g) {
        boolean add = graphs.add(g);
    }


    private BufferedReader openFile(String name) throws FileNotFoundException {

        InputStreamReader is = new InputStreamReader(new FileInputStream(name));
        BufferedReader dinp = new BufferedReader(is);
        return dinp;
    }


    public void readPheno(String name) throws FileNotFoundException {
        BufferedReader dinp = openFile(name);

    }

    public void addPCA(File file) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

<<<<<<< HEAD:src/org/h3abionet/genesis/model/Project.java
    public void addPCA(File file) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
=======

>>>>>>> ayyoub-branch:src/org/h3abionet/genesis/model/Project.java
}
