/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import com.sun.javafx.charts.Legend;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.PCAGraph;

/**
 *
 * @author Henry
 */
public class PCAIndividualDetailsController implements Initializable {

    @FXML
    private Label pcaLabel;

    @FXML
    private ListView<String> phenoLabel;

    @FXML
    private RadioButton hideRadioBtn;

    @FXML
    private ToggleGroup individualGroup;

    @FXML
    private RadioButton topRadioBtn;

    @FXML
    private RadioButton clearRadioBtn;

    @FXML
    private RadioButton seriesRadioBtn;

    @FXML
    private AnchorPane iconDisplay;

    @FXML
    private AnchorPane chosenIconDisplay;

    @FXML
    private ComboBox<String> groupName;

    @FXML
    private Button btnChangeIcon;

    @FXML
    private Button btnOK;

    @FXML
    private Button btnCancel;

    private ScatterChart<Number, Number> chart;
//    private HiddenIndividualsController hiddenIndividualsController;

    private static int iconSize;
    private static String iconColor;
    private static String iconType;

    private boolean hideRadioBtnClicked;
    private boolean topRadioBtnClicked;
    private boolean clearRadioBtnClicked;
    private boolean seriesRadioBtnClicked;

    // set icon properties from the iconOptionsController
    public void setIconSize(int iconSize) {
        PCAIndividualDetailsController.iconSize = iconSize;
        System.out.println("Icon_size is " + iconSize);
    }

    public void setIconColor(String iconColor) {
        PCAIndividualDetailsController.iconColor = iconColor;
        System.out.println("Icon Color is " + iconColor);
    }

    public void setIconType(String iconType) {
        PCAIndividualDetailsController.iconType = iconType;
        System.out.println("Icon type is" + iconType);
    }

    // return requested icon properties
    public int getIconSize() {
        return iconSize;
    }

    public String getIconColor() {
        return iconColor;
    }

    public String getIconType() {
        return iconType;
    }

    // display pc/pheno values on labels  
    public void setPcaLabel(String coordinates) {
        pcaLabel.setText(coordinates);
    }

    public void setPhenoLabel(ObservableList<String> pheno_data) {
        phenoLabel.getItems().addAll(pheno_data);
    }

    // display icon 
    public void setIconDisplay(String style) {
        iconDisplay.setStyle(style);

    }

    // load iconOptionsController upon request
    @FXML
    private void changeIcon(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("view/IconOptions.fxml"));
        Stage iconStage = new Stage();
        iconStage.initOwner(iconDisplay.getScene().getWindow());
        iconStage.setScene(new Scene((Parent) fxmlLoader.load()));
        iconStage.setResizable(false);
        iconStage.showAndWait();

        chosenIconDisplay.setVisible(true);
        chosenIconDisplay.setStyle("-fx-shape: \"" + iconType + "\";"
                + "-fx-background-color: #" + iconColor + ";"
                + "-fx-background-radius: " + iconSize + "px;"
                + "-fx-padding: " + iconSize + "px;"
                + "-fx-pref-width: " + iconSize + "px;"
                + "fx-pref-height: " + iconSize + "px;");
    }

    // get radio selections (only one selection at a time)
    @FXML
    private void getClickedRadioBtn(ActionEvent event) {
        if (hideRadioBtn.isSelected()) {
            hideRadioBtnClicked = true;
        }
        if (topRadioBtn.isSelected()) {
            topRadioBtnClicked = true;
        }
        if (clearRadioBtn.isSelected()) {
            clearRadioBtnClicked = true;
        }
        if (seriesRadioBtn.isSelected()) {
            seriesRadioBtnClicked = true;
        }
    }

    @FXML
    @SuppressWarnings("empty-statement")
    private void entryOkButton(ActionEvent event) {

        for (XYChart.Series<Number, Number> series : chart.getData()) {
            if (seriesRadioBtnClicked) {
                    if (series.getName().equals(groupName.getValue())) {                       
                        for (XYChart.Data<Number, Number> dt : series.getData()) {                            
                            dt.getNode().lookup(".chart-symbol").setStyle("-fx-shape: \"" + iconType + "\";"
                                    + "-fx-background-color: #" + iconColor + ";");

                            for (Node n : chart.getChildrenUnmodifiable()) {
                                if (n instanceof Legend) {
                                    Legend l = (Legend) n;
                                    for (Legend.LegendItem li : l.getItems()) {                                        
                                        if (li.getText().equals(groupName.getValue())) {
                                            li.getSymbol().lookup(".chart-legend-item-symbol").setStyle("-fx-shape: \"" + iconType + "\";"
                                                    + "-fx-background-color: #" + iconColor + ";");
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
            }

            for (XYChart.Data<Number, Number> data : series.getData()) {
                if (hideRadioBtnClicked) {
                    data.getNode().setOnMouseClicked(e -> {
                        // hide the visibility of the button
                        data.getNode().setVisible(!data.getNode().isVisible());
                                                
                        // get its coordinates
                        String xValue = data.getXValue().toString();
                        String yValue = data.getYValue().toString();
                        
                        // get pheno data using x & y co-ordinates
                        for(String [] s: PCAGraph.getPcasWithPhenoList()){
                            // if an array in pcasWithPhenoList has both x & y
                            if(Arrays.asList(s).contains(xValue) && Arrays.asList(s).contains(yValue)){
                                // get pheno data: [MKK, AFR, pc1, pc2, pc3, ..., FID IID]
                                String[] coord = {xValue, yValue, s[0]};
                                HiddenIndividualsController.getHidenIds().put(s[s.length-1], coord);
                                break;
                            }
                        }
                        
                    });
                }
                if (topRadioBtnClicked) {
                    data.getNode().setOnMouseClicked(e -> {
                        data.getNode().lookup(".chart-symbol").setStyle("-fx-shape: \"" + iconType + "\";"
                                + "-fx-background-color: #" + iconColor + ";"
                                + "-fx-background-radius: " + iconSize + "px;"
                                + "-fx-padding: " + iconSize + "px;"
                                + "-fx-pref-width: " + iconSize + "px;"
                                + "fx-pref-height: " + iconSize + "px;");
                        data.getNode().toFront();
                    });
                }
                if (clearRadioBtnClicked) {
                    data.getNode().setOnMouseClicked(e -> {
                        data.getNode().setStyle(null);
                    });
                } else {
                    ; // do nothing     
                }

            }
        }

        Genesis.closeOpenStage(event);
    }

    @FXML
    private void entryCancelButton(ActionEvent event) {
        Genesis.closeOpenStage(event);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        hiddenIndividualsController = new HiddenIndividualsController();
        chosenIconDisplay.setVisible(false);
        chart = MainController.getPcaChart();

        ObservableList<String> groups = FXCollections.observableArrayList();

        chart.getData().forEach((series) -> {
            groups.add(series.getName());
        });

        groupName.setItems(groups);

        hideRadioBtnClicked = false;
        topRadioBtnClicked = false;
        clearRadioBtnClicked = false;
        seriesRadioBtnClicked = false;

    }

}
