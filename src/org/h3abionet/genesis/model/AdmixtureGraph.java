/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.model;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.TilePane;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.controller.MainController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author henry
 */
public class AdmixtureGraph extends Graph implements Serializable {

    private static final long serialVersionUID = 2L;

    private String[] ancestryLabels;
    public int currentNumOfAncestries = 0; // number of series
    private final double CHART_HEIGHT = 100; // default height for every chart
    private String defaultHeading = "Admixture plot";
    
    // admixture plot colors -- add more depending on the number of ancestries
    private String[] hexCodes = {"#FF8C00", "#32CD32", "#fffb00", "#055ff0", "#ff0d00"};
    public ArrayList<String> ancestryColors = new ArrayList<>(Arrays.asList(hexCodes));
    
    public ArrayList<String> ancestryOrder = new ArrayList<>();  // to order colour

    private transient ArrayList<StackedBarChart<String, Number>> listOfStackedBarCharts;
    private int numOfAncestries;

    public static void setChartIndex(int chartIndex) {
        AdmixtureGraph.chartIndex = chartIndex;
    }

    private static int chartIndex = 0;
    private MainController mainController;

    /**
     * Constructor
     * @param admixtureFilePath
     * @throws IOException
     */
    public AdmixtureGraph(String admixtureFilePath) throws IOException {
        readGraphData(admixtureFilePath);
//
//        for (String l: ancestryLabels)
//            ancestryOrder.add(l);

        project.setAdmixtureGraph(this);
    }
    
    @Override
    public void readGraphData(String admixtureFilePath) throws IOException {
        
        BufferedReader r = Genesis.openFile(admixtureFilePath);
        String line = r.readLine();
        String fields[] = line.split("\\s+");
        
        // check first value: float or id ?
        try{
            // if it can't be float, then it might be another file type
            float firstValue = Float.valueOf(fields[0]);

            setNumOfAncestries(fields.length);

            project.setImportedKs(numOfAncestries);

            setAncestryLabels(numOfAncestries);

            for(Subject sub: project.getPcGraphSubjects()){
                fields = line.split("\\s+");
                ArrayList<String> qValues = new ArrayList<>(Arrays.asList(fields));
                String iid = sub.getIid();
                qValues.add(0, iid);
                sub.setQs(qValues);
                line = r.readLine();
            }

        }catch(Exception e){
            Genesis.throwInformationException("You might have imported a wrong file");          
        }
    }

    public void setNumOfAncestries(int numOfAncestries){
        this.numOfAncestries = numOfAncestries;
    }

    public void setAncestryLabels(int numOfAncestries){

        ancestryLabels = new String[numOfAncestries];
        for (int i = 0; i < numOfAncestries; i++) {
            ancestryLabels[i] = "Ancestry " + (i + 1);
        }
    }

    /**
     * create multiple plots for every phenotype
     * @return 
     */
    @Override
    public void createAdmixGraph(){
        // charts = [chart1, chart2, chart3, ...]
        ArrayList<StackedBarChart<String, Number>> charts = new ArrayList<>(); // store stackedbars for every group

        for(String groupName : project.getGroupNames()){

            XYChart.Series<String, Number> ancestryValues;
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis(0,1,0.1);
            xAxis.setTickMarkVisible(false);
            xAxis.setTickLabelsVisible(false);

            yAxis.setMinorTickVisible(false);
            yAxis.setTickMarkVisible(false);
            yAxis.setTickLabelsVisible(false);

            StackedBarChart<String, Number> populationGroupChart = new StackedBarChart<>(xAxis, yAxis);

            // collect all values belong to this population groups
            // [{iid, v1, v2}, {iid, v1, v2}, ...]
            List<ArrayList<String>> listOfQValues = new ArrayList<>();

            for(Subject sub: project.getPcGraphSubjects()){
                if(project.isPhenoFileProvided()) {
                    if (sub.getPhenos()[project.getPhenoColumnNumber() - 1].equals(groupName) && sub.getqValuesList().size() != 0 && sub.isHidden()==false) {
                        ArrayList<String> ls = sub.getqValuesList().get(chartIndex);
                        listOfQValues.add(ls); //{iid, v1, v2}
                    }
                }
            }


            // create data series for every ancestry
            for(int i = 0; i< ancestryLabels.length; i++){
                ancestryValues = new XYChart.Series<>();
                ancestryValues.setName(ancestryLabels[i]);

                for(ArrayList<String> qValues: listOfQValues){ // get individual values
                    ancestryValues.getData().add(new XYChart.Data<>(qValues.get(0), Float.parseFloat(qValues.get(1 + i)))); //{iid, v1, v2}
                }

                populationGroupChart.getData().add(ancestryValues); // add values to chart
                setAncestryColors(populationGroupChart, ancestryColors); // set ancestry colors
            }

            // update current num of ancestries
            // used to label Ks (K=1,2,3,..) and create ancestry options buttons
            currentNumOfAncestries = populationGroupChart.getData().size();

            // define populationGroupChart size
            populationGroupChart.getXAxis().setLabel(groupName);
            populationGroupChart.getXAxis().setVisible(false);
            populationGroupChart.setPrefHeight(CHART_HEIGHT);
            populationGroupChart.setMinHeight(CHART_HEIGHT);
            populationGroupChart.setLegendVisible(false);

            // set the css stylesheet
            populationGroupChart.getStylesheets().add("css/admixture.css");

           // remove all legend items
            for (Node n : populationGroupChart.getChildrenUnmodifiable()) {
                if (n.getClass().toString().equals("class com.sun.javafx.charts.Legend")) {
                    TilePane tn = (TilePane) n;
                    ObservableList<Node> children = tn.getChildren();
                    tn.getChildren().remove(0, children.size()); // remove all items in range 0 to size
                }
            }

            if(populationGroupChart.getData().get(0).getData().size()>0){
                charts.add(populationGroupChart);
            }
        }

        listOfStackedBarCharts =  charts;
        chartIndex += 1;
    }

    public ArrayList<StackedBarChart<String, Number>> getListOfStackedBarCharts() {
        return listOfStackedBarCharts;
    }

    /**
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

    public double getCHART_HEIGHT() {
        return CHART_HEIGHT;
    }

    public int getCurrentNumOfAncestries() {
        return currentNumOfAncestries;
    }

    public String getDefaultHeading() {
        return defaultHeading;
    }

    /**
     * set project if reading it as a saved object
     * @param project
     */
    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public void createGraph(int pcaX, int pcaY) {;}

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
