package org.h3abionet.genesis.controller;

import com.sun.javafx.charts.Legend;
import java.io.File;
import org.h3abionet.genesis.model.Project;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.Zoom;
import javafx.scene.chart.Chart;
import javafx.scene.transform.Transform;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
/*
 * Copyright (C) 2018 scott
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 *
 * @author scott This is the main controller
 */
public class Open0Controller implements Initializable {

    @FXML
    private FontSelectorController fontSelectorController;
    @FXML
    private PCADataInputController pCADataInputController;
    @FXML
    private TableView<?> tableTab;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab admixtureTab;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Button newadmixture;
    @FXML
    private Button newpca;
    @FXML
    private Button newproject;
    @FXML
    private Button threeDButton;
    @FXML
    private Button dataButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button downloadButton;
    @FXML
    private Button individualButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button selectionButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button fileButton;
    @FXML
    private Button helpButton;

    private ProjectDetailsController projectDetailsController;
    private Project project;
    private static Tab pcaTab;
    private static int tabCount = 0;
    private static ScatterChart<Number, Number> chart;

    @FXML
    private void newProject(ActionEvent event) throws IOException {
        projectDetailsController.loadProjDialogEntry();

    }

    @FXML
    private void newPCA(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(Genesis.class.getResource("view/PCADataInput.fxml"));
        Stage stage = new Stage();
        stage.initOwner(newpca.getScene().getWindow());
        stage.setScene(new Scene((Parent) loader.load()));
        stage.setResizable(false);
        stage.showAndWait();

        PCADataInputController controller = loader.getController();

        try {
            addChart(controller);
        } catch (NullPointerException e) {
            ; //do nothing if the controller is null
        }

    }

    @FXML
    private void loadData(ActionEvent event) throws IOException {
        pCADataInputController.setPcaDialogStage();
        PCADataInputController controller = PCADataInputController.getController();
        try {
            addChart(controller);            
        } catch (Exception e) {
           ;
        }

    }

