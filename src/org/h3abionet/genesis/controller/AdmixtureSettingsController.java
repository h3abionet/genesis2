/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.AdmixtureGraph;

/**
 * FXML Controller class
 *
 * @author henry
 */
public class AdmixtureSettingsController implements Initializable {

    @FXML
    private TextField titleField;

    @FXML
    private ComboBox<String> headingFontCombo;

    @FXML
    private Spinner<Double> headingFontSizeSpinner;

    @FXML
    private CheckBox boldHeadingCheckbox;

    @FXML
    private CheckBox italicHeadingCheckbox;

    @FXML
    private CheckBox underlineHeadingCheckbox;

    @FXML
    private ColorPicker headingColorPicker;

    @FXML
    private CheckBox hideLabelsCheckbox;

    @FXML
    private ColorPicker labelColorPicker;

    @FXML
    private CheckBox boldLabelCheckbox;

    @FXML
    private CheckBox italicLabelCheckbox;

    @FXML
    private CheckBox underlineLabelCheckbox;

    @FXML
    private Spinner<Double> rotateLabelSpinner;

    @FXML
    private ComboBox<String> labelFontCombo;

    @FXML
    private Spinner<Double> labelFontSizeSpinner;

    @FXML
    private Spinner<Double> leftMarginSpinner;

    @FXML
    private Spinner<Double> rightMarginSpinner;

    @FXML
    private Spinner<Double> topMarginSpinner;

    @FXML
    private Spinner<Double> bottomMarginSpinner;

    @FXML
    private CheckBox showBordersCheckbox;

    @FXML
    private Spinner<Double> borderSizeSpinner;

    @FXML
    private ColorPicker borderColorPicker;

    @FXML
    private Spinner<Double> spaceSpinner;

    @FXML
    private Spinner<Double> thicknessSpinner;

    @FXML
    private Spinner<Double> heightSpinner;

    @FXML
    private ComboBox<String> linearLayoutBox;

    @FXML
    private Button cancelBtn;

    @FXML
    private Button doneBtn;

    private GridPane gridPane;
    private static AnchorPane admixPane;
    private static boolean admixHorizontal = true;
    private static boolean admixVertical = false;
    private static boolean admixRotated = false;
    private static double width, height;
    private static double horiAdmixPaneHeight, horiAdmixPaneWidth; // store initial height and width
    private static double leftMargin = 0, rightMargin = 0, bottomMargin = 0, topMargin = 0;

    // heading
    private static Text chartHeading;
    private static boolean isHeadingBold = false, isHeadingItalic = false, isHeadingUnderlined = false;

    // labels
    private static ArrayList<Double> groupNameWidthList; // keep all label width -> return max'm
    private static boolean isLabelBold = false, isLabelItalic = false, isLabelUnderlined = false;
    private static boolean labelsHidden = false;
    private static double labelAngelOfRotation = 0;
    private static double minHoriLabelHeight, maxHoriLabelHeight;
    private static ObservableList<Node> gridPaneChildren;
    private static int numOfCols;

    // borders
    private static double borderSize;
    private static Color borderColor = Color.BLACK;
    private static boolean showBorders = false;

    // graph height
    private static double heightSpinnerValue;
    private double defaultGraphHeight;
    private final double heightSpinnerFactor = 25; // 1 spin increases plot height by 25 px
    
    // subject thickness
    private final double thicknessSpinnerFactor = 100; // 1 spin increases plot width by 100 px
    

    private static final double titleMargin = 50; // change

    public static boolean isAdmixRotated() {
        return admixRotated;
    }

    public static boolean isAdmixVertical() {
        return admixVertical;
    }

    public static boolean isAdmixHorizontal() {
        return admixHorizontal;
    }

    public static Text getChartHeading() {
        return chartHeading;
    }

    public static double getWidth() {
        return width;
    }

    public static double getHeight() {
        return height;
    }

