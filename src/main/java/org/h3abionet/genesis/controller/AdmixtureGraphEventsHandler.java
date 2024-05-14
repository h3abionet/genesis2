/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.Alert;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.h3abionet.genesis.model.AdmixtureGraph;
import org.h3abionet.genesis.model.Project;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.h3abionet.genesis.Genesis;

/**
 *
 * @author henry
 */
public class AdmixtureGraphEventsHandler {

    // to keep list of admix charts from the main controller
    private ArrayList<StackedBarChart<String, Number>> listOfCharts;

    // pointer to the gridpane defined in the main controller 
    private GridPane gridPane;

    private ArrayList<String> iidDetails;

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
    private Project project;
    private AdmixtureGraph admixtureGraph;
    private MainController mainController;


    public AdmixtureGraphEventsHandler() {
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
                            // was 297x297
                            PDPage chartPage = new PDPage(new PDRectangle(830 *
                                    POINTS_PER_MM* 3,
                                    240 * POINTS_PER_MM * 3));
                            newPDF.addPage(chartPage);

                            PDImageXObject pdImageXObject = LosslessFactory.createFromImage(newPDF, SwingFXUtils.fromFXImage(image, null));
                            PDPageContentStream contentStream = new PDPageContentStream(newPDF, chartPage);

                            // draw image sizes can be adjusted for smaller images
                            // was 830, 570 – not same aspect ratio as drawing soace
                            contentStream.drawImage(pdImageXObject, 5, 5, 830*
                                    POINTS_PER_MM*3,
                                    240*
                                    POINTS_PER_MM*3);
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

    public void setProject(Project project) {
        this.project = project;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
