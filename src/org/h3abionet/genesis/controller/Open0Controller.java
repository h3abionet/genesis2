package org.h3abionet.genesis.controller;



import org.h3abionet.genesis.model.Project;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
/*
 * Copyright (C) 2018 scott
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 *
 * @author scott
 */
public class Open0Controller implements Initializable {
   
    @FXML 
    private PCADataInputController pCADataInputController;
    
    @FXML
    private TableView<?> tableTab;
    @FXML
    private TabPane tabPane;
    @FXML
    private AnchorPane pcaAnchorPane;
    @FXML
    private Tab admixtureTab;
    @FXML 
    private AnchorPane anchorPane;
    @FXML
    private Button newadmixture;
    @FXML
    private Button newpca;
    @FXML
    private Button newproject;
    @FXML
    private Button threeDButton;
    @FXML
    private Button dataButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button downloadButton;
    @FXML
    private Button individualButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button selectionButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button fileButton;
     
    private Project project;

    public AnchorPane getPcaAnchorPane() {
        return pcaAnchorPane;
    }

    public TableView<?> getTableTab() {
        return tableTab;
    }
            
    @FXML
    private void newProject(ActionEvent event) throws IOException{
        ProjectDetailsController controller = new ProjectDetailsController();
        controller.setDialogStage();
    }
      
    @FXML
    private void newPCA(ActionEvent event) throws IOException {
    pCADataInputController = new PCADataInputController();
    pCADataInputController.setPcaDialogStage();
    }
    
    @FXML
    private void loadData(ActionEvent event) throws IOException {
    pCADataInputController = new PCADataInputController();
    pCADataInputController.setPcaDialogStage();
    }
    
    
     
    @FXML
    private void handleKeyInput(final InputEvent event) {
       if (event instanceof KeyEvent) {
        final KeyEvent keyEvent = (KeyEvent) event;
        if (keyEvent.isControlDown() && (keyEvent.getKeyCode() == KeyEvent.VK_H)){
           provideAboutFunctionality();
        }
     }
    }
  
    
    private void provideAboutFunctionality() {
        System.out.println("I need help!");
    }
    
    public void exit(ActionEvent event){
    Platform.exit();
    }
  
    @Override
    public void initialize(java.net.URL arg0, ResourceBundle arg1) {
        
    }
    
    
}