    @FXML
    @SuppressWarnings("empty-statement")
    private void entryDoneButton(ActionEvent event) {
        if ("Horizontal".equals(linearLayoutBox.getValue()) && admixHorizontal == false) {
            horizontalRotation();
        }

        if ("Vertical".equals(linearLayoutBox.getValue()) && admixVertical == false) {
            verticalRotation();
        }

        // format the heading
        String headingFontFamily = headingFontCombo.getValue();
        double headingFontSize = headingFontSizeSpinner.getValue();

        if (boldHeadingCheckbox.isSelected()) {
            isHeadingBold = true;
            chartHeading.setText(titleField.getText());
            chartHeading.setFont(Font.font(headingFontFamily, FontWeight.EXTRA_BOLD, headingFontSize));
        } else {
            isHeadingBold = false;
            chartHeading.setText(titleField.getText());
            chartHeading.setFont(Font.font(headingFontFamily, FontWeight.NORMAL, headingFontSize));
        }

        if (italicHeadingCheckbox.isSelected()) {
            isHeadingItalic = true;
            chartHeading.setText(titleField.getText());
            chartHeading.setFont(Font.font(headingFontFamily, FontPosture.ITALIC, headingFontSize));
        } else {
            isHeadingItalic = false;
            chartHeading.setText(titleField.getText());
            chartHeading.setFont(Font.font(headingFontFamily, FontPosture.REGULAR, headingFontSize));
        }

        if (underlineHeadingCheckbox.isSelected()) {
            isHeadingUnderlined = true;
            chartHeading.setText(titleField.getText());
            chartHeading.setUnderline(true);
        } else {
            chartHeading.setText(titleField.getText());
            isHeadingUnderlined = false;
            chartHeading.setUnderline(false);
        }

        chartHeading.setFill(headingColorPicker.getValue()); // set heading color

        // format labels or group names
        for (Node node : gridPaneChildren) {
            if (GridPane.getRowIndex(node) == MainController.getRowPointer() + 1 && GridPane.getColumnIndex(node) < numOfCols) {
                StackPane pane = (StackPane) node;
                Text lbl = (Text) pane.getChildren().get(0);
                String lblFontFamily = labelFontCombo.getValue();
                double lblFontSize = labelFontSizeSpinner.getValue();

                if (boldLabelCheckbox.isSelected()) {
                    isLabelBold = true;
                    lbl.setFont(Font.font(lblFontFamily, FontWeight.EXTRA_BOLD, lblFontSize));
                } else {
                    isLabelBold = false;
                    lbl.setFont(Font.font(lblFontFamily, FontWeight.NORMAL, lblFontSize));
                }

                if (italicLabelCheckbox.isSelected()) {
                    isLabelItalic = true;
                    lbl.setFont(Font.font(lblFontFamily, FontPosture.ITALIC, lblFontSize));
                } else {
                    isLabelItalic = false;
                    lbl.setFont(Font.font(lblFontFamily, FontPosture.REGULAR, lblFontSize));
                }

                if (underlineLabelCheckbox.isSelected()) {
                    isLabelUnderlined = true;
                    lbl.setUnderline(true);
                } else {
                    isLabelUnderlined = false;
                    lbl.setUnderline(false);
                }

                lbl.setFill(labelColorPicker.getValue()); // set lbl color
                // set angle and rotate
                labelAngelOfRotation = rotateLabelSpinner.getValue();
                lbl.setRotate(labelAngelOfRotation);

                // increase size of panes to fit the size of new labels
                pane.setMinHeight(Collections.max(groupNameWidthList) + 5); // 5 is a margin
                pane.setMaxHeight(Collections.max(groupNameWidthList) + 5);

                // show borders
                if (hideLabelsCheckbox.isSelected()) {
                    pane.setVisible(false);
                    labelsHidden = true;
                } else {
                    pane.setVisible(true);
                    labelsHidden = false;
                }

            }
        }

        // margins
        topMargin = topMarginSpinner.getValue();
        rightMargin = rightMarginSpinner.getValue();
        bottomMargin = bottomMarginSpinner.getValue();
        leftMargin = leftMarginSpinner.getValue();

        // setting margins
        MainController.getAdmixVbox().setPadding(new Insets(topMargin, rightMargin, bottomMargin, leftMargin));
        MainController.getAdmixVbox().setPrefWidth(MainController.getAdmixVbox().getPrefWidth() + (leftMargin + rightMargin));

        gridPane.setVgap(spaceSpinner.getValue()); // set gap

        // subject thickness - (1 spin = 100 px width)
        gridPane.setMinWidth(MainController.getDefaultAdmixPlotWidth() + thicknessSpinner.getValue() * thicknessSpinnerFactor);
        gridPane.setMaxWidth(MainController.getDefaultAdmixPlotWidth() + thicknessSpinner.getValue() * thicknessSpinnerFactor);

        MainController.getAdmixVbox().setPrefWidth(gridPane.getMinWidth() + 50);

        // show borders
        borderSize = borderSizeSpinner.getValue();
        borderColor = borderColorPicker.getValue();
        String hex;
        // check if black
        if("ff".equals(Integer.toHexString(borderColor.hashCode()))){
            hex = "000000";
        }else{
            hex = Integer.toHexString(borderColor.hashCode());
        }
        String cssBorderLayout = "-fx-border-color: #"+hex+ "; -fx-border-width: " + borderSize + ";";

        if (showBordersCheckbox.isSelected()) {
            showBorders = true;
            gridPane.getChildren().forEach((n) -> {
                if (n instanceof Label) {
                    ; // do not add border
                } else {
                    if (n instanceof StackPane) {
                        GridPane.setMargin(n, new Insets(0)); // show white spaces (axes padding)
                        n.setStyle(cssBorderLayout);

                        n.hoverProperty().addListener((observable, oldValue, newValue) -> {
                            if (newValue) {
                                // show background color and a hand cursor
                                n.setCursor(Cursor.HAND);
                                n.setStyle(cssBorderLayout + "-fx-background-color: #e1f3f7;");
                            } else {
                                // remove background color and change cursor type
                                n.setStyle(cssBorderLayout + "-fx-background-color: transparent;");
                            }
                        });
                    } else {
                        GridPane.setMargin(n, new Insets(0)); // show white spaces (axes padding)
                        n.setStyle(cssBorderLayout);
                    }
                }
            });
        } else {
            showBorders = false;
            gridPane.getChildren().forEach((n) -> {
                if (n instanceof StackPane || n instanceof Label) {
                    if(n instanceof StackPane){
                        n.setStyle("-fx-border-color: black; -fx-border-width: 1px;"); // restore default
                    }else{
                        ; // donot add border
                    }
                } else {
                    GridPane.setMargin(n, new Insets(0, 0, -3, -3)); // this removes the white spaces
                    n.setStyle("-fx-border-color: transparent; -fx-border-width: 0;");
                }
            });
        }

        // graph height
        heightSpinnerValue = heightSpinner.getValue();
        for (Node node : gridPaneChildren) {
            if (node instanceof StackedBarChart) { 
                // heightSpinnerFactor is a multiplication factor (1 spin = heightSpinnerFactor)
                ((StackedBarChart) node).setPrefHeight(defaultGraphHeight + heightSpinnerValue * heightSpinnerFactor);
            }
        }

        Genesis.closeOpenStage(event);
    }

