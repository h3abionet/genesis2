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
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 *
 * @author Henry
 */
public class IndividualDetailsController implements Initializable{
    
    @FXML
    public Label id;

    @FXML
    public Label phenoDetails;
    
    @FXML
    public Button okButton;

    public Label getId() {
        return id;
    }

    public Label getPhenoDetails() {
        return phenoDetails;
    }

    public Button getOkButton() {
        return okButton;
    }

    @FXML
    void entryOkButton(ActionEvent event) {
        closeStage(event);
    }
    
    public void closeStage(ActionEvent event){
    Node source = (Node)event.getSource();
    Stage stage = (Stage) source.getScene().getWindow();
    stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    
}
