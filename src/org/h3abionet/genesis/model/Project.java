/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.model;

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author scott
 */
public class Project {

    Fam     fam;
    Pheno   pheno;
    PCAProject pCAProject;
    
    List<String[]> famFile;
    List<String[]> phenoFile;
    List<String[]> pcas;
            
    String pheno_cols[];
    String pca_cols[];

    Object  layout;
    ArrayList<Graph>   graphs;
    
    private int num_inds;
    private String proj_name;
    private String pheno_fname_s;
    private String fam_fname_s;
    
    public Project(String proj_name,String pheno_fname_s) throws IOException {
      this.proj_name = proj_name;
      this.pheno_fname_s = pheno_fname_s;
      pheno = new Pheno(pheno_fname_s);
      layout = null;
      graphs = new ArrayList<>(); 
    }
    
    /**
     * Create a new project, initialised by fam fi
     * @param proj_name
     * @throws java.io.FileNotFoundException
    */
    public Project(String proj_name, String fam_fname_s, String pheno_fname_s) throws IOException{
        this.proj_name = proj_name;
        System.out.println(proj_name);
        fam = new Fam(fam_fname_s);
        pheno = new Pheno(pheno_fname_s);
        layout = null;
        graphs = new ArrayList<>();
        
    }

    /**
     * Given a new graph, add it to the project
     * @param g 
     */
    public void addGraph(Graph g) {
        boolean add = graphs.add(g);
    }


    public void addPCA(File file) {
        
    }
    
    
}
