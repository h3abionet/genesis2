package org.h3abionet.genesis.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import org.h3abionet.genesis.model.Annotation;
import org.h3abionet.genesis.model.Project;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.geometry.Point2D;
import javafx.scene.paint.Paint;

public class LineOptions extends Line {

    private final Line line;
    private boolean isDeleted;
    private boolean isDone;
    GridPane grid;
    private Label StrokeWidthLabel;
    private Slider rotationSlider;
    private Label cpStrokeLabel;
    private ColorPicker cpStroke;
    private ComboBox stkWidth;
    private Slider lineLengthSlider;
    private Label adjustLineLengthLbl;
    private Annotation lineAnnotation;
    private double angleOfRotation = 0.0;
    private Tab selectedTab;
    private Project project;
    private MainController mainController;
    private Rotate rotate;
    private Point2D oldPivot = null;

    private double oldStartX, oldStartY, oldEndX, oldEndy;

    public LineOptions(Line line, Annotation lineAnnotation) {
        isDone = false;
        isDeleted = false;
        this.line = line;
        // the original in case we need to revert (Cancel)
        // and update it once changes are set
        this.lineAnnotation = lineAnnotation;
        rotate = null;

        setControllers();
    }
    
    private static void printLine(String s, Line l) {
        System.out.println(s + "Start: " + l.getStartX() + "," + l.getStartY()
                + " End: " + l.getEndX() + "," + l.getEndY()
        + "translate x,y" + l.getTranslateX()+","+l.getTranslateY());
    }


    public static void lineToAnnotation(Annotation lineAnnotation, Line line) {
        lineAnnotation.setStartX(line.getStartX());
        lineAnnotation.setStartY(line.getStartY());
        lineAnnotation.setEndX(line.getEndX());
        lineAnnotation.setEndY(line.getEndY());
        lineAnnotation.setStrokeWidth(line.getStrokeWidth());
        lineAnnotation.setStrokeColor((Color) line.getStroke());
    }

    public static void annotationToLine(Line line, Annotation lineAnnotation,
            boolean setRotate, boolean setTranslate) {
        if (setRotate) {
            Rotate rotate = new Rotate();
            rotate.setPivotX(lineAnnotation.getEndX());
            rotate.setPivotY(lineAnnotation.getEndY());
            rotate.setAngle(lineAnnotation.getRotation());
            line.getTransforms().add(rotate);
        }
        line.setStartX(lineAnnotation.getStartX());
        line.setStartY(lineAnnotation.getStartY());
        line.setEndX(lineAnnotation.getEndX());
        line.setEndY(lineAnnotation.getEndY());
        line.setStroke(Color.web(lineAnnotation.getStrokeColor()));
        line.setStrokeWidth(lineAnnotation.getStrokeWidth());
        if (setTranslate) {
            line.setTranslateX(lineAnnotation.getTranslateX());
            line.setTranslateY(lineAnnotation.getTranslateY());
        }
    }

    public void setAngleOfRotation(double angleOfRotation) {
        this.angleOfRotation += angleOfRotation;
    }

    private void setControllers() {
        adjustLineLengthLbl = new Label("Adjust Line length");
        StrokeWidthLabel = new Label("Stroke Width");
        cpStrokeLabel = new Label("Stroke color: ");
        StrokeWidthLabel = new Label("Stroke Width");

        cpStroke = new ColorPicker((Color) line.getStroke());
        cpStroke.getStyleClass().add("split-button");

        stkWidth = new ComboBox(FXCollections.
                observableArrayList(IntStream.range(1, 11).boxed().collect(Collectors.toList())));
        stkWidth.setValue((int) line.getStrokeWidth());

        rotationSlider = new Slider(-180, 180, lineAnnotation.getRotation());
        rotationSlider.setShowTickLabels(true);
        rotationSlider.setShowTickMarks(true);
        rotationSlider.setOrientation(Orientation.HORIZONTAL);

        lineLengthSlider = new Slider(30, 360, 10);
        lineLengthSlider.setShowTickLabels(true);
        lineLengthSlider.setShowTickMarks(true);
        lineLengthSlider.setOrientation(Orientation.HORIZONTAL);

        // controls
        // set the grid
        grid = new GridPane();
        grid.setVgap(5);
        grid.setHgap(5);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setAlignment(Pos.CENTER);

        // add controllers to the grid
        grid.add(new Label("Line options"), 0, 0);
        grid.add(new Label("Rotation Slider"), 0, 1);
        grid.add(rotationSlider, 1, 1);

        grid.add(StrokeWidthLabel, 0, 2);
        grid.add(stkWidth, 1, 2);
        grid.add(cpStrokeLabel, 0, 3);
        grid.add(cpStroke, 1, 3);
        grid.add(adjustLineLengthLbl, 0, 4);
        grid.add(lineLengthSlider, 1, 4);

        //add event handlers to the controllers
        cpStroke.setOnAction((ActionEvent e) -> {
            line.setStroke(cpStroke.getValue());
        });

        stkWidth.setOnAction(e -> {
            line.setStrokeWidth((int) stkWidth.getValue());
        });

        //creating the rotation transformation
        rotate = new Rotate();
        //Setting pivot points for the rotation
        rotate.setPivotX(line.getEndX());
        rotate.setPivotY(line.getEndY());
        line.getTransforms().add(rotate);
        // each rotate adds to previous
        // add to lineAnnotation if Done

        //Linking the transformation to the slider
        rotationSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                //Setting the angle for the rotation
                rotate.setAngle((double) newValue-lineAnnotation.getRotation());
            }
        });

        //Linking the transformation to the slider
        lineLengthSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double reduction = (double) newValue;
            line.setStartX(line.getEndX() - reduction);
        });
    }

    public void modifyLine() {
        Dialog dialog = mainController.getDialog(grid);

        Optional<ButtonType> results = dialog.showAndWait();

        dialog.setOnCloseRequest(e -> {
            if (isDone == false && isDeleted == false) {
                e.consume();
            }
        });

        if (results.get() == mainController.getButtonType("Done")) {
            // store the properties of the annotation
            double strokeWidth = Double.parseDouble(stkWidth.getValue().toString());
            // store the properties of the annotation
            lineToAnnotation(lineAnnotation, line);
            // each rotate adds to previous so do this serpately from copying
            // from JavaFX line to model
            lineAnnotation.setRotation(rotate.getAngle()+lineAnnotation.getRotation());
// no idea why you would do this as absolute coordinates seem to work
//            lineAnnotation.setLayoutX(line.getBoundsInParent().getMinX()+20);
//            lineAnnotation.setLayoutY(line.getBoundsInParent().getMinY()-129);
            isDone = true;
        }

        if (results.get() == mainController.getButtonType("Delete")) {
            line.setVisible(false);
            project.removeAnnotation(selectedTab, lineAnnotation);
            isDeleted = true;
        }

        if (results.get() == mainController.getButtonType("Cancel")) {
            annotationToLine (line, lineAnnotation, false, false);
            if (rotate != null) {
                line.getTransforms().remove(rotate);
            }
        }
    }

    public void setSelectedTab(Tab selectedTab) {
        this.selectedTab = selectedTab;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

}
