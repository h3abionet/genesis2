/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.model;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.File;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


/**
 *
 * @author scott
 */
public class Project {
//

    Fam     fam [];
    /* for every individual their phenotype */
//    HashMap<String, String[]> pheno;
//    HashMap<String, String[]> famFile;
//    HashMap<String, String[]> pcaValues;
    
    Map<String, ArrayList<String>> pheno;
    Map<String, ArrayList<String>> famFile;
    Map<String, ArrayList<String>> pcaValues;
    Map<String, ArrayList<String>> map;
    Map<String, ArrayList<String>> final_map;
    
    String pheno_cols[];
    String pca_cols[];

    Object  layout;
    ArrayList<Graph>   graphs;
    private int num_inds;
    private String proj_name;
   


    
    public Project() {
       
    }
    /**
     * Create a new project, initialised by fam fi
     * @param proj_name
     * @param fam_name
     * @param pheno_name
     * @param pca_name
     * @throws java.io.FileNotFoundException
    */
    public Project(String proj_name, String fam_name, String pheno_name, String pca_name) throws FileNotFoundException, IOException {
        this.proj_name = proj_name;
        setFam(fam_name);
        setPheno(pheno_name);
        setPCA(pca_name);
        layout = null;
        graphs = new ArrayList<>();
    }
  
   
   private void setFam (String fam_name) throws FileNotFoundException, IOException {
        BufferedReader r;
        r = openFile(fam_name);
        famFile = new HashMap<>();
        String l = r.readLine();
        String  data [];
        int f =0;
        while (l != null) {
            f++;
            data = l.split("\\s+");    
            String ids = data[0]+":"+data[1];
            String other_cols[] = {data[0],data[1],data[2],data[3],data[4],data[5]};
            famFile.put(Integer.toString(f), new ArrayList<String>(Arrays.asList(other_cols)));
            l = r.readLine();
        }  
    }
   
    public void getFam(){
      Set set = famFile.entrySet();
      Iterator iterator = set.iterator();
      while(iterator.hasNext()) {
         Map.Entry mentry = (Map.Entry)iterator.next();
         System.out.print("key is: "+ mentry.getKey() + " & Value is: ");
         System.out.println(mentry.getValue());
        }
    }
    
  
    private void setPheno(String pheno_name) throws FileNotFoundException, IOException {
        pheno = new HashMap<>();
        BufferedReader r = openFile(pheno_name);
        String line = r.readLine();
        String fields [] = line.split("\\s+");
        int    num_phenos = fields.length-2;
        System.out.println("We have "+num_phenos+" phenotypes");
        pheno_cols = new String[num_phenos];
        boolean header = fields[0].equals("FID") && fields[1].equals("IID");
        for (int i=0; i<num_phenos; i++ )
           if (header)
               pheno_cols[i]=fields[i+2];
           else 
               pheno_cols[i]=Integer.toString(i);
        if (header) line = r.readLine();
        int ph = 0;
        while (line !=null) {
            ph++;
            fields  = line.split("\\s+");
            String id = fields[0]+":"+fields[1];
            pheno.put(Integer.toString(ph), new ArrayList<String>(Arrays.asList(Arrays.copyOfRange(fields,0,2+num_phenos))));
//            ArrayList<String> p = pheno.get(Integer.toString(ph));
//            if (p.size() != 4) { System.out.println(id+" "+p.size()); }
            line = r.readLine();
     
        }

    }
    
    public void getPheno(){
      Set set = pheno.entrySet();
      Iterator iterator = set.iterator();
      while(iterator.hasNext()) {
         Map.Entry mentry = (Map.Entry)iterator.next();
         System.out.print("key is: "+ mentry.getKey() + " & Value is: ");
         System.out.println(mentry.getValue());
        }
    }
    
        
   private void setPCA(String pca_name) throws FileNotFoundException, IOException {
        pcaValues = new HashMap<>();
        BufferedReader r = openFile(pca_name);
        String line = r.readLine();
        String fields [] = line.split("\\s+");
        int    num_pcas = fields.length;
        System.out.println("We have "+num_pcas+" PCAs");
        pca_cols = new String[num_pcas];
        boolean header = fields[0].equals("PCA1") && fields[1].equals("PCA2");
        for (int i=0; i<num_pcas; i++ )
           if (header)
                System.out.println("define pca names");
           else 
               pca_cols[i] = "PCA"+Integer.toString(i+1);
        if (header) line = r.readLine();
        int pc = 0;
        while (line !=null) { 
            pc++;
            fields  = line.split("\\s+");
            pcaValues.put(Integer.toString(pc), new ArrayList<String>(Arrays.asList(Arrays.copyOf(fields,fields.length))));
            line = r.readLine();     
        }

   }
   
   public void getPCAValues(){
   Set set = pcaValues.entrySet();
      Iterator iterator = set.iterator();
      while(iterator.hasNext()) {
         Map.Entry mentry = (Map.Entry)iterator.next();
         System.out.print("key is: "+ mentry.getKey() + " & Value is: ");
         System.out.println(mentry.getValue());
        }
   }
   
    public ObservableList<String> getPca_cols() {
      ObservableList<String> pca_list = FXCollections.observableArrayList();
      pca_list.addAll(Arrays.asList(pca_cols));
        return pca_list;
    }
    
    public Map<String, ArrayList<String>> mergePhenoFam(){     
    map = new HashMap<>();
    map.putAll(pheno);

    famFile.forEach((key , value) -> {
        //Get the value for key in map.
        ArrayList<String> list = map.get(key);
        if (list == null) {
            map.put(key,value);
        }
        else {
            //Merge two list together
            ArrayList<String> mergedValue = new ArrayList<>(value);
            mergedValue.addAll(list);
            map.put(key , mergedValue);
        }
    });
    return map;
}
    
    public Map<String, ArrayList<String>> mergeMapPCA(){ 
    Map<String, ArrayList<String>> mergedMap = mergePhenoFam();
    final_map = new HashMap<>();
    final_map.putAll(mergedMap);

    pcaValues.forEach((key , value) -> {
        //Get the value for key in map.
        ArrayList<String> list = final_map.get(key);
        if (list == null) {
            final_map.put(key,value);
        }
        else {
            //Merge two list together
            ArrayList<String> finalMergedValue = new ArrayList<>(value);
            finalMergedValue.addAll(list);
            final_map.put(key , finalMergedValue);
        }
    });
    return final_map;
}
    
public void getMerged(){
    Map<String, ArrayList<String>> finalMergedMap = mergeMapPCA();
    for (String s : finalMergedMap.keySet()) {
        System.out.println("Current key: " + s); //Optional for better understanding
        for (String r : finalMergedMap.get(s)) {
            System.out.println(r.toString());
        }
    }   
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

    public void addPCA(File file) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
