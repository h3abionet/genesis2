/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.AdmixtureGraph;

/**
 * FXML Controller class
 *
 * @author henry
 */
public class AdmixtureOptionsController implements Initializable {

    @FXML
    private Button creatLabelBtn;

    @FXML
    private Button populationGroupOptionsBtn;

    @FXML
    private Button previousGraphColourBtn;

    @FXML
    private Button nextGraphColourBtn;

    @FXML
    private ComboBox<String> orderCombobox;

    @FXML
    private Button deleteGraphBtn;
    
    @FXML
    private Button cancelBtn;
    
    // list of admixture charts
    private ArrayList<StackedBarChart<String, Number>> listOfCharts;
    
    // clicked admixture chart
    private StackedBarChart<String, Number> admixChart;
    
    // number of series;
    private final int numOfAncestries = AdmixtureGraph.currentNumOfAncestries;
    
    // borders for vbox and hbox containers
    private final String cssLayout = "-fx-border-color: #d9d9d9; -fx-border-width: 1;";

    @FXML
    private void deleteGraph(ActionEvent event) {

    }

    @FXML
    private void loadPopulationGroupOptions(ActionEvent event) {
        VBox optionsContainerVBox = new VBox(5);
        optionsContainerVBox.setPadding(new Insets(10));
        
        HBox populationOptionsHBox = new HBox(2);
        populationOptionsHBox.setStyle(cssLayout);
        
        // create the right vbox to keep the fam order and dominant order buttons
        VBox rightVBox = setVbox(new Label("Sort within populations algorithmically"));
        rightVBox.setStyle(cssLayout);
        
        // fam sort button
        Button famOrderBtn = new Button("Fam order");
        famOrderBtn.setOnMouseClicked((MouseEvent famOrderEvent) -> {
            listOfCharts.forEach((s) -> {
                sortToFamOrder(s);
            });
        });
        
        // dominant sort button
        Button dominantColourBtn = new Button("Dominant colour");
        dominantColourBtn.setOnMouseClicked((MouseEvent devt) -> {
                for (StackedBarChart<String, Number> stackedBarChart : listOfCharts) {
                    // store totals of yvalue elements in every serie                                            
                    ArrayList<Double> sumList = new ArrayList<>();

                    // get sum of all yvalues in every serie / ancestry
                    for(int s=0; s<stackedBarChart.getData().size(); s++){
                        double sum = 0;
                        for(int n=0; n<stackedBarChart.getData().get(s).getData().size(); n++){
                            sum += stackedBarChart.getData().get(s).getData().get(n).getYValue().doubleValue();

                        }
                        sumList.add(sum);                                           
                    }

                    // get index of the max value in sumList = dorminant serie index in the chart
                    int ancestryIndex = sumList.indexOf(Collections.max(sumList));

                    // sort chart by serie with max yvalues (dominant)
                    sortChartByColor(stackedBarChart, ancestryIndex);

                }

            });

        rightVBox.getChildren().addAll(famOrderBtn, dominantColourBtn);

        VBox leftVBox = setVbox(new Label("Change and order colours"));
        
    }
    
    @FXML
    private void cancelHandler(ActionEvent event) {
        Genesis.closeOpenStage(event);
    }

    public void setAdmixChart(StackedBarChart<String, Number> admixChart) {
        this.admixChart = admixChart;
    }
    
    private VBox setVbox(Node node){
        VBox vb = new VBox(node);
        vb.setPadding(new Insets(10));
        vb.setSpacing(5);
        vb.setStyle(cssLayout);
        return vb;
    }
    
    /**
     * sort individuals according to fam order
     * @param s 
     */
    private void sortToFamOrder(StackedBarChart<String, Number> s){
        // get sorted iids
        CategoryAxis xAxis = (CategoryAxis) s.getXAxis();
        ObservableList<String> sorted_iids = xAxis.getCategories();
        
        // get fam order iids for this chart (population group) using its id as the key
        List<String> fam_order_iids = AdmixtureGraph.famOrder.get(s.getId());
        
        // for loop run this number of times
        int numOfIndividuals = s.getData().get(0).getData().size();
        
        // swap positions
        for (int j = 0; j < numOfIndividuals; j++) {
            String temp = sorted_iids.remove(sorted_iids.indexOf(fam_order_iids.get(j)));
            sorted_iids.add(j, temp);
            xAxis.setCategories(sorted_iids);
        }
    }
    
    /**
     * sort iids based on selected serie or ancestry
     * @param stackedBarChart
     * @param ancestryNumber 
     */
    private void sortChartByColor(StackedBarChart<String, Number> stackedBarChart, int ancestryNumber){
        
        CategoryAxis xAxis = (CategoryAxis) stackedBarChart.getXAxis();
        
        XYChart.Series<String, Number>  chosenAncestry = stackedBarChart.getData().get(ancestryNumber);
        int numOfIndividuals = chosenAncestry.getData().size();

        ObservableList<String> iids = xAxis.getCategories();
        
        // sort the chosen ancestry
        chosenAncestry.getData().sort(Comparator.comparingDouble(d -> d.getYValue().doubleValue()));

        // get positions of iids from the sorted ancestry
        String[] sorted_iids = new String[numOfIndividuals];
        for (int p = 0; p < numOfIndividuals; p++) {
            sorted_iids[p] = chosenAncestry.getData().get(p).getXValue();
        }

        // swap positions
        for (int j = 0; j < numOfIndividuals; j++) {
            String temp = iids.remove(iids.indexOf(sorted_iids[j]));
            iids.add(j, temp);
            xAxis.setCategories(iids);
        }

    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        listOfCharts = MainController.getListOfAdmixtureCharts();
        
        // TODO - change this item list based on the position of the graph
        orderCombobox.getItems().addAll("Shift Graph Up", "Shift Graph to Top", 
                "Shift Graph Down", "Shift Graph to Bottom");
    }    
    
}
