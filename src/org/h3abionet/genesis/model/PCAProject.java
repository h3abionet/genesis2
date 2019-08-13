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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;

/**
 *
 * @author Henry
 */
public class PCAProject {
    Map<String, ArrayList<String>> pheno;
    Map<String, ArrayList<String>> pcaValues;
    Map<String, ArrayList<String>> map;
    ArrayList<List<String>> cleanPoints;
    
    
    String pheno_cols[];
    String pca_cols[];
    List<String> eigvals;
    
//    Object  layout;
//    ArrayList<Graph>   graphs;
    
    private int num_inds;
    private String pca_proj_name;

    public String getPca_proj_name() {
        return pca_proj_name;
    }
    
    public PCAProject() {
       
    }
    /**
     * @param pca_proj_name
     * @param pca_pheno_name
     * @param pca_name
     * @throws java.io.FileNotFoundException
    */
    public PCAProject(String pca_proj_name, String pca_pheno_name, String pca_name) throws FileNotFoundException, IOException {
        this.pca_proj_name = pca_proj_name;
        setPheno(pca_pheno_name);
        setPCA(pca_name);
//        layout = null;
//        graphs = new ArrayList<>();
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
        while (line !=null) {
            fields  = line.split("\\s+");
            String id = fields[0]+":"+fields[1];
            pheno.put(id, new ArrayList<String>(Arrays.asList(Arrays.copyOfRange(fields,2,fields.length))));
//            ArrayList<String> p = pheno.get(id);
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
        eigvals = new ArrayList<String>();
        BufferedReader r = openFile(pca_name);
        String line = r.readLine();
        String fields [] = line.split("\\s+");
        int    num_pcas = fields.length-2;
        System.out.println("We have "+num_pcas+" PCAs");
        pca_cols = new String[num_pcas];
        for (int i=0; i<num_pcas; i++ )
             pca_cols[i] = "PCA"+Integer.toString(i+1);
        eigvals.addAll(Arrays.asList(Arrays.copyOfRange(fields,2,fields.length)));
        System.out.println(eigvals);
        line = r.readLine();
        while (line !=null) {                       
            fields  = line.split("\\s+");
            String key = fields[1];
            pcaValues.put(key, new ArrayList<String>(Arrays.asList(Arrays.copyOfRange(fields,2,fields.length-1))));          
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
   
   public Map<String, ArrayList<String>> mergePhenoPCA(){     
    map = new HashMap<>();
    map.putAll(pcaValues);

    pheno.forEach((key , value) -> {
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
   public void getMerged(){
    Map<String, ArrayList<String>> finalMergedMap = mergePhenoPCA();
    for (String s : finalMergedMap.keySet()) {
        System.out.println("Current key: " + s); //Optional for better understanding
        for (String r : finalMergedMap.get(s)) {
            System.out.println(r.toString());
        }
    } 
}
   
   public ArrayList<List<String>> plotPoints(){
        Map<String, ArrayList<String>> finalMergedMap = mergePhenoPCA();
        
        ArrayList<List<String>> points = new ArrayList<List<String>>();
        
//        with no nulls
        cleanPoints = new ArrayList<List<String>>();
        
        //Getting the Set of entries 
         
        Set<Entry<String, ArrayList<String>>> entrySet = finalMergedMap.entrySet(); 
         
        //Creating an ArrayList Of Entry objects 
         
        ArrayList<Entry<String,  ArrayList<String>>> listOfEntry = new ArrayList<Entry<String, ArrayList<String>>>(entrySet); 
         
         
        for (Entry<String,  ArrayList<String>> entry : listOfEntry) 
        {   
            String key = entry.getKey();
            List<String> value = entry.getValue();
  
            value.add(key);
            
            points.add(value);
            
        }
        
        points.removeIf(u -> u.size() < 4);
        
        for(int i=0; i<points.size();i++){
        cleanPoints.add(points.get(i));
        }
        
//        System.out.println(cleanPoints);
        
        return cleanPoints;

    }
   
   public ScatterChart<Number, Number> getGraph(){
        
        NumberAxis xAxis = new NumberAxis();
        xAxis.setSide(Side.TOP);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setSide(Side.RIGHT);
        ScatterChart<Number,Number> sc = new ScatterChart<Number,Number>(xAxis,yAxis);
        
        // setup chart
        xAxis.setLabel("PCA1");
        yAxis.setLabel("PCA2");
        sc.setTitle(pca_proj_name);

        System.out.println(plotPoints());
        
        ObservableList<List<String>> list = FXCollections.observableArrayList(plotPoints());
        XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
        for (int i = 0; i < list.size(); i++) { 
            System.out.println(list.get(i).get(3));
            System.out.println(list.get(i).get(4)); 
//            series.getData().add(new XYChart.Data<Number, Number>(Float.parseFloat(plotPoints().get(i).get(2)),Float.parseFloat(plotPoints().get(i).get(3)))); 
        }
//        System.out.println(series.getData());
//        sc.getData().add(series);
        return sc;
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
