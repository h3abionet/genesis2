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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;

/**
 *
 * @author henry
 */
public class HiddenIndividualsController implements Initializable{
    
    @FXML
    private ComboBox<Node> hiddenIndividual;
    
    static ObservableList<Node> ind = FXCollections.observableArrayList();

    public ObservableList<Node> getItems() {
        return ind;
    }

    public void setInd(Node node) {
        ind.add(node);
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hiddenIndividual.getItems().addAll(ind);
        
    }
    
}
