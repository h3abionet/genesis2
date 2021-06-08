/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.AdmixtureGraph;
import org.h3abionet.genesis.model.Project;

import java.util.List;

/**
 * FXML Controller class
 *
 * @author henry
 */
public class AdmixtureIndividualDetailsController{
    
    @FXML
    private Label valuesLabel;

    @FXML
    private ListView<String> phenoList;

    @FXML
    private CheckBox hideCheckBox;

    @FXML
    private Button btnOK;

    @FXML
    private Button btnCancel;
    private Project project;
    private AdmixtureGraph admixtureGraph;
    private StackedBarChart<String, Number> admixChart;
    private String clickedId;

    /**
     * set proportion values
     * @param proportions
     */
    public void setValuesLabel(String proportions) {
        valuesLabel.setText(proportions);
    }
    
    /**
     * set phenotype details
     * @param phenoDetails
     */
    public void setPhenoList(List<String> phenoDetails) { phenoList.setItems(FXCollections.observableArrayList (phenoDetails)); }

    @FXML
    private void entryBtnCancel(ActionEvent event) { Genesis.closeOpenStage(event); }

    @FXML
    private void entryBtnOK(ActionEvent event) {

        if (hideCheckBox.isSelected()) {
            for (XYChart.Series<String, Number> series : admixChart.getData()) {
                for (XYChart.Data<String, Number> data : series.getData()) {
                    String xValue = String.valueOf(data.getXValue());
                    String yValue = String.valueOf(data.getYValue());
                    if(xValue.equals(clickedId)) {
                        admixtureGraph.hideIndividual(series, new String[]{xValue, yValue});
                        System.out.println("I was clicked"+ clickedId);
                    }
                }
            }
        }
        Genesis.closeOpenStage(event);
    }

    public void setAdmixtureGraph(AdmixtureGraph admixtureGraph) {
        this.admixtureGraph = admixtureGraph;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setAdmixtureChart(StackedBarChart<String, Number> admixChart) {
        this.admixChart = admixChart;
    }

    public void setClickedId(String fid) {
        this.clickedId = fid;
    }
}
