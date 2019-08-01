/*
 * Copyright 2019 University of the Witwatersrand, Johannesburg on behalf of the Pan-African Bioinformatics Network for H3Africa.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.h3abionet.genesis.controller;

/**
 *
 * @author scott
 */
 
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.Project;
import java.io.File;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;



public class ProjectDetailsController extends AnchorPane {

   

    private Stage dialogStage;
    private boolean okClicked = false;

    private String  fam_fname_s="",  pheno_fname_s="";
    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */

    private File our_dir=null; /* which directory we last used so that the user
    does't have to navigate from the roout 
    */
    
    @FXML 
    Button entryOKButton;

    @FXML
    Button entryCancelButton;
    

    @FXML 
    Button fam_fname;

    public String getFam() {
        return fam_fname_s;
    }

    public String getPheno() {
        return pheno_fname_s;
    }
            
    @FXML
    Button pheno_fname;
    
    @FXML private  TextField proj_name;
    
    private Project project;

    public Project getProject() {
        return project;
    }
    
    @FXML
    private void initialize() {
        System.out.println("init the pcadetailscontroller");
    }
    
    public ProjectDetailsController(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(Genesis.class.getResource(fxml));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
    }
    
    /**
     * Sets the stage of this dialog.
     * 
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
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
       if (our_dir != null)
           fileChooser.setInitialDirectory(our_dir);
       fileChooser.setTitle(which);
       wanted = fileChooser.showOpenDialog(dialogStage);
       our_dir = wanted.getParentFile();
       return wanted;
        
    }
    
  
    
    @FXML void handleProjectName() {
        System.out.println(proj_name);
        if (fam_fname.getText().length()>0)  
          entryOKButton.setDisable(false);
        
    }
    
    @FXML void handleFamFname() {
      File fam = getFile("Choose FAM file");  
      fam_fname_s=fam.getAbsolutePath();
      fam_fname.setText(fam.getName());
      fam_fname.setStyle("-fx-text-fill: green");
      if (proj_name.getText().length()>0)  
          entryOKButton.setDisable(false);

    }   
    
    @FXML void handlePhenoFname() {
      File phen = getFile("Choose Pheno file");  
      pheno_fname_s = phen.getAbsolutePath();
      pheno_fname.setText(phen.getName());
      pheno_fname.setStyle("-fx-text-fill: green");
    }  
    
    
    /**
     * Called when the user clicks ok.
     */
    @FXML
    private void handlePcaEntryOK() throws IOException {
            okClicked = true;
            dialogStage.close();
            project = new Project(proj_name.getText(), fam_fname_s, pheno_fname_s);
       
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handlePcaEntryCancel() {
        System.out.println("Cancel");
        dialogStage.close();
    }

    
    public String getProjectName() {
               
        return proj_name.getText();
    }
    
    
    /**
     * Validates the user input in the text fields.
     * 
     * @return true if the input is valid
     */
  
}     