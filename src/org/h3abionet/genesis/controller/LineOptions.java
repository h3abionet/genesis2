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
    private Slider slider;
    private Label cpStrokeLabel;
    private ColorPicker cpStroke;
    private ComboBox stkWidth;
    private Slider lineLengthSlider;
    private Label adjustLineLengthLbl;
    private Annotation lineAnnotation;

    public LineOptions(Line line, Annotation lineAnnotation) {
        this.line = line;
        this.lineAnnotation = lineAnnotation;
        setControllers();
    }


    private void setControllers() {
        adjustLineLengthLbl = new Label("Reduce Line");
        StrokeWidthLabel = new Label("Stroke Width");
        cpStrokeLabel = new Label("Stroke color: ");

        cpStroke = new ColorPicker((Color) line.getStroke());
        cpStroke.getStyleClass().add("split-button");

        stkWidth = new ComboBox(FXCollections.
                observableArrayList(IntStream.range(1, 11).boxed().collect(Collectors.toList())));
        stkWidth.setValue((int)line.getStrokeWidth());

        StrokeWidthLabel = new Label("Stroke Width");
        slider = new Slider(0, 360,line.getRotate());
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setOrientation(Orientation.HORIZONTAL);

        lineLengthSlider = new Slider(0, 360, 5);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setOrientation(Orientation.HORIZONTAL);

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
        grid.add(slider,1,1);

        grid.add(StrokeWidthLabel, 0, 2);
        grid.add(stkWidth, 1, 2);
        grid.add(cpStrokeLabel, 0, 3);
        grid.add(cpStroke, 1, 3);
        grid.add(adjustLineLengthLbl, 0, 4);
        grid.add(lineLengthSlider, 1, 4);

        //add event handlers to the controllers
        cpStroke.setOnAction((ActionEvent e) -> {
            line.setStroke(cpStroke.getValue());
            lineAnnotation.setStroke(cpStroke.getValue());
        });

        stkWidth.setOnAction(e -> {
            line.setStrokeWidth((int) stkWidth.getValue());
        });

        //creating the rotation transformation
        Rotate rotate = new Rotate();
        //Setting pivot points for the rotation
        rotate.setPivotX((line.getStartX() + line.getEndX())/2);
        rotate.setPivotY((line.getStartY() + line.getEndY())/2);
        //Adding the transformation to line
        line.getTransforms().addAll(rotate);

        //Linking the transformation to the slider
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<?extends Number> observable, Number oldValue, Number newValue){
                //Setting the angle for the rotation
                rotate.setAngle((double) newValue);
            }
        });

        //Adding the transformation to the line
        line.getTransforms().add(rotate);

        //Linking the transformation to the slider
        lineLengthSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<?extends Number> observable, Number oldValue, Number newValue){
                //Setting the angle for the rotation
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
                return new Options((int) slider.getValue(), (int) stkWidth.getValue(), cpStroke.getValue());
            } else {
                line.setVisible(false);
            }
            return null;
        });

        Optional<Options> results = dialog.showAndWait();

        results.ifPresent((Options options) -> {
            line.setStrokeWidth(options.strokeWidth);
            line.setStroke(options.strokeColor);
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
