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
    
    private static HashMap<String, String[]> hidenIds = new HashMap<String, String[]>();
    
    public static HashMap<String, String[]> getHidenIds() {
        return hidenIds;
    }
    
    @FXML
    private void entryUnhideBtn(ActionEvent event) {
        // (x,y)
        String [] hidenPoint = hidenIds.get(hiddenIndividual.getValue());
        
        Float x = Float.parseFloat(hidenPoint[0]);
        Float y =Float.parseFloat(hidenPoint[1]);
        
        ScatterChart<Number, Number> chart = MainController.getPcaChart();
        for(XYChart.Series<Number, Number> s: chart.getData()){
            if(s.getName().equals(hidenPoint[2])){
                System.out.println(hidenPoint[2]);
                s.getData().add(new XYChart.Data(x,y));
            }
            break;
        }
        

//        MainController.getPcaChart().getData().get(1).getData().add(new XYChart.Data(x,y));
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
