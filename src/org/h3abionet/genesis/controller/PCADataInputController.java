/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.Fam;
import org.h3abionet.genesis.model.PCAProject;
import org.h3abionet.genesis.model.Pheno;
import org.h3abionet.genesis.model.Project;

/**
 * FXML Controller class
 *
 * @author Henry
 */
public class PCADataInputController implements Initializable {
    
    private Stage dialogStage;
    PCAProject pCAproject;
    Project project;
    Fam fam;
    Pheno pheno;

    private boolean okClicked = false;

    private String  pca_fname_s="";
      
    @FXML
    private Button pca_evec_fname;

    @FXML
    private ComboBox<String> pcaComboButton1;

    @FXML
    private ComboBox<String> pcaComboButton2;

    @FXML
    private Button entryOKButton;

    @FXML
    private Button entryCancelButton;
    
    public void setPcaDialogStage() throws IOException{
    FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("view/PCADataInput.fxml"));
    Parent root1 = (Parent) fxmlLoader.load();
    dialogStage = new Stage();
    dialogStage.initModality(Modality.APPLICATION_MODAL);
    dialogStage.setTitle("Genesis 2.0");
    dialogStage.setScene(new Scene(root1));
    dialogStage.show();
    }                                                                                                        
    
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

    @FXML
    void handlePcaEvecFname(ActionEvent event) throws IOException {
      File pca = getFile("Choose PCA file");  
      pca_fname_s = pca.getAbsolutePath();
      pca_evec_fname.setText(pca.getName());
      pca_evec_fname.setStyle("-fx-text-fill: green");
      pCAproject = new PCAProject(pca_fname_s);
      pcaComboButton1.setItems(pCAproject.getPca_cols());
      pcaComboButton2.setItems(pCAproject.getPca_cols());

    }

    @FXML
    private void handlePcaEntryOK(ActionEvent event) throws IOException {
        okClicked = true;
        Genesis.getPrimaryStage().close();
        pCAproject.getGraph();
        closeStage(event);
    }

    @FXML
    void handlePcaEntryCancel(ActionEvent event) {
        System.out.println("Cancel");
        closeStage(event);
    }

    public void closeStage(ActionEvent event){
    Node source = (Node)event.getSource();
    Stage stage = (Stage) source.getScene().getWindow();
    stage.close();
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
      
    }    
    
}
