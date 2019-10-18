/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import org.h3abionet.genesis.Genesis;

/**
 *
 * @author Henry
 */
public class IndividualDetailsController implements Initializable {
    
    @FXML
    private Label pcaLabel;

    @FXML
    private Label phenoLabel;

    @FXML
    private RadioButton hideRadioBtn;

    @FXML
    private ToggleGroup individualGroup;

    @FXML
    private RadioButton topRadioBtn;

    @FXML
    private RadioButton clearRadioBtn;

    @FXML
    private Button iconDisplay;
    
    @FXML
    private Button chosenIconDisplay;

    @FXML
    private Button btnChangeIcon;

    @FXML
    private Button btnOK;

    @FXML
    private Button btnCancel;
    
    private ScatterChart<Number, Number> chart;
    private Open0Controller open0Controller;
    
    private static int iconSize;
    private static String iconColor;
    private static String iconType;

    private boolean hideRadioBtnClicked;
    private boolean topRadioBtnClicked;
    private boolean clearRadioBtnClicked;
    
    // set icon properties from the iconOptionsController
    public void setIconSize(int iconSize) {
        IndividualDetailsController.iconSize = iconSize;
        System.out.println("Icon_size is "+iconSize);
    }

    public void setIconColor(String iconColor) {
        IndividualDetailsController.iconColor = iconColor;
        System.out.println("Icon Color is "+iconColor);
    }

    public void setIconType(String iconType) {
        IndividualDetailsController.iconType = iconType;
        System.out.println("Icon type is"+ iconType);
    }
    
    // return requested icon properties
    public int getIconSize() {
        return iconSize;
    }

    public String getIconColor() {
        return iconColor;
    }

    public String getIconType() {
        return iconType;
    }
    
    // display pc/pheno values on labels  
    public void setPcaLabel(String coordinates) {
        pcaLabel.setText(coordinates);
    }

    public void setPhenoLabel(String phenotype) {
        phenoLabel.setText(phenotype);
    }
    
    // display icon 
    public void setIconDisplay(Node shape) {
//        Node css = (Node)shape.lookup(".chart-symbol");
//        iconDisplay.setGraphic(shape);
    }

    public void callChosenIconDisplay(String i) {
        setChosenIconDisplay(i);
    }
    
    private void setChosenIconDisplay(String h){
        chosenIconDisplay.setText(h);
    }
    
//    
//    public void removeGraphic(Node btn) {
//        System.out.println(btn);
//        System.out.println(this.chosenIconDisplay);

//        String chosenIcon, int chosenSize, String chosenColor
//        chosenIconDisplay.setStyle("-fx-shape: \"" + chosenIcon + "\";"
//                    + "-fx-background-color: #" + chosenColor + ";"
//                    + "-fx-background-radius: " + chosenSize + "px;"
//                    + "-fx-padding: "+ chosenSize +"px;"
//                    + "-fx-pref-width: "+ chosenSize +"px;"
//                    + "fx-pref-height: "+ chosenSize +"px;");
        
//    }
    
    // load iconOptionsController upon request
    @FXML
    private void changeIcon(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("view/IconOptions.fxml"));
        Stage iconStage = new Stage();
        iconStage.initOwner(iconDisplay.getScene().getWindow());
        iconStage.setScene(new Scene((Parent) fxmlLoader.load()));
        iconStage.setResizable(false);
        iconStage.showAndWait();
        
        IconOptionsController inconOptionsController = fxmlLoader.getController();
//        inconOptionsController.
    }
    
    // get radio selections (only one selection at a time)
    @FXML
    private void getClickedRadioBtn(ActionEvent event) {
        if (hideRadioBtn.isSelected()) {
            hideRadioBtnClicked = true;
//            topRadioBtnClicked = false;
//            clearRadioBtnClicked = false;
        }
        if (topRadioBtn.isSelected()) {
            topRadioBtnClicked = true;
//            hideRadioBtnClicked = false;
//            clearRadioBtnClicked = false;
        
        }
        if (clearRadioBtn.isSelected()) {
            clearRadioBtnClicked = true;
//            topRadioBtnClicked = false;
//            hideRadioBtnClicked = false;
            
        }
    }

    @FXML
    private void entryOkButton(ActionEvent event) {
        
        for (XYChart.Series<Number, Number> series : chart.getData()) {
            for (XYChart.Data<Number, Number> data : series.getData()) {
                if (hideRadioBtnClicked) {
                    data.getNode().setOnMouseClicked(e -> {
                        data.getNode().setVisible(!data.getNode().isVisible());
                    });
                }
                if (topRadioBtnClicked) {
                    data.getNode().setOnMouseClicked(e -> {
                        data.getNode().lookup(".chart-symbol").setStyle("-fx-shape: \"" + iconType + "\";"
                                + "-fx-background-color: #"+iconColor+";"
                                + "-fx-background-radius: " + iconSize + "px;"
                                + "-fx-padding: "+iconSize+"px;"
                                + "-fx-pref-width: "+ iconSize +"px;"
                                + "fx-pref-height: "+ iconSize +"px;");
                        data.getNode().toFront();
                    });
                }
                if (clearRadioBtnClicked) {
                    data.getNode().setOnMouseClicked(e -> {
                        data.getNode().setStyle(null);
                    });
                } else {
                    ; // do nothing     
                }

            }
        }
        
        closeStage(event);
    }

    @FXML
    private void entryCancelButton(ActionEvent event) {
        closeStage(event);
    }

    public void closeStage(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        open0Controller = new Open0Controller();
        chart = Open0Controller.getChart();
        hideRadioBtnClicked = false;
        topRadioBtnClicked = false;
        clearRadioBtnClicked = false;

    }

}
