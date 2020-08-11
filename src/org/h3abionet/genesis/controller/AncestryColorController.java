/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import org.h3abionet.genesis.Genesis;

public class AncestryColorController implements Initializable{

    @FXML
    private Rectangle selectedColorDisplay;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private CheckBox allPlotsCheckBox;

    @FXML
    private Button cancelBtn;

    @FXML
    private Button doneBtn;
    private int rowIndexOfClickedAdmixChart;
    private ArrayList<StackedBarChart<String, Number>> listOfAdmixtureCharts;
    private GridPane gridPane;
    private int serieIndex;
    private static Color chosenColor; // Color
    private String selectedColor; //Hex to string code
    private boolean colorSelected = false;

    @FXML
    private void cancelHandler(ActionEvent event) {
        Genesis.closeOpenStage(event);
    }

    @FXML
    private void doneHandler(ActionEvent event) {
        // change colors of all plots 
        if(allPlotsCheckBox.isSelected()){
            try{
                for(ArrayList<StackedBarChart<String, Number>> a: MainController.getAllAdmixtureCharts()){
                    for (StackedBarChart<String, Number> stackedbarChart : a) {
                        stackedbarChart.getData().forEach((series) -> {
                            series.getData().forEach((bar) -> {
                                bar.getNode().lookupAll(".default-color" + serieIndex + ".chart-bar")
                                        .forEach(n -> n.setStyle("-fx-background-color: #" + selectedColor + ";"));
                            });
                        });
                    }
                
                }
            }catch(Exception e){
                Genesis.throwErrorException("Oops! Some plots don't have your this ancestry");
            }
        }
        Genesis.closeOpenStage(event);
    }
    /**
     * show first color of the ancestry before changing it
     * @param paint 
     */
    public void setAncestryColor(Paint paint) {
        // display default ancestor color
        selectedColorDisplay.setFill(paint);
        
        // colorpicker modifies the plots upon click
        colorPicker.setOnAction((ActionEvent t) -> {
            // display chosen color when the color picker value is clicked
            selectedColorDisplay.setFill(colorPicker.getValue());
            
            // set chosen color
            chosenColor = colorPicker.getValue();
            
            selectedColor = Integer.toHexString(colorPicker.getValue().hashCode());
            // change the color of series
            for (StackedBarChart<String, Number> stackedbarChart : listOfAdmixtureCharts) {
                stackedbarChart.getData().forEach((series) -> {
                    series.getData().forEach((bar) -> {
                        bar.getNode().lookupAll(".default-color" + serieIndex + ".chart-bar")
                                .forEach(n -> n.setStyle("-fx-background-color: #" + selectedColor + ";"));
                    });
                });

            }

            colorSelected = true;
        });

    }
    /**
     * set index of the serie whose color needs to be changed
     * @param ancestorIndex 
     */
    public void setAncestryIndex(int ancestorIndex) {
        this.serieIndex = ancestorIndex;
    }
    /**
     * check if the color picker selected before changing the color
     * of the ancestry color display in the previous stage
     * @return 
     */
    public boolean isColorSelected() {
        return colorSelected;
    }
    /**
     * return new chosen paint(color) of the ancestry
     * @return 
     */
    public Paint getChosenPaint() {
        return colorPicker.getValue();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // get row index of clicked chart
        rowIndexOfClickedAdmixChart = AdmixtureGraphEventsHandler.getRowIndexOfClickedAdmixChart();
        System.out.println(rowIndexOfClickedAdmixChart);

        // get the list of the current staked bar charts being displayed
        listOfAdmixtureCharts = MainController.getAllAdmixtureCharts().get(rowIndexOfClickedAdmixChart);

        // set the current gridpane - to get the row index of any clicked chart
        gridPane = MainController.getGridPane();
              
    }
    
}
