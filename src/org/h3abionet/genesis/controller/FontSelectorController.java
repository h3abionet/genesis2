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
/**
 *
 * @author Henry
 */
public class FontSelectorController implements Initializable{
    @FXML
    private TextField titleLabel;

    @FXML
    private CheckBox titleFormat;
    
    @FXML
    private TextField xLabel;

    @FXML
    private CheckBox xFormat;

    @FXML
    private TextField yLabel;

    @FXML
    private CheckBox yFormat;
    
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
    private String chosenFontColor = "ffffff";
    private String chosenPosture = "REGULAR";
    private int chosenFontSize = 12;
    private String chosenAxis;

    // font weight names 
    String weights[] = {"BOLD", "NORMAL"};
    
    // font posture
    String posture[] = {"REGULAR", "ITALIC"};
    
    // list of font sizes from 8 to 72
    List<Integer> list = IntStream.range(8, 73).boxed().collect(Collectors.toList());
    
    @FXML
    private void entryOkButton(ActionEvent event) {
         
        // only format selected axis
        if(xFormat.isSelected()){
          chart.getXAxis().setLabel(xLabel.getText());
          chart.getXAxis().lookup(".axis-label").setStyle("-fx-fill: #"+chosenFontColor+";"+
                                  "-fx-font-size: "+chosenFontSize+"pt;"+
                                  "-fx-font-weight: "+chosenFontStyle+";"+
                                  "-fx-font-family: \"" + chosenFont + "\";"+
                                  "-fx-text-fill: #"+chosenFontColor+";" );
          
        }
        
        if(yFormat.isSelected()){
          chart.getYAxis().setLabel(yLabel.getText());
          chart.getYAxis().lookup(".axis-label").setStyle("-fx-fill: #"+chosenFontColor+";"+
                                  "-fx-font-size: "+chosenFontSize+"pt;"+
                                  "-fx-font-weight: "+chosenFontStyle+";"+
                                  "-fx-font-family: \"" + chosenFont + "\";"+
                                  "-fx-text-fill: #"+chosenFontColor+";" );
          
        }
        
        if(titleFormat.isSelected()){
            chart.setTitle(titleLabel.getText());
            chart.lookup(".chart-title").setStyle("-fx-fill: #"+chosenFontColor+";"+
                                  "-fx-font-size: "+chosenFontSize+"pt;"+
                                  "-fx-font-weight: "+chosenFontStyle+";"+
                                  "-fx-font-family: \"" + chosenFont + "\";"+
                                  "-fx-text-fill: #"+chosenFontColor+";" );
            
            
        }
        if(hideAxes.isSelected()){
            chart.getXAxis().lookup(".axis-label").setVisible(false);
            chart.getYAxis().lookup(".axis-label").setVisible(false);
        
        }else{
            chart.getXAxis().lookup(".axis-label").setVisible(true);
            chart.getYAxis().lookup(".axis-label").setVisible(true);
        
        }
        
         if(hideAxisLabels.isSelected()){
             chart.getXAxis().setTickLabelsVisible(false);
             chart.getYAxis().setTickLabelsVisible(false);
         }else{
             chart.getXAxis().setTickLabelsVisible(true);
             chart.getYAxis().setTickLabelsVisible(true);
         }
         
         // these are hard coded values -- should be changed
         if(showBorder.isSelected()){
             chart.lookup(".chart-plot-background").setStyle("-fx-border-color: #918f8e;"+ 
                                                                "-fx-border-style: solid;"+ 
                                                                "-fx-border-width: 2px;"+
                                                                "-fx-border-insets: -2px;");
         }else{
             chart.lookup(".chart-plot-background").setStyle(null);
         }
         
         if(hideGrid.isSelected()){
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
        
        Genesis.closeOpenStage(event);
    }
    
    @FXML
    private void entryCancelButton(ActionEvent event) {
        Genesis.closeOpenStage(event);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    /**
     * set all variables
     */
    public void setControls(){
        // set default values
        titleLabel.setText(chart.getTitle());
        xLabel.setText(chart.getXAxis().getLabel());
        yLabel.setText(chart.getYAxis().getLabel());
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
    }

    /**
     * set chart
     * @param scatterChart
     */
    public void setScatterChart(ScatterChart<Number, Number> scatterChart) {
        chart = scatterChart;
    }

    // get chosen properties and update the label
    private void updateSampleLabel(){
        chosenFontColor = Integer.toHexString(colorPicker.getValue().hashCode());
        sample.setFont(Font.font(chosenFont, FontWeight.valueOf(chosenFontStyle), FontPosture.valueOf(chosenPosture), chosenFontSize));
        sample.setTextFill(colorPicker.getValue());
    }

}
