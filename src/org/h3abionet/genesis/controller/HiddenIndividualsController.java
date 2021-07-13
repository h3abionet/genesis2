/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.AdmixtureGraph;
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
    private ComboBox<String> hiddenGroupCombo;

    @FXML
    private Button cancelBtn;

    @FXML
    private Button unhideBtn;

    private MainController mainController;
    private PCAGraph pcaGraph;
    private String currentGraphType;
    private AdmixtureGraph admixtureGraph;
//    private ArrayList<String> hiddenIndividualsList;
//    private ArrayList<String> hiddenGroupsList;

    @FXML
    private void entryUnhideBtn(ActionEvent event) {
        if(hiddenIndividualCombo.getValue()==null && hiddenGroupCombo.getValue()==null){
            Genesis.throwInformationException("Select either individual or group to unhide NOT both");
        }

        if(hiddenIndividualCombo.getValue()!=null){
            try{
                // get ids
                String ids[] = hiddenIndividualCombo.getValue().split("\\s+");

                if(currentGraphType.equals("pca")){
                    pcaGraph.showIndividual(ids);
                }

                if(currentGraphType.equals("admixture")){
                    admixtureGraph.showIndividual(ids);
                }
                Genesis.closeOpenStage(event);

            }catch (Exception e){
                Genesis.throwInformationException("Select the individual to show");
            }
        }

        if(hiddenGroupCombo.getValue()!=null){
//            try{
                // get group
                String group = hiddenGroupCombo.getValue();

                if(currentGraphType.equals("pca")){
                    pcaGraph.showHiddenGroup(group);
                }

                if(currentGraphType.equals("admixture")){
                    admixtureGraph.showHiddenGroup(group);
                }

                Genesis.closeOpenStage(event);
//            }catch (Exception e){
//                Genesis.throwInformationException("Select the group to show");
//            }o
        }

    }

    @FXML
    private void entryCancelBtn(ActionEvent event) {
        Genesis.closeOpenStage(event);
    }

    public void setHiddenIndividualCombo (ArrayList<String> hiddenIndividualsList){
//        this.hiddenIndividualsList = hiddenIndividualsList;
//        if(hiddenGroupsList.size()==0 && hiddenIndividualsList.size()==0){
//            unhideBtn.setDisable(true);
//        }
        hiddenIndividualCombo.getItems().addAll(FXCollections.observableArrayList(hiddenIndividualsList));
    }

    public void setHiddenGroupComboCombo (ArrayList<String> hiddenGroupsList){
//        this.hiddenGroupsList = hiddenGroupsList;
//        if(hiddenGroupsList.size()==0 && hiddenIndividualsList.size()==0){
//            unhideBtn.setDisable(true);
//        }
        hiddenGroupCombo.getItems().addAll(FXCollections.observableArrayList(hiddenGroupsList));
    }


    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setPcaGraph(PCAGraph pcaGraph) {
        this.pcaGraph = pcaGraph;
    }

    public void setCurrentGraphType(String currentGraphType) {
        this.currentGraphType = currentGraphType;
    }

    public void setAdmixtureGraph(AdmixtureGraph admixtureGraph) {
        this.admixtureGraph = admixtureGraph;
    }
}
