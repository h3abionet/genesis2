/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.model;

import com.sun.javafx.charts.Legend;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import jfxtras.labs.util.event.MouseControlUtil;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.controller.AdmixtureIndividualDetailsController;
import org.h3abionet.genesis.controller.AncestorOptionsController;

/**
 *
 * @author henry
 */
public class AdmixtureGraph {

    Pheno pheno;
    AncestorOptionsController ancestorOptionsController;
    private ArrayList<StackedBarChart<String, Number>> listOfCharts;
    private GridPane gridPane;
    private String[] iidDetails;
    private int kValue;
    private int rowIndex;
    private Stage optionsStage;
    private Scene scene, sceneForAddLabelBtn, sceneForPopulationGroupBtn;
    private int maxRowIndex;

    public AdmixtureGraph(ArrayList<StackedBarChart<String, Number>> listOfCharts, GridPane gridPane, int kValue, int rowIndex) {
        this.listOfCharts = listOfCharts;
        this.gridPane = gridPane;
        this.kValue = kValue;
        this.rowIndex = rowIndex;
        pheno = new Pheno();
        optionsStage = new Stage();
        setNumberOfRows(gridPane);

    }

    private void setNumberOfRows(GridPane gridPane) {
        maxRowIndex = gridPane.getChildren().stream().mapToInt(n -> {
            Integer row = GridPane.getRowIndex(n);
            Integer rowSpan = GridPane.getRowSpan(n);

            // default values are 0 / 1 respecively
            return (row == null ? 0 : row) + (rowSpan == null ? 0 : rowSpan - 1);
        }).max().orElse(-1);

    }

    /**
     *
     */
    @SuppressWarnings("empty-statement")
    public GridPane getGridPane() {
        try {

            gridPane.add(new Label("K = " + kValue), 0, rowIndex); // add K value.

            int colIndex = 1;
            for (StackedBarChart<String, Number> s : listOfCharts) {
                // define chart properties
                s.getStylesheets().add(Genesis.class.getResource("css/admixture.css").toExternalForm());
                s.setCategoryGap(0); // remove gaps in categories
                s.setLegendVisible(false);
                s.setPrefWidth(kValue);

                // set the chart size
                s.setMinWidth(Double.MIN_VALUE);
                s.setMaxWidth(Double.MAX_VALUE);

                // clear legend items
                Legend legend = (Legend) s.lookup(".chart-legend");
                legend.getItems().clear();

                // set the margins of the chart
                GridPane.setMargin(s, new Insets(0, 0, -3, -3)); //  top right bottom left

                // remove last rowIndex
                gridPane.getChildren().removeIf(node -> GridPane.getRowIndex(node) == rowIndex + 1);

                // define Group names for individuals
                Label chartGroupName = new Label(s.getXAxis().getLabel());
                chartGroupName.setStyle("-fx-border-color: black;");
                chartGroupName.setAlignment(Pos.CENTER);
                chartGroupName.setMinWidth(Double.MIN_VALUE); // set its width to that of the column
                chartGroupName.setMaxWidth(Double.MAX_VALUE);
                gridPane.add(chartGroupName, colIndex, rowIndex + 2);

                // remove the x-axis label from the stackedbar chart
                s.getXAxis().setLabel(null);

                // add chart to a specific cell
                gridPane.add(s, colIndex, rowIndex);

                // add listeners to chart
                // get the nodes of every individual.
                s.getData().forEach((serie) -> {
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
                                    iidDetails = pheno.getIndividualPhenoDetails().get(item.getXValue()); // get pheno details of clicked individual
                                    admixtureIndividualDetailsController.setPhenoList(iidDetails);
                                    admixtureIndividualDetailsController.setValuesLabel(item.getYValue().toString()); // get Y value
                                    dialogStage.showAndWait();

                                } catch (IOException ex) {
                                    Logger.getLogger(AdmixtureGraph.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        });
                    });
                });

