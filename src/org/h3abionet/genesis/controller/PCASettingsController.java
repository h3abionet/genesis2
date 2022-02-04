/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.PCAGraphLayout;

import java.net.URL;
import java.util.ResourceBundle;

/**
 *
 * @author Henry
 */
public class PCASettingsController implements Initializable{
    @FXML
    private ColorPicker axisColorPicker;

    @FXML
    private ComboBox<String> axisFontCombo;

    @FXML
    private Spinner<Double> axisFontSizeSpinner;

    @FXML
    private CheckBox boldAxisCheckbox;

    @FXML
    private CheckBox boldHeadingCheckbox;

    @FXML
    private ColorPicker headingColorPicker;

    @FXML
    private ComboBox<String> headingFontCombo;

    @FXML
    private Spinner<Double> headingFontSizeSpinner;

    @FXML
    private TextField titleLabel;
    
    @FXML
    private TextField xLabel;

    @FXML
    private TextField yLabel;
    
    @FXML
    private CheckBox hideAxisMarks;

    @FXML
    private CheckBox hideAxisLabels;

    @FXML
    private CheckBox showBorder;

    @FXML
    private CheckBox hideGrid;

    @FXML
    private CheckBox underlineAxisCheckbox;

    @FXML
    private CheckBox underlineHeadingCheckbox;

    @FXML
    private CheckBox italicAxisCheckbox;

    @FXML
    private CheckBox italicHeadingCheckbox;

    @FXML
    private Button btnOK;

    @FXML
    private Button btnCancel;
    
    private ScatterChart<Number, Number> chart;

    // default axis chosen values -- changed by event handlers
    private String axesFont = "Helvetica";
    private String axesFontStyle = "NORMAL";
    private String axesFontColor = "000000";
    private String axesPosture = "REGULAR";
    private double axesFontSize = 13;

    // heading styles
    private String headingFont = "Helvetica";
    private String headingFontStyle = "NORMAL";
    private String headingFontColor = "000000";
    private String headingPosture = "REGULAR";
    private double headingFontSize = 13;

    private PCAGraphLayout pcaGraphLayout;
    private MainController mainController;

    private boolean isHeadingBold = false;
    private boolean isHeadingItalic = false;
    private boolean isHeadingUnderlined = false;

    private boolean isAxisLblBold = false;
    private boolean isAxisLblItalic = false;
    private boolean isAxisLblUnderlined = false;

    public void setAxesProperties(String axesFont, String axesFontStyle, String axesFontColor, String axesPosture, int axesFontSize) {
        this.axesFont = axesFont;
        this.axesFontStyle = axesFontStyle;
        this.axesFontColor = axesFontColor;
        this.axesPosture = axesPosture;
        this.axesFontSize = axesFontSize;
    }

