/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Henry
 */
public class PcaTabController implements Initializable{
    
    @FXML
    private AnchorPane pcaTab;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }

    public AnchorPane getAnchorPane() {
       return pcaTab;
    }
}
