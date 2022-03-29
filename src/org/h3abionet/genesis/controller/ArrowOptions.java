package org.h3abionet.genesis.controller;

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

import java.util.Optional;

public class ArrowOptions {

    private final Arrow arrow;
    GridPane grid;
    private Label StrokeWidthLabel;
    private Slider rotationSlider;
    private Slider arrowLengthSlider;
    private Label adjustArrowLengthLbl;
    private Annotation arrowAnnotation;
    private double angleOfRotation;
    private Project project;
    private Tab selectedTab;
    private boolean isDone;
    private boolean isDeleted;
    private MainController mainController;

    public ArrowOptions(Arrow arrow, Annotation arrowAnnotation) {
        isDone = false;
        isDeleted = false;
        this.arrow = arrow;
        this.arrowAnnotation = arrowAnnotation;
        setControllers();
    }

    public void setAngleOfRotation(double angleOfRotation) {
        this.angleOfRotation = angleOfRotation;
    }

    private void setControllers() {
        adjustArrowLengthLbl = new Label("Adjust Arrow Length");
        arrowLengthSlider = new Slider(50, 360, 5);
        arrowLengthSlider.setShowTickLabels(true);
        arrowLengthSlider.setShowTickMarks(true);
        arrowLengthSlider.setOrientation(Orientation.HORIZONTAL);
        StrokeWidthLabel = new Label("Stroke Width");

        rotationSlider = new Slider(-180, 180, 10);
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
        grid.add(new Label("Rotation Slider"), 0,1);
        grid.add(rotationSlider,1,1);
        grid.add(adjustArrowLengthLbl,0,2);
        grid.add(arrowLengthSlider,1,2);


        // event on the slider
        //creating the rotation transformation
        Rotate rotate = new Rotate();
        //Setting pivot points for the rotation
        rotate.setPivotX(arrow.getEndX());
        rotate.setPivotY(arrow.getEndY());

        //Adding the transformation to rectangle
//        arrow.getTransforms().addAll(rotate);

        //Linking the transformation to the slider
        rotationSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<?extends Number> observable, Number oldValue, Number newValue){
                //Setting the angle for the rotation
                rotate.setAngle((double) newValue);
                setAngleOfRotation((double) newValue);
            }
        });

        //Adding the transformation to the circle
        arrow.getTransforms().add(rotate);

        arrowLengthSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<?extends Number> observable, Number oldValue, Number newValue){
                //Setting the angle for the rotation
                double  reduction = (double) newValue;
                arrow.setStartX(arrow.getEndX()-reduction);
            }
        });
    }

    public void modifyArrow(){
        Dialog dialog = mainController.getDialog(grid);
        Optional<ButtonType> results = dialog.showAndWait();

        dialog.setOnCloseRequest(e->{
            if(isDone==false && isDeleted==false){
                e.consume();
            }
        });

        if (results.get() == mainController.getButtonType("Done")){
//            // store the properties of the annotation
            arrowAnnotation.setStartX(arrow.getStartX());
            arrowAnnotation.setStartY(arrow.getStartY());
            arrowAnnotation.setEndX(arrow.getEndX());
            arrowAnnotation.setEndY(arrow.getEndY());
            arrowAnnotation.setRotation(angleOfRotation);
            arrowAnnotation.setLayoutX(arrow.getBoundsInParent().getMinX());
            arrowAnnotation.setLayoutY(arrow.getBoundsInParent().getMinY());
//            arrowAnnotation.setStrokeColor(Integer.toHexString(cpStroke.getValue().hashCode()));
            isDone = true;
        }

        if (results.get() == mainController.getButtonType("Delete")) {
            arrow.setVisible(false);
            project.revomeAnnotation(selectedTab, arrowAnnotation);
            isDeleted = true;
        }

        if (results.get() == mainController.getButtonType("Cancel")) {
            return;
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