    @FXML
    private void entryOkButton(ActionEvent event) {
        if(axesFontColor.equals("ffff")){
            axesFontColor = "000000";
        }

        if(headingFontColor.equals("ffff")){
            headingFontColor = "000000";
        }

        axesFont = axisFontCombo.getValue();

        if(boldAxisCheckbox.isSelected()){
            axesFontStyle = "BOLD";
        }else{
            axesFontStyle = "NORMAL";
        }

        axesFontColor = Integer.toHexString(axisColorPicker.getValue().hashCode());

        if(italicAxisCheckbox.isSelected()){
            axesPosture = "ITALIC";
        }else {
            axesPosture = "REGULAR";
        }

        axesFontSize = axisFontSizeSpinner.getValue();

        // heading styles
        headingFont = headingFontCombo.getValue();

        if(boldHeadingCheckbox.isSelected()){
            headingFontStyle = "BOLD";
        }else{
            headingFontStyle = "NORMAL";
        }

        headingFontColor = Integer.toHexString(headingColorPicker.getValue().hashCode());

        if(italicHeadingCheckbox.isSelected()){
            headingPosture = "ITALIC";
        }else {
            headingPosture = "REGULAR";
        }

        headingFontSize = headingFontSizeSpinner.getValue();
         
        // only format selected axis
          chart.getXAxis().setLabel(xLabel.getText());
          pcaGraphLayout.setxAxisLabel(xLabel.getText());

          // set x-axis label
          chart.getXAxis().lookup(".axis-label").setStyle("-fx-fill: #"+axesFontColor+";"+
                                  "-fx-font-size: "+axesFontSize+"pt;"+
                                  "-fx-font-weight: "+axesFontStyle+";"+
                                  "-fx-font-family: \"" + axesFont + "\";"+
                                  "-fx-text-fill: #"+axesFontColor+";" );

          // set y-axis label
            chart.getYAxis().setLabel(yLabel.getText());
            pcaGraphLayout.setyAxisLabel(yLabel.getText());

            chart.getYAxis().lookup(".axis-label").setStyle("-fx-fill: #"+axesFontColor+";"+
                    "-fx-font-size: "+axesFontSize+"pt;"+
                    "-fx-font-weight: "+axesFontStyle+";"+
                    "-fx-font-family: \"" + axesFont + "\";"+
                    "-fx-text-fill: #"+axesFontColor+";" );

            chart.setTitle(titleLabel.getText());
            pcaGraphLayout.setGraphTitle(titleLabel.getText());
            chart.lookup(".chart-title").setStyle("-fx-fill: #"+headingFontColor+";"+
                                  "-fx-font-size: "+headingFontSize+"pt;"+
                                  "-fx-font-weight: "+headingFontStyle+";"+
                                  "-fx-font-family: \"" + headingFont + "\";"+
                                  "-fx-text-fill: #"+headingFontColor+";" );

        if(hideAxisLabels.isSelected()){
            pcaGraphLayout.setShowAxisLabels(false);
            chart.getXAxis().lookup(".axis-label").setVisible(false);
            chart.getYAxis().lookup(".axis-label").setVisible(false);
        }else{
            chart.getXAxis().lookup(".axis-label").setVisible(true);
            chart.getYAxis().lookup(".axis-label").setVisible(true);
        }
        
         if(hideAxisMarks.isSelected()){
             pcaGraphLayout.setShowAxisMarks(false);
             chart.getXAxis().setTickLabelsVisible(false);
             chart.getYAxis().setTickLabelsVisible(false);
             chart.getXAxis().setTickMarkVisible(false);
             chart.getYAxis().setTickMarkVisible(false);
         }else{
             chart.getXAxis().setTickLabelsVisible(true);
             chart.getYAxis().setTickLabelsVisible(true);
             chart.getXAxis().setTickMarkVisible(true);
             chart.getYAxis().setTickMarkVisible(true);
         }
         
         // these are hard coded values -- should be changed
         if(showBorder.isSelected()){
             pcaGraphLayout.setShowBorders(true);
             chart.lookup(".chart-plot-background").setStyle("-fx-border-color: #918f8e;"+ 
                                                                "-fx-border-style: solid;"+ 
                                                                "-fx-border-width: 2px;"+
                                                                "-fx-border-insets: -2px;");
         }else{
             chart.lookup(".chart-plot-background").setStyle(null);
         }
         
         if(hideGrid.isSelected()){
             pcaGraphLayout.setShowGrid(false);
             chart.lookup(".chart-vertical-grid-lines").setStyle(
             "-fx-stroke: transparent;"
             );
             
             chart.lookup(".chart-horizontal-grid-lines").setStyle(
             "-fx-stroke: transparent;"
             );

         }else{
             chart.lookup(".chart-vertical-grid-lines").setStyle(null);     
             chart.lookup(".chart-horizontal-grid-lines").setStyle(null);
         
         }

         // store font properties for heading
         pcaGraphLayout.setAxesFont(axesFont);
         pcaGraphLayout.setAxesFontColor(axesFontColor);
         pcaGraphLayout.setAxesPosture(axesPosture);
         pcaGraphLayout.setAxesFontSize(axesFontSize);
         pcaGraphLayout.setAxesFontStyle(axesFontStyle);

        pcaGraphLayout.setHeadingFont(headingFont);
        pcaGraphLayout.setHeadingFontColor(headingFontColor);
        pcaGraphLayout.setHeadingPosture(headingPosture);
        pcaGraphLayout.setHeadingFontSize(headingFontSize);
        pcaGraphLayout.setHeadingFontStyle(headingFontStyle);

         mainController.disableSettingsBtn(false);
        Genesis.closeOpenStage(event);
    }
    
