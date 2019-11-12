/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 *
 * @author henry
 */
public class LabelOptionsController implements Initializable{
    
    @FXML
    private Label labelHandler;

    @FXML
    private Button fontBtn;

    @FXML
    private CheckBox deleteBox;

    @FXML
    private ColorPicker labelColorPicker;

    @FXML
    private Button doneBtn;

    @FXML
    private Button cancelBtn;
    
    public void setLabel(String label) {
        labelHandler.setText(label);
    }
    
    @FXML
    private void setLabelFont(ActionEvent event) {
 
    }
    
    
    @FXML
    private void setLabelColor(ActionEvent event) {
 
    }
    
    @FXML
    private void entryDoneBtn(ActionEvent event) {
       
        closeStage(event);
    }

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
        
    }
    
}
