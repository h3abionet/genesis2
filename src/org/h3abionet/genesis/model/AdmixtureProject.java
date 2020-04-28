/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.model;

import com.sun.javafx.charts.Legend;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
 import java.util.HashMap;
import java.util.List;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import org.h3abionet.genesis.controller.ProjectDetailsController;

/**
 *
 * @author henry
 */
public final class AdmixtureProject {
    
    Fam fam;
    Pheno pheno;
    ProjectDetailsController projectDetailsController; // get the project name
    List<String[]> admixValues; // arraylist of array to store row values
    List<String[]> listOfPhenoRows; // arraylist of pheno values
    List<String[]> listOfPhenoAndAdmixture; // arraylist of pheno and admixture arrays (rows)
    
    // fam section
    HashMap<String, String[]> listOfFamAndAdmixture; // [[fid iid] -> admix], [fid iid] -> admix], ...]
    List<String[]> listOfFamRows;
    String ancestors[]; // columns in admixture file
    
    HashMap<String, String[]> admixturePhenoHashMap; // store pheno details
    HashMap<String, String[]> admixtureFamHashMap; // store fam details
    HashMap<String, List<String []>> groupsOfIndividuals; // store pheno as key and qvalues with fam ids
    
            
    public AdmixtureProject(String admixFilePath) throws IOException {
        fam = new Fam();
        pheno = new Pheno();
        projectDetailsController = new ProjectDetailsController();
        
        setAdmixturePhenoHashMap(pheno);
        setAdmixtureFamHashMap(fam);
        setAdmixture(admixFilePath);
    }

    public void setAdmixturePhenoHashMap(Pheno pheno) {
        this.admixturePhenoHashMap = pheno.getAdmixturePhenoHashMap();
    }

    public void setAdmixtureFamHashMap(Fam fam) {
        this.admixtureFamHashMap = fam.getFamFileMap();
    }
    
    
    private void setAdmixture(String admixFilePath) throws FileNotFoundException, IOException {
        
        admixValues  = new ArrayList<>();
        BufferedReader r = openFile(admixFilePath);
        String line = r.readLine();
        String fields[] = line.split("\\s+");
        
        int num_ancestors = fields.length; // if ids are not included in the file
        ancestors = new String[num_ancestors];
        for (int i = 0; i < num_ancestors; i++) {
            ancestors[i] = "Ancestry " + Integer.toString(i + 1);
        }
        
//        line = r.readLine();
        while (line != null) {
            fields = line.split("\\s+");
            admixValues.add(fields);
            line = r.readLine();
        }
        
        
        setGroupsOfIndividuals();
        
    }
    
    /**
     * create groups of individuals based on the phenotype selected column
     */
    private void setGroupsOfIndividuals(){
        groupsOfIndividuals = new HashMap<>();
        HashMap<String, String []> famAndQvalues = getFamAndQvalues();
        
        for(String k : admixtureFamHashMap.keySet()){
            ArrayList<String[]> valuesList = new ArrayList<>();
            String [] values = famAndQvalues.get(k);
            
            if(admixturePhenoHashMap.containsKey(k)){
                String group = admixturePhenoHashMap.get(k)[pheno.getColWithPheno()-3];
                if(!groupsOfIndividuals.containsKey(group)){
                    valuesList.add(values);
                    groupsOfIndividuals.put(group, valuesList);
                }else{
                    groupsOfIndividuals.get(group).add(values);
                    
                }
                
            }else{
                ;               
            }
        
        }
        
//        print to test if individuals have been grouped according to specified column.
//        for (String i : groupsOfIndividuals.keySet()) {
//            System.out.println("key: " + i + " value: " + groupsOfIndividuals.get(i).toString());
//        }
        
    }
    
    /**
     * create multiple plots for every phenotype
     * @return 
     */
    public ArrayList<StackedBarChart<String, Number>> createAdmixturePlots(){
        
        ArrayList<StackedBarChart<String, Number>> charts = new ArrayList<>(); // store stackedbars for every group
        
        for(String k : groupsOfIndividuals.keySet()){ 
            List<String []> listOfQValues = groupsOfIndividuals.get(k); // for list of individual values for every pheno
            
            
            XYChart.Series<String, Number> series;
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis(0,1,0.1);
            xAxis.setTickMarkVisible(false);
            xAxis.setTickLabelsVisible(false);

            yAxis.setMinorTickVisible(false);
            yAxis.setTickMarkVisible(false);
            yAxis.setTickLabelsVisible(false);

            StackedBarChart<String, Number> chart = new StackedBarChart<>(xAxis, yAxis);

            for(int i=0; i<ancestors.length; i++){
                series = new XYChart.Series<>();
                series.setName(ancestors[i]);
                
                for(String [] qValues: listOfQValues){ // get individual values
                    series.getData().add(new XYChart.Data<>(qValues[1], Float.parseFloat(qValues[2+i]))); //[fid, iid, v1, v2]

                }
                
                chart.getData().add(series);
              
            }
            // define chart size
            chart.getXAxis().setLabel(k);
            chart.getXAxis().setVisible(false);
            chart.setPrefHeight(100);
            chart.setMinHeight(100);
            chart.setMaxHeight(200);
            charts.add(chart);

        }
        return charts;

    }

