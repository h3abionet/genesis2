/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.PCAGraph;
import org.h3abionet.genesis.model.Project;
import org.h3abionet.genesis.model.Subject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Henry
 */
public class PCAIndividualDetailsController{

    @FXML
    private Label pcaLabel;

    @FXML
    private ListView<String> phenoListView;
    @FXML
    private RadioButton hideRadioBtn;

    @FXML
    private ToggleGroup individualGroup;

    @FXML
    private RadioButton topRadioBtn;

    @FXML
    private RadioButton clearRadioBtn;

    @FXML
    private RadioButton seriesRadioBtn;

    @FXML
    private AnchorPane iconDisplay;

    @FXML
    private AnchorPane chosenIconDisplay;

    @FXML
    private Label phenotypeLabel;

    @FXML
    private Button btnChangeIcon;

    @FXML
    private Button btnOK;

    @FXML
    private Button btnCancel;

    private ScatterChart<Number, Number> chart;

    private String iconSVGShape;
    private int iconSize;
    private String iconColor;
    private String iconType;
    private String clickedIconStyle;
    private String xValueOfClickedPoint;
    private String yValueOfClickedPoint;
    private String phenotypeGroup;
    private String[] idsOfClickedPoint;

    private boolean hideRadioBtnClicked = false;
    private boolean topRadioBtnClicked = false;
    private boolean clearRadioBtnClicked = false;
    private boolean seriesRadioBtnClicked = false;

    private PCAGraph pcaGraph;
    private Project project;
    private MainController mainController;
    private IconOptionsController iconCtrlr;

    public void setIdsOfClickedPoint(String idsOfClickedPoint[]) {
        this.idsOfClickedPoint = idsOfClickedPoint;
    }

    public void setChart(ScatterChart<Number, Number> chart) { this.chart = chart; }

    // set icon properties from the iconOptionsController
    public void setIconSize(int iconSize) {
        this.iconSize = iconSize;
    }

    public void setIconColor(String iconColor) {
        this.iconColor = iconColor;
    }

    public void setIconType(String iconType) {
        this.iconType = iconType;
        // set the svg shape
        getSVGShape(iconType);
    }

    // display pc/pheno values on labels
    public void setPcaLabel(String coordinates) {
        pcaLabel.setText(coordinates);
    }

    public void setPhenoListView(ObservableList<String> phenoDetails) {
        phenoListView.getItems().addAll(phenoDetails);
    }

    /**
     * display icon of the clicked point
     * @param style
     */
    public void setIconDisplay(String style) {
        setClickedIconStyle(style);
        iconDisplay.setStyle(clickedIconStyle);
    }

    /**
     * set the data node style
     * @param clickedIconStyle
     */
    public void setClickedIconStyle(String clickedIconStyle) {
        this.clickedIconStyle = clickedIconStyle;
    }

