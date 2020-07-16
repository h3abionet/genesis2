/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.h3abionet.genesis.model.AdmixtureGraph;
import org.h3abionet.genesis.Genesis;

/**
 * FXML Controller class
 *
 * @author henry
 */
public class AdmixtureDataInputController implements Initializable {

    private Stage dialogStage;
    private AdmixtureGraph admixtureGraph;
    public static ArrayList<StackedBarChart<String, Number>> listOfStackedBarCharts;

    private String admixtureFilePath = "";
    private String admixtureFileName = "";
    private static boolean importOk;

    @FXML
    private Button btnAdmixtureData;

    @FXML
    private Button entryOkButton;

    @FXML
    private Button entryCancelButton;

    @FXML
    private void handleAdmixEntryCancel(ActionEvent event) {
        Genesis.closeOpenStage(event);
    }

    @FXML
    private void handleAdmixEntryOK(ActionEvent event) {
        try {
            // set list of stackedBarCharts
            listOfStackedBarCharts = admixtureGraph.createGraph();
            importOk = true;

        } catch (Exception e) {
            Genesis.throwErrorException("Sorry. You might have imported a wromg file");
        }
        Genesis.closeOpenStage(event);
    }

    @FXML
    private void handleEntryBtnAdmixtureData(ActionEvent event) throws IOException {
        File admixture = getFile("Choose admixture file");
        admixtureFilePath = admixture.getAbsolutePath();
        admixtureFileName = admixture.getName();
        btnAdmixtureData.setText(admixtureFileName);
        btnAdmixtureData.setStyle("-fx-text-fill: green");
        admixtureGraph = new AdmixtureGraph(admixtureFilePath); // read the file using module class     

    }

    public static boolean isImportOk() {
        return importOk;
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
        wanted = fileChooser.showOpenDialog(dialogStage);
        return wanted;

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        importOk = false;
    }

}
