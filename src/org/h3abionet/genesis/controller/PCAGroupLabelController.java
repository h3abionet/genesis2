package org.h3abionet.genesis.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.*;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.PCAGraph;
import org.h3abionet.genesis.model.Project;

public class PCAGroupLabelController {

    @FXML
    private CheckBox hideGroupCheckbox;

    @FXML
    private TextField groupNameLbl;

    @FXML
    private ComboBox<String> legendPosition;

    @FXML
    private Button cancelBtn;

    @FXML
    private Button doneBtn;

    private String oldGroupName;
    private Project proj;
    private Label label;
    private ScatterChart<Number, Number> chart;
    PCAGraph pcaGraph;

    @FXML
    void cancelHandler(ActionEvent event) {
        Genesis.closeOpenStage(event);
    }

    @FXML
    void doneHandler(ActionEvent event) {
        // String oldGroupName, String newGroupName, StackPane pane, Lbe
        String newGroupName = groupNameLbl.getText();
        
        if (!oldGroupName.equals(newGroupName)) { // is name changed?
            // rename the groups on the legend
            pcaGraph.renameLegendGroupNames(oldGroupName, newGroupName);

            // rename group name in the project
            proj.renameGroupName(oldGroupName, newGroupName);
        }

        if(hideGroupCheckbox.isSelected()==true){
            pcaGraph.hideGroup(oldGroupName);

            // add group name to project
            if(!proj.getHiddenGroups().contains(newGroupName)){
                proj.getHiddenGroups().add(newGroupName);
            }
        }

        // change legend position
        chart.lookup(".chart").setStyle("-fx-legend-side: "+legendPosition.getValue()+";");

        Genesis.closeOpenStage(event);

    }

    public void setGroupNameLbl(String groupName) {
        this.oldGroupName = groupName;
        groupNameLbl.setText(groupName);
    }

    public void setComboBox() {
        legendPosition.setItems(FXCollections.observableArrayList(new String[]{"Right", "Bottom"}));
        legendPosition.setValue("Right");
    }

    public void setProj(Project proj) {
        this.proj = proj;
    }

    public void setGroupNameNode(Label label) {
        this.label = label;
    }

    public void setChart(ScatterChart<Number, Number> sc) {
        this.chart = sc;
    }

    public void setPCAGraph(PCAGraph pcaGraph) {
        this.pcaGraph = pcaGraph;
    }
}
