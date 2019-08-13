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
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Henry
 */
public class TableTabController implements Initializable {
    
    @FXML
    private AnchorPane tableTab;

    @FXML
    private ListView<?> listViewTableTab;

    
    public ListView listViewTableTab(){
    return listViewTableTab;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    
        
        
    }

    
}
