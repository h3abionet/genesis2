/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
/**
 *
 * @author Henry
 */
public class Pheno {
    
    static HashMap<String, String[]> pcaPhenoHashMap; // hashmap to store fam records. key = id; value = list of phenos.
    static List<String[]> listOfRows; // store list of rows in the pheno file
    static int numOfColumnsInPheno; // store the number of phenos
    
    static HashMap<String, String[]> admixturePhenoHashMap; // used to map pheno details to fam details
    static HashMap<String, String[]> individualPhenoDetails; // Hashmap to store individual details for searching
    
    static int colWithPheno; // store selected column with phenotype
   
    /**
     *
     * @param phenoFilePath
     * @throws IOException
     */
    public Pheno(String phenoFilePath) throws IOException {     
        setPheno(phenoFilePath);
    }

    /**
     * Default constructor
     */
    Pheno() {

    }

    /**
     *
     * @param phenoFilePath
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void setPheno(String phenoFilePath ) throws FileNotFoundException, IOException {
        pcaPhenoHashMap = new HashMap<>(); // key is FID:IID
        listOfRows = new ArrayList<>();
        individualPhenoDetails = new HashMap<>(); // key is IID
        admixturePhenoHashMap =  new HashMap<>(); // key is "FID IID"
                
        BufferedReader r = openFile(phenoFilePath);
        String line = r.readLine();
        String fields [] = line.split("\\s+");
        numOfColumnsInPheno = fields.length;
//        boolean header = fields[0].equals("FID") && fields[1].equals("IID");
//        if (header) line = r.readLine();
        while (line !=null) {
            fields  = line.split("\\s+");
            String ids = fields[0]+":"+fields[1];
            String[] curr_phenos = Arrays.copyOfRange(fields, 2, fields.length);
            listOfRows.add(fields);
            // used to map with evec file individual pcs
            pcaPhenoHashMap.put(ids, curr_phenos); 
            
            // provide details when individual is clicked or searched
            individualPhenoDetails.put(fields[1], fields);
            
            // used to map with the fam file
            admixturePhenoHashMap.put(fields[0]+" "+fields[1], curr_phenos);
            line = r.readLine();
     
        }
        
        // print phenotype rows -- testing
//        for (int i = 0; i < listOfRows.size(); i++){
//            System.out.println(Arrays.asList(listOfRows.get(i)));
//        }

    }
    
    /**
     * set column with phenotype
     * @param colWithPheno 
     */
    public void setColWithPheno(int colWithPheno) {
        Pheno.colWithPheno = colWithPheno;
    }
    
    /**
     * get column number with phenotype
     * @return 
     */
    public int getColWithPheno() {
        return colWithPheno;
    }
    
    /**
     * return a hashmap with ids and phenotypes in pheno file
     * @return
     */
    public HashMap<String, String[]> getPcaPhenoHashMap(){
        return pcaPhenoHashMap;
    }
    
    /**
     * get hashMap top map to fam details
     * @return 
     */
    public HashMap<String, String[]> getAdmixturePhenoHashMap() {
        return admixturePhenoHashMap;
    }
    
    
    /**
     *  return an arraylist of rows in pheno file
     * @return 
     */
    public static List<String[]> getListOfRows() {
        return listOfRows;
    }
    
    /**
     * return the number of columns in pheno file
     * @return 
     */
    public int getNumOfColumnsInPheno() {
        return numOfColumnsInPheno;
    }
    
    /**
     * get individual details
     * @return 
     */
    public HashMap<String, String[]> getIndividualPhenoDetails() {
        return individualPhenoDetails;
    }
    
    
    /**
     *
     * @param name Absolute path of the pheno file
     * @return
     * @throws FileNotFoundException
     */
    public BufferedReader openFile(String name) throws FileNotFoundException  {

	InputStreamReader is = new InputStreamReader(new FileInputStream(name));
	BufferedReader       dinp  = new BufferedReader(is);
	return dinp;
   }

  
}
