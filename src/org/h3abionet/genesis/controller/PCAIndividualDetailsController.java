/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

//import com.sun.javafx.charts.Legend;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
import javafx.scene.layout.TilePane;
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

    private String iconSVGShape;
    private int iconSize = 5; // default value
    private String iconColor;
    private String iconType;
    private String clickedIconStyle;
    private String xValueOfClickedPoint;
    private String yValueOfClickedPoint;

    private boolean hideRadioBtnClicked = false;
    private boolean topRadioBtnClicked = false;
    private boolean clearRadioBtnClicked = false;
    private boolean seriesRadioBtnClicked = false;

    private PCAGraph pcaGraph;
    private ObservableList<String> pheno_data;

    // set icon properties from the iconOptionsController
    public void setIconSize(int iconSize) {
        this.iconSize = iconSize;
    }

    public void setIconColor(String iconColor) {
        this.iconColor = iconColor;
    }

    public void setIconType(String iconType) {
        this.iconType = iconType;
    }

    // display pc/pheno values on labels  
    public void setPcaLabel(String coordinates) {
        pcaLabel.setText(coordinates);
    }

    public void setPhenoLabel(ObservableList<String> pheno_data) {
        this.pheno_data = pheno_data;
        phenoLabel.getItems().addAll(pheno_data);
    }

    // display icon
    public void setIconDisplay(String style) {
        clickedIconStyle = style;
        iconDisplay.setStyle(clickedIconStyle);
    }

    // load iconOptionsController upon request
    @FXML
    private void changeIcon(ActionEvent event) throws IOException {
        if(seriesRadioBtnClicked) {
            FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("view/IconOptions.fxml"));
            Stage iconStage = new Stage();
            iconStage.initOwner(iconDisplay.getScene().getWindow());
            Scene icon_root = new Scene((Parent) fxmlLoader.load());
            IconOptionsController iconCtrlr = fxmlLoader.getController();
            iconCtrlr.setPCAController(this);

            // set default variables
            iconColor = (String) pcaGraph.getGroupColors().get(pheno_data.get(1));
            iconType = (String) pcaGraph.getIconsHashmap().get(pcaGraph.getGroupIcons().get(pheno_data.get(1)));

            iconCtrlr.setIconDisplay(clickedIconStyle); // show clicked icon
            iconCtrlr.setIconColorValue(iconColor);
            iconCtrlr.setIconTypeValue(iconType);
            iconCtrlr.setIconSizeValue(iconSize);

            iconCtrlr.setShapesList(FXCollections.observableArrayList(new ArrayList<>(pcaGraph.getIconsHashmap().values())));

            iconStage.setScene(icon_root);
            iconStage.setResizable(false);
            iconStage.showAndWait();
        }else {
            Genesis.throwInformationException("Please first check the group icon radio button");
        }

        // show users' icon details
        chosenIconDisplay.setStyle(pcaGraph.getStyle(iconColor, iconType, iconSize));
    }

    // get radio selections (only one selection at a time)
    @FXML
    private void getClickedRadioBtn(ActionEvent event) {
        if (hideRadioBtn.isSelected()) {
            hideRadioBtnClicked = true;
            topRadioBtnClicked = false;
            seriesRadioBtnClicked = false;
            clearRadioBtnClicked = false;
        }else if (topRadioBtn.isSelected()) {
            topRadioBtnClicked = true;
            hideRadioBtnClicked = false;
            seriesRadioBtnClicked = false;
            clearRadioBtnClicked = false;
        }else if (clearRadioBtn.isSelected()) {
            clearRadioBtnClicked = true;
            hideRadioBtnClicked = false;
            topRadioBtnClicked = false;
            seriesRadioBtnClicked = false;
        }else if (seriesRadioBtn.isSelected()) {
            seriesRadioBtnClicked = true;
            hideRadioBtnClicked = false;
            topRadioBtnClicked = false;
            clearRadioBtnClicked = false;
            // set default group
            groupName.getSelectionModel().select(pheno_data.get(1));
        }else{
            ;
        }
    }

    @FXML
    @SuppressWarnings("empty-statement")
    private void entryOkButton(ActionEvent event) {
        // hide or place the point on top
        for (XYChart.Series<Number, Number> series : chart.getData()) {
            for (XYChart.Data<Number, Number> data : series.getData()) {
                String x = String.valueOf(data.getXValue());
                String y = String.valueOf(data.getYValue());
                if((x.equals(xValueOfClickedPoint) & y.equals(yValueOfClickedPoint))){
                    if(hideRadioBtnClicked){
                        pcaGraph.hideIndividual(data, true);
                        break;
                    }else if(topRadioBtnClicked){
                        data.getNode().toFront();
                        break;
                    }else if (clearRadioBtnClicked) {
                            data.getNode().setStyle(null);
                    }else {
                        ;
                    }
                }
            }
        }

        if(seriesRadioBtnClicked){
            for (XYChart.Series<Number, Number> series : chart.getData()) {
                if (series.getName().equals(groupName.getValue())) {
//                        graph.getGroupIcons().put(series.getName(), iconType);
                    for (XYChart.Data<Number, Number> dt : series.getData()) {
                        dt.getNode().lookup(".chart-symbol").setStyle(pcaGraph.getStyle(iconColor, iconType, iconSize));

                        // set the legend
                        for (Node n : chart.getChildrenUnmodifiable()) {
                            if (n.getClass().toString().equals("class com.sun.javafx.charts.Legend")) {
                                TilePane tn = (TilePane) n;
                                ObservableList<Node> children = tn.getChildren();
                                for(int i=0;i<children.size();i++){
                                    Label lab = (Label) children.get(i).lookup(".chart-legend-item");
                                    if(lab.getText().equals(groupName.getValue())){
                                        lab.getGraphic().setStyle(pcaGraph.getStyle(iconColor, iconType, iconSize));
                                        break;
                                    }

                                }

                            }

                        }
                    }
                }
            }
        }
        // set it back to false
        hideRadioBtnClicked = false;
        topRadioBtnClicked = false;
        seriesRadioBtnClicked = false;
        clearRadioBtnClicked = false;
        Genesis.closeOpenStage(event);
    }

    @FXML
    private void entryCancelButton(ActionEvent event) {
        Genesis.closeOpenStage(event);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chart = MainController.getPcaChart();
    }

    public void enableOK() {
        btnOK.setDisable(false);
    }

    public void setPCAGraph(PCAGraph pcaGraph) {
        this.pcaGraph = pcaGraph;
    }

    public void setClickedPoint(String xValue, String yValue) {
        this.xValueOfClickedPoint = xValue;
        this.yValueOfClickedPoint = yValue;
    }

    public void setGroupName(ObservableList<String> groups) {
        groupName.setItems(groups);
    }

    public String getShape(String iconTypeValue) {
        pcaGraph.getIconsHashmap().forEach((key, value) -> {
            if (value.equals(iconTypeValue)) {
                iconSVGShape = (String) key;
            }
            return;
        });
        return iconSVGShape;
    }
}
