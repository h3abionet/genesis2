/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.PCAGraph;

import java.util.ArrayList;

/**
 *
 * @author henry
 */
public class HiddenIndividualsController{

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
        try{
            // get ids
            String ids[] = hiddenIndividualCombo.getValue().split("\\s+");
            pcaGraph.unhideIndividual(mainController.getPcaChart(), ids);

            Genesis.closeOpenStage(event);
        }catch (Exception e){
            Genesis.throwInformationException("Select the individual to unhide");
        }
    }

    @FXML
    private void entryCancelBtn(ActionEvent event) {
        Genesis.closeOpenStage(event);
    }

    public void setHiddenIndividualCombo (ArrayList<String> hiddenIndividualsList){
        if(hiddenIndividualsList.size()==0){
            unhideBtn.setDisable(true);
        }
        hiddenIndividualCombo.getItems().addAll(FXCollections.observableArrayList(hiddenIndividualsList));
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setPcaGraph(PCAGraph pcaGraph) {
        this.pcaGraph = pcaGraph;
    }
}
