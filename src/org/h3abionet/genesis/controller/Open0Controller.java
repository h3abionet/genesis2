package org.h3abionet.genesis.controller;



import org.h3abionet.genesis.model.Project;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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
    
    @FXML private AdmixtureTabController admixtureTabController;

    @FXML private ClustersTabController clustersTabController;

    @FXML private HelpTabController helpTabController;

    @FXML private PcaTabController pcaTabController;

    @FXML private SearchTabController searchTabController;

    @FXML private TableTabController tableTabController;

    @FXML private PCADataInputController pCADataInputController;
    
    @FXML private AnchorPane anchorPane;
    
    @FXML
    private Button admixtureButton;
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
    
    public AnchorPane getAnchorPane(){
    return pcaTabController.getAnchorPane();
    }
    
    public ListView getlistViewTableTab(){
    return tableTabController.listViewTableTab();
    }
            
    private VBox projectVBox(ProjectDetailsController control) {
       String proj_name  =  control.getProjectName();
       String fam_name   =  control.getFam();
       String pheno_name =  control.getPheno();
       VBox v = new VBox();
       HBox h = new HBox(10);
       Label lab = new Label();
       lab.setText("Project: "+proj_name);
       Label fam = new Label();
       fam.setText(" FAM file: "+fam_name);
       Label pheno = new Label();
       if (pheno_name.length()==0) pheno_name="None";
       pheno.setText(" Pheno file: "+pheno_name);
       h.getChildren().addAll(lab, fam, pheno);
       h.setPadding(new Insets(10,10,10,10));
       v.getChildren().add(h);
       return v;
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
