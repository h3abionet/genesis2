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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Scott Hazelhurst
 */

public class Fam {
    Project project;
    HashMap<String, String[]> famFile;
    static List<String[]> listOfRows;
    String famName;
    String fid, iid;
    String pat;
    String mat;
    String sex;
    String phe;
    

    public Fam(String famName) throws IOException {
        setFam(famName);
    }

    Fam() {

    }

    public void setFam (String famName) throws FileNotFoundException, IOException {
        BufferedReader r;
        r = openFile(famName);
        famFile = new HashMap<>();
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
            String ids = fid+":"+iid;
            String rows[] = {ids,pat,mat,sex,phe};
            listOfRows.add(rows); 
            String other_cols[] = {pat,mat,sex,phe};
            famFile.put(ids, other_cols);
            l = r.readLine();      
        }
        
    }
    
    public void getFam(){
        for (int i = 0; i < listOfRows.size(); i++){
            System.out.println(Arrays.asList(listOfRows.get(i)));
        }
    }
    
    public BufferedReader openFile(String name) throws FileNotFoundException  {

	InputStreamReader is = new InputStreamReader(new FileInputStream(name));
	BufferedReader       dinp  = new BufferedReader(is);
	return dinp;
   }

}

