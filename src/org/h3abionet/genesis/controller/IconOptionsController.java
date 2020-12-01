/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.PCAGraph;

/**
 *
 * @author Henry
 */
public class IconOptionsController implements Initializable {

    private PCAIndividualDetailsController individualDetailsController;

    private PCAGraph pcaGraph;
    private ObservableList<String> shapesList;

    @FXML
    private ComboBox<String> iconTypeCombo;

    @FXML
    private ComboBox<Integer> iconSizeCombo;

    @FXML
    private AnchorPane iconDisplay;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private Button btnOK;
    
    @FXML
    private Button btnCancel;
    
    private String iconTypeValue;
    private Integer iconSizeValue = 5; // default
    private String iconColorValue;

    @FXML
    private void entryOKBtn(ActionEvent event) {
        individualDetailsController.setIconSize(this.iconSizeValue);
        individualDetailsController.setIconType(this.iconTypeValue);
        individualDetailsController.setIconColor(this.iconColorValue);
        individualDetailsController.enableOK();
        Genesis.closeOpenStage(event);
    }

    @FXML
    private void entryCancelBtn(ActionEvent event) {
        Genesis.closeOpenStage(event);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // set a list of icon sizes
//        SpinnerValueFactory<Integer> iconSizesFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 30);
//        iconSizeSpinner.setValueFactory(iconSizesFactory);
//        iconSizesFactory.setValue(iconSizeValue);
//        iconSizeSpinner.setEditable(true);

//        iconTypeSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
//            iconTypeValue = (String) newValue;
//            iconColorValue = Integer.toHexString(colorPicker.getValue().hashCode());
//            setStyle(iconTypeValue, iconSizeValue, iconColorValue);
//
//        });
////
//        iconSizeSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
//            iconDisplay.setVisible(true);
//            this.iconSizeValue = (Integer) newValue;
//            this.iconColorValue = Integer.toHexString(colorPicker.getValue().hashCode());
//            setStyle(this.iconTypeValue, this.iconSizeValue, this.iconColorValue);
//        });
    }

    @FXML
    private void setIconSizeComboPressed(ActionEvent event){
        setStyle(iconTypeValue, iconSizeValue, iconColorValue);
    }

    @FXML
    private void iconTypeComboPressed(ActionEvent event){
        iconTypeValue = (String) iconTypeCombo.getValue();
        setStyle(iconTypeValue, iconSizeValue, iconColorValue);
    }

    @FXML
    private void colorPickerPressed(ActionEvent event){
        iconColorValue = Integer.toHexString(colorPicker.getValue().hashCode());
        System.out.println(iconColorValue);
        String icon = individualDetailsController.getShape(iconTypeValue);
        setStyle(icon, iconSizeValue, iconColorValue);
    }
    
    // set css style for icons
    public void setStyle(String chosenIcon, int chosenSize, String chosenColor){
        iconDisplay.setStyle("-fx-shape: \""+ chosenIcon+ "\";"
                    + "-fx-background-color: #" + chosenColor + ", white;"
                    + "-fx-background-radius: " + chosenSize + "px;"
                    + "-fx-padding: "+ chosenSize +"px;"
                    + "-fx-pref-width: "+ chosenSize +"px;"
                    + "fx-pref-height: "+ chosenSize +"px;");
    }

    public void setShapesList(ObservableList<String> shapesList) {
        this.shapesList = shapesList;
        // set a list of icon names
        iconTypeCombo.getItems().addAll(shapesList);
        iconTypeCombo.setValue(iconTypeValue);
    }

    public void setPCAController(PCAIndividualDetailsController caller) {
        individualDetailsController = caller;
    }

    // set default icon on the display
    public void setIconDisplay(String style) {
        iconDisplay.setStyle(style);
    }
    // set default color
    public void setIconColorValue(String color) {
        this.iconColorValue = color;
        colorPicker.setValue(Color.web(color));
    }

    // set default icon type
    public void setIconTypeValue(String iconTypeValue) {
        this.iconTypeValue = iconTypeValue;
    }
    // set default icon size
    public void setIconSizeValue(Integer iconSizeValue) {
        this.iconSizeValue = iconSizeValue;
        iconSizeCombo.getItems().addAll(FXCollections.observableArrayList(3,4,5,6,7,8,9,10,11,12,13,14,15));
        iconSizeCombo.setValue(iconSizeValue);
    }
}
