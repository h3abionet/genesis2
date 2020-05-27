/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.AdmixtureGraph;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.h3abionet.genesis.model.AdmixtureGraphEventsHandler;

/**
 * FXML Controller class
 *
 * @author henry
 */
public class AncestorOptionsController implements Initializable {

    @FXML
    private Label ancestorNameLabel;

    @FXML
    private Rectangle selectedColorDisplay;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private Button shiftUpBtn;

    @FXML
    private Button shiftDownBtn;

    @FXML
    private Button doneBtn;

    @FXML
    private Button cancelBtn;

    private static Color chosenColor;
    private ArrayList<StackedBarChart<String, Number>> listOfAdmixtureCharts;
    private StackedBarChart<String, Number> newChart;
    private ArrayList<StackedBarChart<String, Number>> newListOfCharts;
    private GridPane gridPane;
    private static final int SWAP_STEP = 1; // swap series one step down or up
    private int ancestryPosition; // position of the serie in the chart
    private boolean isColorSelected = false;
    private static ArrayList<HBox> listOfAncenstorHBox; // list of hboxes with ancestry btns
    private int rowIndexOfClickedAdmixChart;
    private boolean isShiftUpOrDownBtnClicked = false;

    /**
     * set ancestry position
     * @param serieIndex - position of the selectedSerie
     */
    public void setAncestryPosition(int serieIndex) {
        this.ancestryPosition = serieIndex;
        
        // if position is 0, the serie is at the bottom - disable the down btn
        if(ancestryPosition==0){
            shiftDownBtn.setDisable(true);
        }
        
        // if position is = to the index of last serie, disable the shiftup button
        if(ancestryPosition==listOfAdmixtureCharts.get(0).getData().size()-1){
            shiftUpBtn.setDisable(true);
        }
    }

    /**
     * set name of ancestor when loading this interface
     * @param ancestorName
     */
    public void setAncestorNumberLabel(String ancestorName) {
        ancestorNameLabel.setText(ancestorName);
        ancestorNameLabel.setAlignment(Pos.CENTER);
    }

    /**
     * display the default ancestor color when loading this interface
     * @param paint
     */
    public void setDefaultAncestorColor(Paint paint) {
        // display default ancestor color
        selectedColorDisplay.setFill(paint);
        
        // display chosen color when the color picker value is clicked
        colorPicker.setOnAction((ActionEvent t) -> {
            selectedColorDisplay.setFill(colorPicker.getValue());
            chosenColor = colorPicker.getValue();
            isColorSelected = true;
        });
    }

    @FXML
    private void cancelHandler(ActionEvent event) {
        Genesis.closeOpenStage(event);
    }

    @FXML
    private void doneHandler(ActionEvent event) {
        // change the order of the hboxes (ancestry buttons) in the previous window
        // check if any of the shift buttons was clicked otherwise listOfAncenstorHBox will be null
        if(isShiftUpOrDownBtnClicked){
            AdmixtureGraphEventsHandler.getLeftVBox().getChildren().clear();
            AdmixtureGraphEventsHandler.getLeftVBox().getChildren().addAll(listOfAncenstorHBox);
        }else{
            ;
        }
        
        Genesis.closeOpenStage(event);
        
    }

    @FXML
    private void shiftDownHandler(ActionEvent event) {
        // swap selected serie @ancestryPosition by 1 step down at every btn press
        moveSeries(ancestryPosition - SWAP_STEP);
        
        // reduce the position by one
        ancestryPosition--;
        
        // if position is 0, disable the shift down button
        if(ancestryPosition==0){
            shiftDownBtn.setDisable(true);
        }
        
    }

    @FXML
    private void shiftUpHandler(ActionEvent event) {
        // swap selected serie @ancestryPosition by 1 step up at every btn press
        moveSeries(ancestryPosition + SWAP_STEP);
        
        // reduce the position by one
        ancestryPosition++;
        
        // if position is = to the index of last serie, disable the shiftup button
        if(ancestryPosition==listOfAdmixtureCharts.get(0).getData().size()-1){
            shiftUpBtn.setDisable(true);
        }

    }
    