    /**
     * calculate populations
     * @return 
     */
    public StackedBarChart<String, Number> getAdmixtureGraph(){
        
        XYChart.Series<String, Number> series;
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        String kValue = "K = "+ancestors.length; // get the value of K
        yAxis.setLabel(kValue); // label: K = 1, 2, ...
        yAxis.setMinorTickVisible(false); // disable yaxis marks
        
        StackedBarChart<String, Number> admixChart = new StackedBarChart<>(xAxis, yAxis);
            
           for(int i=0; i<ancestors.length; i++){
                series = new XYChart.Series<>();
                series.setName(ancestors[i]);
                
                for(String k : groupsOfIndividuals.keySet()){
                    List<String []> listOfQValues = groupsOfIndividuals.get(k); // for list of individual values for every pheno
                    
                    for(String [] qValues: listOfQValues){ // get individual values
                        series.getData().add(new XYChart.Data<>(qValues[1], Float.parseFloat(qValues[2+i]))); //[fid, iid, v1, v2]

                    }

                }
                admixChart.getData().add(series);

            }
           
           // remove series names from the legend.
           // Note:  setLegendVisible(false) didnot work
           Legend legend = (Legend)admixChart.lookup(".chart-legend");
           int legendSize = legend.getItems().size();
           for(int i=0; i<legendSize; i++){
                String seriesName = ancestors[i];
                legend.getItems().removeIf(item->item.getText().equals(seriesName));

            }


        return admixChart;
    }
    
    /**
     * Merge pheno and admixture
     * returns a merged ArrayList of phenotypes and admixture [2431, NA19916, hapmap, west-africa, ASW, m, 0.862588, 0.113654, ...]
     * @return 
     */
    private List<String[]> mergePhenoAndAdmixture(){
        listOfPhenoRows = Pheno.getListOfRows(); // get arrays of phenos
        listOfPhenoAndAdmixture  = new ArrayList<>();
        for (int i = 0; i < admixValues.size(); i++) {
//          merge each array from admixValues with corresponding array of phenotypes
//          combine function is used to merge
            String[] com = combine(listOfPhenoRows.get(i), admixValues.get(i));
            listOfPhenoAndAdmixture.add(com);
        }
        
        // testing to see whether arrays were merged correctly
//        for (String[] strArr : listOfPhenoAndAdmixture) {
//			System.out.println(Arrays.toString(strArr));
//		}
        
        return listOfPhenoAndAdmixture;
    }
    
    /**
     * Map fam ids with admixture
     * return hashmap of fam ids as keys with associated Q values
     */
    private HashMap<String, String[]> getFamAndQvalues(){
        listOfFamRows = fam.getListOfRows(); // get arrays of phenos
        listOfFamAndAdmixture  = new HashMap<>();
        
        for (int i = 0; i < admixValues.size(); i++){
            String [] ids = listOfFamRows.get(i);
            String [] idsWithValues = combine(ids, admixValues.get(i)); // [fid, iid, a1, a2]
            String id = ids[0]+" "+ids[1];
            listOfFamAndAdmixture.put(id, idsWithValues);
        }
        
        return listOfFamAndAdmixture;
        
    }
    
    
     /**
     *
     * @param a
     * @param b
     * @return
     */
    private static String[] combine(String[] a, String[] b) {
        int length = a.length + b.length;
        String[] result = new String[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
    
    /**
     * For reading the admixture file path
     * @param admixFilePath
     * @return
     * @throws FileNotFoundException 
     */
    private BufferedReader openFile(String admixFilePath) throws FileNotFoundException {
        InputStreamReader is = new InputStreamReader(new FileInputStream(admixFilePath));
        BufferedReader dinp = new BufferedReader(is);
        return dinp;
    }
    
}
