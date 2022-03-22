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
import javafx.geometry.Bounds;
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
    private ColorPicker cpStroke;
    private Slider slider;
    private ComboBox stkWidth;
    private Annotation circleAnnotation;

    public CircleOptions(Circle circle, Annotation circleAnn) {
        this.circle = circle;
        this.circleAnnotation = circleAnn;
        setControllers();
    }
    
    private void setControllers(){
        StrokeWidthLabel = new Label("Stroke Width");
        radiusSizeLabel = new Label("Radius size: ");
        cpStrokeLabel = new Label("Stroke color: ");

        cpStroke = new ColorPicker((Color) circle.getStroke());
        cpStroke.getStyleClass().add("split-button");
        
        slider = new Slider(5, 300, circle.getRadius());
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        
        stkWidth = new ComboBox(FXCollections.
                        observableArrayList(IntStream.range(1, 11).boxed().collect(Collectors.toList())));
        stkWidth.setValue(circle.getStrokeWidth());

        stkWidth.setOnAction(e -> {
            circle.setStrokeWidth((int)stkWidth.getValue());
        });
        
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
        
        //add event handlers to the controllers
        slider.valueProperty().addListener((ObservableValue <? extends Number >  
                observable, Number oldValue, Number newValue) -> {
                circle.setRadius((double) newValue); 
        });
        
        cpStroke.setOnAction((ActionEvent e) -> {
            circle.setStroke(cpStroke.getValue());
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
                double strokeWidth = Double.parseDouble(stkWidth.getValue().toString());
                circleAnnotation.setRadius((double) slider.getValue());
                circleAnnotation.setStrokeWidth(strokeWidth);
                circleAnnotation.setStrokeColor(cpStroke.getValue());

                return new Options((double) slider.getValue(), strokeWidth, cpStroke.getValue());
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
        });

    }

    private static class Options {
        double radius;
        double strokeWidth;
        Color strokeColor;

        public Options(double radius, double strokeWidth, Color strokeColor) {
            this.radius = radius;
            this.strokeWidth = strokeWidth;
            this.strokeColor = strokeColor;
        }
    }
}
