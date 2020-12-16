/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import org.h3abionet.genesis.Genesis;

/**
 *
 * @author henry
 */
public class AdmixtureGraph extends Graph{

    private List<String[]> admixValues; // arraylist of array to store row values
    private String[] ancestries;
    
    private final HashMap<String, String[]> admixturePhenoData; // store pheno details
    private final HashMap<String, String[]> famData; // store fam details
    // track fam order when user sort individuals by colour
    public static final HashMap<String, List<String>> famOrder = new HashMap<>();
    
    public static int currentNumOfAncestries = 0; // number of series
    private static final double CHART_HEIGHT = 100; // default height for every chart
    private static String defaultHeading = "Admixture plot";
    
    // admixture plot colors -- add more depending on the number of ancestries
    static String[] hexCodes = {"#FF8C00", "#32CD32","#fffb00","#055ff0", "#ff0d00"};
    public static ArrayList<String> ancestryColors = new ArrayList<>(Arrays.asList(hexCodes));
    
    public static ArrayList<String> ancestryOrder = new ArrayList<String>();  // to order colour        
            
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
        
        // check first value: float or id ?
        try{
            // if it can't be float, then it might be another file type
            float firstValue = Float.valueOf(fields[0]);

            int num_ancestors = fields.length;
            ancestries = new String[num_ancestors];
            for (int i = 0; i < num_ancestors; i++) {
                ancestries[i] = "Ancestry " + Integer.toString(i + 1);
            }

            while (line != null) {
                fields = line.split("\\s+");
                admixValues.add(fields);
                line = r.readLine();
            }
            
            setPopulationGroups();

        }catch(Exception e){
            Genesis.throwInformationException("You might have imported a wrong file");          
        }
            
    }
    
    /**
     * create groups of individuals based on the phenotype selected column
     */
    @Override
    protected void setPopulationGroups(){
        populationGroups = new HashMap<>(); // k values grouped according to pheno column
        HashMap<String, String []> famAndQvalues = combineFamAndAdmixValues();
        
        for(String k : famData.keySet()){
            List<String> famIds = new ArrayList<>();
            ArrayList<String[]> valuesList = new ArrayList<>();
            String [] values = famAndQvalues.get(k); // [fid, iid, v1, v2, ...]
            
            if(admixturePhenoData.containsKey(k)){
                String group = admixturePhenoData.get(k)[Project.phenoColumnNumber-3];
                
                // if group doesnot exist, create the group and its new list
                if(!populationGroups.containsKey(group)){
                    valuesList.add(values);
                    populationGroups.put(group, valuesList);
                    
                    // fam ids grouped according to pheno column
                    famIds.add(values[1]); // value = iid
                    famOrder.put(group, famIds); // [MKK -> [iid1, iid2, iid3 ...]]
                }else{
                    populationGroups.get(group).add(values);
                    famOrder.get(group).add(values[1]);
                    
                }
                
            }else{
                ;               
            }
        
        }
        
    }
    
    /**
     * create multiple plots for every phenotype
     * @return 
     */
    @Override
    public ArrayList<StackedBarChart<String, Number>> createGraph(){
        
        // charts = [chart1, chart2, chart3, ...]
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
                setAncestryColors(populationGroupChart, ancestryColors); // set ancestry colors

            }
            
            // update current num of ancestries
            // used to label Ks (K=1,2,3,..) and create ancestry options buttons
            currentNumOfAncestries = populationGroupChart.getData().size();
                    
            // define populationGroupChart size
            populationGroupChart.getXAxis().setLabel(k);
            populationGroupChart.getXAxis().setVisible(false);
            populationGroupChart.setPrefHeight(CHART_HEIGHT);
            populationGroupChart.setMinHeight(CHART_HEIGHT);

            // legend
            for (Node n : populationGroupChart.getChildrenUnmodifiable()) {
                if (n.getClass().toString().equals("class com.sun.javafx.charts.Legend")) {
                    TilePane tn = (TilePane) n;
                    ObservableList<Node> children = tn.getChildren();
                    for(int j=0;j<children.size();j++){
                        Label lab = (Label) children.get(j).lookup(".chart-legend-item");
                        lab.setText(null);
                        lab.setGraphic(null);
                    }
                }
            }
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
