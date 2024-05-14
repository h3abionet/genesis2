package org.h3abionet.genesis.controller;

import java.io.Serializable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.transform.Rotate;
import org.h3abionet.genesis.model.Annotation;
import org.h3abionet.genesis.model.Project;
import java.io.Serializable;
import java.util.ArrayList;

import java.util.Optional;

public class ArrowOptions implements Serializable {

    private final Arrow arrow;
    GridPane grid;
    private Label StrokeWidthLabel;
    private Slider rotationSlider;
    private Slider arrowLengthSlider;
    private Label adjustArrowLengthLbl;
    private Annotation arrowAnnotation;
    private Project project;
    private Tab selectedTab;
    private boolean isDone;
    private boolean isDeleted;
    private MainController mainController;
    private Rotate rotate;

    public ArrowOptions(Arrow arrow, Annotation arrowAnnotation) {
        isDone = false;
        isDeleted = false;
        this.arrow = arrow;
        this.arrowAnnotation = arrowAnnotation;
        rotate = null;

        setControllers();
    }

    public static void arrowToAnnotation(Annotation arrowAnnotation, Arrow arrow) {
        arrowAnnotation.setStartX(arrow.getStartX());
        arrowAnnotation.setStartY(arrow.getStartY());
        arrowAnnotation.setEndX(arrow.getEndX());
        arrowAnnotation.setEndY(arrow.getEndY());
    }

    public static void annotationToArrow(Arrow arrow, Annotation arrowAnnotation,
            boolean setRotate, boolean setTranslate) {
        if (setRotate) {
            Rotate rotate = new Rotate();
            rotate.setPivotX(arrowAnnotation.getEndX());
            rotate.setPivotY(arrowAnnotation.getEndY());
            rotate.setAngle(arrowAnnotation.getRotation());
            arrow.getTransforms().add(rotate);
        }
        arrow.setStartX(arrowAnnotation.getStartX());
        arrow.setStartY(arrowAnnotation.getStartY());
        arrow.setEndX(arrowAnnotation.getEndX());
        arrow.setEndY(arrowAnnotation.getEndY());
        if (setTranslate) {
        arrow.setTranslateX(arrowAnnotation.getTranslateX());
            arrow.setTranslateY(arrowAnnotation.getTranslateY());
        }
    }

    private void setControllers() {
        adjustArrowLengthLbl = new Label("Adjust Arrow Length");
        arrowLengthSlider = new Slider(50, 360, 5);
        arrowLengthSlider.setShowTickLabels(true);
        arrowLengthSlider.setShowTickMarks(true);
        arrowLengthSlider.setOrientation(Orientation.HORIZONTAL);
        
        // not implemented
        StrokeWidthLabel = new Label("Stroke Width");

        rotationSlider = new Slider(-180, 180, arrowAnnotation.getRotation());
        rotationSlider.setShowTickLabels(true);
        rotationSlider.setShowTickMarks(true);
        rotationSlider.setOrientation(Orientation.HORIZONTAL);

        // controls
        // set the grid
        grid = new GridPane();
        grid.setVgap(5);
        grid.setHgap(5);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setAlignment(Pos.CENTER);

        // add controllers to the grid
        grid.add(new Label("Arrow options"), 0, 0);
        grid.add(new Label("Rotation Slider"), 0, 1);
        grid.add(rotationSlider, 1, 1);
        grid.add(adjustArrowLengthLbl, 0, 2);
        grid.add(arrowLengthSlider, 1, 2);

        // event on the slider
        //creating the rotation transformation
        rotate = new Rotate();
        //Setting pivot points for the rotation
        rotate.setPivotX(arrow.getEndX());
        rotate.setPivotY(arrow.getEndY());
        arrow.getTransforms().add(rotate);
        // each rotate adds to previous
        // add to arrowAnnotation if Done

        //Adding the transformation to rectangle
//        arrow.getTransforms().addAll(rotate);
        //Linking the transformation to the slider
        rotationSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                //Setting the angle for the rotation
                rotate.setAngle((double) newValue-arrowAnnotation.getRotation());
            }
        });

        //Adding the transformation to the arrow
        //arrow.getTransforms().add(rotate);

        arrowLengthSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                //Setting the angle for the rotation
                double reduction = (double) newValue;
                arrow.setStartX(arrow.getEndX() - reduction);
            }
        });
    }

    public void modifyArrow() {
        int index = 0;
        String chartType = "admix";
        if (selectedTab.getId().contains("admix")) {
            index = project.getAdmixtureAnnotationsList().indexOf(arrowAnnotation);
        }
        // if pca tab - replace annotation
        if (selectedTab.getId().contains("tab")) {
            chartType = "admix";
            String[] s = selectedTab.getId().split(" "); // [pca, 0] or [pca, 11]
            int tabIndex = Integer.valueOf(s[1]);
            ArrayList<Annotation> list = project.getPcGraphAnnotationsList().get(tabIndex);
            index = list.indexOf(arrowAnnotation);
        }

        Dialog dialog = mainController.getDialog(grid);
        
        Optional<ButtonType> results = dialog.showAndWait();

        dialog.setOnCloseRequest(e -> {
            if (isDone == false && isDeleted == false) {
                e.consume();
            }
        });

        if (results.get() == mainController.getButtonType("Done")) {
            arrowToAnnotation(arrowAnnotation, arrow);
            arrowAnnotation.setRotation(rotate.getAngle()+arrowAnnotation.getRotation());
            arrowAnnotation.setLayoutX(arrow.getBoundsInParent().getMinX());
            arrowAnnotation.setLayoutY(arrow.getBoundsInParent().getMinY());
            isDone = true;
        }

        if (results.get() == mainController.getButtonType("Delete")) {
            arrow.setVisible(false);
            project.removeAnnotation(selectedTab, arrowAnnotation);
            isDeleted = true;
        }

        if (results.get() == mainController.getButtonType("Cancel")) {
            annotationToArrow (arrow, arrowAnnotation, false, false);
            if (rotate != null) {
                arrow.getTransforms().remove(rotate);
            }

        }

    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void selectedTab(Tab selectedTab) {
        this.selectedTab = selectedTab;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
