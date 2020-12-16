/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import org.h3abionet.genesis.Genesis;

/**
 *
 * @author Henry
 */
public class IconOptionsController implements Initializable {

    private PCAIndividualDetailsController individualDetailsController;

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
    private int iconSizeValue = 5; // default
    private String iconColorValue;
    private String iconSVG;

    @FXML
    private void entryOKBtn(ActionEvent event) {
        individualDetailsController.setIconSize(this.iconSizeValue);
        individualDetailsController.setIconType(this.iconTypeValue);
        individualDetailsController.setIconColor(this.iconColorValue);
        individualDetailsController.setChosenIconDisplay(getStyle(iconSVG, iconSizeValue, iconColorValue));
        individualDetailsController.enableOK();
        Genesis.closeOpenStage(event);
    }

    @FXML
    private void entryCancelBtn(ActionEvent event) {
        Genesis.closeOpenStage(event);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @FXML
    private void setIconSizeComboPressed(ActionEvent event){
        iconSizeValue = iconSizeCombo.getValue();
        iconDisplay.setStyle(getStyle(iconSVG, iconSizeValue, iconColorValue));
    }

    @FXML
    private void iconTypeComboPressed(ActionEvent event){
        // get new selected icon type
        iconTypeValue = iconTypeCombo.getValue();
        // get svg of selected icon
        iconSVG = individualDetailsController.getShape(iconTypeValue);
        // show new icon shape
        iconDisplay.setStyle(getStyle(iconSVG, iconSizeValue, iconColorValue));
    }

    @FXML
    private void colorPickerPressed(ActionEvent event){
        // set icon new selected icon color
        iconColorValue = Integer.toHexString(colorPicker.getValue().hashCode());
        // display new color
        iconDisplay.setStyle(getStyle(iconSVG, iconSizeValue, iconColorValue));
    }

    private String getStyle(String icon, int size, String color){
        String s = "-fx-background-color: "+color+", white;"
                + "-fx-shape: \""+icon+"\";"
                + "-fx-background-insets: 0, 2;"
                + "-fx-background-radius: 5px;"
                + "-fx-padding: "+size+"px;"
                + "-fx-pref-width: "+size+"px;"
                + "fx-pref-height: "+size+"px;";
        return s;
    }

    public void setShapesList(ObservableList<String> shapesList) {
        this.shapesList = shapesList;
        // set a list of icon names
        iconTypeCombo.getItems().addAll(shapesList);
        // set default value
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
        // set default value of color picker
        colorPicker.setValue(Color.web(color));
    }

    // set default icon type
    public void setIconTypeValue(String iconTypeValue) {
        this.iconTypeValue = iconTypeValue;
        // set svg of icon
        iconSVG = individualDetailsController.getShape(iconTypeValue);
    }

    // set default icon size
    public void setIconSizeValue(Integer iconSizeValue) {
        // get value form pca individual ctrl
        this.iconSizeValue = iconSizeValue;
        // set values of combo box
        iconSizeCombo.getItems().setAll(
                IntStream.rangeClosed(1,20).boxed().collect(Collectors.toList())
        );
        // set default value
        iconSizeCombo.setValue(iconSizeValue);
    }
}
