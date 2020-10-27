/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.AnchorPane;
import org.h3abionet.genesis.Genesis;

/**
 *
 * @author Henry
 */
public class IconOptionsController implements Initializable {

    private PCAIndividualDetailsController individualDetailsController;

    private final ObservableList<String> shapesList = FXCollections.observableArrayList("star", "arrow", "kite", "cross",
            "rectangle", "tick", "triangle", "square");

    HashMap<String, String> iconsHashmap;

    public IconOptionsController() {
        this.iconsHashmap = new HashMap<>();
        iconsHashmap = new HashMap<String, String>();
        iconsHashmap.put("star", "M 0.0 10.0 L 3.0 3.0 L 10.0 0.0 L 3.0 -3.0 L 0.0 -10.0 L -3.0 -3.0 L -10.0 0.0 L -3.0 3.0 Z");
        iconsHashmap.put("arrow", "M0 -3.5 v7 l 4 -3.5z");
        iconsHashmap.put("kite", "M5,0 L10,9 L5,18 L0,9 Z");
        iconsHashmap.put("cross", "M2,0 L5,4 L8,0 L10,0 L10,2 L6,5 L10,8 L10,10 L8,10 L5,6 L2,10 L0,10 L0,8 L4,5 L0,2 L0,0 Z");
        iconsHashmap.put("rectangle", "M 20.0 20.0  v24.0 h 10.0  v-24   Z");
        iconsHashmap.put("tick", "M0,4 L2,4 L4,8 L7,0 L9,0 L4,11 Z");
        iconsHashmap.put("triangle", "M 2 2 L 6 2 L 4 6 z");
        iconsHashmap.put("square", "M 10 10 H 90 V 90 H 10 L 10 10");
    }

    @FXML
    private Spinner iconType;

    @FXML
    private Spinner iconSize;

    @FXML
    private AnchorPane iconDisplay;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private Button btnOK;
    
    @FXML
    private Button btnCancel;
    
    private String iconTypeValue;
    private Integer iconSizeValue = 5;
    private String colorPickerValue;

    @FXML
    private void entryOKBtn(ActionEvent event) {
        individualDetailsController.setIconSize(this.iconSizeValue);
        individualDetailsController.setIconType(iconsHashmap.get(this.iconTypeValue));
        individualDetailsController.setIconColor(this.colorPickerValue);
        individualDetailsController.enableOK();
        Genesis.closeOpenStage(event);
    }

    @FXML
    private void entryCancelBtn(ActionEvent event) {
        Genesis.closeOpenStage(event);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        this.iconSizeValue = 5;
//        iconDisplay.setVisible(false);
        
        SpinnerValueFactory<Integer> iconSizes = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 30, 5);
        iconSize.setValueFactory(iconSizes);
        iconSize.setEditable(true);

        SpinnerValueFactory<String> shapeFactory = new SpinnerValueFactory.ListSpinnerValueFactory<>(shapesList);
        iconType.setValueFactory(shapeFactory);

        iconType.valueProperty().addListener((obs, oldValue, newValue) -> {
//            iconDisplay.setVisible(true);
            this.iconTypeValue = (String) newValue;
            this.colorPickerValue = Integer.toHexString(colorPicker.getValue().hashCode());
            setStyle(this.iconTypeValue, this.iconSizeValue, this.colorPickerValue);

        });

        iconSize.valueProperty().addListener((obs, oldValue, newValue) -> {
            iconDisplay.setVisible(true);
            this.iconSizeValue = (Integer) newValue;
            this.colorPickerValue = Integer.toHexString(colorPicker.getValue().hashCode());
            setStyle(this.iconTypeValue, this.iconSizeValue, this.colorPickerValue);

        }
        );

        
    }
    
    // set css style for icons
    public void setStyle(String chosenIcon, int chosenSize, String chosenColor){
        iconDisplay.setStyle("-fx-shape: \"" + iconsHashmap.get(chosenIcon) + "\";"
                    + "-fx-background-color: #" + chosenColor + ";"
                    + "-fx-background-radius: " + chosenSize + "px;"
                    + "-fx-padding: "+ chosenSize +"px;"
                    + "-fx-pref-width: "+ chosenSize +"px;"
                    + "fx-pref-height: "+ chosenSize +"px;");
    
    }

    public void setPCAController(PCAIndividualDetailsController caller) {
        individualDetailsController = caller;
    }

    // display icon
    public void setIconDisplay(String style) {
        iconDisplay.setStyle(style);
    }

}