    @FXML
    private void entryCancelButton(ActionEvent event) {
        mainController.disableSettingsBtn(false);
        Genesis.closeOpenStage(event);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    /**
     * set all variables
     */
    public void setControls(){
        // set the heading title and axis labels
        Text chartHeading = new Text(pcaGraphLayout.getGraphTitle());
        titleLabel.setText(pcaGraphLayout.getGraphTitle());

        xLabel.setText(pcaGraphLayout.getxAxisLabel());
        yLabel.setText(pcaGraphLayout.getyAxisLabel());

        // set the heading controls
        headingFontCombo.setItems(FXCollections.observableArrayList(Font.getFamilies()));
        headingFontCombo.setValue(chartHeading.getFont().getFamily());

        // set the axis controls
        axisFontCombo.setItems(FXCollections.observableArrayList(Font.getFamilies()));
        axisFontCombo.setValue(xLabel.getFont().getFamily());

        // heading boxes
        if (isHeadingBold) {
            boldHeadingCheckbox.setSelected(true);
        }
        if (isHeadingItalic) {
            italicHeadingCheckbox.setSelected(true);
        }
        if (isHeadingUnderlined) {
            underlineHeadingCheckbox.setSelected(true);
        }

        // axis check boxes
        if (isAxisLblBold) {
            boldAxisCheckbox.setSelected(true);
        }
        if (isAxisLblItalic) {
            italicAxisCheckbox.setSelected(true);
        }
        if (isAxisLblUnderlined) {
            underlineAxisCheckbox.setSelected(true);
        }

        // set spinners for the font sizes
        setSpinner(headingFontSizeSpinner, 0, 100, chartHeading.getFont().getSize());
        setSpinner(axisFontSizeSpinner, 0, 100, yLabel.getFont().getSize());

        // default colors for the pickers
        headingColorPicker.setValue(Color.BLACK);
        axisColorPicker.setValue(Color.BLACK);

        hideAxisLabels.setSelected(!pcaGraphLayout.isShowAxisLabels());
        hideAxisMarks.setSelected(!pcaGraphLayout.isShowAxisMarks());
        hideGrid.setSelected(!pcaGraphLayout.isShowGrid());
        showBorder.setSelected(pcaGraphLayout.isShowBorders());
    }

    private void setSpinner(Spinner sp, double startValue, double endValue, double defaultValue) {
        SpinnerValueFactory<Double> spValues = new SpinnerValueFactory.DoubleSpinnerValueFactory(startValue, endValue);
        sp.setEditable(true);
        sp.setValueFactory(spValues);
        sp.getValueFactory().setValue(defaultValue);
        TextFormatter textFormatter = new TextFormatter(spValues.getConverter(),
                spValues.getValue());
        sp.getEditor().setTextFormatter(textFormatter);
        spValues.valueProperty().bindBidirectional(textFormatter.valueProperty());
    }

    /**
     * set chart
     * @param scatterChart
     */
    public void setScatterChart(ScatterChart<Number, Number> scatterChart) {
        chart = scatterChart;
    }

    public void setPCAGraphLayout(PCAGraphLayout pcaGraphLayout){
        this.pcaGraphLayout = pcaGraphLayout;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
