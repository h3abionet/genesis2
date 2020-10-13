/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

//import com.sun.javafx.charts.Legend;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.Alert;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
import org.h3abionet.genesis.model.AdmixtureGraph;
import org.h3abionet.genesis.model.Project;

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

    /**
     * To store all ancestor HBoxes which will be added the leftVBox
     */
    private static ArrayList<HBox> listOfAncenstorHBox;

    /**
     * before swapping the series, it is important to know which row contains
     * the charts being modified
     */
    private static int rowIndexOfClickedAdmixChart;

    private static int labelClickCounter = 0;
    private static int kClickCounter = 0;
    private StackPane firstGroupLabel, secondGroupLabel;
    private static StackPane firstKLabel, secondKLabel;
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
        listOfAncenstorHBox = new ArrayList<>();
    }

    /**
     *
     */
    @SuppressWarnings("empty-statement")
    public GridPane getGridPane() {
        try {
            Text kValue = new Text("K = " + numOfAncestries);
            StackPane kValuePane = new StackPane(kValue);
            kValuePane.setAlignment(Pos.CENTER);
            kValuePane.setMargin(kValue, new Insets(5));
            kValuePane.hoverProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    kValuePane.setCursor(Cursor.HAND);
                }
            });
            // add event to the label
            kValuePane.setOnMouseClicked((MouseEvent e) -> kValueClicked(e.getSource()));
            
            gridPane.add(kValuePane, 0, rowPointer); // add K value.

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
//                Legend legend = (Legend) admixChart.lookup(".chart-legend");
//                legend.getItems().clear();

                // set the margins of the chart
                GridPane.setMargin(admixChart, new Insets(0, 0, -3, -3)); // TODO remove the chart content margins on axes

                // remove last rowPointer - population group labels
                gridPane.getChildren().removeIf(node -> GridPane.getRowIndex(node) == rowPointer+1);
                
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
                                    ;
                                }
                            }
                        });
                    });
                });
                
                // right click mouse event handler
                admixChart.setOnMouseClicked((MouseEvent event) -> {
                    MouseButton button = event.getButton();
                    if (button == MouseButton.SECONDARY) {
                        // set the rowIndex of the clicked chart
                        rowIndexOfClickedAdmixChart = GridPane.getRowIndex(admixChart);
                        try {
                            FXMLLoader loader = new FXMLLoader(Genesis.class.getResource("view/AdmixtureOptions.fxml"));
                            Parent p = (Parent) loader.load();
                            Stage dialogStage = new Stage();
                            dialogStage.setScene(new Scene(p));
                            dialogStage.setResizable(false);
                            AdmixtureOptionsController aop = loader.getController();
                            aop.setAdmixChart(admixChart);
                            dialogStage.show();

                        } catch (IOException ex) {
                            Genesis.throwErrorException("Failed to load plot format options");
                        }
                    }
                });

                // incremement the column index
                colIndex++;
            }

        } catch (Exception e) {
            Genesis.throwErrorException("Sorry. Try Again");//do nothing
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
    
    /**
     * identify clicked population group labels
     * @param source 
     */
    private void chartGroupNameClicked(Object source) {
        if (!(source instanceof StackPane)) {
            return;
        }
        StackPane lbl = (StackPane) source;

        if (labelClickCounter == 0) {
            firstGroupLabel = lbl;
        } else {
            secondGroupLabel = lbl;
            groupNameSwap();
        }
        labelClickCounter = ++labelClickCounter % 2;  // changes values between 0 1

    }
    
    /**
     * groupNameSwap population group labels
     */
    private void groupNameSwap() {
        // column and row index for clicked labels
        int firstRow = GridPane.getRowIndex(firstGroupLabel);
        int firstCol = GridPane.getColumnIndex(firstGroupLabel);
        int secondRow = GridPane.getRowIndex(secondGroupLabel);
        int secondCol = GridPane.getColumnIndex(secondGroupLabel);

        // groupNameSwap their column constraints
        Collections.swap(gridPane.getColumnConstraints(), firstCol, secondCol);

        // remove group existing labels
        gridPane.getChildren().removeAll(firstGroupLabel, secondGroupLabel);

        // groupNameSwap population group nodes
        gridPane.add(firstGroupLabel, secondCol, secondRow);
        gridPane.add(secondGroupLabel, firstCol, firstRow);

        int rowIndex = 0;
        ObservableList<Node> children = gridPane.getChildren();
        while (rowIndex < firstRow) {
            for (Node node : children) {
                if (GridPane.getColumnIndex(node) == firstCol && GridPane.getRowIndex(node) == rowIndex) {
                    firstChart = node;
                }
                if (GridPane.getColumnIndex(node) == secondCol && GridPane.getRowIndex(node) == rowIndex) {
                    secondChart = node;
                }
            }

            if (firstChart != null && secondChart != null) {
                // remove nodes
                gridPane.getChildren().removeAll(firstChart, secondChart);

                // groupNameSwap nodes
                gridPane.add(firstChart, secondCol, rowIndex);
                gridPane.add(secondChart, firstCol, rowIndex);

            } else {
                ;
            }

            // reset nodes
            firstChart = null;
            secondChart = null;

            rowIndex++;
        }

    }
    
    /**
     * identify clicked rows (using k value labels)
     * @param source 
     */
    private void kValueClicked(Object source) {
        if (!(source instanceof StackPane)) {
            return;
        }
        StackPane kValuePane = (StackPane) source;

        if (kClickCounter == 0) {
            firstKLabel = kValuePane;
        } else {
            secondKLabel = kValuePane;
            swapPlots();
        }
        kClickCounter = ++kClickCounter % 2;  // changes values between 0 1
    }
    
    /**
     * swap admixture plots
     */
    private void swapPlots() {
        int firstRow = GridPane.getRowIndex(firstKLabel);
        int firstCol = GridPane.getColumnIndex(firstKLabel);
        System.out.println(firstRow);
        int secondRow = GridPane.getRowIndex(secondKLabel);
        int secondCol = GridPane.getColumnIndex(secondKLabel);
        System.out.println(secondRow);
        
        // swap the plot lists
        Collections.swap(MainController.getAllAdmixtureCharts(), firstRow, secondRow);
        
        // remove group existing labels
        gridPane.getChildren().removeAll(firstKLabel, secondKLabel);

        // swap the stackpanes with k values first
        gridPane.add(firstKLabel, secondCol, secondRow);
        gridPane.add(secondKLabel, firstCol, firstRow);

        int columnIndex = 1; // swap all the plots from second column to last
        ObservableList<Node> children = gridPane.getChildren();
        while (columnIndex < children.size()+1) {
            for (Node node : children) {
                if (GridPane.getColumnIndex(node) == columnIndex && GridPane.getRowIndex(node) == firstRow) {
                    firstChart = node;
                }
                if (GridPane.getColumnIndex(node) == columnIndex && GridPane.getRowIndex(node) == secondRow) {
                    secondChart = node;
                }
            }

            if (firstChart != null && secondChart != null) {
                // remove nodes
                gridPane.getChildren().removeAll(firstChart, secondChart);

                // groupNameSwap nodes
                gridPane.add(firstChart, columnIndex, secondRow);
                gridPane.add(secondChart, columnIndex, firstRow);

            } else {
                ;
            }

            // reset nodes
            firstChart = null;
            secondChart = null;

            columnIndex++;
        }

    }
    
  /**
   * Save the parent of all plots
   * @param vBox 
   */
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
                    alert.setContentText("ERROR occurred while saving the file.");
                    alert.showAndWait();
                }
            } else {
                // do nothing if file selector is closed
                ;
            }
        }

    }

}
