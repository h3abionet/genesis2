/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.model;

import com.sun.javafx.charts.Legend;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.controller.AdmixtureIndividualDetailsController;
import org.h3abionet.genesis.controller.AncestorOptionsController;

/**
 *
 * @author henry
 */
public class AdmixtureGraphEventsHandler {

    // to keep list of admix charts from the main controller
    private ArrayList<StackedBarChart<String, Number>> listOfCharts;

    // pointer to the gridpane defined in the main controller 
    private GridPane gridPane;

    private String[] iidDetails;
    private int numOfAncestries = AdmixtureGraph.currentNumOfAncestries; // number of series;
    private int rowPointer; // defines the correct row with the cells for keeping charts
    private static Stage optionsStage;
    private Scene scene, sceneForAddLabelBtn, sceneForPopulationGroupBtn;

    /**
     * To store all ancestor HBoxes which will be added the leftVBox
     */
    private static ArrayList<HBox> listOfAncenstorHBox;

    /**
     * this keeps the HBoxes with ancestry buttons the order of the buttons
     * changes when the ancestry are shifted up or down It is accessed by the
     * Ancestor option details controller.
     */
    private static VBox leftVBox;

    /**
     * before swapping the series, it is important to know which row contains
     * the charts being modified
     */
    private static int rowIndexOfClickedAdmixChart;

    private static int labelClickCounter = 0;
    private Label firstGroupLabel, secondGroupLabel;
    private Node firstChart, secondChart;

    /**
     *
     * @param listOfCharts
     * @param gridPane
     * @param rowPointer
     */
    public AdmixtureGraphEventsHandler(ArrayList<StackedBarChart<String, Number>> listOfCharts, GridPane gridPane, int rowPointer) {
        this.listOfCharts = listOfCharts;
        this.gridPane = gridPane;
        this.rowPointer = rowPointer;
        optionsStage = new Stage();
        listOfAncenstorHBox = new ArrayList<>();
    }

