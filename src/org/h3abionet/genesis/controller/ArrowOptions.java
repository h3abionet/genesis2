package org.h3abionet.genesis.controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import java.util.Optional;

public class ArrowOptions {
    // arrow variables
    // TODO

    private final Arrow arrow;
    GridPane grid;

    public ArrowOptions(Arrow arrow) {
        this.arrow = arrow;
        setControllers();
    }

    private void setControllers() {
        // controls
        // set the grid
        grid = new GridPane();
        grid.setVgap(5);
        grid.setHgap(5);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setAlignment(Pos.CENTER);

        // add controllers to the grid
        grid.add(new Label("Arrow options"), 1, 1);

    }

    public void modifyArrow(){
        Dialog<ArrowOptions.Options> dialog = new Dialog<>();
        dialog.setTitle("Arrow options");
        dialog.setHeaderText(null);
        dialog.setResizable(false);

        dialog.getDialogPane().setContent(grid);

        ButtonType deleteBtn = new ButtonType("Delete", ButtonBar.ButtonData.CANCEL_CLOSE);
        // ButtonType doneBtn = new ButtonType("Done", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().setAll(deleteBtn);

        dialog.setResultConverter(new Callback<ButtonType, ArrowOptions.Options>() {
            @Override
            public ArrowOptions.Options call(ButtonType b) {
                if (b == deleteBtn) {
                    arrow.setVisible(false);
                }else{
                    // TODO - add controls effects to arrow
                }
                return null;
            }
        });
        Optional<ArrowOptions.Options> results = dialog.showAndWait();
    }

    private static class Options {
        // TODO constructor for the options

    }

}
