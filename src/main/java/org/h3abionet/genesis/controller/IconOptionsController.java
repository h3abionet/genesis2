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
import org.h3abionet.genesis.model.PCAGraph;

/**
 *
 * @author Henry
 */
public class IconOptionsController implements Initializable {

    private PCAIndividualDetailsController individualDetailsController;

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
    private int iconSizeValue;
    private String iconColorValue;
    private String iconSVG;
    private PCAGraph pcaGraph;

    @FXML
    private void entryOKBtn(ActionEvent event) {
        individualDetailsController.setIconSize(this.iconSizeValue);
        individualDetailsController.setIconType(this.iconTypeValue);
        individualDetailsController.setIconColor(this.iconColorValue);
        individualDetailsController.setClickedIconStyle(iconDisplay.getStyle());
        individualDetailsController.setChosenIconDisplay(pcaGraph.getStyle(iconColorValue, iconSVG, iconSizeValue));
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
        iconDisplay.setStyle(pcaGraph.getStyle(iconColorValue, iconSVG, iconSizeValue));
    }

    @FXML
    private void iconTypeComboPressed(ActionEvent event){
        // get new selected icon type
        iconTypeValue = iconTypeCombo.getValue();
        // get svg of selected icon
        iconSVG = individualDetailsController.getSVGShape(iconTypeValue);
        // show new icon shape
        iconDisplay.setStyle(pcaGraph.getStyle(iconColorValue, iconSVG, iconSizeValue));
    }

    @FXML
    private void colorPickerPressed(ActionEvent event){
        // set icon new selected icon color
        iconColorValue = toHexString(colorPicker.getValue());
        if("ff".equals(iconColorValue))
            iconColorValue = "000000";

        // display new color
        iconDisplay.setStyle(pcaGraph.getStyle(iconColorValue, iconSVG, iconSizeValue));
    }

    public void setIconTypeComboValues(ObservableList<String> iconTypes) {
        // set a list of icon names
        iconTypeCombo.getItems().addAll(iconTypes);
        // set default value
        iconTypeCombo.setValue(iconTypeValue);
    }

    public void setPcaGraph(PCAGraph pcaGraph) {
        this.pcaGraph = pcaGraph;
    }

    public void setPCAController(PCAIndividualDetailsController caller) {
        individualDetailsController = caller;
    }

    /**
     * set default icon on the display
     * @param style
     */
    public void setIconDisplay(String style) {
        iconDisplay.setStyle(style);
    }

    /**
     *  set default color
     * @param color
     */
    public void setIconColorValue(String color) {
        this.iconColorValue = color;
        // set default value of color picker
        colorPicker.setValue(Color.web(color));
    }

    /**
     * set default icon type
     * @param iconTypeValue
     */
    public void setIconTypeValue(String iconTypeValue) {
        this.iconTypeValue = iconTypeValue;
        // set svg of icon
        iconSVG = individualDetailsController.getSVGShape(iconTypeValue);
    }

    /**
     * set default icon size
     * @param iconSizeValue
     */
    public void setIconSizeValue(int iconSizeValue) {
        // get value form pca individual ctrl
        this.iconSizeValue = iconSizeValue;
        // set values of combo box
        iconSizeCombo.getItems().setAll(
                IntStream.rangeClosed(5,30).boxed().collect(Collectors.toList())
        );
        // set default value
        iconSizeCombo.setValue(iconSizeValue);
    }

    /**
     * Converting Color to Hex String
     * @param color
     * @return
     */
    private String toHexString(Color color) {
        int r = ((int) Math.round(color.getRed()     * 255)) << 24;
        int g = ((int) Math.round(color.getGreen()   * 255)) << 16;
        int b = ((int) Math.round(color.getBlue()    * 255)) << 8;
        int a = ((int) Math.round(color.getOpacity() * 255));
        return String.format("#%08X", (r + g + b + a));
    }

}