    @FXML
    private void entryCancelButton(ActionEvent event) {
        Genesis.closeOpenStage(event);
    }

    public static void horizontalRotation() {

        admixPane.getTransforms().add(new Rotate(-90, admixPane.getLayoutX(), horiAdmixPaneHeight));

        for (Node node : gridPaneChildren) {
            if (GridPane.getRowIndex(node) == MainController.getRowPointer() + 1 && GridPane.getColumnIndex(node) < numOfCols) {
                StackPane pane = (StackPane) node;
                Text lbl = (Text) pane.getChildren().get(0);
                lbl.setRotate(0);
                pane.setMinHeight(minHoriLabelHeight); // TODO - change default height of text
                pane.setMaxHeight(maxHoriLabelHeight);

            }
        }

        admixPane.relocate(0, 0);

        // exchange height with width
        MainController.getAdmixVbox().setMinSize(horiAdmixPaneWidth + 10, horiAdmixPaneHeight + 40);
        MainController.getAdmixVbox().setMaxSize(horiAdmixPaneWidth + 10, horiAdmixPaneHeight + 40);

        MainController.getAdmixVbox().setMaxHeight(Double.MAX_VALUE); // restore vgrow

        // reset bool values
        admixHorizontal = true;
        admixVertical = false;
        admixRotated = true;

    }

