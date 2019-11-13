/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.model;

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
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Callback;

/**
 *
 * @author Henry
 */
public class CircleOptions {
    
    Circle circle;

    public CircleOptions(Circle circle) {
        this.circle = circle;
    }
    
    public void modifyCircle(){
        Dialog<Options> dialog = new Dialog<>();
        dialog.setTitle("Label options");
        dialog.setHeaderText(null);
        dialog.setResizable(false);
        
        Label radiusSize = new Label("radiusSize");
        Label cpLabel = new Label("Stroke color: ");
        
        Circle cir = new Circle();
        cir.setRadius(50);
        cir.setFill(Color.TRANSPARENT);
        cir.setStroke(Color.BLACK);
        
        AnchorPane pane = new AnchorPane();
        pane.getChildren().add(cir);
        
        ColorPicker cp = new ColorPicker((Color) circle.getStroke());
        cp.getStyleClass().add("split-button");
        
        Slider slider = new Slider(5, 200, (int)circle.getRadius());
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        
        ComboBox stkWidth = new ComboBox(FXCollections.
                        observableArrayList(IntStream.range(1, 11).boxed().collect(Collectors.toList())));
                stkWidth.setValue((int)circle.getStrokeWidth());

        GridPane grid = new GridPane();
        grid.setVgap(5); 
        grid.setHgap(5);
        grid.setPadding(new Insets(10, 10, 10, 10)); 
        grid.setAlignment(Pos.CENTER); 

        grid.add(stkWidth, 1, 1);
        grid.add(pane, 2, 1);
        grid.add(radiusSize, 1, 2);
        grid.add(slider, 2, 2);
        grid.add(cpLabel, 1, 3);
        grid.add(cp, 2, 3);
        dialog.getDialogPane().setContent(grid);

        ButtonType deleteBtn = new ButtonType("Delete", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType doneBtn = new ButtonType("Done", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().setAll(doneBtn, deleteBtn);

        dialog.setResultConverter(new Callback<ButtonType, Options>() {
            @Override
            public Options call(ButtonType b) {                      
                if (b == doneBtn) {        
                    return new Options((int)slider.getValue(), (int) stkWidth.getValue(), cp.getValue());
                }else{
//                    circle.setText("");
                }

                return null;
            }
        });    
        Optional<Options> results = dialog.showAndWait();
            results.ifPresent((Options options) -> {
            circle.setRadius(options.radius);
            circle.setStrokeWidth(options.strokeWidth);
            circle.setStroke(options.strokeColor);
        });
    
    }
    
    
    private static class Options {
        int radius;
        int strokeWidth;
        Color strokeColor;

        public Options(int radius, int strokeWidth, Color strokeColor) {
            this.radius = radius;
            this.strokeWidth = strokeWidth;
            this.strokeColor = strokeColor;
        }
    }
}
