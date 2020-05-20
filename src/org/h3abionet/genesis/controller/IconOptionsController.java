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
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;
import org.h3abionet.genesis.Genesis;

/**
 *
 * @author Henry
 */
public class IconOptionsController implements Initializable {

    private PCAIndividualDetailsController individualDetailsController;

    private final ObservableList<String> shapesList = FXCollections.observableArrayList("kite", "cross",
            "triangle", "tick", "rectangle");

    HashMap<String, String> iconsHashmap;

    public IconOptionsController() {
        this.iconsHashmap = new HashMap<>();
        iconsHashmap.put("kite", "M5,0 L10,9 L5,18 L0,9 Z");
        iconsHashmap.put("cross", "M2,0 L5,4 L8,0 L10,0 L10,2 L6,5 L10,8 L10,10 L8,10 L5,6 L2,10 L0,10 L0,8 L4,5 L0,2 L0,0 Z");
        iconsHashmap.put("rectangle", "M5,0 L10,8 L0,8 Z");
        iconsHashmap.put("tick", "M0,4 L2,4 L4,8 L7,0 L9,0 L4,11 Z");
        iconsHashmap.put("triangle", "M 2 2 L 6 2 L 4 6 z");

    }

    @FXML
    private Spinner iconType;

    @FXML
    private Spinner iconSize;

    @FXML
    private Button iconDisplay;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private Button btnOK;
    
    @FXML
    private Button btnCancel;
    
    private String iconTypeValue;
    private Integer iconSizeValue;
    private String colorPickerValue;

    @FXML
    private void entryOKBtn(ActionEvent event) {
        individualDetailsController.setIconSize(this.iconSizeValue);
        individualDetailsController.setIconType(iconsHashmap.get(this.iconTypeValue));
        individualDetailsController.setIconColor(this.colorPickerValue);
    
        Genesis.closeOpenStage(event);
    }

    @FXML
    private void entryCancelBtn(ActionEvent event) {
        Genesis.closeOpenStage(event);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.iconSizeValue = 5;
        iconDisplay.setVisible(false);
        
        SpinnerValueFactory<Integer> iconSizes = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 5);
        iconSize.setValueFactory(iconSizes);
        iconSize.setEditable(true);

        SpinnerValueFactory<String> shapeFactory = new SpinnerValueFactory.ListSpinnerValueFactory<>(shapesList);
        iconType.setValueFactory(shapeFactory);
        

        iconType.valueProperty().addListener((obs, oldValue, newValue) -> {
            iconDisplay.setVisible(true);
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

        individualDetailsController = new PCAIndividualDetailsController();
        
        
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

}