    /**
     *
     */
    @SuppressWarnings("empty-statement")
    public GridPane getGridPane() {
        try {
            gridPane.add(new Label("K = " + numOfAncestries), 0, rowPointer); // add K value.

            int colIndex = 1;
            for (StackedBarChart<String, Number> admixChart : listOfCharts) {
                // define chart properties
                admixChart.getStylesheets().add(Genesis.class.getResource("css/admixture.css").toExternalForm());
                admixChart.setCategoryGap(0); // remove gaps in categories
                admixChart.setLegendVisible(false);

                // set the chart size
                admixChart.setMinWidth(Double.MIN_VALUE);
                admixChart.setMaxWidth(Double.MAX_VALUE);

                // clear legend items
                Legend legend = (Legend) admixChart.lookup(".chart-legend");
                legend.getItems().clear();

                // set the margins of the chart
                GridPane.setMargin(admixChart, new Insets(0, 0, -3, -3)); //  top right bottom left

                // remove last rowPointer - population group labels
                gridPane.getChildren().removeIf(node -> GridPane.getRowIndex(node) == rowPointer + 1);

                // define Group names for individuals
                Label chartGroupName = new Label(admixChart.getXAxis().getLabel());
                chartGroupName.setStyle("-fx-border-color: black;");
                chartGroupName.setAlignment(Pos.CENTER);
                chartGroupName.setMinWidth(Double.MIN_VALUE); // set its width to that of the column
                chartGroupName.setMaxWidth(Double.MAX_VALUE);
                gridPane.add(chartGroupName, colIndex, rowPointer + 2);

                // add event to the label
                chartGroupName.setOnMouseClicked((MouseEvent e) -> chartGroupNameClicked(e.getSource()));

                // remove the x-axis label from the stackedbar chart
                admixChart.getXAxis().setLabel(null);

                // add chart to a specific cell
                gridPane.add(admixChart, colIndex, rowPointer);
                
                // add listeners to chart
                // on left click mouse event handler
                admixChart.getData().forEach((serie) -> {
                    serie.getData().forEach((item) -> {
                        item.getNode().setOnMousePressed((MouseEvent event) -> {
                            MouseButton button = event.getButton();
                            if (button == MouseButton.PRIMARY) {
                                try {
                                    FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("view/AdmixtureIndividualDetails.fxml"));
                                    Parent parent = (Parent) fxmlLoader.load();
                                    Stage dialogStage = new Stage();
                                    dialogStage.setScene(new Scene(parent));
                                    dialogStage.setResizable(false);

                                    AdmixtureIndividualDetailsController admixtureIndividualDetailsController = fxmlLoader.getController();
                                    iidDetails = Project.individualPhenoDetails.get(item.getXValue()); // get pheno details of clicked individual
                                    admixtureIndividualDetailsController.setPhenoList(iidDetails);
                                    admixtureIndividualDetailsController.setValuesLabel(item.getYValue().toString()); // get Y value
                                    dialogStage.showAndWait();

                                } catch (IOException ex) {
                                    Logger.getLogger(AdmixtureGraphEventsHandler.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        });
                    });
                });

                // right click mouse event handler
                admixChart.setOnMouseClicked((MouseEvent event) -> {
                    MouseButton button = event.getButton();
                    if (button == MouseButton.SECONDARY) {

                        rowIndexOfClickedAdmixChart = GridPane.getRowIndex(admixChart);

                        // charts in the firstGroupLabel row
                        if (rowIndexOfClickedAdmixChart < rowPointer + 1) {
                            // create vbox for editing options
                            VBox editChartOptionsVBox = new VBox();
                            editChartOptionsVBox.setPadding(new Insets(10));
                            editChartOptionsVBox.setSpacing(5);

                            // declare two buttons and add them to the vbox
                            Button addLabelBtn = new Button("Create Label at Mouse Cursor");
                            addLabelBtn.setOnMouseClicked((MouseEvent labelEvent) -> {
                                System.out.println("Add implementation for adding ");
                            });

                            // declare population group options button
                            Button populationGroupOptionsBtn = new Button("Population Group Options (Colours)");

                            populationGroupOptionsBtn.setOnMouseClicked((MouseEvent popEvent) -> {

                                // close the parent stage when the button is clicked
                                optionsStage.close();

                                // borders for vbox and hbox containers
                                String cssLayout = "-fx-border-color: #d9d9d9;\n"
                                        + "-fx-border-width: 1;\n";

                                VBox optionsContainerVBox = new VBox();
                                optionsContainerVBox.setPadding(new Insets(10));
                                optionsContainerVBox.setSpacing(5);

                                HBox populationOptionsHBox = new HBox();
                                populationOptionsHBox.setStyle(cssLayout);
                                populationOptionsHBox.setSpacing(2);

                                // create the right vbox to keep the fam order and dominant order buttons
                                VBox rightVBox = new VBox(new Label("Sort within populations algorithmically"));
                                rightVBox.setPadding(new Insets(10));
                                rightVBox.setSpacing(5);
                                rightVBox.setStyle(cssLayout);

                                Button famOrderBtn = new Button("Fam order");
                                famOrderBtn.setOnMouseClicked((MouseEvent famOrderEvent) -> {
                                    System.out.println("add fam order code ");
                                });

                                Button dominantColourBtn = new Button("Dominant colour");
                                dominantColourBtn.setOnMouseClicked((MouseEvent dominantEvent) -> {
                                    System.out.println("add dominant color sort code ");
                                });
                                rightVBox.getChildren().addAll(famOrderBtn, dominantColourBtn);

                                // create the left vbox to keep all ancestor elements added to hbox
                                leftVBox = new VBox(new Label("Change and order colours"));
                                leftVBox.setPadding(new Insets(10));
                                leftVBox.setSpacing(5);
                                leftVBox.setStyle(cssLayout);

                                // for every ancestor, get its color, create a button with its name and create a sort button
                                for (int i = numOfAncestries-1; i >=0; i--) {

                                    // HBox of ancestor buttons in the leftVBox
                                    HBox ancenstorHBox = new HBox(10);
                                    ancenstorHBox.setId("Ancestry"+i);

                                    // rectangle to display ancestor colors
                                    Rectangle ancestorColorDisplay = new Rectangle();
                                    ancestorColorDisplay.setWidth(25);
                                    ancestorColorDisplay.setHeight(25);
                                    ancestorColorDisplay.setArcWidth(5);
                                    ancestorColorDisplay.setArcHeight(5);

                                    String defaultColor = ".default-color" + i + ".chart-bar";
                                    // get the default color of every ancestor (using the default-color0,1,2 class) and fill it in the rectangle
                                    StackPane node = (StackPane) admixChart.getData().get(i).getData().get(i).getNode().lookup(defaultColor);
                                    Background bg = node.getBackground();
                                    Paint paint = bg.getFills().get(0).getFill();
                                    ancestorColorDisplay.setFill(paint);
//                                    ancestorColorDisplay.setFill(Color.web(AdmixtureGraph.admixColors.get(i)));

                                    //  set ancestor name -> used to name buttons and as a label for color options window
                                    String ancestorName = admixChart.getData().get(i).getName();

                                    // create ancestor button
                                    Button ancenstorNameBtn = new Button("Change " + ancestorName);

                                    // and mouse click events to the button - to load a color options stage with shift up or down buttons
                                    int serieIndex = i;
                                    ancenstorNameBtn.setOnMouseClicked((MouseEvent anacestorEvent) -> {
                                        try {
                                            FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("view/AncestorOptions.fxml"));
                                            Parent parent = (Parent) fxmlLoader.load();
                                            Stage dialogStage = new Stage();
                                            dialogStage.setScene(new Scene(parent));
                                            dialogStage.setResizable(false);

                                            AncestorOptionsController ancestorOptionsController = fxmlLoader.getController();
                                            ancestorOptionsController.setAncestorNumberLabel(ancestorName);
                                            ancestorOptionsController.setDefaultAncestorColor(paint);
                                            ancestorOptionsController.setAncestryPosition(listOfAncenstorHBox.indexOf(ancenstorHBox));

                                            dialogStage.showAndWait();

                                            // change the color of the ancestor color display if the color was selected
                                            if (ancestorOptionsController.isIsColorSelected()) {
                                                ancestorColorDisplay.setFill(ancestorOptionsController.getChosenPaint());

                                                String selectedColor = ancestorOptionsController.getChosenColor();

                                                // change the color of series
                                                for (StackedBarChart<String, Number> stackedbarChart : listOfCharts) {
                                                    stackedbarChart.getData().forEach((series) -> {
                                                        series.getData().forEach((bar) -> {
                                                            bar.getNode().lookupAll(".default-color" + serieIndex + ".chart-bar")
                                                                    .forEach(n -> n.setStyle("-fx-background-color: #" + selectedColor + ";"));
                                                        });
                                                    });

                                                }

                                            } else {
                                                ; // no color is selected
                                            }

                                        } catch (IOException e) {
                                        }

                                    });

                                    // create a sort individuals button
                                    Button colorSortBtn = new Button("Sort indivs by colour");
                                    colorSortBtn.setOnMouseClicked((MouseEvent dominantEvent) -> {
                                        System.out.println("Add sorting by colour code here");
                                    });

                                    // for every serie, store its default color, change color btn, and sort btn in HBox
                                    ancenstorHBox.getChildren().addAll(ancestorColorDisplay, ancenstorNameBtn, colorSortBtn);

                                    // add the the HBox to the list of HBoxes.
                                    listOfAncenstorHBox.add(ancenstorHBox);

                                }

                                // add the hbox to its parent (the left vbox)
                                leftVBox.getChildren().addAll(listOfAncenstorHBox);

                                // add the left and right vbox to their parent hbox
                                populationOptionsHBox.getChildren().addAll(leftVBox, rightVBox);

                                // create done button - > add it to the hbox
                                Button doneBtn = new Button("Done");
                                doneBtn.setAlignment(Pos.CENTER);

                                // define what happens when the done btn is clicked
                                // should close the stage and the list of hboxes for reordering purpose
                                doneBtn.setOnMouseClicked((MouseEvent dominantEvent) -> {
                                    optionsStage.close();
                                    listOfAncenstorHBox.clear(); // clear all hboxes in the list
                                });

                                HBox buttonCollectionHBox = new HBox(doneBtn);
                                buttonCollectionHBox.setAlignment(Pos.CENTER);

                                // add all the above parent hboxes to their parent vbox 
                                optionsContainerVBox.getChildren().addAll(populationOptionsHBox, buttonCollectionHBox);

                                // then show the stage
                                sceneForPopulationGroupBtn = new Scene(optionsContainerVBox);
                                optionsStage.setTitle(null);
                                optionsStage.setScene(sceneForPopulationGroupBtn);
                                optionsStage.setResizable(false);
                                optionsStage.show();

                            });

                            // chart editing options -> firstGroupLabel window to display when you right click the chart
                            editChartOptionsVBox.getChildren().addAll(addLabelBtn, populationGroupOptionsBtn);
                            scene = new Scene(editChartOptionsVBox);
                            optionsStage.setTitle("Options");
                            optionsStage.setScene(scene);
                            optionsStage.setResizable(false);
                            optionsStage.show();

                        } else { // charts in the last row
                            System.out.println("Design interface for Last or middle charts");
                        }
                    }
                });

                // incremement the column index
                colIndex++;

            }

        } catch (Exception e) {
            System.out.println("No chart dude");//do nothing
        }
        return gridPane;

    }

    /**
     * provide the list of ancestor HBoxes intent: the indexes of its HBoxes are
     * swapped by the ancestor option controller, new swapped boxes are
     * displayed when the controller scene is closed.
     *
     * @return
     */
    public static ArrayList<HBox> getListOfAncenstorHBox() {
        return listOfAncenstorHBox;
    }

    /**
     * this is the container for all the ancestor HBoxes it displays HBoxes
     * according to their swapped order
     *
     * @return
     */
    public static VBox getLeftVBox() {
        return leftVBox;
    }

    /**
     * track row with the charts that will be modified
     *
     * @return
     */
    public static int getRowIndexOfClickedAdmixChart() {
        return rowIndexOfClickedAdmixChart;
    }

    private void chartGroupNameClicked(Object source) {
        if (!(source instanceof Label)) {
            return;
        }
        Label lbl = (Label) source;

        if (labelClickCounter == 0) {
            firstGroupLabel = lbl;
        } else {
            secondGroupLabel = lbl;
            swap();
        }
        labelClickCounter = ++labelClickCounter % 2;  // changes values between 0 1
    }

    private void swap() {
        int firstRow = GridPane.getRowIndex(firstGroupLabel);
        int firstCol = GridPane.getColumnIndex(firstGroupLabel);
        int secondRow = GridPane.getRowIndex(secondGroupLabel);
        int secondCol = GridPane.getColumnIndex(secondGroupLabel);

        // swap their column constraints
        Collections.swap(gridPane.getColumnConstraints(), firstCol, secondCol);
        
        // remove group existing labels
        gridPane.getChildren().removeAll(firstGroupLabel, secondGroupLabel);

        // swap population group nodes
        gridPane.add(firstGroupLabel, secondCol, secondRow);
        gridPane.add(secondGroupLabel, firstCol, firstRow);
        
        int r = 0;
        ObservableList<Node> children = gridPane.getChildren();
        while (r < firstRow) {
            for (Node node : children) {
                if (GridPane.getColumnIndex(node) == firstCol && GridPane.getRowIndex(node) == r) {
                    firstChart = node;
                }
                if (GridPane.getColumnIndex(node) == secondCol && GridPane.getRowIndex(node) == r) {
                    secondChart = node;
                }
            }
            
            if(firstChart!=null && secondChart!=null){
                // remove nodes
                gridPane.getChildren().removeAll(firstChart, secondChart);

                // swap nodes
                gridPane.add(firstChart, secondCol, r);
                gridPane.add(secondChart, firstCol, r);
            
            }else{
                ;
            }
            
            // reset nodes
            firstChart = null;
            secondChart = null;
            
            r++;
        }
            
        
    }

}
