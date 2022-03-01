package org.h3abionet.genesis.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.transform.Rotate;

import java.util.Optional;

public class ArrowOptions {
    // arrow variables
    // TODO

    private final Arrow arrow;
    GridPane grid;
    private Label StrokeWidthLabel;
    private Slider slider;
    private Slider arrowLengthSlider;
    private Label adjustArrowLengthLbl;

    public ArrowOptions(Arrow arrow) {
        this.arrow = arrow;
        setControllers();
    }


    private void setControllers() {
        adjustArrowLengthLbl = new Label("Adjust Arrow Length");
        arrowLengthSlider = new Slider(0, 360, 5);
        arrowLengthSlider.setShowTickLabels(true);
        arrowLengthSlider.setShowTickMarks(true);
        arrowLengthSlider.setOrientation(Orientation.HORIZONTAL);


        StrokeWidthLabel = new Label("Stroke Width");
        slider = new Slider(0, 360, 5);
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
        grid.add(new Label("Arrow options"), 0, 0);
        grid.add(new Label("Rotation Slider"), 0,1);
        grid.add(slider,1,1);
        grid.add(adjustArrowLengthLbl,0,2);
        grid.add(arrowLengthSlider,1,2);


        // event on the slider
        //creating the rotation transformation
        Rotate rotate = new Rotate();
        //Setting pivot points for the rotation
        rotate.setPivotX((arrow.getStartX() + arrow.getEndX())/2);
        rotate.setPivotY((arrow.getStartY() + arrow.getEndY())/2);

        //Adding the transformation to rectangle
        arrow.getTransforms().addAll(rotate);
        //Linking the transformation to the slider
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<?extends Number> observable, Number oldValue, Number newValue){
                //Setting the angle for the rotation
                rotate.setAngle((double) newValue);
            }
        });
        //Adding the transformation to the circle
        arrow.getTransforms().add(rotate);

        arrowLengthSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<?extends Number> observable, Number oldValue, Number newValue){
                //Setting the angle for the rotation
                double  reduction = (double) newValue;
                arrow.setStartX(arrow.getStartX()-reduction);
                arrow.setStartY(arrow.getStartY()-reduction);
                arrow.setStartX(arrow.getEndX()-reduction);
                arrow.setStartY(arrow.getEndY()-reduction);
            }
        });
    }

    public void modifyArrow(){
        Dialog<ArrowOptions.Options> dialog = new Dialog<>();
        dialog.setTitle("Arrow options");
        dialog.setHeaderText(null);
        dialog.setResizable(false);

        dialog.getDialogPane().setContent(grid);

        ButtonType deleteBtn = new ButtonType("Delete", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType doneBtn = new ButtonType("Done", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().setAll(doneBtn, deleteBtn);

        dialog.setResultConverter(b -> {
            if (b == doneBtn) {
                return new Options((int) slider.getValue());
            } else {
                arrow.setVisible(false);
            }
            return null;
        });

        Optional<Options> results = dialog.showAndWait();
        results.ifPresent((Options options) -> {
            return;
        });

    }

    private static class Options {
        // TODO constructor for the options
        int angle;

        public Options(int angle) {
            this.angle = angle;
        }
    }

}
