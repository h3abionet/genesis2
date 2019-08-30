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
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.Fam;
import org.h3abionet.genesis.model.Pheno;



public class ProjectDetailsController {

    private boolean okClicked = false;

    private static String  fam_fname_s="",  pheno_fname_s="", proj_name_s="";
    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private AnchorPane newProjectEntry;
    
    @FXML 
    Button entryOKButton;

    @FXML
    Button entryCancelButton;  

    @FXML 
    Button fam_fname;
    
    @FXML
    Button pheno_fname;
    
    @FXML private  TextField proj_name;
    
    private final Stage dialogStage= new Stage();
    
    Project project;
    Fam fam;
    Pheno pheno;
    
    @FXML
    private void initialize() {
        System.out.println("init the pcadetailscontroller");
    }
    
    public ProjectDetailsController(){
        
    }

    /**
     * Sets the stage of this dialog.
     * 
     * 
     * @throws java.io.IOException
     */
    public void setDialogStage() throws IOException{
    FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("view/ProjDialogEntry.fxml"));
    Parent root = (Parent) fxmlLoader.load();
    dialogStage.initModality(Modality.APPLICATION_MODAL);
    dialogStage.setTitle("Genesis 2.0");
    dialogStage.setScene(new Scene(root));
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
    
    /**
     * Called when the user clicks ok.
     * @param event
     * @throws java.io.IOException
     */
    @FXML
    private void handlePcaEntryOK(ActionEvent event) throws IOException {
        okClicked = true;
        proj_name_s = proj_name.getText();
        if(fam_fname_s.length() != 0){
            fam = new Fam(fam_fname_s);
            pheno = new Pheno(pheno_fname_s);
        }else{
             pheno = new Pheno(pheno_fname_s);
        }
        
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
        return proj_name_s;
    }
    
     public String getFam() {
        System.out.println(fam_fname_s);
        return fam_fname_s;
    }

    public String getPheno() {
        System.out.println(pheno_fname_s);
        return pheno_fname_s;
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