/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private static int counter = 0;
    /**
     * set ancestry position
     *
     * @param serieIndex - position of the selectedSerie
     */
    public void setAncestryPosition(int serieIndex) {
        int numOfSeries = listOfAdmixtureCharts.get(0).getData().size() - 1;
        System.out.println(numOfSeries);
        this.ancestryPosition = serieIndex;

        if (ancestryPosition == 0) { // first position
            shiftUpBtn.setDisable(true);
        } else if (ancestryPosition == numOfSeries) { // last position
            shiftDownBtn.setDisable(true);
        } else {
            // disable btns if ancestry is in middle position
            shiftDownBtn.setDisable(false);
            shiftUpBtn.setDisable(false);
        }
    }

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
        // change the order of the hboxes (ancestry buttons) in the previous window
        // check if any of the shift buttons was clicked otherwise listOfAncenstorHBox will be null
        if (isShiftUpOrDownBtnClicked) {
            AdmixtureGraphEventsHandler.getLeftVBox().getChildren().clear();
            AdmixtureGraphEventsHandler.getLeftVBox().getChildren().addAll(listOfAncenstorHBox);
        } else {
            ;
        }

        Genesis.closeOpenStage(event);

    }
    
    @FXML
    private void shiftDownHandler(ActionEvent event) {
        isShiftUpOrDownBtnClicked = true;
        listOfAncenstorHBox = AdmixtureGraphEventsHandler.getListOfAncenstorHBox();
        counter++;
        if(counter==1){
            Collections.reverse(listOfAncenstorHBox); // reverse elements in the list
        }else{
            HBox hbox; // change the order of hboxes
            hbox = listOfAncenstorHBox.remove(0);
            listOfAncenstorHBox.add(hbox);       
        }
        
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
        
        // now we make arbitrary chaneg to order of colours for next time round
        String first;
        first = AdmixtureGraph.ancestryOrder.remove(0);
        AdmixtureGraph.ancestryOrder.add(first);  // and the first shall be last

    }

    @FXML
    private void shiftUpHandler(ActionEvent event) {
                                    
        Comparator<XYChart.Data<String, Number>> sortComp
                = (o1, o2)
                -> { 
                    Number xValue1 = (Number) o1.getYValue();
                    Number xValue2 = (Number) o2.getYValue();
                    return new BigDecimal(xValue1.toString()).compareTo(new BigDecimal(xValue2.toString()));
                };
        
        for (Node node : gridPane.getChildren()) {

            // from the second column to the last column. The 1st col stores K values
            for (int col = 1; col < listOfAdmixtureCharts.size(); col++) {

                // change the row index. 
                if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == rowIndexOfClickedAdmixChart) {
                    newChart = (StackedBarChart<String, Number>) node;
                    newChart.getData().get(0).getData().sorted(sortComp);
                    System.out.println(newChart.getData().get(0).getData().toString());
                }
            }
        }

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
     * This method sets the new listOfAncenstorHBox which added to the leftVBox
     * It also performs the job of swapping of series
     *
     * @param newPosition
     */
    public void moveSeries(int newPosition) {
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
     *
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
