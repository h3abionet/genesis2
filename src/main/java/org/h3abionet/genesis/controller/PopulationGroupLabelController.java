package org.h3abionet.genesis.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.AdmixtureGraph;
import org.h3abionet.genesis.model.Project;

public class PopulationGroupLabelController {

    @FXML
    private TextField groupNameLbl;

    @FXML
    private CheckBox hideGroupCheckbox;

    @FXML
    private Button cancelBtn;

    @FXML
    private Button doneBtn;

    private String oldGroupName;
    private AdmixtureGraph admixtureGraph;
    private Project proj;
    private StackPane pane;

    @FXML
    void cancelHandler(ActionEvent event) {
        Genesis.closeOpenStage(event);
    }

    @FXML
    void doneHandler(ActionEvent event) {

        if (hideGroupCheckbox.isSelected() == true) {
            admixtureGraph.hideGroup(oldGroupName);
        }

        String newGroupName = groupNameLbl.getText();
        if (!oldGroupName.equals(newGroupName)) { // is name changed?

            // change the text on the graph
            String style = pane.getChildren().get(0).getStyle();
            Text txt = new Text(newGroupName);
            txt.setStyle(style);
            pane.getChildren().set(0, txt);

            // rename the group in the project
            proj.renameGroupName(oldGroupName, newGroupName);
        }

        Genesis.closeOpenStage(event);
    }

    public void setGroupNameLbl(String groupName) {
        this.oldGroupName = groupName;
        groupNameLbl.setText(groupName);
    }

    public void setAdmixtureGraph(AdmixtureGraph admixtureGraph) {
        this.admixtureGraph = admixtureGraph;
    }

    public void setProj(Project proj) {
        this.proj = proj;
    }

    public void setStackedPane(StackPane pane) {
        this.pane = pane;
    }
}
