/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.h3abionet.genesis.model.AdmixtureGraphEventsHandler;
import org.h3abionet.genesis.Genesis;

/**
 * FXML Controller class
 *
 * @author henry
 */
public class AncestorOptionsController implements Initializable {

    @FXML
    private Label ancestorNameLabel;

    @FXML
    private Rectangle selectedColorDisplay;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private Button shiftUpBtn;

    @FXML
    private Button shiftDownBtn;
    
    @FXML
    private Button doneBtn;

    @FXML
    private Button cancelBtn;
    
    private AdmixtureGraphEventsHandler admixtureGraph;
    private static Color chosenColor;

    /**
     * set name of ancestor when loading this interface
     *
     * @param ancestorName
     */
    public void setAncestorNumberLabel(String ancestorName) {
        ancestorNameLabel.setText(ancestorName);
        ancestorNameLabel.setAlignment(Pos.CENTER);
    }

    /**
     * set default ancestor color when loading this interface
     *
     * @param defaulColor
     */
    public void setDefaultAncestorColor(Paint paint) {
        selectedColorDisplay.setFill(paint);
        
        //  set the default color of the picker
        //  colorPicker.setValue((Color)paint);
        
        colorPicker.setOnAction((ActionEvent t) -> {
            selectedColorDisplay.setFill(colorPicker.getValue());
            this.chosenColor = colorPicker.getValue();
        });
    }

    @FXML
    private void cancelHandler(ActionEvent event) {
        Genesis.closeOpenStage(event);
    }

    @FXML
    private void doneHandler(ActionEvent event) {
        Genesis.closeOpenStage(event);
   
    }

    @FXML
    private void setChosenColor(ActionEvent event) {

    }

    @FXML
    private void shiftDownHandler(ActionEvent event) {

    }

    @FXML
    private void shiftUpHandler(ActionEvent event) {

    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public String getChosenColor() {
        String selectedColor = Integer.toHexString(chosenColor.hashCode());
        return selectedColor;
    }

    public Paint getColor() {
        return chosenColor;
    }

}
