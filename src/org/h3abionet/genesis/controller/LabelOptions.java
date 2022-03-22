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
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.h3abionet.genesis.model.Annotation;

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

    public LabelOptions(Text text, Annotation textAnnotation) {
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
                Dialog<Options> dialog = new Dialog<>();
                dialog.setTitle("Label options");
                dialog.setHeaderText(null);
                dialog.setResizable(false);

                dialog.getDialogPane().setContent(grid);
                
                ButtonType deleteBtn = new ButtonType("Delete", ButtonBar.ButtonData.CANCEL_CLOSE);
                ButtonType doneBtn = new ButtonType("Done", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().setAll(doneBtn, deleteBtn);
                
                dialog.setResultConverter(new Callback<ButtonType, Options>() {
                    @Override
                    public Options call(ButtonType b) {                      
                        if (b == doneBtn) {
                            // annotations
                            textAnnotation.setStartX(text.getX());
                            textAnnotation.setStartY(text.getY());
                            textAnnotation.setText(text.getText());
                            textAnnotation.setFill(cp.getValue());
                            textAnnotation.setFontFamily(String.valueOf(fontCombo.getValue()));
                            textAnnotation.setFontSize((int) sizeCombo.getValue());
                            textAnnotation.setFontWeight(String.valueOf(weightCombo.getValue()));

                            return new Options(textField.getText(), String.valueOf(fontCombo.getValue()), 
                                    (int) sizeCombo.getValue(), String.valueOf(weightCombo.getValue()), cp.getValue());
                        }else{
                            text.setText("");
                        }
                        
                        return null;
                    }
                });    
                Optional<Options> results = dialog.showAndWait();
                    results.ifPresent((Options options) -> {
                    text.setText(options.lbl);
                    text.setFill(options.color);
                    text.setFont(Font.font(options.fontFamily, options.fontSize));
                    
                });
            }

    private static class Options {
        String lbl;
        String fontFamily;
        int fontSize;
        Color color;
        String fontWeight;

        public Options(String lbl, String fontFamily, int fontSize, String fontWeight, Color color) {
            this.lbl = lbl;
            this.fontFamily = fontFamily;
            this.fontSize = fontSize;
            this.fontWeight = fontWeight;
            this.color = color;
        }
    }
}
