/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
 import java.util.HashMap;
import java.util.List;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import org.h3abionet.genesis.Genesis;

/**
 *
 * @author henry
 */
public class AdmixtureGraph extends Graph{

    private List<String[]> admixValues; // arraylist of array to store row values
    private String[] ancestries; // Labels_order represents @K or column
    
    private final HashMap<String, String[]> admixturePhenoData; // store pheno details
    private final HashMap<String, String[]> famData; // store fam details
    
    public static int currentNumOfAncestries = 0; // number of series
    private static final double CHART_HEIGHT = 100; // default height for every chart
    private static String defaultHeading = "Admixture plot";
    
    // admixture plot colors -- add more depending on the number of ancestries
    static String[] hexCodes = {"#FF8C00", "#32CD32","#fffb00","#055ff0", "#ff0d00"};
    public static ArrayList<String> admixColors = new ArrayList<>(Arrays.asList(hexCodes));
    
    public static ArrayList<String> ancestryOrder=new ArrayList<String>();  // to order colour
        
            
    public AdmixtureGraph(String admixtureFilePath) throws IOException {
        this.admixturePhenoData = Project.admixturePhenoData;
        this.famData = Project.famData;
        readGraphData(admixtureFilePath);
        
        for (String l:ancestries)
            ancestryOrder.add(l);

    }
    
    @Override
    protected final void readGraphData(String admixtureFilePath) throws FileNotFoundException, IOException {
        
        admixValues  = new ArrayList<>();
        BufferedReader r = Genesis.openFile(admixtureFilePath);
        String line = r.readLine();
        String fields[] = line.split("\\s+");
        
        int num_ancestors = fields.length; // if ids are not included in the file
        ancestries = new String[num_ancestors];
        for (int i = 0; i < num_ancestors; i++) {
            ancestries[i] = "Ancestry " + Integer.toString(i + 1);
        }
        
//        line = r.readLine();
        while (line != null) {
            fields = line.split("\\s+");
            admixValues.add(fields);
            line = r.readLine();
        }
        setPopulationGroups();
        
    }
    
    /**
     * create groups of individuals based on the phenotype selected column
     */
    @Override
    protected void setPopulationGroups(){
        populationGroups = new HashMap<>();
        HashMap<String, String []> famAndQvalues = combineFamAndAdmixValues();
        
        for(String k : famData.keySet()){
            ArrayList<String[]> valuesList = new ArrayList<>();
            String [] values = famAndQvalues.get(k);
            
            if(admixturePhenoData.containsKey(k)){
                String group = admixturePhenoData.get(k)[Project.phenoColumnNumber-3];
                if(!populationGroups.containsKey(group)){
                    valuesList.add(values);
                    populationGroups.put(group, valuesList);
                }else{
                    populationGroups.get(group).add(values);
                    
                }
                
            }else{
                ;               
            }
        
        }
        
//        print to test if individuals have been grouped according to specified column.
//        for (String i : populationGroups.keySet()) {
//            System.out.println("key: " + i + " value: " + populationGroups.get(i).toString());
//        }
        
    }
    
    /**
     * create multiple plots for every phenotype
     * @return 
     */
    @Override
    public ArrayList<StackedBarChart<String, Number>> createGraph(){
        
//      charts = [chart1, chart2, chart3, ...]
        ArrayList<StackedBarChart<String, Number>> charts = new ArrayList<>(); // store stackedbars for every group
        
        // hashmap [k, v] = [MKK, [[fid, iid, v1, v2],    [fid, iid, v1, v2],   [fid, iid, v1, v2]]]
        for(String k : populationGroups.keySet()){ 
            List<String []> listOfQValues = populationGroups.get(k); // for list of individual values for every pheno
            
            XYChart.Series<String, Number> ancestryValues;
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis(0,1,0.1);
            xAxis.setTickMarkVisible(false);
            xAxis.setTickLabelsVisible(false);

            yAxis.setMinorTickVisible(false);
            yAxis.setTickMarkVisible(false);
            yAxis.setTickLabelsVisible(false);

            StackedBarChart<String, Number> populationGroupChart = new StackedBarChart<>(xAxis, yAxis);

            for(int i=0; i<ancestries.length; i++){
                ancestryValues = new XYChart.Series<>();
                ancestryValues.setName(ancestries[i]);
                
                for(String [] qValues: listOfQValues){ // get individual values
                    ancestryValues.getData().add(new XYChart.Data<>(qValues[1], Float.parseFloat(qValues[2+i]))); //[fid, iid, v1, v2]

                }
                
                populationGroupChart.getData().add(ancestryValues); // add values to chart
                setAncestryColors(populationGroupChart, admixColors); // set ancestry colors
                
              
            }
            
            // update current num of ancestries
            currentNumOfAncestries = populationGroupChart.getData().size();
                    
            // define populationGroupChart size
            populationGroupChart.getXAxis().setLabel(k);
            populationGroupChart.getXAxis().setVisible(false);
            populationGroupChart.setPrefHeight(CHART_HEIGHT);
            populationGroupChart.setMinHeight(CHART_HEIGHT);
            charts.add(populationGroupChart);

        }
        return charts;

    }
    
    /**
     * Map fam ids with admixture
     * return hashmap of fam ids as keys with associated Q values
     */
    private HashMap<String, String[]> combineFamAndAdmixValues(){
        List<String[]> famIDsList = Project.famIDsList; // get ids in fam file
        HashMap<String, String[]> listOfFamAndAdmixture  = new HashMap<>(); // [[fid iid] -> admix], [fid iid] -> admix], ...]
        
        for (int i = 0; i < admixValues.size(); i++){
            String [] ids = famIDsList.get(i);
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
    private String[] combine(String[] a, String[] b) {
        int length = a.length + b.length;
        String[] result = new String[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
    
    /**
     *
     * Set colors
     */
    private void setAncestryColors(StackedBarChart<String, Number> stackedBarChart, ArrayList<String> admixColors) {
        for (int i=0; i<stackedBarChart.getData().size(); i++) {
            int ancestryIndex = i;
            String ancestryColor = admixColors.get(i);
            stackedBarChart.getData().get(i).getData().forEach((bar) -> {
                bar.getNode().lookupAll(".default-color"+ancestryIndex+".chart-bar")
                        .forEach(n -> n.setStyle("-fx-bar-fill: "+ancestryColor+";"));
            });
        }
    }

    public static double getCHART_HEIGHT() {
        return CHART_HEIGHT;
    }

    public static String getDefaultHeading() {
        return defaultHeading;
    }
    
    // not necessary
    @Override
    public ScatterChart<Number, Number> createGraph(String PCA1, String PCA2){
        return null;
    }
    
}
