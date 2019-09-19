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
    
    /**
     *
     */
    Project project;

    /**
     *
     */
    static HashMap<String, String[]> pheno;

    /**
     *
     */
    static List<String[]> listOfows;

    /**
     *
     */
    String pheno_cols[];

    /**
     *
     */
    String phenoName;

    /**
     *
     */
    static int num_phenos;

    /**
     *
     * @param phenoName
     * @throws IOException
     */
    public Pheno(String phenoName) throws IOException {     
        setPheno(phenoName);
    }

    /**
     *
     */
    Pheno() {

    }

    /**
     *
     * @param phenoName
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void setPheno(String phenoName ) throws FileNotFoundException, IOException {
        pheno = new HashMap<>();
        listOfows = new ArrayList<>();
        BufferedReader r = openFile(phenoName);
        String line = r.readLine();
        String fields [] = line.split("\\s+");
        num_phenos = fields.length-2;
        System.out.println("We have "+num_phenos+" phenotypes");
        pheno_cols = new String[num_phenos];
        boolean header = fields[0].equals("FID") && fields[1].equals("IID");
        for (int i=0; i<num_phenos; i++ )
           if (header)
               pheno_cols[i]=fields[i+2];
           else 
               pheno_cols[i]=Integer.toString(i);
        if (header) line = r.readLine();
        while (line !=null) {
            fields  = line.split("\\s+");
            String ids = fields[0]+":"+fields[1];
            String[] curr_phenos = Arrays.copyOfRange(fields,2,2+num_phenos);
            String keys[] = {ids};
            String [] rows = combine(keys, curr_phenos);
            listOfows.add(rows);
            pheno.put(ids, curr_phenos);
            line = r.readLine();
     
        }

    }

    /**
     *
     * @return
     */
    public int getNum_phenos() {
        return num_phenos;
    }
    
    /**
     *
     * @return
     */
    public HashMap<String, String[]> getPheno(){
        return pheno;
    }
    
    /**
     *
     * @param a
     * @param b
     * @return
     */
    public static String[] combine(String[] a, String[] b){
        int length = a.length + b.length;
        String[] result = new String[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
    
    /**
     *
     * @param name
     * @return
     * @throws FileNotFoundException
     */
    public BufferedReader openFile(String name) throws FileNotFoundException  {

	InputStreamReader is = new InputStreamReader(new FileInputStream(name));
	BufferedReader       dinp  = new BufferedReader(is);
	return dinp;
   }

  
}
