/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;
import org.h3abionet.genesis.model.Annotation;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * @author Henry
 */
public class RectangleOptions{
    private Rectangle rectangle;
    private GridPane grid;
    
    // grid components or controllers
    private Label StrokeWidthLabel;
    private Label widthSizeLabel;
    private Label heightSizeLabel;
    private Label cpStrokeLabel;
    private Label archSizeLabel;
    private ColorPicker cpStroke;
    private Slider widthSlider;
    private Slider heightSlider;
    private ComboBox stkWidth;
    private ComboBox archSizeCombo;
    private Annotation rectangleAnnotation;

    public RectangleOptions(Rectangle rectangle, Annotation rectangleAnnotation) {
        this.rectangle = rectangle;
        this.rectangleAnnotation = rectangleAnnotation;
        setControllers();
    }
    
    private void setControllers(){
        StrokeWidthLabel = new Label("Stroke Width");
        widthSizeLabel = new Label("Width: ");
        heightSizeLabel = new Label("Height: ");
        cpStrokeLabel = new Label("Stroke color: ");
        archSizeLabel = new Label("Arch size: ");
        
        cpStroke = new ColorPicker((Color) rectangle.getStroke());
        cpStroke.getStyleClass().add("split-button");
        
        widthSlider = new Slider(5, 300, (int)rectangle.getWidth());
        widthSlider.setShowTickLabels(true);
        widthSlider.setShowTickMarks(true);
        
        heightSlider = new Slider(5, 300, (int)rectangle.getHeight());
        heightSlider.setShowTickLabels(true);
        heightSlider.setShowTickMarks(true);
        
        stkWidth = new ComboBox(FXCollections.
                        observableArrayList(IntStream.range(1, 11).boxed().collect(Collectors.toList())));
                stkWidth.setValue((int)rectangle.getStrokeWidth());
        
        archSizeCombo = new ComboBox(FXCollections.
                observableArrayList(IntStream.range(1, 11).boxed().collect(Collectors.toList())));
        archSizeCombo.setValue((int)rectangle.getArcWidth());
        
        // set the grid
        grid = new GridPane();
        grid.setVgap(5); 
        grid.setHgap(5);
        grid.setPadding(new Insets(10, 10, 10, 10)); 
        grid.setAlignment(Pos.CENTER);
        
        // add controllers to the grid
        grid.add(StrokeWidthLabel, 1, 1);
        grid.add(stkWidth, 2, 1);
        grid.add(widthSizeLabel, 1, 2);
        grid.add(widthSlider, 2, 2);
        grid.add(heightSizeLabel, 1, 3);
        grid.add(heightSlider, 2, 3);
        grid.add(cpStrokeLabel, 1, 4);
        grid.add(cpStroke, 2, 4);
        grid.add(archSizeLabel, 1, 5);
        grid.add(archSizeCombo, 2, 5);
        
        //add event handlers to the controllers
        widthSlider.valueProperty().addListener((ObservableValue <? extends Number >  
                observable, Number oldValue, Number newValue) -> {
                rectangle.setWidth((double) newValue); 
        });
        
        heightSlider.valueProperty().addListener((ObservableValue <? extends Number >  
                observable, Number oldValue, Number newValue) -> {
                rectangle.setHeight((double) newValue); 
        });
        
        cpStroke.setOnAction((ActionEvent e) -> {
            rectangle.setStroke(cpStroke.getValue());
            rectangleAnnotation.setStrokeColor(cpStroke.getValue());
        });
        
        stkWidth.setOnAction(e -> {
            rectangle.setStrokeWidth((int) stkWidth.getValue());
        });
        
        archSizeCombo.setOnAction(e ->{
            rectangle.setArcHeight((int)archSizeCombo.getValue());
            rectangle.setArcWidth((int)archSizeCombo.getValue());
        });
    }

    public void modifyRectangle(){
        Dialog<Options> dialog = new Dialog<>();
        dialog.setTitle("Rectangle options");
        dialog.setHeaderText(null);
        dialog.setResizable(false);

        dialog.getDialogPane().setContent(grid);

        ButtonType deleteBtn = new ButtonType("Delete", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType doneBtn = new ButtonType("Done", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().setAll(doneBtn, deleteBtn);

        dialog.setResultConverter(new Callback<>() {
            @Override
            public Options call(ButtonType b) {
                if (b == doneBtn) {
                    // set annotations
                    double strokeWidth = Double.parseDouble(stkWidth.getValue().toString());
                    double arc = Double.parseDouble(archSizeCombo.getValue().toString());

                    rectangleAnnotation.setWidth(rectangle.getWidth());
                    rectangleAnnotation.setHeight(rectangle.getHeight());
                    rectangleAnnotation.setArcHeight(rectangle.getArcHeight());
                    rectangleAnnotation.setArcWidth(rectangle.getArcWidth());
                    rectangleAnnotation.setStrokeWidth(rectangle.getStrokeWidth());
                    rectangleAnnotation.setStrokeColor(cpStroke.getValue());
                    return new Options(widthSlider.getValue(), heightSlider.getValue(),
                            arc, arc, strokeWidth, cpStroke.getValue());
                } else {
                    rectangle.setVisible(false);
                }

                return null;
            }
        });
        
        Optional<Options> results = dialog.showAndWait();
            results.ifPresent((Options options) -> {
            rectangle.setWidth(options.width);
            rectangle.setHeight(options.height);
            rectangle.setArcHeight(options.archHeight);
            rectangle.setArcWidth(options.archWidth);
            rectangle.setStrokeWidth(options.strokeWidth);
            rectangle.setStroke(options.strokeColor);
        });

    }

    private static class Options {
        double width;
        double height;
        double archWidth;
        double archHeight;
        double strokeWidth;
        Color strokeColor;

        public Options(double width, double height, double archWidth, double archHeight, double strokeWidth,Color strokeColor) {
            this.width = width;
            this.height = height;
            this.archWidth = archWidth;
            this.archHeight = archHeight;
            this.strokeWidth = strokeWidth;
            this.strokeColor = strokeColor;
        }
    }
}