    public static void verticalRotation() {
        // before rotation, store initial height and width of admixpane
        horiAdmixPaneHeight = height;
        horiAdmixPaneWidth = width;

        // rotate
        admixPane.getTransforms().add(new Rotate(90, admixPane.getLayoutX(), height));

        for (Node node : gridPaneChildren) {
            if (GridPane.getRowIndex(node) == MainController.getRowPointer() + 1 && GridPane.getColumnIndex(node) < numOfCols) {
                StackPane pane = (StackPane) node;
                Text lbl = (Text) pane.getChildren().get(0);
                lbl.setRotate(0);

                // store intial size of group name labels
                minHoriLabelHeight = pane.getMinHeight();
                maxHoriLabelHeight = pane.getMaxHeight();

                // then increase the height of the labels
                pane.setMinHeight(Collections.max(groupNameWidthList) + 5); // 5 is a margin
                pane.setMaxHeight(Collections.max(groupNameWidthList) + 5);

            }
        }

        // then shift the graph up by it's height        
        admixPane.setLayoutY(-height);

        // for the admix pane, exchange height with width
        MainController.getAdmixVbox().setMinSize(height + titleMargin, width + titleMargin);
        MainController.getAdmixVbox().setMaxSize(height + titleMargin, width + titleMargin);

        // set the boolean trackers
        admixVertical = true;
        admixHorizontal = false;
        admixRotated = false;

    }
    
