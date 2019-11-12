/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Spinner;
import javafx.scene.shape.Line;

/**
 *
 * @author henry
 */
public class LineArrowOptionsController {
    
    @FXML
    private Line line;

    @FXML
    private Spinner<?> lineSpinner;

    @FXML
    private CheckBox deleteBtn;

    @FXML
    private ColorPicker lineColorPicker;

    @FXML
    private Button doneBtn;

    @FXML
    private Button cancelBtn;
    
    
}