    /**
     * check if the color was selected before replacing the old colors
     * @return 
     */
    public boolean isIsColorSelected() {
        return isColorSelected;
    }
    
    /**
     * return chosen color to change the color of the series - replaces old color
     * @return 
     */
    public String getChosenColor() {
        String selectedColor = Integer.toHexString(chosenColor.hashCode());
        return selectedColor;
    }
    
    /**
     * return the paint to fill the ancestor color display - replaces old color
     * @return 
     */
    public Paint getChosenPaint() {
        return chosenColor;
    }
    
    /**
     * This method sets the new listOfAncenstorHBox which added to the leftVBox
     * It also performs the job of swapping of series
     * @param newPosition
     */
    public void moveSeries(int newPosition){
        // set this boolean to true
        isShiftUpOrDownBtnClicked = true;
        
        // swap colors
        Collections.swap(AdmixtureGraph.admixColors, ancestryPosition, newPosition);
        
        // set the  listOfAncenstorHBox and swap the position of HBoxes
        listOfAncenstorHBox = AdmixtureGraphEventsHandler.getListOfAncenstorHBox();
        Collections.swap(listOfAncenstorHBox, ancestryPosition, newPosition);
        
        for (Node node : gridPane.getChildren()) {
            
            // from the second column to the last column. The 1st col stores K values
            for (int col = 1; col < listOfAdmixtureCharts.size(); col++) {
                
                // change the row index. 
                if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == rowIndexOfClickedAdmixChart) {
                    newChart = (StackedBarChart<String, Number>) node;

                    List<XYChart.Series<String, Number>> newSeries = new ArrayList<>();

                    newChart.setAnimated(false);
                    newChart.getData().forEach(oldSerie -> {
                        XYChart.Series<String, Number> newSerie = new XYChart.Series<>();
                        newSerie.setName(oldSerie.getName());

                        oldSerie.getData().stream().map(d -> new XYChart.Data<String, Number>(d.getXValue(), d.getYValue()))
                                .forEach(newSerie.getData()::add);
                        newSeries.add(newSerie);
                    });
                    
                    // swap order order of series
                    Collections.swap(newSeries, ancestryPosition, newPosition);
                    
                    newChart.getData().clear();
                    newChart.getData().addAll(newSeries);

                    // add to new chart to the list of new swapped charts
                    newListOfCharts.add(newChart);

                    // set colors
                    setAncestryColors(newChart, AdmixtureGraph.admixColors);

                }
            }

        }
        
    }
    
    /**
     * set the color of the series in a stacked bar chart
     * @param stackedBarChart
     * @param admixColors 
     */
    private void setAncestryColors(StackedBarChart<String, Number> stackedBarChart, ArrayList<String> admixColors) {
        for (int i = 0; i < stackedBarChart.getData().size(); i++) {
            int serieIndex = i; // serie position or index
            String serieColor = admixColors.get(i); // get color in this serie position
            
            // change the default serie color with the new color
            stackedBarChart.getData().get(i).getData().forEach((bar) -> {
                bar.getNode().lookupAll(".default-color" + serieIndex + ".chart-bar")
                        .forEach(n -> n.setStyle("-fx-bar-fill: " + serieColor + ";"));
            });
        }
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // intialise new list to store new list of swapped charts
        newListOfCharts = new ArrayList<>();
        
        // get the list of the current staked bar charts being displayed
        listOfAdmixtureCharts = MainController.getListOfAdmixtureCharts();
        
        // set the current gridpane - to get the row index of any clicked chart
        gridPane = MainController.getGridPane();
        
        // set row index
        rowIndexOfClickedAdmixChart = AdmixtureGraphEventsHandler.getRowIndexOfClickedAdmixChart();
    }
}