    // load iconOptionsController upon request
    @FXML
    private void changeIcon(ActionEvent event) throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("view/IconOptions.fxml"));
            Stage iconStage = new Stage();
            iconStage.initOwner(iconDisplay.getScene().getWindow());
            Scene icon_root = new Scene((Parent) fxmlLoader.load());
            iconCtrlr = fxmlLoader.getController();
            iconCtrlr.setPCAController(this);
            iconCtrlr.setPcaGraph(pcaGraph);

            // set default variables
            iconCtrlr.setIconDisplay(clickedIconStyle); // show clicked icon
            iconCtrlr.setIconColorValue(iconColor);
            iconCtrlr.setIconTypeValue(iconType);
            iconCtrlr.setIconSizeValue(iconSize);

            iconCtrlr.setIconTypeComboValues(FXCollections.observableArrayList(new ArrayList<>(project.getIconTypes().values()))); // star, kite, ...

            iconStage.setScene(icon_root);
            iconStage.setResizable(false);
            iconStage.showAndWait();
    }

    // get radio selections (only one selection at a time)
    @FXML
    private void getClickedRadioBtn(ActionEvent event) {
        if (hideRadioBtn.isSelected()) {
            hideRadioBtnClicked = true;
            topRadioBtnClicked = false;
            seriesRadioBtnClicked = false;
            clearRadioBtnClicked = false;
            enableOK();
        }else if (topRadioBtn.isSelected()) {
            topRadioBtnClicked = true;
            hideRadioBtnClicked = false;
            seriesRadioBtnClicked = false;
            clearRadioBtnClicked = false;
            enableOK();
        }else if (clearRadioBtn.isSelected()) {
            clearRadioBtnClicked = true;
            hideRadioBtnClicked = false;
            topRadioBtnClicked = false;
            seriesRadioBtnClicked = false;
            enableOK();
        }else if (seriesRadioBtn.isSelected()) {
            seriesRadioBtnClicked = true;
            hideRadioBtnClicked = false;
            topRadioBtnClicked = false;
            clearRadioBtnClicked = false;
            enableOK();

        }else{
            ;
        }
    }

    @FXML
    private void entryOkButton(ActionEvent event) {
        // hide or place the point on top
        if(seriesRadioBtnClicked){
                for (XYChart.Series<Number, Number> series : chart.getData()) {
                    // change the properties of the selected group
                    if(seriesRadioBtnClicked){
                        if (series.getName().equals(phenotypeGroup)) {
                            pcaGraph.changeSeriesProperties(phenotypeGroup, iconColor, iconSVGShape, iconSize);
                        }
                        Genesis.closeOpenStage(event);
                    }
                }
        }

        for (XYChart.Series<Number, Number> series : chart.getData()) {

            for (XYChart.Data<Number, Number> data : series.getData()) {
                String xValue = String.valueOf(data.getXValue());
                String yValue = String.valueOf(data.getYValue());

                if((xValue.equals(xValueOfClickedPoint) & yValue.equals(yValueOfClickedPoint))){
                    if(hideRadioBtnClicked){
                        // pcaGraph.hideIndividual(series, idsOfClickedPoint);
                        break;
                    }else if(topRadioBtnClicked){
                        data.getNode().toFront();
                        break;
                    }else if (clearRadioBtnClicked) {
                        pcaGraph.resetSubjectProperties(data, xValue, yValue);
                        break;
                    }
                    else {
                        // set icon and color for all a particular data point
                        pcaGraph.changeSubjectProperties(data, xValue, yValue, iconColor, iconSVGShape, iconSize);
                        Genesis.closeOpenStage(event);
                    }
                }
            }
    }

        // set it back to false
        hideRadioBtnClicked = false;
        topRadioBtnClicked = false;
        seriesRadioBtnClicked = false;
        clearRadioBtnClicked = false;
        Genesis.closeOpenStage(event);
    }

    @FXML
    private void entryCancelButton(ActionEvent event) {
        Genesis.closeOpenStage(event);
    }

    public void disableOK(){btnOK.setDisable(true);}

    public void enableOK() {
        btnOK.setDisable(false);
    }

    public void setPCAGraph(PCAGraph pcaGraph) {
        this.pcaGraph = pcaGraph;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setClickedPoint(String xValue, String yValue) {
        this.xValueOfClickedPoint = xValue;
        this.yValueOfClickedPoint = yValue;
    }

    public void setPhenotypeGroup(String phenotypeGroup) {
        this.phenotypeGroup = phenotypeGroup;
        phenotypeLabel.setText(phenotypeGroup);
    }

    /**
     * return icon svg shape for every selected icon type
     * @param iconTypeValue
     * @return
     */
    public String getSVGShape(String iconTypeValue) {
        project.getIconTypes().forEach((key, value) -> {
            if (value.equals(iconTypeValue)) {
                iconSVGShape = (String) key;
            }
        });
        return iconSVGShape;
    }

    public void setChosenIconDisplay(String style) { chosenIconDisplay.setStyle(style); }

    /**
     * default icon shape
     * @param icon
     */
    public void setIconSVGShape(String icon) {
        // set svg shape of clicked subject
        this.iconSVGShape = icon;
        // then set the type of its svg shape
        this.iconType = (String) project.getIconTypes().get(icon);
    }
}
