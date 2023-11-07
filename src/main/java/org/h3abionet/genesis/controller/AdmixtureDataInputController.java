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
import org.h3abionet.genesis.model.Project;

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
    private Project project;

    @FXML
    private void handleAdmixEntryCancel(ActionEvent event) {
        mainController.setAdmixCreationSuccessful(false);
        Genesis.closeOpenStage(event);
    }

    @FXML
    private void handleAdmixEntryOK(ActionEvent event) {
        // set list of stackedBarCharts
        try{
            admixtureGraph = new AdmixtureGraph(admixtureFilePath, project); // read the file using module class
            admixtureGraph.setMainController(mainController);
            mainController.setAdmixtureGraph(admixtureGraph);

            if (admixtureGraph.isCorrectAdmixFile()) {
                admixtureGraph.createAdmixGraph();
                mainController.setAdmixCreationSuccessful(true);
            } else {
                mainController.setAdmixCreationSuccessful(false);
                Genesis.throwErrorException("Imported file contains strings");
            }
        }catch (Exception e){
            Genesis.throwErrorException(e.toString()+" \n"+"Wrong file imported");
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
            entryOkButton.setDisable(false);
            btnAdmixtureData.setStyle("-fx-text-fill: #06587F");
        }catch(Exception ex){
            mainController.setAdmixCreationSuccessful(false);
            Genesis.throwErrorException("No file imported");
        }
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * read Q files
     * @param which
     * @return
     */
    private File getFile(String which) {
        File wanted;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(which);

        wanted = fileChooser.showOpenDialog(dialogStage);

        return wanted;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void enbleOkButton(boolean btnStatus){
        this.entryOkButton.setDisable(btnStatus);
    }
}
