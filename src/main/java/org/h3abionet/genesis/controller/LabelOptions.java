/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.h3abionet.genesis.model.Annotation;
import org.h3abionet.genesis.model.Project;

/**
 *
 * @author Henry
 */
public class LabelOptions{
    Text text;
    GridPane grid;
    Label label;
    Label cpLabel;
    TextField textField;
    ColorPicker cp;
    ComboBox fontCombo;
    ComboBox sizeCombo;
    ComboBox weightCombo;
    Label weightComboLbl;

    private Annotation textAnnotation;
    private Project project;
    private Tab selectedTab;
    private boolean isDone;
    private boolean isDeleted;
    private MainController mainController;

    public LabelOptions(Text text, Annotation textAnnotation) {
        isDone = false;
        isDeleted = false;
        this.text = text;
        this.textAnnotation = textAnnotation;
        setControllers();
    }
    
    private void setControllers() {
        label = new Label("Label: ");
        cpLabel = new Label("Label color: ");
        textField = new TextField(text.getText());
        cp = new ColorPicker((Color) text.getFill());
        cp.getStyleClass().add("split-button");

        weightComboLbl = new Label("Font weight: ");
        String weight[] = {"EXTRA_BOLD","NORMAL"};
        weightCombo = new ComboBox(FXCollections.observableArrayList(weight));
        weightCombo.setValue(textAnnotation.getFontWeight());

        fontCombo = new ComboBox(FXCollections.observableArrayList(Font.getFamilies()));
        fontCombo.setValue(text.getFont().getFamily());

        sizeCombo = new ComboBox(FXCollections.
                observableArrayList(IntStream.range(8, 73).boxed().collect(Collectors.toList())));
        sizeCombo.setValue((int)text.getFont().getSize());

        grid = new GridPane();
        grid.setVgap(5); 
        grid.setHgap(5);
        grid.setPadding(new Insets(10, 10, 10, 10)); 
        grid.setAlignment(Pos.CENTER); 

        grid.add(label, 1, 1);
        grid.add(textField, 2, 1);
        grid.add(fontCombo, 1, 2);
        grid.add(sizeCombo, 2, 2);
        grid.add(cpLabel, 1, 3);
        grid.add(cp, 2, 3);
        grid.add(weightComboLbl,1,4);
        grid.add(weightCombo,2,4);
        
        fontCombo.setOnAction(e ->{
            text.setFont(Font.font(String.valueOf(fontCombo.getValue()), FontWeight.valueOf((String)weightCombo.getValue()),(int)sizeCombo.getValue()));
        });
        
        sizeCombo.setOnAction(e ->{
            text.setFont(Font.font(String.valueOf(fontCombo.getValue()), FontWeight.valueOf((String)weightCombo.getValue()),(int)sizeCombo.getValue()));
        });
        
        cp.setOnAction(e ->{
            text.setFill(cp.getValue());
        });
        
    }
    
    public void modifyLabel(){
        Dialog dialog = mainController.getDialog(grid);
        Optional<ButtonType> results = dialog.showAndWait();

        dialog.setOnCloseRequest(e->{
            if(isDone==false && isDeleted==false){
                e.consume();
            }
        });

        if (results.get() == mainController.getButtonType("Done")){
            // set annotations -- 
            textAnnotation.setStartX(text.getX());
            textAnnotation.setStartY(text.getY());
            text.setText(textField.getText()); // all were textAnnotation FIXME
            textAnnotation.setFill(cp.getValue());
            textAnnotation.setFontFamily(String.valueOf(fontCombo.getValue()));
            textAnnotation.setFontSize((int) sizeCombo.getValue());
            textAnnotation.setFontWeight(String.valueOf(weightCombo.getValue()));
            textAnnotation.setLayoutX(text.getBoundsInParent().getCenterX()-20);//error margin
            textAnnotation.setLayoutY(text.getBoundsInParent().getCenterY());
            isDone = true;
            System.out.println("Label modify Done, setting text to "+textField.getText());
            System.out.println("Label text now "+text.getText());
        }

        if (results.get() == mainController.getButtonType("Delete")) {
            text.setVisible(false);
            project.revomeAnnotation(selectedTab, textAnnotation);
            isDeleted = true;
        }

        if (results.get() == mainController.getButtonType("Cancel")) {
            return;
        }
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setSelectedTab(Tab selectedTab) {
        this.selectedTab = selectedTab;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
