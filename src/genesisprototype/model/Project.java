/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genesisprototype.model;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedReader;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;


/**
 *
 * @author scott
 */
public class Project {
//

    Fam     fam [];
    /* for every individual their phenotype */
    HashMap<String, String[]> pheno;
    Object  layout;
    ArrayList<Graph>   graphs;


    /**
     * Create a new project, initialised by fam fi
     * @param fam_name
    */
    public Project(String fam_name) {
        FileReader r;
        fam   = null;
        pheno  = new HashMap<> ();
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



    private BufferedReader openFile(String name) throws FileNotFoundException  {

	InputStreamReader is = new InputStreamReader(new FileInputStream(name));
	BufferedReader       dinp  = new BufferedReader(is);
	return dinp;
   }


    public void readPheno(String name) throws FileNotFoundException {
	BufferedReader dinp = openFile(name);
	
    }
    
    
}
