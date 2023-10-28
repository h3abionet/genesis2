package org.h3abionet.genesis.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public class TestPaneCtler {

    @FXML
    private AnchorPane pane;

    public void addToPane(Node n){
        pane.getChildren().add(n);
    }
}
