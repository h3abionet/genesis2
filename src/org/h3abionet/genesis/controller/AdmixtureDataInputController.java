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
import javafx.scene.Node;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.h3abionet.genesis.model.AdmixtureProject;
import org.h3abionet.genesis.model.Fam;
import org.h3abionet.genesis.model.Pheno;

/**
 * FXML Controller class
 *
 * @author henry
 */
public class AdmixtureDataInputController implements Initializable {
    private Stage dialogStage;
    AdmixtureProject admixtureProject;
    Fam fam;
    Pheno pheno;
    private ArrayList<StackedBarChart<String, Number>> listOfCharts;

    private static  String admixFilePath = "";
    private static  String admix_file = "";
    private StackedBarChart<String, Number> admixChart; // chart from the AdmixtureProject
    
    @FXML
    private Button btnAdmixtureData;

    @FXML
    private Button entryOkButton;

    @FXML
    private Button entryCancelButton;

    @FXML
    private void handleAdmixEntryCancel(ActionEvent event) {
        closeStage(event);
    }

    @FXML
    private void handleAdmixEntryOK(ActionEvent event) {
        setAdmixChart(admixtureProject.getAdmixtureGraph()); // one chart
        setListOfCharts(admixtureProject.createAdmixturePlots()); // list of charts
        closeStage(event);
    }

    @FXML
    private void handleEntryBtnAdmixtureData(ActionEvent event) throws IOException {
        File admixture = getFile("Choose admixture file");
        admixFilePath = admixture.getAbsolutePath();
        admix_file = admixture.getName();
        btnAdmixtureData.setText(admix_file);
        btnAdmixtureData.setStyle("-fx-text-fill: green");
        admixtureProject = new AdmixtureProject(admixFilePath); // read the file using module class
        
    }
    
    /**
     * 
     * get admixture chart
     * @return 
     */
    public StackedBarChart<String, Number> getAdmixChart() {
        return admixChart;
    }
    
    /**
     * set admixture chart
     * @param chartsList 
     */
    public void setAdmixChart(StackedBarChart<String, Number> chart) {
        this.admixChart = chart;
    }
    
    /**
     * get a list of admixture charts
     * @return 
     */
    public ArrayList<StackedBarChart<String, Number>> getListOfCharts() {
        return listOfCharts;
    }
    
    /**
     * set listOfCharts
     * @param listOfCharts 
     */
    public void setListOfCharts(ArrayList<StackedBarChart<String, Number>> listOfCharts) {
        this.listOfCharts = listOfCharts;
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
    
    /**
     * Close the current stage
     * @param event 
     */
    private void closeStage(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
