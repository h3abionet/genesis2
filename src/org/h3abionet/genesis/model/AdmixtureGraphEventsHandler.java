/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.model;

import com.sun.javafx.charts.Legend;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.controller.AdmixtureIndividualDetailsController;
import org.h3abionet.genesis.controller.AdmixtureOptionsController;
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
    private StackPane firstGroupLabel, secondGroupLabel;
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
            Label groupLbl = new Label("K = " + numOfAncestries);
            GridPane.setMargin(groupLbl, new Insets(0, 5, 0 ,5)); // add a margin of 5 to right and left
            
            gridPane.add(groupLbl, 0, rowPointer); // add K value.

            int colIndex = 1;
            for (StackedBarChart<String, Number> admixChart : listOfCharts) {
                // define chart properties
                admixChart.getStylesheets().add(Genesis.class.getResource("css/admixture.css").toExternalForm());
                admixChart.setCategoryGap(0); // remove gaps in iids
                admixChart.setLegendVisible(false);

                // set the chart size
                admixChart.setMinWidth(Double.MIN_VALUE);
                admixChart.setMaxWidth(Double.MAX_VALUE);

                // clear legend items
                Legend legend = (Legend) admixChart.lookup(".chart-legend");
                legend.getItems().clear();

                // set the margins of the chart
                GridPane.setMargin(admixChart, new Insets(0, 0, -3, -3)); // TODO remove the chart content margins on axes

                // remove last rowPointer - population group labels
                gridPane.getChildren().removeIf(node -> GridPane.getRowIndex(node) == rowPointer + 1);

                // define Group names for individuals
                Text chartGroupName = new Text(admixChart.getXAxis().getLabel());
                StackPane pane = new StackPane(chartGroupName);
                pane.setAlignment(Pos.CENTER);
                pane.setMargin(chartGroupName, new Insets(5));
                String paneCssStyle = "-fx-border-color: black; -fx-border-width: 1px;";
                pane.setStyle(paneCssStyle);
                
                pane.hoverProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        pane.setCursor(Cursor.HAND);
                        pane.setStyle(paneCssStyle+"-fx-background-color: #e1f3f7;");
                    } else {
                        pane.setStyle(paneCssStyle+"-fx-background-color: transparent;");

                    }
                });
                
                gridPane.add(pane, colIndex, rowPointer + 2);
                
                gridPane.getChildren().removeIf(node -> GridPane.getRowIndex(node) == rowPointer + 1);

                // add event to the label
                pane.setOnMouseClicked((MouseEvent e) -> chartGroupNameClicked(e.getSource()));

                // remove the x-axis label from the stackedbar chart
                admixChart.setId(admixChart.getXAxis().getLabel()); // set chart id
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
                                    
                                    // show subject details when clicked
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
                        
                        try{
                            FXMLLoader loader = new FXMLLoader(Genesis.class.getResource("view/AdmixtureOptions.fxml"));
                            Parent parent = (Parent) loader.load();
                            Stage dialogStage = new Stage();
                            dialogStage.setScene(new Scene(parent));
                            dialogStage.setResizable(false);
                            AdmixtureOptionsController aop = loader.getController();
                            aop.setAdmixChart(admixChart);
                            dialogStage.showAndWait();
                        }catch(Exception e){
                            Genesis.throwErrorException("Sorry! Try again");
                        }
                                    
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
                                    listOfCharts.forEach((s) -> {
                                        sortToFamOrder(s);
                                    });
                                    
                                });

                                Button dominantColourBtn = new Button("Dominant colour");
                                dominantColourBtn.setOnMouseClicked((MouseEvent devt) -> {
                                        for (StackedBarChart<String, Number> stackedBarChart : listOfCharts) {
                                            // store totals of yvalue elements in every serie                                            
                                            ArrayList<Double> sumList = new ArrayList<>();
                                            
                                            // get sum of all yvalues in every serie / ancestry
                                            for(int s=0; s<stackedBarChart.getData().size(); s++){
                                                double sum = 0;
                                                for(int n=0; n<stackedBarChart.getData().get(s).getData().size(); n++){
                                                    sum += stackedBarChart.getData().get(s).getData().get(n).getYValue().doubleValue();
                                       
                                                }
                                                sumList.add(sum);                                           
                                            }
                                            
                                            // get index of the max value in sumList = dorminant serie index in the chart
                                            int ancestryIndex = sumList.indexOf(Collections.max(sumList));

                                            // sort chart by serie with max yvalues (dominant)
                                            sortChartByColor(stackedBarChart, ancestryIndex);
                                            
                                        }

                                    });
                                
                                rightVBox.getChildren().addAll(famOrderBtn, dominantColourBtn);

                                // create the left vbox to keep all ancestor elements added to hbox
                                leftVBox = new VBox(new Label("Change and order colours"));
                                leftVBox.setPadding(new Insets(10));
                                leftVBox.setSpacing(5);
                                leftVBox.setStyle(cssLayout);

                                // for every ancestor, get its color, create a button with its name and create a sort button
                                for (int i = numOfAncestries - 1; i >= 0; i--) {

                                    // HBox of ancestor buttons in the leftVBox
                                    HBox ancenstorHBox = new HBox(10);
                                    ancenstorHBox.setId("Ancestry" + i);

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
                                    int serieNum = i+1;
                                    Button colorSortBtn = new Button("Sort Ancestry "+serieNum);
                                    colorSortBtn.setOnMouseClicked((MouseEvent devt) -> {
                                        for (StackedBarChart<String, Number> stackedBarChart : listOfCharts) {
                                            // get last character on a btn and use it use it as the index of the ancestry
                                            String ancestryNumber = colorSortBtn.getText().substring(colorSortBtn.getText().length() - 1);
                                            // sort
                                            sortChartByColor(stackedBarChart, Integer.valueOf(ancestryNumber)-1);
                                        }
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
     * track row with the charts that will be modified
     *
     * @return
     */
    public static int getRowIndexOfClickedAdmixChart() {
        return rowIndexOfClickedAdmixChart;
    }

    private void chartGroupNameClicked(Object source) {
        if (!(source instanceof StackPane)) {
            return;
        }
        StackPane lbl = (StackPane) source;

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

            if (firstChart != null && secondChart != null) {
                // remove nodes
                gridPane.getChildren().removeAll(firstChart, secondChart);

                // swap nodes
                gridPane.add(firstChart, secondCol, r);
                gridPane.add(secondChart, firstCol, r);

            } else {
                ;
            }

            // reset nodes
            firstChart = null;
            secondChart = null;

            r++;
        }

    }
    
    /**
     * sort individuals according to fam order
     * @param s 
     */
    private void sortToFamOrder(StackedBarChart<String, Number> s){
        // get sorted iids
        CategoryAxis xAxis = (CategoryAxis) s.getXAxis();
        ObservableList<String> sorted_iids = xAxis.getCategories();
        
        // get fam order iids for this chart (population group) using its id as the key
        List<String> fam_order_iids = AdmixtureGraph.famOrder.get(s.getId());
        
        // for loop run this number of times
        int numOfIndividuals = s.getData().get(0).getData().size();
        
        // swap positions
        for (int j = 0; j < numOfIndividuals; j++) {
            String temp = sorted_iids.remove(sorted_iids.indexOf(fam_order_iids.get(j)));
            sorted_iids.add(j, temp);
            xAxis.setCategories(sorted_iids);
        }
    }
    
    /**
     * sort iids based on selected serie or ancestry
     * @param stackedBarChart
     * @param ancestryNumber 
     */
    private void sortChartByColor(StackedBarChart<String, Number> stackedBarChart, int ancestryNumber){
        
        CategoryAxis xAxis = (CategoryAxis) stackedBarChart.getXAxis();
        
        XYChart.Series<String, Number>  chosenAncestry = stackedBarChart.getData().get(ancestryNumber);
        int numOfIndividuals = chosenAncestry.getData().size();

        ObservableList<String> iids = xAxis.getCategories();
        
        // sort the chosen ancestry
        chosenAncestry.getData().sort(Comparator.comparingDouble(d -> d.getYValue().doubleValue()));

        // get positions of iids from the sorted ancestry
        String[] sorted_iids = new String[numOfIndividuals];
        for (int p = 0; p < numOfIndividuals; p++) {
            sorted_iids[p] = chosenAncestry.getData().get(p).getXValue();
        }

        // swap positions
        for (int j = 0; j < numOfIndividuals; j++) {
            String temp = iids.remove(iids.indexOf(sorted_iids[j]));
            iids.add(j, temp);
            xAxis.setCategories(iids);
        }

    }
    
    public void saveChart(VBox vBox) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);

        if (vBox == null) {
            alert.setContentText("There is no chart to save");
            alert.showAndWait();
        } else {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save chart");
            FileChooser.ExtensionFilter pngFilter = new FileChooser.ExtensionFilter("png", "*.png");
            FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("pdf", "*.pdf");
            fileChooser.getExtensionFilters().addAll(pngFilter, pdfFilter);
            File file = fileChooser.showSaveDialog(null);

            // tranform scale can be reduced for lower resolutions (10, 10 or 5, 5)
            int pixelScale = 5;
            WritableImage writableImage = new WritableImage((int)Math.rint(pixelScale*vBox.getWidth()),
                    (int)Math.rint(pixelScale*vBox.getHeight()));
            
            SnapshotParameters sp = new SnapshotParameters();
            sp.setTransform(Transform.scale(pixelScale, pixelScale));
            WritableImage image = vBox.snapshot(sp, writableImage);

            if (file != null) {

                try {
                    String fileName = file.getName();
                    String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1, file.getName().length());

                    // save as png or pdf (as A4 landscape)
                    switch (fileExtension) {
                        case "png":
                            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                            break;
                        case "pdf":
                            float POINTS_PER_INCH = 72;
                            float POINTS_PER_MM = 1 / (10 * 2.54f) * POINTS_PER_INCH;

                            PDDocument newPDF = new PDDocument();
                            PDPage chartPage = new PDPage(new PDRectangle(297 * POINTS_PER_MM, 210 * POINTS_PER_MM));
                            newPDF.addPage(chartPage);

                            PDImageXObject pdImageXObject = LosslessFactory.createFromImage(newPDF, SwingFXUtils.fromFXImage(image, null));
                            PDPageContentStream contentStream = new PDPageContentStream(newPDF, chartPage);

                            // draw image sizes can be adjusted for smaller images
                            contentStream.drawImage(pdImageXObject, 5, 5, 830, 570);
                            contentStream.close();

                            newPDF.save(file);
                            newPDF.close();
                            break;
                    }

                } catch (IOException e) {
                    alert.setContentText("An ERROR occurred while saving the file.");
                    alert.showAndWait();
                }
            } else {
                // do nothing if file selector is closed
                ;
            }
        }

    }

}
