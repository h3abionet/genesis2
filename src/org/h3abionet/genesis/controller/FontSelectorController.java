/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
/**
 *
 * @author Henry
 */
public class FontSelectorController implements Initializable{
     @FXML
    private ListView<String> fontList;

    @FXML
    private ListView<String> styleList;

    @FXML
    private ListView<Integer> sizeList;

    @FXML
    private Label sample;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private Button btnOK;

    @FXML
    private Button btnCancel;
    
    private String chosenFont = "System";
    private String chosenFontStyle = "NORMAL";
    private String chosenFontColor = "BLACK";
    private int chosenFontSize = 12;
    
    // font weight names 
    String weights[] = {"BLACK", "BOLD",  
                        "EXTRA_BOLD",  
                        "EXTRA_LIGHT",  
                        "LIGHT",  
                        "MEDIUM",  
                        "NORMAL",  
                        "SEMI_BOLD", 
                        "THIN"};
    
    // list of font sizes
    List<Integer> list = IntStream.range(8, 72).boxed().collect(Collectors.toList());
    
    @FXML
    private void entryOkButton(ActionEvent event) {     
        System.out.println(chosenFontStyle+" "+chosenFont+" "+chosenFontSize+" "+chosenFontColor);
        
        closeStage(event);
    }
    
    @FXML
    private void entryCancelButton(ActionEvent event) {
        closeStage(event);
    }
    
    public void closeStage(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colorPicker.setValue(Color.BLACK);
        styleList.setItems(FXCollections.observableArrayList(weights));
        fontList.setItems(FXCollections.observableArrayList(Font.getFamilies()));
        sizeList.setItems(FXCollections.observableList(list));
        
        styleList.setOnMouseClicked((MouseEvent event) -> {
            chosenFontStyle = styleList.getSelectionModel().getSelectedItem();
            sample.setFont(Font.font(chosenFont, FontWeight.valueOf(chosenFontStyle), chosenFontSize));
            chosenFontColor = Integer.toHexString(colorPicker.getValue().hashCode());
            sample.setTextFill(colorPicker.getValue());
        });
        
        fontList.setOnMouseClicked((MouseEvent event) -> {
            chosenFont = fontList.getSelectionModel().getSelectedItem();
            sample.setFont(Font.font(chosenFont, FontWeight.valueOf(chosenFontStyle), chosenFontSize));
            chosenFontColor = Integer.toHexString(colorPicker.getValue().hashCode());
            sample.setTextFill(colorPicker.getValue());

        });
        
        sizeList.setOnMouseClicked((MouseEvent event) -> {
            chosenFontSize = sizeList.getSelectionModel().getSelectedItem();
            sample.setFont(Font.font(chosenFont, FontWeight.valueOf(chosenFontStyle), chosenFontSize));
            chosenFontColor = Integer.toHexString(colorPicker.getValue().hashCode());
            sample.setTextFill(colorPicker.getValue());

        });
        
    }

}
