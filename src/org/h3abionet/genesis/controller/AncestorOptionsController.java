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
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
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
import org.h3abionet.genesis.model.AdmixtureGraphEventsHandler;
import javafx.scene.layout.GridPane;

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
    private ArrayList<StackedBarChart<String, Number>> newlistOfCharts = new ArrayList<>();
    private GridPane gridPane;
    private int swapIndex = 1;
    private int numOfSeries;

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
     * set default ancestor color when loading this interface
     *
     * @param defaulColor
     */
    public void setDefaultAncestorColor(Paint paint) {
        // display default ancestor color
        selectedColorDisplay.setFill(paint);

        // display chosen color
        colorPicker.setOnAction((ActionEvent t) -> {
            selectedColorDisplay.setFill(colorPicker.getValue());
            this.chosenColor = colorPicker.getValue();
        });
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
    private void setChosenColor(ActionEvent event) {

    }

    @FXML
    private void shiftDownHandler(ActionEvent event) {
        Collections.swap(AdmixtureGraph.admixColors, numOfSeries, numOfSeries - swapIndex); // swap colors

        for (Node node : gridPane.getChildren()) {

            for (int col = 1; col < listOfAdmixtureCharts.size(); col++) {
                if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == 0) {
                    newChart = (StackedBarChart<String, Number>) node;
                    System.out.println(newChart.getData().size());

                    List<XYChart.Series<String, Number>> newSeries = new ArrayList<>();

                    newChart.setAnimated(false);
                    newChart.getData().forEach(oldSerie -> {
                        XYChart.Series<String, Number> newSerie = new XYChart.Series<>();
                        newSerie.setName(oldSerie.getName());

                        oldSerie.getData().stream().map(d -> new XYChart.Data<String, Number>(d.getXValue(), d.getYValue()))
                                .forEach(newSerie.getData()::add);
                        newSeries.add(newSerie);
                    });

                    Collections.swap(newSeries, numOfSeries, numOfSeries - swapIndex); // swap order order of series

                    newChart.getData().clear();
                    newChart.getData().addAll(newSeries);

                    // add to charts
                    newlistOfCharts.add(newChart);

                    // set colors
                    setAncestryColors(newChart, AdmixtureGraph.admixColors);

                }
            }

        }
        swapIndex++;
    }

    @FXML
    private void shiftUpHandler(ActionEvent event) {

    }

    public String getChosenColor() {
        String selectedColor = Integer.toHexString(chosenColor.hashCode());
        return selectedColor;
    }

    public Paint getChosenPaint() {
        return chosenColor;
    }

    private void setAncestryColors(StackedBarChart<String, Number> stackedBarChart, ArrayList<String> admixColors) {
        for (int i = 0; i < stackedBarChart.getData().size(); i++) {
            int serieIndex = i;
            String serieColor = admixColors.get(i);
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
        listOfAdmixtureCharts = MainController.getListOfAdmixtureCharts();
        numOfSeries = AdmixtureDataInputController.listOfStackedBarCharts.get(0).getData().size() - 1;
        gridPane = MainController.getGridPane();
    }
}
