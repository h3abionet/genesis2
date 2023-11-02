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
        System.out.println("in handleAdmixEntryOK project = "+project+"file ="+admixtureFilePath); // FIXME
        System.out.println("in handleAdmixEntryOK admixtureGraph = "+admixtureGraph); // FIXME
        try{
            System.out.println("in handleAdmixEntryOK in try admixtureGraph = "+admixtureGraph); // FIXME
            System.out.println("in handleAdmixEntryOK in try mainController = "+mainController); // FIXME
            admixtureGraph = project.getAdmixtureGraph();
            if (admixtureGraph == null)
                admixtureGraph = new AdmixtureGraph(admixtureFilePath, project);
            admixtureGraph.setMainController(mainController);
            System.out.println("in handleAdmixEntryOK in try after setMain admixtureGraph = "+admixtureGraph); // FIXME
            mainController.setAdmixtureGraph(admixtureGraph);
            System.out.println("in handleAdmixEntryOK in try after set admixtureGraph = "+admixtureGraph); // FIXME

            if (admixtureGraph.isCorrectAdmixFile()) {
                System.out.println("Creating admixtureGraph"); // FIXME
                admixtureGraph.createAdmixGraph();
                System.out.println("Creating admixtureGraph successful"); // FIXME
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