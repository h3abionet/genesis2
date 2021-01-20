/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.PCAGraph;
import org.h3abionet.genesis.model.Project;
import org.h3abionet.genesis.model.Subject;

/**
 *
 * @author henry
 */
public class HiddenIndividualsController implements Initializable{

    @FXML
    private ComboBox<String> hiddenIndividualCombo;

    @FXML
    private Button cancelBtn;

    @FXML
    private Button unhideBtn;

    private MainController mainController;
    private PCAGraph pcaGraph;

    @FXML
    private void entryUnhideBtn(ActionEvent event) {
        // get ids
        String ids[] = hiddenIndividualCombo.getValue().split("\\s+");
        pcaGraph.unhideIndividual(mainController.getPcaChart(), ids);

        Genesis.closeOpenStage(event);
    }

    @FXML
    private void entryCancelBtn(ActionEvent event) {
        Genesis.closeOpenStage(event);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    public void setHiddenIndividualCombo (ObservableList<String> hiddenIndividualsList){
        hiddenIndividualCombo.getItems().addAll(hiddenIndividualsList);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setPcaGraph(PCAGraph pcaGraph) {
        this.pcaGraph = pcaGraph;
    }

}
