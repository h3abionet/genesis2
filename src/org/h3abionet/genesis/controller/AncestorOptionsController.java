/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
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
    private GridPane gridPane;
//    private int ancestryPosition; // position of the serie in the chart
    private boolean isColorSelected = false;
    private static ArrayList<HBox> listOfAncenstorHBox; // list of hboxes with ancestry btns
    private int rowIndexOfClickedAdmixChart;
    private boolean isShiftUpOrDownBtnClicked = false;
    /**
     * set ancestry position
     *
     * @param serieIndex - position of the selectedSerie
     */
//    public void setAncestryPosition(int serieIndex) {
//        ancestryPosition = serieIndex;
//        
//    }

    /**
     * set name of ancestor when loading this interface
     *
     * @param ancestorName
     */
    public void setAncestorNumberLabel(String ancestorName) {
        ancestorNameLabel.setText(ancestorName);
        ancestorNameLabel.setAlignment(Pos.CENTER);
    }

    /**
     * display the default ancestor color when loading this interface
     *
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
        // add code to change the order of the hboxes (ancestry buttons) in the previous window
        // if any of the shift buttons was clicked otherwise listOfAncenstorHBox wont change
        Genesis.closeOpenStage(event);

    }
    
    @FXML
    private void shiftDownHandler(ActionEvent event) {
        verticalMove(AdmixtureGraph.ancestryOrder.size()-1, 0);
    }

    @FXML
    private void shiftUpHandler(ActionEvent event) {
        verticalMove(0, AdmixtureGraph.ancestryOrder.size()-1);
    }

    /**
     * check if the color was selected before replacing the old colors
     *
     * @return
     */
    public boolean isIsColorSelected() {
        return isColorSelected;
    }

    /**
     * return chosen color to change the color of the series - replaces old
     * color
     *
     * @return
     */
    public String getChosenColor() {
        String selectedColor = Integer.toHexString(chosenColor.hashCode());
        return selectedColor;
    }

    /**
     * return the paint to fill the ancestor color display - replaces old color
     *
     * @return
     */
    public Paint getChosenPaint() {
        return chosenColor;
    }

    /**
     * performs the job of moving series
     *
     * @param newPosition
     */
    public void verticalMove(int pos_removed, int pos_added) {
        // set this boolean to true
        isShiftUpOrDownBtnClicked = true;
        
        Comparator<XYChart.Series<String, Number>> mycomp
                = (s1, s2)
                -> AdmixtureGraph.ancestryOrder.indexOf(s2.getName()) - AdmixtureGraph.ancestryOrder.indexOf(s1.getName());
        
        for (Node node : gridPane.getChildren()) {

            // from the second column to the last column. The 1st col stores K values
            for (int col = 1; col < listOfAdmixtureCharts.size(); col++) {

                // change the row index. 
                if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == rowIndexOfClickedAdmixChart) {
                    newChart = (StackedBarChart<String, Number>) node;
                    newChart.setData(newChart.getData().sorted(mycomp));
                }
            }
        }
     
        // now we make arbitrary change to order of colours for next time round
        String moved_label;
        moved_label = AdmixtureGraph.ancestryOrder.remove(pos_removed);
        AdmixtureGraph.ancestryOrder.add(pos_added, moved_label);

    }
    
    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // get the list of the current staked bar charts being displayed
        listOfAdmixtureCharts = MainController.getListOfAdmixtureCharts();

        // set the current gridpane - to get the row index of any clicked chart
        gridPane = MainController.getGridPane();

        // set row index
        rowIndexOfClickedAdmixChart = AdmixtureGraphEventsHandler.getRowIndexOfClickedAdmixChart();

    }
}