                // right click mouse event handler
                s.setOnMouseClicked((MouseEvent event) -> {
                    MouseButton button = event.getButton();
                    if (button == MouseButton.SECONDARY) {

                        if (GridPane.getRowIndex(s) == 0) {
                            // create vbox for editing options
                            VBox editChartOptionsVBox = new VBox();
                            editChartOptionsVBox.setPadding(new Insets(10));
                            editChartOptionsVBox.setSpacing(5);

                            // declare two buttons and add them to the vbox
                            Button addLabelBtn = new Button("Create Label at Mouse Cursor");

                            addLabelBtn.setOnMouseClicked((MouseEvent labelevent) -> {
                                // close the parent stage when the button is clicked
                                optionsStage.close();

                                TextInputDialog dialog = new TextInputDialog();
                                dialog.setTitle("Text");
                                dialog.setHeaderText(null);
                                dialog.setGraphic(null);
                                dialog.setContentText("Text:");

                                Optional<String> result = dialog.showAndWait();
                                // set text default color and position on the chart
                                Text text = new Text(300, 50, result.get());
                                text.setFill(Color.BLACK);

                                // can drag text
                                MouseControlUtil.makeDraggable(text);
                                // add text to chart
                                addShapeToChart(text, s);
                                // add mouse event to text for editing options
                                text.setOnMouseClicked((MouseEvent e) -> {
                                    if (e.getButton() == MouseButton.SECONDARY) {
                                        // use class label options -- accepts chosen label as a paremeter
                                        LabelOptions labelOptions = new LabelOptions(text);
                                        // modify the chosen label
                                        labelOptions.modifyLabel();
                                    }
                                });

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
                                Button dominantColourBtn = new Button("Dominant colour");
                                rightVBox.getChildren().addAll(famOrderBtn, dominantColourBtn);

                                // create the left vbox to keep all ancestor elements added to hbox
                                VBox leftVBox = new VBox(new Label("Change and order colours"));
                                leftVBox.setPadding(new Insets(10));
                                leftVBox.setSpacing(5);
                                leftVBox.setStyle(cssLayout);

                                // for every ancestor, get its color, create a button with its name and create a sort button
                                for (int i = 0; i < kValue; i++) {

                                    // HBox of ancestor buttons in the leftVBox
                                    HBox ancenstorHBox = new HBox(10);

                                    // rectangle to display ancestor colors
                                    Rectangle ancestorColorDisplay = new Rectangle();
                                    ancestorColorDisplay.setWidth(25);
                                    ancestorColorDisplay.setHeight(25);
                                    ancestorColorDisplay.setArcWidth(5);
                                    ancestorColorDisplay.setArcHeight(5);

                                    String defaultColor = ".default-color" + i + ".chart-bar";
                                    // get the default color of every ancestor (using the default-color0,1,2 class) and fill it in the rectangle
                                    StackPane node = (StackPane) s.getData().get(i).getData().get(i).getNode().lookup(defaultColor);
                                    Background bg = node.getBackground();
                                    Paint paint = bg.getFills().get(0).getFill();
                                    ancestorColorDisplay.setFill(paint);

                                    //  set ancestor name -> used to name buttons and as a label for color options window
                                    String ancestorName = "Ancestor " + i;

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

                                            ancestorOptionsController = fxmlLoader.getController();
                                            ancestorOptionsController.setAncestorNumberLabel(ancestorName);
                                            ancestorOptionsController.setDefaultAncestorColor(paint);

                                            dialogStage.showAndWait();

                                            // change the color of the color display
                                            ancestorColorDisplay.setFill(ancestorOptionsController.getColor());

                                            // change the color of series
                                            for (StackedBarChart<String, Number> stackedbarChart : listOfCharts) {
                                                stackedbarChart.getData().forEach((series) -> {
                                                    series.getData().forEach((bar) -> {
                                                        bar.getNode().lookupAll(".default-color" + serieIndex + ".chart-bar")
                                                                .forEach(n -> n.setStyle("-fx-background-color: #"+ancestorOptionsController.getChosenColor()+";"));
                                                    });
                                                });

                                            }

                                        } catch (IOException e) {
                                        }

                                    });

                                    // create a sort individuals button
                                    Button colorSortBtn = new Button("Sort indivs by colour");

                                    // add all buttons and the rectangle (color display to the hbox)
                                    ancenstorHBox.getChildren().addAll(ancestorColorDisplay, ancenstorNameBtn, colorSortBtn);

                                    // add the hbox to its parent (the left vbox)
                                    leftVBox.getChildren().add(ancenstorHBox);
                                }

                                // add the left and right vbox to their parent hbox
                                populationOptionsHBox.getChildren().addAll(leftVBox, rightVBox);

                                // create done and cancel buttons - > add them to the hbox
                                Button doneBtn = new Button("Done");
                                Button cancelBtn = new Button("Cancel");
                                HBox buttonCollectionHBox = new HBox(30, cancelBtn, doneBtn);
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

                            // chart editing options -> first window to display when you right click the chart
                            editChartOptionsVBox.getChildren().addAll(addLabelBtn, populationGroupOptionsBtn);
                            scene = new Scene(editChartOptionsVBox);
                            optionsStage.setTitle("Options");
                            optionsStage.setScene(scene);
                            optionsStage.setResizable(false);
                            optionsStage.show();

                        } else if (GridPane.getRowIndex(s) == rowIndex) {
                            System.out.println(GridPane.getRowIndex(s));
                            System.out.println("Design Last chart interface");
                        } else {
                            System.out.println("Design Middle chart interface");
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

    private void addShapeToChart(Shape shape, StackedBarChart<String, Number> chart) {
        Pane p = (Pane) chart.getChildrenUnmodifiable().get(1);
//        Region r = (Region) p.getChildren().get(0);
        Group gr = new Group();

        shape.setOnMouseEntered(e -> {
            shape.getScene().setCursor(Cursor.HAND);
            shape.setEffect(new DropShadow(20, Color.BLUE));
        });

        shape.setOnMouseExited(e -> {
            shape.getScene().setCursor(Cursor.DEFAULT);
            shape.setEffect(null);
        });

        gr.getChildren().addAll(shape);
        p.getChildren().add(gr);

    }

}
