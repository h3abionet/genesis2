/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import java.io.Serializable;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Callback;
import org.h3abionet.genesis.model.Annotation;

/**
 *
 * @author Henry
 */
public class CircleOptions implements Serializable{

    private static final long serialVersionUID = 2L;

    private Circle circle;
    private GridPane grid;
    
    // grid components or controllers
    private Label StrokeWidthLabel;
    private Label radiusSizeLabel;
    private Label cpStrokeLabel;
    private Label cpFillLabel;
    private ColorPicker cpStroke;
//    private ColorPicker cpFill;
    private Slider slider;
    private ComboBox stkWidth;
    private Annotation annotation;

    public CircleOptions(Circle circle, Annotation circleAnn) {
        this.circle = circle;
        this.annotation = circleAnn;
        setControllers();
    }
    
    private void setControllers(){
        StrokeWidthLabel = new Label("Stroke Width");
        radiusSizeLabel = new Label("Radius size: ");
        cpStrokeLabel = new Label("Stroke color: ");
//        cpFillLabel = new Label("Fill color: ");

        cpStroke = new ColorPicker((Color) circle.getStroke());
        cpStroke.getStyleClass().add("split-button");
//
//        cpFill = new ColorPicker((Color) circle.getFill());
//        cpFill.getStyleClass().add("split-button");
        
        slider = new Slider(5, 300, (int)circle.getRadius());
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        
        stkWidth = new ComboBox(FXCollections.
                        observableArrayList(IntStream.range(1, 11).boxed().collect(Collectors.toList())));
                stkWidth.setValue((int)circle.getStrokeWidth());
        
        // set the grid
        grid = new GridPane();
        grid.setVgap(5); 
        grid.setHgap(5);
        grid.setPadding(new Insets(10, 10, 10, 10)); 
        grid.setAlignment(Pos.CENTER);
        
        // add controllers to the grid
        grid.add(StrokeWidthLabel, 1, 1);
        grid.add(stkWidth, 2, 1);
        grid.add(radiusSizeLabel, 1, 2);
        grid.add(slider, 2, 2);
        grid.add(cpStrokeLabel, 1, 3);
        grid.add(cpStroke, 2, 3);
//        grid.add(cpFillLabel, 1, 4);
//        grid.add(cpFill, 2, 4);
        
        //add event handlers to the controllers
        slider.valueProperty().addListener((ObservableValue <? extends Number >  
                observable, Number oldValue, Number newValue) -> {
                circle.setRadius((double) newValue); 
        });
        
        cpStroke.setOnAction((ActionEvent e) -> {
            circle.setStroke(cpStroke.getValue());
        });
        
//        cpFill.setOnAction((ActionEvent e) -> {
//            circle.setFill(cpFill.getValue());
//
//        });
        
        stkWidth.setOnAction(e -> {
            circle.setStrokeWidth((int) stkWidth.getValue());
        });
    }

    public void modifyCircle(){
        Dialog<Options> dialog = new Dialog<>();
        dialog.setTitle("Circle options");
        dialog.setHeaderText(null);
        dialog.setResizable(false);

        dialog.getDialogPane().setContent(grid);

        ButtonType deleteBtn = new ButtonType("Delete", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType doneBtn = new ButtonType("Done", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().setAll(doneBtn, deleteBtn);

        dialog.setResultConverter(b -> {
            if (b == doneBtn) {
                // set annotation values
                annotation.setRadius((int) slider.getValue());
                annotation.setStrokeWidth( (int) stkWidth.getValue());
                annotation.setStrokeColor(Integer.toHexString(cpStroke.getValue().hashCode()));
//                annotation.setFill(Integer.toHexString(cpFill.getValue().hashCode()));
                return new Options((int) slider.getValue(), (int) stkWidth.getValue(), cpStroke.getValue());
            } else {
                circle.setVisible(false);
            }
            return null;
        });
        
        Optional<Options> results = dialog.showAndWait();
            results.ifPresent((Options options) -> {
            circle.setRadius(options.radius);
            circle.setStrokeWidth(options.strokeWidth);
            circle.setStroke(options.strokeColor);
//            circle.setFill(options.fillColor);
        });

    }

    private static class Options {
        int radius;
        int strokeWidth;
        Color strokeColor;
//        Color fillColor;

        public Options(int radius, int strokeWidth, Color strokeColor) {
            this.radius = radius;
            this.strokeWidth = strokeWidth;
            this.strokeColor = strokeColor;
//            this.fillColor = fillColor;
        }
    }
}
