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

/**
 *
 * @author henry
 */
public class HiddenIndividualsController implements Initializable{
    
    @FXML
    private ComboBox<String> hiddenIndividual;
    
    @FXML
    private Button cancelBtn;

    @FXML
    private Button unhideBtn;
    
    /**
     * keep iids and their x, y values in a hashMap 
     */
    private static HashMap<String, String[]> hidenIds = new HashMap<String, String[]>();
    
    public static HashMap<String, String[]> getHidenIds() {
        return hidenIds;
    }
    
    @FXML
    private void entryUnhideBtn(ActionEvent event) {
        // get iid
        String iid = hiddenIndividual.getValue();
        // get (x & y coordinates, and group name)
        String [] hidenPoint = hidenIds.get(iid);
        
        // (x,y)
        Float x = Float.parseFloat(hidenPoint[0]);
        Float y = Float.parseFloat(hidenPoint[1]);
        
        // group name
        String groupName = hidenPoint[2];
                                
        ScatterChart<Number, Number> chart = MainController.getPcaChart();
        for(XYChart.Series<Number, Number> s: chart.getData()){
            if(s.getName().equals(groupName)){
                s.getData().add(new XYChart.Data(x,y));
                break;
            }
        }
        
        //remove the id from the hidden ids
        hidenIds.remove(iid);
        
        Genesis.closeOpenStage(event);

    }
    
    @FXML
    private void entryCancelBtn(ActionEvent event) {
        Genesis.closeOpenStage(event);
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hiddenIndividual.getItems().addAll(hidenIds.keySet());
        
    }
    
}
