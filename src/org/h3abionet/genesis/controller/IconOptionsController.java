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
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/**
 *
 * @author Henry
 */
public class IconOptionsController implements Initializable {

    private IndividualDetailsController individualDetailsController;

    private final ObservableList<String> shapes = FXCollections.observableArrayList("kite", "cross",
            "triangle", "tick");

    HashMap<String, String> icons;

    public IconOptionsController() {
        this.icons = new HashMap<>();
        icons.put("kite", "M5,0 L10,9 L5,18 L0,9 Z");
        icons.put("cross", "M2,0 L5,4 L8,0 L10,0 L10,2 L6,5 L10,8 L10,10 L8,10 L5,6 L2,10 L0,10 L0,8 L4,5 L0,2 L0,0 Z");
        icons.put("triange", "M5,0 L10,8 L0,8 Z");
        icons.put("tick", "M0,4 L2,4 L4,8 L7,0 L9,0 L4,11 Z");

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
    private void entryOKBtn(ActionEvent event) {
        individualDetailsController.setIconSize(iconSize.getValue().toString());
        individualDetailsController.setIconType(icons.get(iconType.getValue().toString()));
        individualDetailsController.setIconColor(Integer.toHexString(colorPicker.getValue().hashCode()));

        closeStage(event);
    }

    @FXML
    private Button btnCancel;

    @FXML
    private void entryCancelBtn(ActionEvent event) {
        closeStage(event);

    }

    public void closeStage(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SpinnerValueFactory<Integer> iconSizes = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 30, 5);
        iconSize.setValueFactory(iconSizes);
        iconSize.setEditable(true);

        SpinnerValueFactory<String> shapeFactory = new SpinnerValueFactory.ListSpinnerValueFactory<>(shapes);
        iconType.setValueFactory(shapeFactory);
        
        Region iconRegion = new Region();

        iconType.valueProperty().addListener((obs, oldValue, newValue) -> {
         
            iconDisplay.setText((String) newValue);
            
        });

        iconSize.valueProperty().addListener((obs, oldValue, newValue)
                -> {
         
            
            iconDisplay.setText((String) newValue);
            

        }
        );

        individualDetailsController = new IndividualDetailsController();
        
//        iconRegion.getStyleClass().add("-fx-shape: " + icons.get(iconType.getValue().toString()) + ";"
//                    + "-fx-background-color: #" + Integer.toHexString(colorPicker.getValue().hashCode()) + ";"
//                    + "-fx-padding: " + iconSize.getValue().toString() + "px;");
//            iconDisplay.setGraphic(iconRegion);
    }

}
