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

import javafx.application.HostServices;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.h3abionet.genesis.Genesis;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
        chart.getStylesheets().add(Genesis.class.getClassLoader().getResource("pca.css").toExternalForm());

        if (chart != null) {
            String xAxisLabel = chart.getXAxis().getLabel();
            String yAxisLabel = chart.getYAxis().getLabel();
            String x = xAxisLabel.substring(4);
            String y = yAxisLabel.substring(4);

            // Set chart container and its anchors to 0 to make the parent 
            // AnchorPane resize the child to fill it's whole area:
            chartContainer = new AnchorPane();
            AnchorPane.setBottomAnchor(chart, 0.0);
            AnchorPane.setTopAnchor(chart, 0.0);
            AnchorPane.setLeftAnchor(chart, 0.0);
            AnchorPane.setRightAnchor(chart, 0.0);
            chartContainer.getChildren().add(chart);

            // add the chart and the container to the Zoom class              
//            Zoom zoom = new Zoom(chart, chartContainer);

        } else {
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
            FileChooser.ExtensionFilter jpgFilter = new FileChooser.ExtensionFilter("jpeg", "*.jpeg");
            FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("pdf", "*.pdf");
            fileChooser.getExtensionFilters().addAll(pngFilter, tiffFilter, jpgFilter, pdfFilter);
            File file = fileChooser.showSaveDialog(null);

            // tranform scale can be reduced for lower resolutions (10, 10 or 5, 5)
            int pixelScale = 5;

            int width = (int) Math.rint(pixelScale * chart.getWidth());
            int height = (int) Math.rint(pixelScale * chart.getHeight());
            WritableImage writableImage = new WritableImage(width, height);

            SnapshotParameters sp = new SnapshotParameters();
            sp.setTransform(Transform.scale(pixelScale, pixelScale));
            Image image = chart.snapshot(sp, writableImage);
            BufferedImage bufImage = SwingFXUtils.fromFXImage(image, null);

            if (file != null) {

                try {
                    String fileName = file.getName();
                    String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1, file.getName().length());

                    // save as png or pdf
                    switch (fileExtension) {
                        case "png":
                            ImageIO.write(bufImage, "png", file);
                            break;
                        case "tiff":
                            ImageIO.write(bufImage, "tiff", file);
                            break;
                        case "jpeg":
                            BufferedImage bufImageRGB = new BufferedImage(bufImage.getWidth(),
                                    bufImage.getHeight(), BufferedImage.TYPE_INT_RGB);
                            Graphics2D graphics = bufImageRGB.createGraphics();
                            graphics.drawImage(bufImage, 0, 0, null);

                            ImageIO.write(bufImageRGB, "jpeg", file);
                            break;
                        case "pdf":
                            PDDocument newPDF = new PDDocument();
                            PDPage chartPage = new PDPage(new PDRectangle(bufImage.getWidth(), bufImage.getHeight()));
                            newPDF.addPage(chartPage);

                            PDImageXObject pdImageXObject = LosslessFactory.createFromImage(newPDF, bufImage);
                            PDPageContentStream contentStream = new PDPageContentStream(newPDF, chartPage);

                            // draw image sizes can be adjusted for smaller images
                            contentStream.drawImage(pdImageXObject, 0, 0, bufImage.getWidth(), bufImage.getHeight());
                            contentStream.close();

                            newPDF.save(file);
                            newPDF.close();
                            Desktop.getDesktop().browse(file.toURI());
                            break;
                    }

                } catch (IOException e) {
                    alert.setContentText("An ERROR occurred while saving the file.");
                    alert.showAndWait();
                }
            } else {
                // do nothing
            }
        }

    }

}

