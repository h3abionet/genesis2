/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.h3abionet.genesis.Genesis;

/**
 * FXML Controller class
 *
 * @author henry
 */
public class AdmixtureIndividualDetailsController implements Initializable {
    
    @FXML
    private Label valuesLabel;

    @FXML
    private ListView<String> phenoList;

    @FXML
    private CheckBox hideCheckBox;

    @FXML
    private Button btnOK;

    @FXML
    private Button btnCancel;
    
    /**
     * set proportion values
     * @param proportions
     */
    public void setValuesLabel(String proportions) {
        valuesLabel.setText(proportions);
    }
    
    /**
     * set phenotype details
     * @param phenoDetails
     */
    public void setPhenoList(String[] phenoDetails) {
        phenoList.setItems(FXCollections.observableArrayList (phenoDetails));
    }

    @FXML
    private void entryBtnCancel(ActionEvent event) {
        Genesis.closeOpenStage(event);

    }

    @FXML
    private void entryBtnOK(ActionEvent event) {
        Genesis.closeOpenStage(event);

    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       
        //TODO
        
    }    
    
}
