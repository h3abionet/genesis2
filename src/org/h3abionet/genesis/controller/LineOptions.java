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

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LineOptions extends Line{

    private final Line line;
    GridPane grid;
    private Label StrokeWidthLabel;
    private Slider rotationSlider;
    private Label cpStrokeLabel;
    private ColorPicker cpStroke;
    private ComboBox stkWidth;
    private Slider lineLengthSlider;
    private Label adjustLineLengthLbl;
    private Annotation lineAnnotation;
    private double angleOfRotation;

    public LineOptions(Line line, Annotation lineAnnotation) {
        this.line = line;
        this.lineAnnotation = lineAnnotation;
        setControllers();
    }

    public void setAngleOfRotation(double angleOfRotation) {
        this.angleOfRotation = angleOfRotation;
    }

    private void setControllers() {
        adjustLineLengthLbl = new Label("Reduce Line");
        StrokeWidthLabel = new Label("Stroke Width");
        cpStrokeLabel = new Label("Stroke color: ");
        StrokeWidthLabel = new Label("Stroke Width");

        cpStroke = new ColorPicker((Color) line.getStroke());
        cpStroke.getStyleClass().add("split-button");

        stkWidth = new ComboBox(FXCollections.
                observableArrayList(IntStream.range(1, 11).boxed().collect(Collectors.toList())));
        stkWidth.setValue((int)line.getStrokeWidth());


        rotationSlider = new Slider(-180, 180, line.getRotate());
        rotationSlider.setShowTickLabels(true);
        rotationSlider.setShowTickMarks(true);
        rotationSlider.setOrientation(Orientation.HORIZONTAL);

        lineLengthSlider = new Slider(0, 360, 5);
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
        grid.add(new Label("Rotation Slider"), 0,1);
        grid.add(rotationSlider,1,1);

        grid.add(StrokeWidthLabel, 0, 2);
        grid.add(stkWidth, 1, 2);
        grid.add(cpStrokeLabel, 0, 3);
        grid.add(cpStroke, 1, 3);
        grid.add(adjustLineLengthLbl, 0, 4);
        grid.add(lineLengthSlider, 1, 4);

        //add event handlers to the controllers
        cpStroke.setOnAction((ActionEvent e) -> {
            line.setStroke(cpStroke.getValue());
            lineAnnotation.setStroke(Integer.toHexString(cpStroke.getValue().hashCode()));
        });

        stkWidth.setOnAction(e -> {
            line.setStrokeWidth((int) stkWidth.getValue());
            lineAnnotation.setStrokeWidth((int) stkWidth.getValue());
        });

        //creating the rotation transformation
        Rotate rotate = new Rotate();
        //Setting pivot points for the rotation
        rotate.setPivotX((line.getStartX() + line.getEndX())/2);
        rotate.setPivotY((line.getStartY() + line.getEndY())/2);

        //Linking the transformation to the slider
        rotationSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<?extends Number> observable, Number oldValue, Number newValue){
                //Setting the angle for the rotation
                rotate.setAngle((double) newValue);
                setAngleOfRotation((double) newValue);
                //Adding the transformation to the line
                line.getTransforms().add(rotate);
            }
        });

        //Linking the transformation to the slider
        lineLengthSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<?extends Number> observable, Number oldValue, Number newValue){
                double  reduction = (double) newValue;
                line.setStartX(line.getStartX()-reduction);
                line.setStartY(line.getStartY()-reduction);
                line.setStartX(line.getEndX()-reduction);
                line.setStartY(line.getEndY()-reduction);
            }
        });
    }

    public void modifyArrow(){
        Dialog<LineOptions.Options> dialog = new Dialog<>();
        dialog.setTitle("Line options");
        dialog.setHeaderText(null);
        dialog.setResizable(false);

        dialog.getDialogPane().setContent(grid);

        ButtonType deleteBtn = new ButtonType("Delete", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType doneBtn = new ButtonType("Done", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().setAll(doneBtn, deleteBtn);

        dialog.setResultConverter(b -> {
            if (b == doneBtn) {
                return new Options((int) rotationSlider.getValue(), (int) stkWidth.getValue(), cpStroke.getValue());
            } else {
                line.setVisible(false);
            }
            return null;
        });

        Optional<Options> results = dialog.showAndWait();

        results.ifPresent((Options options) -> {
            line.setStrokeWidth(options.strokeWidth);
            line.setStroke(options.strokeColor);

            // store the properties of the annotation
            lineAnnotation.setStartX(line.getStartX());
            lineAnnotation.setStartY(line.getStartY());
            lineAnnotation.setEndY(line.getEndX());
            lineAnnotation.setEndY(line.getEndY());
            lineAnnotation.setRotation(angleOfRotation);
            lineAnnotation.setStrokeColor(Integer.toHexString(cpStroke.getValue().hashCode()));
        });

    }

    private static class Options {
        int strokeWidth;
        Color strokeColor;
        int angle;

        public Options(int angle, int strokeWidth, Color strokeColor) {
            this.angle = angle;
            this.strokeWidth = strokeWidth;
            this.strokeColor = strokeColor;
        }
    }
}
