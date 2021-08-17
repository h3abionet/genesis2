/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.AdmixtureGraph;

import java.io.File;
import java.io.IOException;

/**
 * FXML Controller class
 *
 * @author henry
 */
public class AdmixtureDataInputController{

    private Stage dialogStage;
    private AdmixtureGraph admixtureGraph;

    private String admixtureFilePath = "";
    private String admixtureFileName = "";

    @FXML
    private Button btnAdmixtureData;

    @FXML
    private Button entryOkButton;

    @FXML
    private Button entryCancelButton;
    private MainController mainController;

    @FXML
    private void handleAdmixEntryCancel(ActionEvent event) {
        Genesis.closeOpenStage(event);
    }

    @FXML
    private void handleAdmixEntryOK(ActionEvent event) {
        try {
            // set list of stackedBarCharts
            admixtureGraph.createAdmixGraph();
            mainController.setAdmixCreationSuccessful(true);

        } catch (Exception e) {
            mainController.setAdmixCreationSuccessful(false);
            Genesis.throwErrorException("Sorry. You might have imported a wrong file");
        }
        Genesis.closeOpenStage(event);
    }

    @FXML
    private void handleEntryBtnAdmixtureData(ActionEvent event) throws IOException {
        File admixture = getFile("Choose admixture file");
        try{
            admixtureFilePath = admixture.getAbsolutePath();
            admixtureFileName = admixture.getName();
            btnAdmixtureData.setText(admixtureFileName);
            btnAdmixtureData.setStyle("-fx-text-fill: #06587F");
            admixtureGraph = new AdmixtureGraph(admixtureFilePath); // read the file using module class
            admixtureGraph.setMainController(mainController);
            mainController.setAdmixtureGraph(admixtureGraph);

        }catch(Exception ex){
            Genesis.throwErrorException("No file imported");
        }   
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * read Q files
     *
     * @param which
     * @return
     */
    private File getFile(String which) {
        File wanted;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(which);

        if(Genesis.getPreviouslyOpenedPath()==null){
            ;
        }else{
            File validator = new File(Genesis.getPreviouslyOpenedPath());
            if (validator.exists() && validator.isDirectory())
                fileChooser.setInitialDirectory(validator);
        }

        wanted = fileChooser.showOpenDialog(dialogStage);
        if(wanted.getParent()!=null){
            Genesis.setPreviouslyOpenedPath(wanted.getParent());
        }else {
            ;
        }

        return wanted;
    }
}