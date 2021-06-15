/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.model;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.controller.AdmixtureIndividualDetailsController;
import org.h3abionet.genesis.controller.MainController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
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
    private int rowIndexOfClickedAdmixChart;
    private String clickedId;

    public static void setChartIndex(int chartIndex) {
        AdmixtureGraph.chartIndex = chartIndex;
    }

    private static int chartIndex = 0;
    private MainController mainController;
    private ArrayList<String> iidDetails;

    /**
     * Constructor
     *
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
        try {
            setNumOfAncestries(fields.length);
            project.setImportedKs(numOfAncestries);
            setAncestryLabels(numOfAncestries);

            for (Subject sub : project.getSubjectsList()) {
                fields = line.split("\\s+");
                ArrayList<String> qValues = new ArrayList<>(Arrays.asList(fields));
                String iid = sub.getIid();
                qValues.add(0, iid); // add qs
                sub.setQs(qValues);
                line = r.readLine();
            }
        } catch (Exception e) {
            Genesis.throwInformationException("You might have imported a wrong file");
        }
    }

    public void setNumOfAncestries(int numOfAncestries) {
        this.numOfAncestries = numOfAncestries;
    }

    public void setAncestryLabels(int numOfAncestries) {

        ancestryLabels = new String[numOfAncestries];
        for (int i = 0; i < numOfAncestries; i++) {
            ancestryLabels[i] = "Ancestry " + (i + 1);
        }
    }

    /**
     * create multiple plots for every phenotype
     *
     * @return
     */
    @Override
    public void createAdmixGraph() {
        // charts = [chart1, chart2, chart3, ...]
        ArrayList<StackedBarChart<String, Number>> charts = new ArrayList<>(); // store stacked charts for every group

        for (String groupName : project.getGroupNames()) {

            XYChart.Series<String, Number> ancestry;
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis(0, 1, 0.1);
            xAxis.setTickMarkVisible(false);
            xAxis.setTickLabelsVisible(false);

            yAxis.setMinorTickVisible(false);
            yAxis.setTickMarkVisible(false);
            yAxis.setTickLabelsVisible(false);

            StackedBarChart<String, Number> populationGroupChart = new StackedBarChart<>(xAxis, yAxis);

            // collect all values belong to this population groups
            // [{iid, v1, v2}, {iid, v1, v2}, ...]
            List<ArrayList<String>> listOfQValues = new ArrayList<>();

            for (Subject sub : project.getSubjectsList()) {
                if (project.isPhenoFileProvided()) {
                    if (sub.getPhenos()[project.getPhenoColumnNumber() - 1].equals(groupName) && sub.getqValuesList().size() != 0 && sub.isHidden() == false) {
                        ArrayList<String> ls = sub.getqValuesList().get(chartIndex);
                        listOfQValues.add(ls); //{iid, v1, v2}
                    }
                }
            }

            // create data series for every ancestry
            for (int i = 0; i < ancestryLabels.length; i++) {
                ancestry = new XYChart.Series<>();
                ancestry.setName(ancestryLabels[i]);

                for (ArrayList<String> qValues : listOfQValues) { // get individual values
                    ancestry.getData().add(new XYChart.Data<>(qValues.get(0), Float.parseFloat(qValues.get(1 + i)))); //{iid, v1, v2}
                }

                populationGroupChart.getData().add(ancestry); // add values to chart
            }
            setAncestryColors(populationGroupChart, ancestryColors); // set ancestry colors

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
            // only add charts with data
            if (populationGroupChart.getData().get(0).getData().size() > 0) {
                showIndividualDetails(populationGroupChart);
                charts.add(populationGroupChart);
            }
        }

        listOfStackedBarCharts = charts;
        chartIndex += 1;
    }

    private void showIndividualDetails(StackedBarChart<String, Number> admixChart) {
        // add listeners to chart
        // on left click mouse event handler
        admixChart.getData().forEach((serie) -> {
            serie.getData().forEach((item) -> {
                item.getNode().setOnMousePressed((MouseEvent event) -> {
                    MouseButton button = event.getButton();
                    if (button == MouseButton.PRIMARY) {
                        try {
                            FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("view/AdmixtureIndividualDetails.fxml"));
                            Parent parent = (Parent) fxmlLoader.load();
                            Stage dialogStage = new Stage();
                            dialogStage.setScene(new Scene(parent));
                            dialogStage.setResizable(false);

                            // show subject details when clicked
                            AdmixtureIndividualDetailsController admixIndivDetailsCtrler = fxmlLoader.getController();
                            admixIndivDetailsCtrler.setProject(project);
                            admixIndivDetailsCtrler.setAdmixtureGraph(this);
                            admixIndivDetailsCtrler.setAdmixtureChart(admixChart);
                            admixIndivDetailsCtrler.setClickedId(item.getXValue());
                            setClickedId(item.getXValue()); // set id of the clicked individual on the graph

                            for (Subject sub : project.getSubjectsList()) {
                                if (sub.getIid().equals(item.getXValue())) {
                                    List<String> list = sub.getqValuesList().get(rowIndexOfClickedAdmixChart).subList(1,
                                            sub.getqValuesList().get(rowIndexOfClickedAdmixChart).size());
                                    iidDetails = new ArrayList<>(Arrays.asList(sub.getPhenos()));
                                    iidDetails.add(sub.getSex());
                                    admixIndivDetailsCtrler.setValuesList(list); // get Y value
                                    break;
                                }
                            }
                            admixIndivDetailsCtrler.setPhenoList(iidDetails);
                            dialogStage.showAndWait();
                        } catch (IOException ex) {
                            ;
                        }
                    }
                });
            });
        });
    }

    /**
     * set id for clicked individual
     * @param clickedId
     */
    private void setClickedId(String clickedId) {
        this.clickedId = clickedId;
    }

    public void showIndividual(String[] ids){
        String iid = ids[1];
        String groupName = null;
        ArrayList<ArrayList<String>> qValues = null;

        // set subject group and add individual
        for (Subject s : project.getSubjectsList()) {
            if (s.getIid().equals(iid)) {
                s.setHidden(false); // set hidden to false
                groupName = s.getPhenos()[project.getPhenoColumnNumber() - 1];
                // remove this individual to a list of hidden individuals
                project.getHiddenPoints().remove(s.getFid()+" "+s.getIid()); // fid+" "+iid
                qValues = s.getqValuesList();
                break;
            }
        }

        for (int i = 0; i < mainController.getAllAdmixtureCharts().size(); i++) {
            // get one admix plot: // plot1 -> {[chart 1,chart 2,..]}
            for (StackedBarChart<String, Number> admixGraph : mainController.getAllAdmixtureCharts().get(i)) {
                if(admixGraph.getId().equals(groupName)){ // right graph or group for the individual
                    CategoryAxis xAxis = (CategoryAxis) admixGraph.getXAxis();
                    xAxis.getCategories().add(iid); //TODO add subject to right position if graph was sorted (track order of series)

                    for (int s=0; s<admixGraph.getData().size();s++){
                        XYChart.Series<String, Number> serie = admixGraph.getData().get(s);
                        Float yValue = Float.parseFloat(qValues.get(i).get(s+1));
                        serie.getData().add(new XYChart.Data<>(iid, yValue));
                        //TODO add this data node to a list of all graphs
                    }
                }
                //TODO add admix colors for every subject [if k = 3, sub.setAdmixColor([b, w, y])] - same as qVaules
                setAncestryColors(admixGraph, ancestryColors);
                // add click event handler
                showIndividualDetails(admixGraph);
            }
        }
    }

    /**
     * hide individual in all plots
     */
    public void hideIndividual() {
        String groupName = null;

        // loop through charts to remove all individual { plot1 -> {[chart 1,chart 2,..]}, plot2 -> {[chart 1,chart 2,..]},...}
        for (int i = 0; i < mainController.getAllAdmixtureCharts().size(); i++) { // plot1 -> {[chart 1,chart 2,..]}
            // get one admix plot: // plot1 -> {[chart 1,chart 2,..]}
            for (StackedBarChart<String, Number> admixGraph : mainController.getAllAdmixtureCharts().get(i)) { // chart1
                for (XYChart.Series<String, Number> ancestry : admixGraph.getData()) { // series
                    for (XYChart.Data<String, Number> individual : ancestry.getData()) {
                        String iid = individual.getXValue(); // individual id

                        if (iid.equals(clickedId)) {

                            // get individual with this iid and set to hide
                            for (Subject s : project.getSubjectsList()) {
                                if (s.getIid().equals(iid)) {
                                    s.setHidden(true); // set hidden
                                    groupName = s.getPhenos()[project.getPhenoColumnNumber() - 1];

                                    // add this individual to a list of hidden individuals
                                    project.getHiddenPoints().add(s.getFid()+" "+s.getIid()); // fid+" "+iid
                                    break;
                                }
                            }
                            // get categories and remove the iid
                            CategoryAxis xAxis = (CategoryAxis) admixGraph.getXAxis();
                            ObservableList<String> iids = xAxis.getCategories();
                            int index = iids.indexOf(iid);
                            iids.remove(index);
                            xAxis.setCategories(iids);
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

    public ArrayList<StackedBarChart<String, Number>> getListOfStackedBarCharts() {
        return listOfStackedBarCharts;
    }

    /**
     * Set colors
     */
    private void setAncestryColors(StackedBarChart<String, Number> stackedBarChart, ArrayList<String> admixColors) {
        for (int i = 0; i < stackedBarChart.getData().size(); i++) {
            int ancestryIndex = i;
            String ancestryColor = admixColors.get(i);
            stackedBarChart.getData().get(i).getData().forEach((bar) -> {
                bar.getNode().lookupAll(".default-color" + ancestryIndex + ".chart-bar")
                        .forEach(n -> n.setStyle("-fx-bar-fill: " + ancestryColor + ";"));
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
     *
     * @param project
     */
    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public void createGraph(int pcaX, int pcaY) {
        ;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setRowIndexOfClickedAdmixChart(int rowIndexOfClickedAdmixChart) {
        this.rowIndexOfClickedAdmixChart = rowIndexOfClickedAdmixChart;
    }
}
