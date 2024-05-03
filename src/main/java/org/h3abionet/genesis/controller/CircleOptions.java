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
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import org.h3abionet.genesis.model.Annotation;
import org.h3abionet.genesis.model.Project;

/**
 *
 * @author Henry
 */
public class CircleOptions implements Serializable{

    private static final long serialVersionUID = 2L;
    private boolean isDeleted;
    private boolean isDone;

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
    private Project project;
    private Tab selectedTab;
    private MainController mainController;

    public CircleOptions(Circle circle, Annotation circleAnn) {
        isDone = false;
        isDeleted = false;
        this.circle = circle;
        this.circleAnnotation = circleAnn;
        setControllers();
    }
    
    public static void printCircle (String prefix, Annotation circleAnn) {
    System.out.print(" Centre X, Y: "+circleAnn.getCenterX()+", "
            +circleAnn.getCenterY()+"; radius: "
            +circleAnn.getRadius()+"; translate X,y: "
            +circleAnn.getTranslateX()+","+circleAnn.getTranslateY());
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
    
    public static void circleToAnnotation(Annotation circleAnn, Circle circle) {
        circleAnn.setCenterX(circle.getCenterX());
        circleAnn.setCenterY(circle.getCenterY());
        //circleAnn.setFill((Color) circle.getFill()); // transparent doesn't translate easily
        circleAnn.setRadius(circle.getRadius());
        circleAnn.setStrokeColor((Color)circle.getStroke());
        circleAnn.setStrokeWidth(circle.getStrokeWidth());
//        circleAnn.setTranslateX(circle.getTranslateX());
//        circleAnn.setTranslateY(circle.getTranslateY());
//        System.out.println("Setting translate in annotation X,Y="
//                +circleAnn.getTranslateX()+","
//                +circleAnn.getTranslateY());
//
    }

    public static void annotationToCircle(Circle circle, Annotation circleAnn,
            boolean setTranslate) {
        circle.setCenterX(circleAnn.getCenterX());
        circle.setCenterY(circleAnn.getCenterY());
        circle.setFill(Color.TRANSPARENT);
        circle.setRadius(circleAnn.getRadius());
        circle.setStroke(Paint.valueOf(circleAnn.getStrokeColor()));
        circle.setStrokeWidth(circleAnn.getStrokeWidth());
        if (setTranslate) {
            circle.setTranslateX(circleAnn.getTranslateX());
            circle.setTranslateY(circleAnn.getTranslateY());
        }
    }


    public void modifyCircle(){
        circleToAnnotation (circleAnnotation, circle);
        Dialog dialog = mainController.getDialog(grid);
        Optional<ButtonType> results = dialog.showAndWait();

        dialog.setOnCloseRequest(e->{
            if(isDone==false && isDeleted==false){
                e.consume();
            }
        });

        if (results.get() == mainController.getButtonType("Done")){
            // set annotations
            circleToAnnotation (circleAnnotation, circle);
            isDone = true;
        }

        if (results.get() == mainController.getButtonType("Delete")) {
            circle.setVisible(false);
            project.removeAnnotation(selectedTab, circleAnnotation);
            isDeleted = true;
        }

        if (results.get() == mainController.getButtonType("Cancel")) {
            annotationToCircle (circle, circleAnnotation, false);
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
