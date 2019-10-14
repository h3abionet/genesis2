/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 *
 * @author Henry
 */
public class IconOptionsController implements Initializable{
    
    private IndividualDetailsController individualDetailsController; 
            
    @FXML
    private Spinner iconType;

    @FXML
    private Spinner iconSize;

    @FXML
    private ImageView iconDisplay;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private Button btnOK;
    
    @FXML
    private void entryOKBtn(ActionEvent event){
        individualDetailsController.setIconSize(iconSize.getValue().toString());
//        individualDetailsController.setIconType(iconType.toString());
        individualDetailsController.setIconColor(Integer.toHexString(colorPicker.getValue().hashCode()));
//        System.out.println(colorPicker.toString());
//        System.out.println(iconSize.toString());
        closeStage(event);
    }
    

    @FXML
    private Button btnCancel;
    
    @FXML
    private void entryCancelBtn(ActionEvent event){
        closeStage(event);
        
    }
    
    
    public void closeStage(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SpinnerValueFactory<Integer> iconSizes = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,30,5);
        iconSize.setValueFactory(iconSizes);
        iconSize.setEditable(true);
        individualDetailsController =  new IndividualDetailsController();
    }
    
    
    

}
