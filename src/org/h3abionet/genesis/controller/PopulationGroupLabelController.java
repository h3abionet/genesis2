package org.h3abionet.genesis.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.AdmixtureGraph;

public class PopulationGroupLabelController {

    @FXML
    private TextField groupNameLbl;

    @FXML
    private HBox hideGroupCheckbox;

    @FXML
    private Button cancelBtn;

    @FXML
    private Button doneBtn;

    private String groupName;
    private AdmixtureGraph admixtureGraph;

    @FXML
    void cancelHandler(ActionEvent event) {
        Genesis.closeOpenStage(event);
    }

    @FXML
    void doneHandler(ActionEvent event) {
        admixtureGraph.hideGroup(groupName);
        Genesis.closeOpenStage(event);
    }

    public void setGroupNameLbl(String groupName) {
        this.groupName = groupName;
        groupNameLbl.setText(groupName);
    }

    public void setAdmixtureGraph(AdmixtureGraph admixtureGraph) {
        this.admixtureGraph = admixtureGraph;
    }
}
