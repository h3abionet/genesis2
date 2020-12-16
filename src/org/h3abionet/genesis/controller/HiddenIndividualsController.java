/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.PCAGraph;

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
        // get iid
        String iid = hiddenIndividualCombo.getValue();

        // get (x & y coordinates, and group name)
        String [] hiddenPoint =  pcaGraph.getHiddenIndividual().get(iid);
        
        // (x,y)
        Float x = Float.parseFloat(hiddenPoint[0]);
        Float y = Float.parseFloat(hiddenPoint[1]);
        
        // group name
        String groupName = hiddenPoint[2];
                                
        ScatterChart<Number, Number> chart = mainController.getPcaChart();

        for(XYChart.Series<Number, Number> s: chart.getData()){
            if(s.getName().equals(groupName)){
                XYChart.Data<Number, Number> data = new XYChart.Data(x, y);
                s.getData().add(data); // add point to graph

                // get group properties
                String color = (String) pcaGraph.getGroupColors().get(groupName);
                String iconType = (String) pcaGraph.getGroupIcons().get(groupName);

                // set the style of icon
                data.getNode().setStyle(pcaGraph.getStyle(color, iconType, 5));

                data.getNode().setOnMouseClicked(e ->{
                    try {
                        pcaGraph.setMouseEvent(data, chart);
                    } catch (Exception ex) {
                        ;
                    }
                });
                break;
            }
        }
        
        //remove the id from the hidden ids
        pcaGraph.getHiddenIndividual().remove(iid);
        
        Genesis.closeOpenStage(event);
    }
    
    @FXML
    private void entryCancelBtn(ActionEvent event) {
        Genesis.closeOpenStage(event);
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    public void setHiddenIndividualCombo (){
        hiddenIndividualCombo.getItems().addAll(pcaGraph.getHiddenIndividual().keySet());
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setPcaGraph(PCAGraph pcaGraph) {
        this.pcaGraph = pcaGraph;
    }
}
