/*
 * Copyright 2019 University of the Witwatersrand, Johannesburg on behalf of the Pan-African Bioinformatics Network for H3Africa.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.h3abionet.genesis.model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Scott Hazelhurst
 */

public class Fam {
    // Hash map to store fam ids as keys and array of other fields as values
    // Can be used to join the fam file with the phenotype file
    static HashMap<String, String[]> famFileMap; // [iid, [...]]
    
    // Arraylist of array to store row values in the fam file
    // Used to merge the fam file with the Q file
    static List<String[]> listOfRows;
    String fid;
    String iid;
    String pat;
    String mat;
    String sex;
    String phe;
    

    /**
     *
     * @param  famFilePath // absolute file path
     * @throws IOException
     */
    public Fam(String famFilePath) throws IOException {
        setFam(famFilePath);
    }

    /**
     * Default constructor
     */
    Fam() {

    }

    /**
     *
     * @param famFilePath
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void setFam (String famFilePath) throws FileNotFoundException, IOException {
        BufferedReader r;
        r = openFile(famFilePath);
        famFileMap = new HashMap<>();
        listOfRows = new ArrayList<>();
        String l = r.readLine();
        String  fields [];
        while (l != null) {
            fields = l.split("\\s+");
            fid = fields[0];
            iid = fields[1];
            pat = fields[2];
            mat = fields[3];
            sex = fields[4];
            phe = fields[5];     
            String ids = fid+" "+iid;
            String [] idsList = {fid, iid};
            listOfRows.add(idsList); // store ids of the farm file to map with the admixture values
            String other_cols[] = {pat, mat, sex, phe};
            famFileMap.put(ids, other_cols);
            l = r.readLine();      
        }
        
        // test if the fam file is imported -- comment this section otherwise it prints in the results in the terminal
//        for (int i = 0; i < listOfRows.size(); i++){
//            System.out.println(Arrays.asList(listOfRows.get(i)));
//        }
        
    }
    
    /**
     * return a hash map of fam file 
     * @return 
     */
     public HashMap<String, String[]> getFamFileMap() {
        return famFileMap;
    }

    
     /**
      * return an arraylist of array of rows in the fam file
      * @return 
      */
    public List<String[]> getListOfRows() {
        return listOfRows;
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

