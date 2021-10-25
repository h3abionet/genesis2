/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.FontPosture;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.PCAGraphLayout;

/**
 *
 * @author Henry
 */
public class PCASettingsController implements Initializable{
    @FXML
    private TextField titleLabel;

    @FXML
    private CheckBox titleCheckbox;
    
    @FXML
    private TextField xLabel;

    @FXML
    private CheckBox axisLabelCheckbox;

    @FXML
    private TextField yLabel;
    
    @FXML
    private CheckBox hideAxisMarks;

    @FXML
    private CheckBox hideAxes;

    @FXML
    private CheckBox hideAxisLabels;

    @FXML
    private CheckBox showBorder;

    @FXML
    private CheckBox hideGrid;
    
    @FXML
    private ListView<String> fontList;

    @FXML
    private ListView<String> styleList;

    @FXML
    private ListView<Integer> sizeList;
    
    @FXML
    private ListView<String> postureList;

    @FXML
    private Label sample;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private Button btnOK;

    @FXML
    private Button btnCancel;
    
    private ScatterChart<Number, Number> chart;

    // default chosen values -- changed by event handlers 
    private String chosenFont = "System";
    private String chosenFontStyle = "NORMAL";
    private String chosenFontColor = "000000";
    private String chosenPosture = "REGULAR";
    private int chosenFontSize = 12;

    // default chosen values -- changed by event handlers
    private String axesFont = "System";
    private String axesFontStyle = "NORMAL";
    private String axesFontColor = "000000";
    private String axesPosture = "REGULAR";
    private int axesFontSize = 12;

    // font weight names 
    String weights[] = {"BOLD", "NORMAL"};
    
    // font posture
    String posture[] = {"REGULAR", "ITALIC"};
    
    // list of font sizes from 8 to 72
    List<Integer> list = IntStream.range(8, 36).boxed().collect(Collectors.toList());
    private PCAGraphLayout pcaGraphLayout;
    private MainController mainController;

    public void setAxesProperties(String axesFont, String axesFontStyle, String axesFontColor, String axesPosture, int axesFontSize) {
        this.axesFont = axesFont;
        this.axesFontStyle = axesFontStyle;
        this.axesFontColor = axesFontColor;
        this.axesPosture = axesPosture;
        this.axesFontSize = axesFontSize;
    }

    @FXML
    private void entryOkButton(ActionEvent event) {
        System.out.println(chosenFontColor);
        if(chosenFontColor.equals("ffff")){
            chosenFontColor = "000000";
        }
         
        // only format selected axis
        if(axisLabelCheckbox.isSelected()){
          chart.getXAxis().setLabel(xLabel.getText());
          pcaGraphLayout.setxAxisLabel(xLabel.getText());

          // set x-axis label
          chart.getXAxis().lookup(".axis-label").setStyle("-fx-fill: #"+chosenFontColor+";"+
                                  "-fx-font-size: "+chosenFontSize+"pt;"+
                                  "-fx-font-weight: "+chosenFontStyle+";"+
                                  "-fx-font-family: \"" + chosenFont + "\";"+
                                  "-fx-text-fill: #"+chosenFontColor+";" );

          // set y-axis label
            chart.getYAxis().setLabel(yLabel.getText());
            pcaGraphLayout.setyAxisLabel(yLabel.getText());
            chart.getYAxis().lookup(".axis-label").setStyle("-fx-fill: #"+chosenFontColor+";"+
                    "-fx-font-size: "+chosenFontSize+"pt;"+
                    "-fx-font-weight: "+chosenFontStyle+";"+
                    "-fx-font-family: \"" + chosenFont + "\";"+
                    "-fx-text-fill: #"+chosenFontColor+";" );

          // set axes values
          setAxesProperties(chosenFont, chosenFontStyle, chosenFontColor, chosenPosture, chosenFontSize);
        }
        
        if(titleCheckbox.isSelected()){
            chart.setTitle(titleLabel.getText());
            pcaGraphLayout.setGraphTitle(titleLabel.getText());
            chart.lookup(".chart-title").setStyle("-fx-fill: #"+chosenFontColor+";"+
                                  "-fx-font-size: "+chosenFontSize+"pt;"+
                                  "-fx-font-weight: "+chosenFontStyle+";"+
                                  "-fx-font-family: \"" + chosenFont + "\";"+
                                  "-fx-text-fill: #"+chosenFontColor+";" );
        }

        if(hideAxes.isSelected()){
            pcaGraphLayout.setShowAxes(false);
            chart.lookup(".chart-vertical-zero-line").setStyle("-fx-stroke: transparent;");
            chart.lookup(".chart-horizontal-zero-line").setStyle("-fx-stroke: transparent;");
        }else {
            chart.lookup(".chart-vertical-zero-line").setStyle(null);
            chart.lookup(".chart-horizontal-zero-line").setStyle(null);
        }

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

         // store font properties
         pcaGraphLayout.setFont(chosenFont);
         pcaGraphLayout.setFontColor(chosenFontColor);
         pcaGraphLayout.setFontPosture(chosenPosture);
         pcaGraphLayout.setFontSize(chosenFontSize);
         pcaGraphLayout.setFontStyle(chosenFontStyle);
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
        titleLabel.setText(pcaGraphLayout.getGraphTitle());
        xLabel.setText(pcaGraphLayout.getxAxisLabel());
        yLabel.setText(pcaGraphLayout.getyAxisLabel());
        colorPicker.setValue(Color.BLACK);

        styleList.setItems(FXCollections.observableArrayList(weights));
        fontList.setItems(FXCollections.observableArrayList(Font.getFamilies()));
        postureList.setItems(FXCollections.observableArrayList(posture));
        sizeList.setItems(FXCollections.observableList(list));

        styleList.setOnMouseClicked((MouseEvent event) -> {
            chosenFontStyle = styleList.getSelectionModel().getSelectedItem();
            updateSampleLabel();
        });

        postureList.setOnMouseClicked((MouseEvent event) -> {
            chosenPosture = postureList.getSelectionModel().getSelectedItem();
            updateSampleLabel();
        });

        fontList.setOnMouseClicked((MouseEvent event) -> {
            chosenFont = fontList.getSelectionModel().getSelectedItem();
            updateSampleLabel();

        });

        sizeList.setOnMouseClicked((MouseEvent event) -> {
            chosenFontSize = sizeList.getSelectionModel().getSelectedItem();
            updateSampleLabel();

        });

        colorPicker.setOnAction((ActionEvent event) -> {
            updateSampleLabel();

        });

        // set checkboxes
        hideAxes.setSelected(!pcaGraphLayout.isShowAxes());
        hideAxisLabels.setSelected(!pcaGraphLayout.isShowAxisLabels());
        hideAxisMarks.setSelected(!pcaGraphLayout.isShowAxisMarks());
        hideGrid.setSelected(!pcaGraphLayout.isShowGrid());
        showBorder.setSelected(pcaGraphLayout.isShowBorders());
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

    // get chosen properties and update the label
    private void updateSampleLabel(){
        chosenFontColor = Integer.toHexString(colorPicker.getValue().hashCode());
        sample.setFont(Font.font(chosenFont, FontWeight.valueOf(chosenFontStyle), FontPosture.valueOf(chosenPosture), chosenFontSize));
        sample.setTextFill(colorPicker.getValue());
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