    @FXML
    private void fontSelector(ActionEvent event) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("view/FontSelector.fxml"));
            Stage iconStage = new Stage();
            iconStage.initOwner(settingsButton.getScene().getWindow());
            iconStage.setScene(new Scene((Parent) fxmlLoader.load()));
            iconStage.setResizable(false);
            iconStage.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Sorry, there is no chart to format");

            alert.showAndWait();
        }

    }

    /**
     * Saves the chart in the right format
     */
    @FXML
    public void saveChart() throws IOException {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);

        if (chart == null) {
            alert.setContentText("There is no chart to save");
            alert.showAndWait();
        } else {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save file");
            fileChooser.setInitialFileName("chart.pdf");
            File file = fileChooser.showSaveDialog(null);
            SnapshotParameters sp = new SnapshotParameters();
            Transform transform = Transform.scale(5, 5);
            sp.setTransform(transform);
            WritableImage image = chart.snapshot(sp, null);

            if (file != null) {

//                try {
                    // save as png
                    System.out.println(file);
//                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                    
                    //save as pdf
                    float POINTS_PER_INCH = 72;
                    float POINTS_PER_MM = 1 / (10 * 2.54f) * POINTS_PER_INCH;
                    
                    PDDocument newPDF=new PDDocument();
                    PDPage chartPage = new PDPage(new PDRectangle(297 * POINTS_PER_MM, 210 * POINTS_PER_MM));
                    newPDF.addPage(chartPage);
                    
                    PDImageXObject pdImageXObject = LosslessFactory.createFromImage(newPDF, SwingFXUtils.fromFXImage(image, null));
                    PDPageContentStream contentStream = new PDPageContentStream(newPDF, chartPage);
                    contentStream.drawImage(pdImageXObject, pdImageXObject.getWidth(), pdImageXObject.getHeight());
                    contentStream.close();               
                    newPDF.save(file);
                    newPDF.close();
//                } catch (IOException e) {
//                    alert.setContentText("An ERROR occurred while saving the file.");
//                    alert.showAndWait();
//                }
            } else {
                alert.setContentText("File selection cancelled.");
                alert.showAndWait();
            }
        }
    }

    /**
     * This function returns the chart
     * when called by the individual details controller
     * @return
     */
    public static ScatterChart<Number, Number> getChart() {
        return chart;
    }

    private void addChart(PCADataInputController controller) {
        chart = controller.getChart();
        chart.getStylesheets().add(Genesis.class.getResource("css/scatterchart.css").toExternalForm());
        
        if (chart != null) {
            String xAxisLabel = chart.getXAxis().getLabel();
            String yAxisLabel = chart.getYAxis().getLabel();
            String x = xAxisLabel.substring(4, xAxisLabel.length());
            String y = yAxisLabel.substring(4, yAxisLabel.length());

            tabCount++;
            pcaTab = new Tab();
            pcaTab.setText("PCA " + x + " & " + y);
            pcaTab.setClosable(true);
            pcaTab.setId("tab" + tabCount);
            
            // Set chart container and its anchors to 0 to make the parent 
            // AnchorPane resize the child to fill it's whole area:
            final AnchorPane chartContainer = new AnchorPane();
            AnchorPane.setBottomAnchor(chart, 0.0);
            AnchorPane.setTopAnchor(chart, 0.0);
            AnchorPane.setLeftAnchor(chart, 0.0);
            AnchorPane.setRightAnchor(chart, 0.0);
            chartContainer.getChildren().add(chart);
            
            // add the chart and the container to the Zoom class              
            Zoom zoom = new Zoom(chart, chartContainer);
            
            // add the container to the tab
            pcaTab.setContent(chartContainer);
            tabPane.getTabs().add(pcaTab);
    
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

                                IndividualDetailsController individualDetailsController = fxmlLoader.getController();
                                individualDetailsController.setPcaLabel(xAxisLabel + ": " + data.getXValue() + "\n" + yAxisLabel + ": " + data.getYValue());
                                individualDetailsController.setIconDisplay(data.getNode());
                                dialogStage.showAndWait();

                            } catch (IOException ex) {
                                Logger.getLogger(Open0Controller.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                    });
                    // manage tooltip delay
                    Tooltip.install(data.getNode(), new Tooltip(data.getXValue() + "\n" + data.getYValue()));
                }
            }
            
            // Legend section
            for (Node n : chart.getChildrenUnmodifiable()) {
                if (n instanceof Legend) {
                    Legend l = (Legend) n;
                    l.setOnMouseClicked(event -> {
                        setLegendPosition(chart);
                    });
                    
                    for (Legend.LegendItem li : l.getItems()) {
                        for (XYChart.Series<Number, Number> s : chart.getData()) {
                            if (s.getName().equals(li.getText())) {
                               li.getSymbol().setCursor(Cursor.HAND); // Hint user that legend symbol is clickable
                                li.getSymbol().setOnMouseClicked(me -> {
                                    if (me.getButton() == MouseButton.PRIMARY) {
                                        for (XYChart.Data<Number, Number> d : s.getData()) {
                                            if (d.getNode() != null) {
                                                d.getNode().setVisible(!d.getNode().isVisible()); // Toggle visibility of every node in the series
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            }

        } else {
            //            return to the main window if no pcas were selected
            ;
        }
    }

    @FXML
    private void help(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog("Help");
        dialog.setTitle("Help");
        dialog.setHeaderText("Hi, let's help you");
        dialog.setContentText("Please enter your keywords:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> System.out.println("Your keywords: " + name));
    }

    @FXML
    private void closeProgram(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setContentText("Are you sure you want to close this program?");

        ButtonType yesBtn = new ButtonType("YES");
        ButtonType cancelBtn = new ButtonType("NO");
        alert.getButtonTypes().setAll(yesBtn, cancelBtn);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == yesBtn) {
            Platform.exit();
        } else {
            ; //do nothing
        }

    }

    @Override
    public void initialize(java.net.URL arg0, ResourceBundle arg1) {
        pCADataInputController = new PCADataInputController();
        pCADataInputController.setOpen0Controller(this);
        projectDetailsController = new ProjectDetailsController();
    }
    
    /**
     * This method takes a node parameter (the chart)
     * and loads a legend position selection window
     * It is called by the addChart method under the legend section 
     */
    private void setLegendPosition(Node ch){
        List<String> choices = new ArrayList<>();
        choices.add("bottom");
        choices.add("right");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("right", choices);
        dialog.setTitle("Legend");
        dialog.setHeaderText("Select legend position");
        dialog.setContentText("Position:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(letter -> ch.lookup(".chart").setStyle("-fx-legend-side: "+letter+";"));
    }

}
