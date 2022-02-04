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
import javafx.scene.layout.VBox;
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
    private static VBox vBox; // parent of gridpane
    private static AnchorPane admixPane;
    private static boolean admixHorizontal = true;
    private static boolean admixVertical = false;
    
    // track if plot was rotated (from vertical to horizontal)
    private static boolean admixRotated = false;
    
    // keep intial size of the pane storing the admixture plot
    private static double initalAdmixPaneWidth, initalAdmixPaneHeight;
    
    // keep the horizontal height and width before vertical rotation
    private static double horiAdmixPaneHeight, horiAdmixPaneWidth;
    
    private static double leftMargin = 0, rightMargin = 0, bottomMargin = 0, topMargin = 0;
    
    // keep new vbox vertical size - used when increasing the left and right margins
    // Note: During rotation height and width are swapped which affects the vBox size
    private static double newVerticalVboxWidth;
    private static double newVerticalVboxHeight;
    
    // keep new vbox horizontal size - used when increasing the left and right margins
    private static double newHorizontalVboxWidth;
    private static double newHorizontalVboxHeight;
    
    // heading
    private static Text chartHeading;
    private static boolean isHeadingBold = false, isHeadingItalic = false, isHeadingUnderlined = false;

    // labels
    // keep all label width -> return max'm (labels have different sizes - make them uniform)
    private static ArrayList<Double> groupNameWidthList; 
    private static boolean isLabelBold = false, isLabelItalic = false, isLabelUnderlined = false;
    private static boolean labelsHidden = false;
    private static double labelAngelOfRotation = 90;
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
    

    private static final double extraSpace = 50; // change
    private AdmixtureGraph admixtureGraph;
    private MainController mainController;

    /**
     * check if plot was rotated from vertical back to horizontal orientation
     * this helps to restore the v-grow of the VBox when importing other -
     * admixture files
     * @return 
     */
    public static boolean isAdmixRotated() {
        return admixRotated;
    }
    
    /**
     * check if vertical and tell the user to restore a horizontal orientation -
     * of the plot before importing new admixture files
     * @return 
     */
    public static boolean isAdmixVertical() {
        return admixVertical;
    }

    @FXML
    @SuppressWarnings("empty-statement")
    private void entryDoneButton(ActionEvent event) {
        // format the heading
        setHeading(headingFontCombo.getValue(), headingFontSizeSpinner.getValue());
        
        // format labels
        setPopulationGroupLabels();
        
        // set gap
        gridPane.setVgap(spaceSpinner.getValue());
        
        // show borders
        borderSize = borderSizeSpinner.getValue();
        borderColor = borderColorPicker.getValue();
        setBorders(borderColor, borderSize);

        // margins
        topMargin = topMarginSpinner.getValue();
        rightMargin = rightMarginSpinner.getValue();
        bottomMargin = bottomMarginSpinner.getValue();
        leftMargin = leftMarginSpinner.getValue();
        double leftRightMargin = leftMargin+rightMargin;
        double topBottomMargin = topMargin+bottomMargin;

        // default vbox size with margins - used if no rotation was done
        vBox.setPadding(new Insets(topMargin, rightMargin, bottomMargin, leftMargin));
        vBox.setPrefWidth(MainController.getAdmixVbox().getPrefWidth() + leftRightMargin);
        
        // if plot rotated, the defualt prefSize is affected
        // vertical rotation - increase the vbox size by sum of the margins
        if(admixVertical){
            setVboxWidth(newVerticalVboxWidth + leftRightMargin);
            setVboxHeight(newVerticalVboxHeight + topBottomMargin);
        }
        
        // if rotated - increase the vbox size by sum of the margins
        if(admixRotated){
            setVboxWidth(newHorizontalVboxWidth + leftRightMargin);
            setVboxHeight(newHorizontalVboxHeight + topBottomMargin);        
        }

        // subject thickness - (1 spin = 100 px width)
        double newGridpaneWidth = MainController.getDefaultAdmixPlotWidth() + thicknessSpinner.getValue() * thicknessSpinnerFactor;
        gridPane.setMinWidth(newGridpaneWidth); // change size of gridpane
        gridPane.setMaxWidth(newGridpaneWidth);
        if(admixRotated){
            setVboxWidth(newGridpaneWidth+extraSpace+leftRightMargin+5);
        
        }

        // graph height
        heightSpinnerValue = heightSpinner.getValue();
        for (Node node : gridPaneChildren) {
            if (node instanceof StackedBarChart) { 
                // heightSpinnerFactor is a multiplication factor (1 spin = heightSpinnerFactor)
                ((StackedBarChart) node).setPrefHeight(defaultGraphHeight + heightSpinnerValue * heightSpinnerFactor);
            }
            MainController.getAdmixVbox().setMaxHeight(Double.MAX_VALUE);
        }
                
        // orientation
        if ("Horizontal".equals(linearLayoutBox.getValue()) && admixHorizontal == false) {
            horizontalRotation();
        }

        if ("Vertical".equals(linearLayoutBox.getValue()) && admixVertical == false) {
            verticalRotation();
        }

        mainController.disableSettingsBtn(false);
        Genesis.closeOpenStage(event);
    }

    @FXML
    private void entryCancelButton(ActionEvent event) {
        mainController.disableSettingsBtn(false);
        Genesis.closeOpenStage(event);
    }

    public static void horizontalRotation() {

        admixPane.getTransforms().add(new Rotate(-90, admixPane.getLayoutX(), horiAdmixPaneHeight));

        for (Node node : gridPaneChildren) {
            if (GridPane.getRowIndex(node) == MainController.getRowPointer() + 1 && GridPane.getColumnIndex(node) < numOfCols) {
                StackPane pane = (StackPane) node;
                Text lbl = (Text) pane.getChildren().get(0);
                lbl.setRotate(0);

            }
        }

        admixPane.relocate(0, 0);

        // change size of vbox
        setVBoxSize(horiAdmixPaneWidth+extraSpace/2, horiAdmixPaneHeight+extraSpace);
        
        // get vbox size
        newHorizontalVboxHeight = vBox.getMaxHeight();
        newHorizontalVboxWidth = vBox.getMaxWidth();
             
        // reset bool values
        admixHorizontal = true;
        admixVertical = false;
        admixRotated = true;

    }

    public static void verticalRotation() {
        // only initial
        initalAdmixPaneHeight = MainController.getAdmixPane().getHeight();
        initalAdmixPaneWidth = MainController.getAdmixPane().getWidth();
        
        // before rotation, store initial height and width of admixpane
        horiAdmixPaneHeight = initalAdmixPaneHeight;
        horiAdmixPaneWidth = initalAdmixPaneWidth;
        
        
        // rotate - rotate at bottom left corner
        admixPane.getTransforms().add(new Rotate(90, admixPane.getLayoutX(), initalAdmixPaneHeight));

        for (Node node : gridPaneChildren) {
            if (GridPane.getRowIndex(node) == MainController.getRowPointer() && GridPane.getColumnIndex(node) < numOfCols) {
                StackPane pane = (StackPane) node;
                Text lbl = (Text) pane.getChildren().get(0);
                lbl.setRotate(-90);

            }
        }

        // then shift the graph up by it's height        
        admixPane.setLayoutY(-initalAdmixPaneHeight);

        // set new size of vbox - swap height with width
        setVBoxSize(initalAdmixPaneHeight + extraSpace/2, initalAdmixPaneWidth + extraSpace);
        
        // get new vbox vertical size - used when increasing the left and right margins
        newVerticalVboxHeight = vBox.getMaxHeight();
        newVerticalVboxWidth = vBox.getMaxWidth();
              
        // set the boolean trackers
        admixVertical = true;
        admixHorizontal = false;
        admixRotated = false;

    }
    
    private void setHeading(String headingFontFamily, double headingFontSize){
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
    }
    
    private void setPopulationGroupLabels(){
        // format labels or group names
        for (Node node : gridPaneChildren) {

            if (GridPane.getRowIndex(node) == mainController.getRowPointer()) { //  && GridPane.getColumnIndex(node) > 0

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

                // increase size of panes to fit the size of new label sizes
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
    }

    private void setBorders(Color borderColor, double borderSize){
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
//                        if (n.getId().startsWith("K = ")) { // do not put borders on ks
//                            ;
//                        }else {
//                            n.setStyle("-fx-border-color: black; -fx-border-width: 1px;"); // restore default
//                        }
                    }else{
                        ; // do not add border
                    }
                } else {
                    GridPane.setMargin(n, new Insets(0, 0, -3, -3)); // this removes the white spaces
                    n.setStyle("-fx-border-color: transparent; -fx-border-width: 0;");
                }
            });
        }
    }
    
    private void setVboxWidth(double newWidth){
        vBox.setMinWidth(newWidth);
        vBox.setMaxWidth(newWidth);
    }
    
    private void setVboxHeight(double newHeight){
        vBox.setMinHeight(newHeight);
        vBox.setMaxHeight(newHeight);
    }
    
    private static void setVBoxSize(double newWidth, double newHeight){
        vBox.setMinSize(newWidth, newHeight);
        vBox.setMaxSize(newWidth, newHeight);
    }
    
    /**
     * set spinner and make it editable without user 
     * pressing ENTER to activate every input
     * @param sp
     * @param startValue
     * @param endValue
     * @param defaultValue 
     */
    private void setSpinner(Spinner sp, double startValue, double endValue, double defaultValue){
        SpinnerValueFactory<Double> spValues = new SpinnerValueFactory.DoubleSpinnerValueFactory(startValue, endValue);
        sp.setEditable(true);
        sp.setValueFactory(spValues);
        sp.getValueFactory().setValue(defaultValue);
        TextFormatter textFormatter = new TextFormatter(spValues.getConverter(),
        spValues.getValue());
        sp.getEditor().setTextFormatter(textFormatter);
        spValues.valueProperty().bindBidirectional(textFormatter.valueProperty());
    }

    public void setControls(){
        vBox = MainController.getAdmixVbox();

        // get default admixPane size everytime the setting button is clicked
        admixPane = MainController.getAdmixPane();
//        initalAdmixPaneHeight = admixPane.getHeight();
//        initalAdmixPaneWidth = admixPane.getWidth();

        // get gridpane
        gridPane = mainController.getGridPane();
        gridPaneChildren = gridPane.getChildren();
        numOfCols = gridPane.getColumnConstraints().size();

        double lblFontSize = 0;
        Paint lblDefaultColor = null;
        String lblFontFamily = null;
        groupNameWidthList = new ArrayList();
        for (Node node : gridPaneChildren) {
            if (GridPane.getRowIndex(node) == MainController.getRowPointer() && GridPane.getColumnIndex(node) < numOfCols) {
                StackPane pane = (StackPane) node;
                Text lbl = (Text) pane.getChildren().get(0);
                lblFontSize = lbl.getFont().getSize(); // keep default or selected font color
                lblFontFamily = lbl.getFont().getFamily(); // keep default or selected font family
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
                if (GridPane.getRowIndex(node) == MainController.getRowPointer() && GridPane.getColumnIndex(node) < numOfCols) {
                    StackPane pane = (StackPane) node;
                    Text lbl = (Text) pane.getChildren().get(0);
                    lbl.setFont(Font.font(labelFontCombo.getValue(), labelFontSizeSpinner.getValue()));
                }
            }
        });

        // update the groupNameWidthList everytime you spin the label size
        // prefSize of pane (Text parent) increases by default
        setSpinner(labelFontSizeSpinner, 0, 72, lblFontSize);
        labelFontSizeSpinner.setOnMouseClicked(event -> {
            groupNameWidthList.clear(); // remove exisiting label width
            for (Node node : gridPaneChildren) {
                if (GridPane.getRowIndex(node) == MainController.getRowPointer() && GridPane.getColumnIndex(node) < numOfCols) {
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
        chartHeading = mainController.getChartHeading();
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
            double newGridpaneWidth = MainController.getDefaultAdmixPlotWidth() + thicknessSpinner.getValue() * thicknessSpinnerFactor;
            gridPane.setMinWidth(newGridpaneWidth);
            gridPane.setMaxWidth(newGridpaneWidth);
            MainController.getAdmixVbox().setPrefWidth(gridPane.getMinWidth() + 50);
            if(admixRotated){
                MainController.getAdmixVbox().setMinWidth(newGridpaneWidth+extraSpace);
                MainController.getAdmixVbox().setMaxWidth(newGridpaneWidth+extraSpace);
            }
        });

        // graph height
//        defaultGraphHeight = admixtureGraph.getCHART_HEIGHT();

        setSpinner(heightSpinner, 0, 50, heightSpinnerValue);
        heightSpinner.setOnMouseClicked(event -> {
            for (Node node : gridPaneChildren) {
                if (node instanceof StackedBarChart) {
                    ((StackedBarChart) node).setPrefHeight(defaultGraphHeight + heightSpinner.getValue() * heightSpinnerFactor);
                }
            }
            MainController.getAdmixVbox().setMaxHeight(Double.MAX_VALUE);
        });

        // orientation
        linearLayoutBox.getItems().addAll("Horizontal", "Vertical");
        if (admixHorizontal) {
            linearLayoutBox.getSelectionModel().select(0);
        } else {
            linearLayoutBox.getSelectionModel().select(1);
        }

        // disable these spinners - they affect the width and height of the vbox
        // TODO - increase the size of vbox everytime you spin these boxes
        if(admixVertical){
            spaceSpinner.setDisable(true);
            thicknessSpinner.setDisable(true);
            heightSpinner.setDisable(true);
        }
    }

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    public void setAdmixtureGraph(AdmixtureGraph admixtureGraph) {
        this.admixtureGraph = admixtureGraph;
        defaultGraphHeight = admixtureGraph.getCHART_HEIGHT();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