    /**
     * set spinner and make it editable without user 
     * pressing ENTER to activate every input
     * @param sp
     * @param startV
     * @param endValue
     * @param defaultValue 
     */
    private void setSpinner(Spinner sp, double startV, double endValue, double defaultValue){
	SpinnerValueFactory<Double> spValues = new SpinnerValueFactory.DoubleSpinnerValueFactory(startV, endValue);
	sp.setEditable(true);
	sp.setValueFactory(spValues);
	sp.getValueFactory().setValue(defaultValue);
	TextFormatter textFormatter = new TextFormatter(spValues.getConverter(),
	spValues.getValue());
	sp.getEditor().setTextFormatter(textFormatter);
	spValues.valueProperty().bindBidirectional(textFormatter.valueProperty());
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        admixPane = MainController.getAdmixPane();
        height = admixPane.getHeight();
        width = admixPane.getWidth();

        // get gridpane
        gridPane = MainController.getGridPane();
        gridPaneChildren = gridPane.getChildren();
        numOfCols = gridPane.getColumnConstraints().size();

        double lblFontSize = 0;
        Paint lblDefaultColor = null;
        String lblFontFamily = null;
        groupNameWidthList = new ArrayList();
        for (Node node : gridPaneChildren) {
            if (GridPane.getRowIndex(node) == MainController.getRowPointer() + 1 && GridPane.getColumnIndex(node) < numOfCols) {
                StackPane pane = (StackPane) node;
                Text lbl = (Text) pane.getChildren().get(0);
                lblFontSize = lbl.getFont().getSize();
                lblFontFamily = lbl.getFont().getFamily();
                lblDefaultColor = lbl.getFill();
                groupNameWidthList.add(lbl.getLayoutBounds().getWidth()); // keep all the width of labels
            }
        }
        labelColorPicker.setValue((Color) lblDefaultColor); // default lblDefaultColor of picker

        // labels
        labelFontCombo.setItems(FXCollections.observableArrayList(Font.getFamilies()));
        labelFontCombo.setValue(lblFontFamily);
        labelFontCombo.setOnAction(event -> {
            for (Node node : gridPaneChildren) {
                if (GridPane.getRowIndex(node) == MainController.getRowPointer() + 1 && GridPane.getColumnIndex(node) < numOfCols) {
                    StackPane pane = (StackPane) node;
                    Text lbl = (Text) pane.getChildren().get(0);
                    lbl.setFont(Font.font(labelFontCombo.getValue(), labelFontSizeSpinner.getValue()));
                }
            }
        });
        
        setSpinner(labelFontSizeSpinner, 0, 72, lblFontSize);
        labelFontSizeSpinner.setOnMouseClicked(event -> {
            groupNameWidthList.clear(); // remove exisiting label width
            for (Node node : gridPaneChildren) {
                if (GridPane.getRowIndex(node) == MainController.getRowPointer() + 1 && GridPane.getColumnIndex(node) < numOfCols) {
                    StackPane pane = (StackPane) node;
                    Text lbl = (Text) pane.getChildren().get(0);
                    lbl.setFont(Font.font(labelFontCombo.getValue(), labelFontSizeSpinner.getValue()));
                    groupNameWidthList.add(lbl.getLayoutBounds().getWidth()); // keep all the width of labels
                }
            }
        });

        if (isLabelBold) {
            boldLabelCheckbox.setSelected(true);
        }
        if (isLabelItalic) {
            italicLabelCheckbox.setSelected(true);
        }
        if (isLabelUnderlined) {
            underlineLabelCheckbox.setSelected(true);
        }
        // rotate labels
        setSpinner(rotateLabelSpinner, -360, 360, labelAngelOfRotation);


        // heading
        chartHeading = MainController.getChartHeading();
        titleField.setText(chartHeading.getText());
        headingFontCombo.setItems(FXCollections.observableArrayList(Font.getFamilies()));
        headingFontCombo.setValue(chartHeading.getFont().getFamily());
        if (isHeadingBold) {
            boldHeadingCheckbox.setSelected(true);
        }
        if (isHeadingItalic) {
            italicHeadingCheckbox.setSelected(true);
        }
        if (isHeadingUnderlined) {
            underlineHeadingCheckbox.setSelected(true);
        }
        if (labelsHidden) {
            hideLabelsCheckbox.setSelected(true);
        }

        headingFontCombo.setOnAction(event -> {
            chartHeading.setFont(Font.font(headingFontCombo.getValue()));
        });
        
        setSpinner(headingFontSizeSpinner, 0, 100, chartHeading.getFont().getSize());
        headingFontSizeSpinner.setOnMouseClicked(event -> {
            chartHeading.setFont(Font.font(headingFontSizeSpinner.getValue()));
        });

        headingColorPicker.setValue((Color) chartHeading.getFill());
        headingColorPicker.setOnAction(event -> chartHeading.setFill(headingColorPicker.getValue()));

        // margins
        ArrayList<Spinner> spinners = new ArrayList<>();
        spinners.add(topMarginSpinner);
        spinners.add(rightMarginSpinner);
        spinners.add(bottomMarginSpinner);
        spinners.add(leftMarginSpinner);
        double marginList[] = {topMargin, rightMargin, bottomMargin, leftMargin};
        for (int i = 0; i < spinners.size(); i++) {
            setSpinner(spinners.get(i), 0, 100, marginList[i]);
        }

        // borders
        if (showBorders) {
            showBordersCheckbox.setSelected(true);
        }
        setSpinner(borderSizeSpinner, 1, 5, borderSize);
        borderColorPicker.setValue(borderColor);

        // graph spacing
        setSpinner(spaceSpinner, 0, 30, gridPane.getVgap());

        // subject thickness
        setSpinner(thicknessSpinner, 0, 200, (gridPane.getMaxWidth()-1200)/100);
        thicknessSpinner.setOnMouseClicked(event -> {
             gridPane.setMinWidth(MainController.getDefaultAdmixPlotWidth() + thicknessSpinner.getValue() * thicknessSpinnerFactor);
             gridPane.setMaxWidth(MainController.getDefaultAdmixPlotWidth() + thicknessSpinner.getValue() * thicknessSpinnerFactor);
             MainController.getAdmixVbox().setPrefWidth(gridPane.getMinWidth() + 50);
        });

        // graph height
        defaultGraphHeight = AdmixtureGraph.getCHART_HEIGHT();
        setSpinner(heightSpinner, 0, 50, heightSpinnerValue);
        heightSpinner.setOnMouseClicked(event -> {
            for (Node node : gridPaneChildren) {
                if (node instanceof StackedBarChart) {
                    ((StackedBarChart) node).setPrefHeight(defaultGraphHeight + heightSpinner.getValue() * heightSpinnerFactor);
                }
            }
        });

        // Linear layout
        linearLayoutBox.getItems().addAll("Horizontal", "Vertical");
        if (admixHorizontal) {
            linearLayoutBox.getSelectionModel().select(0);
        } else {
            linearLayoutBox.getSelectionModel().select(1);
        }

    }

}
