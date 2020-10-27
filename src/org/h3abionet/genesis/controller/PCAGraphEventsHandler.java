/*
 * Copyright 2019 University of the Witwatersrand, Johannesburg on behalf of the Pan-African Bioinformatics Network for H3Africa.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.h3abionet.genesis.controller;

import com.idrsolutions.image.tiff.TiffEncoder;
//import com.sun.javafx.charts.Legend;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
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
import org.h3abionet.genesis.model.PCAGraph;

/**
 *
 * @author scott
 */
public class PCAGraphEventsHandler {

    private XYChart<Number, Number> chart;
    private AnchorPane chartContainer;

    public PCAGraphEventsHandler(XYChart<Number, Number> chart) {
        this.chart = chart;

    }

    @SuppressWarnings("empty-statement")
    public AnchorPane addGraph() {
        chart.getStylesheets().add(Genesis.class.getResource("css/pca.css").toExternalForm());

        if (chart != null) {
            String xAxisLabel = chart.getXAxis().getLabel();
            String yAxisLabel = chart.getYAxis().getLabel();
            String x = xAxisLabel.substring(4, xAxisLabel.length());
            String y = yAxisLabel.substring(4, yAxisLabel.length());

            // Set chart container and its anchors to 0 to make the parent 
            // AnchorPane resize the child to fill it's whole area:
            chartContainer = new AnchorPane();
            AnchorPane.setBottomAnchor(chart, 0.0);
            AnchorPane.setTopAnchor(chart, 0.0);
            AnchorPane.setLeftAnchor(chart, 0.0);
            AnchorPane.setRightAnchor(chart, 0.0);
            chartContainer.getChildren().add(chart);

            // add the chart and the container to the Zoom class              
            Zoom zoom = new Zoom(chart, chartContainer);

            for (XYChart.Series<Number, Number> series : chart.getData()) {
                for (XYChart.Data<Number, Number> data : series.getData()) {
                    data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            try {
                                FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("view/IndividualDetails.fxml"));
                                Parent parent = (Parent) fxmlLoader.load();
                                Stage dialogStage = new Stage();
                                dialogStage.setScene(new Scene(parent));
                                dialogStage.setResizable(false);

                                PCAIndividualDetailsController individualDetailsController = fxmlLoader.getController();
                                String xValue = data.getXValue().toString();
                                String yValue = data.getYValue().toString();
                                individualDetailsController.setPcaLabel(xAxisLabel + ": " + xValue + "\n" + yAxisLabel + ": " + yValue);
                                // get pheno data using x & y co-ordinates
                                for(String [] s: PCAGraph.getPcasWithPhenoList() ){
                                    // if an array in pcasWithPhenoList has both x & y
                                    if(Arrays.asList(s).contains(xValue) && Arrays.asList(s).contains(yValue)){
                                        // get pheno data: [MKK, AFR, pc1, pc2, pc3, ..., FID IID]
                                        ObservableList<String> phenos = FXCollections.<String>observableArrayList(s[s.length-1], s[0], s[1]);
                                        individualDetailsController.setPhenoLabel(phenos);
                                        break;
                                    }
                                }
                                individualDetailsController.setIconDisplay(data.getNode().getStyle());
                                dialogStage.showAndWait();

                            } catch (Exception ex) {
                                ;
                            }
                        }

                    });
                    // manage tooltip delay
                    Tooltip.install(data.getNode(), new Tooltip(data.getXValue() + "\n" + data.getYValue()));
                }
            }

            // set the legend
            for (Node n : chart.getChildrenUnmodifiable()) { // get all nodes on the chart
                if (n.getClass().toString().equals("class com.sun.javafx.charts.Legend")) { // if legend
                    TilePane tn = (TilePane) n; // get tile pane
                    ObservableList<Node> children = tn.getChildren(); // get all items (population groups)
                    for(Node child : children){
                        Label lab = (Label) child.lookup(".chart-legend-item"); // popn group + its icon
                        for (XYChart.Series<Number, Number> s : chart.getData()) {
                            if (s.getName().equals(lab.getText())) {
                                lab.setCursor(Cursor.HAND); // Hint user that legend symbol is clickable
                                lab.setOnMouseClicked(me -> {
                                    // Toggle group (phenotype) visibility on left click
                                    if (me.getButton() == MouseButton.PRIMARY) {
                                        for (XYChart.Data<Number, Number> d : s.getData()) {
                                            if (d.getNode() != null) {
                                                d.getNode().setVisible(!d.getNode().isVisible()); // Toggle visibility of every node in the series
                                            }
                                        }
                                    }else{ // right click
                                        // show dialog for legend position and hiding phenotype
                                          List<String> choices = new ArrayList<>();
                                          choices.add("bottom");
                                          choices.add("right");

                                          ChoiceDialog<String> dialog = new ChoiceDialog<>("right", choices);
                                          dialog.setTitle("Legend");
                                          dialog.setHeaderText("Select legend position");
                                          dialog.setContentText("Position:");

                                          Optional<String> result = dialog.showAndWait();
                                          result.ifPresent(position -> chart.lookup(".chart").setStyle("-fx-legend-side: " + position + ";"));

                                    }
                                });
                                break;
                            }
                        }
                    }

                }
            }

        } else {
            ;
        }
        return chartContainer;
    }

    public void saveChart() throws Exception {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);

        if (chart == null) {
            alert.setContentText("There is no chart to save");
            alert.showAndWait();
        } else {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save chart");
            FileChooser.ExtensionFilter pngFilter = new FileChooser.ExtensionFilter("png", "*.png");
            FileChooser.ExtensionFilter tiffFilter = new FileChooser.ExtensionFilter("tiff", "*.tiff");
            FileChooser.ExtensionFilter jpgFilter = new FileChooser.ExtensionFilter("JPG", "*.JPG");
            FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("pdf", "*.pdf");
            fileChooser.getExtensionFilters().addAll(pngFilter, tiffFilter,jpgFilter,pdfFilter);
            File file = fileChooser.showSaveDialog(null);

            // tranform scale can be reduced for lower resolutions (10, 10 or 5, 5)

            int pixelScale = 5;
            int width = (int) Math.rint(pixelScale*chart.getWidth());
            int height = (int) Math.rint(pixelScale*chart.getHeight());
            WritableImage writableImage = new WritableImage(width, height);

            SnapshotParameters sp = new SnapshotParameters();
            sp.setTransform(Transform.scale(pixelScale, pixelScale));
            Image image = chart.snapshot(sp, writableImage);

            BufferedImage bufImageARGB = SwingFXUtils.fromFXImage(image, null);
            BufferedImage bufImageRGB = new BufferedImage(bufImageARGB.getWidth(),
                    bufImageARGB.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = bufImageRGB.createGraphics();
            graphics.drawImage(bufImageARGB, 0, 0, null);

            if (file != null) {

                try {
                    String fileName = file.getName();
                    String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1, file.getName().length());

                    // save as png or pdf (as A4 landscape)
                    switch (fileExtension) {
                        case "png":
                            ImageIO.write(bufImageRGB, "png", file);
                            break;
                        case "tiff":
//                            ImageIO.write(bufImageRGB, "tiff", file);
                            OutputStream out = new FileOutputStream(file);
                            TiffEncoder tiffEncoder = new TiffEncoder();
                            tiffEncoder.setCompressed(true);
                            tiffEncoder.write(bufImageRGB, out);
                            break;
                        case "JPG":
                            ImageIO.write(bufImageRGB, "JPG", file);
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
//                ; 
            }
        }

    }


}

