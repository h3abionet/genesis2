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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private Button btnChangeIcon;

    @FXML
    private Button btnOK;

    @FXML
    private Button btnCancel;
    
    private ScatterChart<Number, Number> chart;
    private Open0Controller open0Controller;
    
    private static String iconSize;
    private static String iconColor;
    private static String iconType;

    private boolean hideRadioBtnClicked = false;
    private boolean topRadioBtnClicked = false;
    private boolean clearRadioBtnClicked = false;
    
    public void setIconSize(String iconSize) {
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

    public String getIconSize() {
        return iconSize;
    }

    public String getIconColor() {
        return iconColor;
    }

    public String getIconType() {
        return iconType;
    }

    public void setPcaLabel(String coordinates) {
        pcaLabel.setText(coordinates);
    }

    public void setPhenoLabel(String phenotype) {
        phenoLabel.setText(phenotype);
    }

    public void setIconDisplay(Node shape) {
        Node css = shape.lookup(".chart-symbol");
        iconDisplay.setGraphic(shape);
    }

    @FXML
    private void changeIcon(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("view/IconOptions.fxml"));
        Parent parent = (Parent) fxmlLoader.load();
        Stage iconStage = new Stage();
        iconStage.setScene(new Scene(parent));
        iconStage.setResizable(false);
        iconStage.showAndWait();
    }

    @FXML
    private void getClickedRadioBtn(ActionEvent event) {
        if (hideRadioBtn.isSelected()) {
            hideRadioBtnClicked = true;
            
        }
        if (topRadioBtn.isSelected()) {
            topRadioBtnClicked = true;
        
        }
        if (clearRadioBtn.isSelected()) {
            clearRadioBtnClicked = true;
            
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
                        data.getNode().lookup(".chart-symbol").setStyle("-fx-shape: \"M5,0 L10,9 L5,18 L0,9 Z\";"
                                + "-fx-background-color: #"+iconColor+";"
                                + "-fx-padding: "+iconSize+"px;");
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
