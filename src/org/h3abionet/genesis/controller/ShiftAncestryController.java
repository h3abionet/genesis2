/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

//import com.sun.javafx.charts.Legend;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import org.h3abionet.genesis.Genesis;
import javafx.scene.layout.GridPane;

/**
 * FXML Controller class
 *
 * @author henry
 */
public class ShiftAncestryController implements Initializable {

    @FXML
    private Label ancestorNameLabel;

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
    private int rowIndexOfClickedAdmixChart;
    private String[] ancestries;
    private static int numOfSeries;
    private ArrayList<String> ancestryOrder = new ArrayList<String>();

    /**
     * set name of ancestor when loading this interface
     * @param ancestorName
     */
    public void setAncestorNumberLabel(String ancestorName) {
        ancestorNameLabel.setText(ancestorName);
    }
    
    @FXML
    private void cancelHandler(ActionEvent event) {
        Genesis.closeOpenStage(event);
    }

    @FXML
    private void doneHandler(ActionEvent event) {
        Genesis.closeOpenStage(event);
    }
    
    @FXML
    private void shiftDownHandler(ActionEvent event) {
        verticalMove(ancestryOrder.size()-1, 0);
    }

    @FXML
    private void shiftUpHandler(ActionEvent event) {
        verticalMove(0, ancestryOrder.size()-1);
    }

    /**
     * performs the job of moving series
     *
     * @param newPosition
     */
    public void verticalMove(int pos_removed, int pos_added) {
        Comparator<XYChart.Series<String, Number>> mycomp
                = (s1, s2)
                -> ancestryOrder.indexOf(s2.getName()) - ancestryOrder.indexOf(s1.getName());
        
        for (Node node : gridPane.getChildren()) {
            
            // from the second column to the last column. The 1st col stores K values
            for (int col = 1; col < listOfAdmixtureCharts.size()+1; col++) {

                // change the row index. 
                if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == rowIndexOfClickedAdmixChart) {
                    newChart = (StackedBarChart<String, Number>) node;
                    newChart.setData(newChart.getData().sorted(mycomp));
                    
                    // clear legend items
//                    Legend legend = (Legend) newChart.lookup(".chart-legend").;
//                    legend.getItems().clear();

                }
            }
        }
     
        // now we make arbitrary change to order of colours for next time round
        String moved_label;
        moved_label = ancestryOrder.remove(pos_removed);
        ancestryOrder.add(pos_added, moved_label);

    }

    public void setNumOfAncestry(int size) {
        this.numOfSeries = size;
        // set ancestries and their order
        ancestries = new String[numOfSeries];
        for (int i = 0; i < numOfSeries; i++) {
            ancestries[i] = "Ancestry " + Integer.toString(i + 1);
        }
        
        for (String l:ancestries)
            ancestryOrder.add(l);
    }
    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // get row index of clicked chart
        rowIndexOfClickedAdmixChart = AdmixtureGraphEventsHandler.getRowIndexOfClickedAdmixChart();

        // get the list of the current staked bar charts being displayed
        listOfAdmixtureCharts = MainController.getAllAdmixtureCharts().get(rowIndexOfClickedAdmixChart);

        // set the current gridpane - to get the row index of any clicked chart
        gridPane = MainController.getGridPane();
              
    }

}
