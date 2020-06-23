/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import org.h3abionet.genesis.Genesis;

/**
 * FXML Controller class
 *
 * @author henry
 */
public class AdmixtureSettingsController implements Initializable {

    @FXML
    private TextField titleField;

    @FXML
    private Spinner<?> headingFontSpinner;

    @FXML
    private Spinner<Integer> headingFontSizeSpinner;

    @FXML
    private CheckBox boldHeadingCheckbox;

    @FXML
    private CheckBox italicHeadingCheckbox;

    @FXML
    private CheckBox underlineHeadingCheckbox;

    @FXML
    private CheckBox showLabelCheckbox;

    @FXML
    private CheckBox boldLabelCheckbox;

    @FXML
    private CheckBox italicLabelCheckbox;

    @FXML
    private CheckBox underlineLabelCheckbox;

    @FXML
    private Spinner<?> rotateLabelSpinner;

    @FXML
    private Spinner<?> labelFontSpinner;

    @FXML
    private Spinner<Integer> labelFontSizeSpinner;

    @FXML
    private Spinner<?> leftMarginSpinner;

    @FXML
    private Spinner<?> rightMarginSpinner;

    @FXML
    private Spinner<?> topMarginSpinner;

    @FXML
    private Spinner<?> bottomMarginSpinner;

    @FXML
    private CheckBox showBordersCheckbox;

    @FXML
    private Spinner<?> spaceSpinner;

    @FXML
    private Spinner<?> thicknessSpinner;

    @FXML
    private Spinner<?> heightSpinner;

    @FXML
    private ComboBox<String> linearLayoutBox;

    @FXML
    private Button cancelBtn;

    @FXML
    private Button doneBtn;

    private GridPane gridPane;
    private static boolean isAdmixHorizontal = true;
    private static boolean isAdmixVertical = false;
    private static String title = "Admixture plot"; // default
    private static Text chartTitle = new Text(title);
    
    private double heightOfRows;

    public static boolean isIsAdmixVertical() {
        return isAdmixVertical;
    }

    public static Text getChartTitle() {
        return chartTitle;
    }
    
    
    @FXML
    private void entryDoneButton(ActionEvent event) {
        double angle = 90;
        double margin = 20.0;
        Rotate rotate = new Rotate();

        // Setting pivot points for the rotation
        // half the width of the gridPane
        double halfWidth = gridPane.getBoundsInParent().getMinY() + gridPane.getBoundsInLocal().getWidth()/2;        
        rotate.setPivotX(halfWidth);
        rotate.setPivotY(halfWidth);

        if ("Horizontal".equals(linearLayoutBox.getValue()) && isAdmixHorizontal == false) {
            //Setting the angle for the rotation
            rotate.setAngle(angle);
            gridPane.getTransforms().addAll(rotate);
            
            gridPane.setMinHeight(heightOfRows); // height set to height of all rows
            gridPane.setMaxHeight(heightOfRows);
            
            isAdmixHorizontal = true;
            isAdmixVertical = false;

        }
        if ("Vertical".equals(linearLayoutBox.getValue()) && isAdmixVertical == false) {
            //Setting the angle for the rotation
            rotate.setAngle(-angle);
            gridPane.getTransforms().add(rotate);
            gridPane.setMinHeight(halfWidth*2); // height set to full width
            gridPane.setMaxHeight(halfWidth*2);

            isAdmixVertical = true;
            isAdmixHorizontal = false;
        }

        Genesis.closeOpenStage(event);
    }

    @FXML
    private void entryCancelButton(ActionEvent event) {
        Genesis.closeOpenStage(event);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        titleField.setText(title);
        
        // get gridpane
        gridPane = MainController.getGridPane();
        
        for(Node n : gridPane.getChildren()){
            if(GridPane.getRowIndex(n)==0 && GridPane.getColumnIndex(n)==1){
                heightOfRows = n.getBoundsInLocal().getHeight() * MainController.getRowPointer()+2;
                break;
            }
        }

        SpinnerValueFactory<Integer> fontSizes = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 5);
        labelFontSizeSpinner.setValueFactory(fontSizes);
        labelFontSizeSpinner.setEditable(true);
        headingFontSizeSpinner.setValueFactory(fontSizes);
        headingFontSizeSpinner.setEditable(true);

        // Linear layout
        linearLayoutBox.getItems().addAll("Horizontal", "Vertical");
        if (isAdmixHorizontal) {
            linearLayoutBox.getSelectionModel().select(0);
        } else {
            linearLayoutBox.getSelectionModel().select(1);
        }

    }

}
