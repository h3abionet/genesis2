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
package org.h3abionet.genesis.controller;

/**
 *
 * @author scott
 */
 

import org.h3abionet.genesis.model.Project;
import java.io.File;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.Fam;



public class ProjectDetailsController extends AnchorPane{

   

    private final Stage dialogStage= new Stage();
    

    private boolean okClicked = false;

    private String  fam_fname_s="",  pheno_fname_s="", pca_fname_s="";
    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private AnchorPane fileDialogAnchorPane;
    
    @FXML 
    Button entryOKButton;

    @FXML
    Button entryCancelButton;  

    @FXML 
    Button fam_fname;
    
    @FXML 
    Button pca_fname;
    
    @FXML
    Button pheno_fname;
    
    @FXML private  TextField proj_name;
    
    @FXML
    private ComboBox<String> pcaComboButton1;
    
    @FXML
    private ComboBox<String> pcaComboButton2;

    private Project project;
    private Fam fam;

    @FXML
    private void initialize() {
        System.out.println("init the pcadetailscontroller");
    }

    /**
     * Sets the stage of this dialog.
     * 
     * 
     * @throws java.io.IOException
     */
    public void setDialogStage() throws IOException{
    System.out.println("genesisprototype.controller.Open0Controller.newProject()");
    FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("view/InputData.fxml"));
    Parent root1 = (Parent) fxmlLoader.load();
    dialogStage.initModality(Modality.APPLICATION_MODAL);
    dialogStage.setTitle("Genesis 2.0");
    dialogStage.setScene(new Scene(root1));
    dialogStage.show();
    }                                                                                                        

    
    /**
     * Returns true if the user clicked OK, false otherwise.
     * 
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    
    private File getFile(String which) {
       File wanted;
       FileChooser fileChooser = new FileChooser();
       fileChooser.setTitle(which);
       wanted = fileChooser.showOpenDialog(dialogStage); 
       return wanted;
        
    }
  
    
    @FXML void handleProjectName() {
        System.out.println(proj_name);
        if (fam_fname.getText().length()>0)  
          entryOKButton.setDisable(false);
        
    }
    
    @FXML void handleFamFname() {
      File famFile = getFile("Choose FAM file");  
      fam_fname_s=famFile.getAbsolutePath();
      fam_fname.setText(famFile.getName());
      fam_fname.setStyle("-fx-text-fill: green");
      if (proj_name.getText().length()>0)  
          entryOKButton.setDisable(false);
    }   
    
    @FXML void handlePhenoFname() {
      File phen = getFile("Choose FAM file");  
      pheno_fname_s = phen.getAbsolutePath();
      pheno_fname.setText(phen.getName());
      pheno_fname.setStyle("-fx-text-fill: green");
    }
    
    @FXML void handlePCAFname() throws IOException{
      File pca = getFile("Choose FAM file");  
      pca_fname_s = pca.getAbsolutePath();
      pca_fname.setText(pca.getName());
      pca_fname.setStyle("-fx-text-fill: green");   
      project = new Project(proj_name.getText(), fam_fname_s, pheno_fname_s, pca_fname_s);
      pcaComboButton1.setItems(project.getPca_cols());
      pcaComboButton2.setItems(project.getPca_cols());
    }  
    
    
    /**
     * Called when the user clicks ok.
     * @param event
     * @throws java.io.IOException
     */
    @FXML
    private void handlePcaEntryOK(ActionEvent event) throws IOException {
        okClicked = true;
        project.getMerged();
        closeStage(event);     
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handlePcaEntryCancel(ActionEvent event) {
        System.out.println("Cancel");
        closeStage(event);
    }

    
    public String getProjectName() {
        return proj_name.getText();
    }
    
     public String getFam() {
        return fam_fname_s;
    }

    public String getPheno() {
        return pheno_fname_s;
    }
    
    public String getPCA() {
        return pca_fname_s;
    }
    
    public void closeStage(ActionEvent event){
    Node source = (Node)event.getSource();
    Stage stage = (Stage) source.getScene().getWindow();
    stage.close();
    }
    
    /**
     * Validates the user input in the text fields.
     * 
     * @return true if the input is valid
     */
  
}     