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
import org.h3abionet.genesis.model.Annotation;
import org.h3abionet.genesis.model.Project;

import java.awt.Toolkit;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.scene.paint.Paint;

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
    private Label arcSizeLabel;
    private ColorPicker cpStroke;
    private Slider widthSlider;
    private Slider heightSlider;
    private ComboBox stkWidth;
    private ComboBox arcSizeCombo;
    private Annotation rectangleAnnotation;
    private Project project;
    private Tab selectedTab;
    private boolean isDone;
    private boolean isDeleted;
    private MainController mainController;
    
    private static int dpi = java.awt.Toolkit.getDefaultToolkit().getScreenResolution();

    public static double mmToPixels (double mm) {
        return mm*dpi/25.4;
    }

    public static double pixelsToMM (double pixels) {
        return pixels/(dpi/25.4);
    }

    
    public RectangleOptions(Rectangle rectangle, Annotation rectangleAnnotation) {
        isDone = false;
        isDeleted = false;
        this.rectangle = rectangle;
        this.rectangleAnnotation = rectangleAnnotation;
        setControllers();
    }
    
    private void setControllers(){
        StrokeWidthLabel = new Label("Stroke Width");
        widthSizeLabel = new Label("Width: ");
        heightSizeLabel = new Label("Height: ");
        cpStrokeLabel = new Label("Stroke color: ");
        arcSizeLabel = new Label("Corner arc diameter (pixels): ");
        
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
        
        arcSizeCombo = new ComboBox(FXCollections.
                observableArrayList(IntStream.range(0, 41).
                        filter(x -> x % 8 == 0).boxed().collect(Collectors.toList())));
        arcSizeCombo.setValue(rectangle.getArcWidth()); //pixelsToMM(rectangle.getArcWidth()));
        
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
        grid.add(arcSizeLabel, 1, 5);
        grid.add(arcSizeCombo, 2, 5);
        
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
        });
        
        stkWidth.setOnAction(e -> {
            rectangle.setStrokeWidth((int) stkWidth.getValue());
        });
        
        arcSizeCombo.setOnAction(e ->{
            int pixels = 0;
            if (arcSizeCombo.getValue() != null)
                pixels = (int) arcSizeCombo.getValue(); // mmToPixels((int)arcSizeCombo.getValue());
            rectangle.setArcHeight(pixels);
            rectangle.setArcWidth(pixels);
        });
    }

        public static void rectangleToAnnotation(Annotation rectangleAnn, Rectangle rectangle) {
        rectangleAnn.setWidth(rectangle.getWidth());
        rectangleAnn.setHeight(rectangle.getHeight());
        rectangleAnn.setArcHeight(rectangle.getArcHeight());
        rectangleAnn.setArcWidth(rectangle.getArcWidth());
        rectangleAnn.setStrokeColor((Color)rectangle.getStroke());
        rectangleAnn.setStrokeWidth(rectangle.getStrokeWidth());
        rectangleAnn.setStartX(rectangle.getX());
        rectangleAnn.setStartY(rectangle.getY());
    }

    public static void annotationToRectangle(Rectangle rectangle, Annotation rectangleAnn) {
        rectangle.setX(rectangleAnn.getStartX());
        rectangle.setY(rectangleAnn.getStartY());
        rectangle.setWidth(rectangleAnn.getWidth());
        rectangle.setHeight(rectangleAnn.getHeight());
        rectangle.setArcHeight(rectangleAnn.getArcHeight());
        rectangle.setArcWidth(rectangleAnn.getArcWidth());
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Paint.valueOf(rectangleAnn.getStrokeColor()));
        rectangle.setStrokeWidth(rectangleAnn.getStrokeWidth());
        rectangle.setTranslateX(rectangleAnn.getTranslateX());
        rectangle.setTranslateY(rectangleAnn.getTranslateY());
    }


    public void modifyRectangle(){
        Dialog dialog = mainController.getDialog(grid);
        Optional<ButtonType> results = dialog.showAndWait();

        dialog.setOnCloseRequest(e->{
            if(isDone==false && isDeleted==false){
                e.consume();
            }
        });

        if (results.get() == mainController.getButtonType("Done")){
            // set annotations
            rectangleToAnnotation(rectangleAnnotation, rectangle);
            isDone = true;
        }

        if (results.get() == mainController.getButtonType("Delete")) {
            rectangle.setVisible(false);
            project.removeAnnotation(selectedTab, rectangleAnnotation);
            isDeleted = true;
        }

        if (results.get() == mainController.getButtonType("Cancel")) {
            // recover from annotation object
            annotationToRectangle(rectangle, rectangleAnnotation);
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
