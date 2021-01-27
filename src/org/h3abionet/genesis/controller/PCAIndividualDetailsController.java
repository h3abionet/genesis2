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
import java.util.Arrays;
import java.util.HashMap;
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
import org.h3abionet.genesis.model.Project;
import org.h3abionet.genesis.model.Subject;

/**
 *
 * @author Henry
 */
public class PCAIndividualDetailsController implements Initializable {

    @FXML
    private Label pcaLabel;

    @FXML
    private ListView<String> phenoListView;

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
    private ComboBox<String> phenotypeComboBox;

    @FXML
    private Button btnChangeIcon;

    @FXML
    private Button btnOK;

    @FXML
    private Button btnCancel;

    private ScatterChart<Number, Number> chart;

    private String iconSVGShape;
    private int iconSize;
    private String iconColor;
    private String iconType;
    private String clickedIconStyle;
    private String xValueOfClickedPoint;
    private String yValueOfClickedPoint;
    private String phenotypeGroup;

    private boolean hideRadioBtnClicked = false;
    private boolean topRadioBtnClicked = false;
    private boolean clearRadioBtnClicked = false;
    private boolean seriesRadioBtnClicked = false;

    private PCAGraph pcaGraph;
    private Project project;

    // set icon properties from the iconOptionsController
    public void setIconSize(int iconSize) {
        this.iconSize = iconSize;
    }

    public void setIconColor(String iconColor) {
        this.iconColor = iconColor;
    }

    public void setIconType(String iconType) {
        this.iconType = iconType;
        // set the svg shape
        getSVGShape(iconType);
    }

    // display pc/pheno values on labels
    public void setPcaLabel(String coordinates) {
        pcaLabel.setText(coordinates);
    }

    public void setPhenoListView(ObservableList<String> phenoDetails) {
        phenoListView.getItems().addAll(phenoDetails);
    }

    /**
     * display icon of the clicked point
     * @param style
     */
    public void setIconDisplay(String style) {
        setClickedIconStyle(style);
        iconDisplay.setStyle(clickedIconStyle);
    }

    /**
     * set the data node style
     * @param clickedIconStyle
     */
    public void setClickedIconStyle(String clickedIconStyle) {
        this.clickedIconStyle = clickedIconStyle;
    }

    // load iconOptionsController upon request
    @FXML
    private void changeIcon(ActionEvent event) throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("view/IconOptions.fxml"));
            Stage iconStage = new Stage();
            iconStage.initOwner(iconDisplay.getScene().getWindow());
            Scene icon_root = new Scene((Parent) fxmlLoader.load());
            IconOptionsController iconCtrlr = fxmlLoader.getController();
            iconCtrlr.setPCAController(this);
            iconCtrlr.setPcaGraph(pcaGraph);

            // set default variables
            iconCtrlr.setIconDisplay(clickedIconStyle); // show clicked icon
            iconCtrlr.setIconColorValue(iconColor);
            iconCtrlr.setIconTypeValue(iconType);
            iconCtrlr.setIconSizeValue(iconSize);

            iconCtrlr.setIconTypeComboValues(FXCollections.observableArrayList(new ArrayList<>(project.getIconTypes().values()))); // star, kite, ...

            iconStage.setScene(icon_root);
            iconStage.setResizable(false);
            iconStage.showAndWait();
    }

    // get radio selections (only one selection at a time)
    @FXML
    private void getClickedRadioBtn(ActionEvent event) {
        if (hideRadioBtn.isSelected()) {
            hideRadioBtnClicked = true;
            topRadioBtnClicked = false;
            seriesRadioBtnClicked = false;
            clearRadioBtnClicked = false;
            enableOK();
        }else if (topRadioBtn.isSelected()) {
            topRadioBtnClicked = true;
            hideRadioBtnClicked = false;
            seriesRadioBtnClicked = false;
            clearRadioBtnClicked = false;
            enableOK();
        }else if (clearRadioBtn.isSelected()) {
            clearRadioBtnClicked = true;
            hideRadioBtnClicked = false;
            topRadioBtnClicked = false;
            seriesRadioBtnClicked = false;
            enableOK();
        }else if (seriesRadioBtn.isSelected()) {
            seriesRadioBtnClicked = true;
            hideRadioBtnClicked = false;
            topRadioBtnClicked = false;
            clearRadioBtnClicked = false;
            enableOK();

            // set default group - if group is not null
            if(phenotypeGroup!= null){ // LWK
                phenotypeComboBox.getSelectionModel().select(phenotypeGroup);
            }else {;}

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

                String xValue = String.valueOf(data.getXValue());
                String yValue = String.valueOf(data.getYValue());

                if((xValue.equals(xValueOfClickedPoint) & yValue.equals(yValueOfClickedPoint))){
                    if(hideRadioBtnClicked){
                        pcaGraph.hideIndividual(series, data);
                        break;
                    }else if(topRadioBtnClicked){
                        data.getNode().toFront();
                        break;
                    }else if (clearRadioBtnClicked) {
                        for(Subject s: project.getPcGraphSubjectsList().get(project.getCurrentTabIndex())){
                            if(s.getPcs() != null && Arrays.asList(s.getPcs()).contains(xValue) && Arrays.asList(s.getPcs()).contains(yValue)){
                                int currentTab = project.getCurrentTabIndex();
                                // get group / phenotype color for graph in this tab
                                HashMap groupColors = project.getListOfGraphsGroupColors().get(currentTab);
                                HashMap groupIcon = project.getListOfGraphsGroupIcons().get(currentTab);
                                if(project.getPhenoColumnNumber()==3) {

                                    // get the color and icon of the chart in this position
                                    String color = (String) groupColors.get(s.getPhenotypeA());
                                    String icon = (String) groupIcon.get(s.getPhenotypeA());

                                    // set back the subject properties
                                    s.setIcon(icon);
                                    s.setColor(color);

                                    // set the style to default
                                    data.getNode().lookup(".chart-symbol").setStyle(pcaGraph.getStyle(color, icon, iconSize));
                                }else{
                                    // get the color and icon of the chart in this position based on column 4 of the pheno file
                                    String color = (String) groupColors.get(s.getPhenotypeB());
                                    String icon = (String) groupIcon.get(s.getPhenotypeB());

                                    // set back the subject properties
                                    s.setIcon(icon);
                                    s.setColor(color);

                                    // set the style
                                    data.getNode().lookup(".chart-symbol").setStyle(pcaGraph.getStyle(color, icon, iconSize));
                                }
                                break;
                            }
                        }
                        break;
                    }else if(seriesRadioBtnClicked){
                        if (series.getName().equals(phenotypeComboBox.getValue())) {
                            // change the color and icon of this phenotype category
                            project.getListOfGraphsGroupColors().get(project.getCurrentTabIndex()).put(series.getName(), iconColor);
                            project.getListOfGraphsGroupIcons().get(project.getCurrentTabIndex()).put(series.getName(), iconSVGShape);

                            // set icons and colors for all subjects of this group
                            for(Subject s: project.getPcGraphSubjectsList().get(project.getCurrentTabIndex())){
                                if(project.getPhenoColumnNumber()==3){
                                    if(s.getPhenotypeA().equals(phenotypeComboBox.getValue())){
                                        s.setColor(iconColor);
                                        s.setIcon(iconSVGShape);
                                    }
                                }else{
                                    if(s.getPhenotypeB().equals(phenotypeComboBox.getValue())){
                                        s.setColor(iconColor);
                                        s.setIcon(iconSVGShape);
                                    }
                                }
                            }

                            // change population group color and icon on the chart
                            for (XYChart.Data<Number, Number> dt : series.getData()) {
                                dt.getNode().lookup(".chart-symbol").setStyle(pcaGraph.getStyle(iconColor, iconSVGShape, iconSize));
                            }

                            // change population group color and icon the legend
                            for (Node n : chart.getChildrenUnmodifiable()) {
                                if (n.getClass().toString().equals("class com.sun.javafx.charts.Legend")) {
                                    TilePane tn = (TilePane) n;
                                    ObservableList<Node> children = tn.getChildren();
                                    for(int i=0;i<children.size();i++){
                                        Label lab = (Label) children.get(i).lookup(".chart-legend-item");
                                        if(lab.getText().equals(phenotypeComboBox.getValue())){
                                            // divide legend icon size by 2 - otherwise it will be twice bigger than the icons of the graph
                                            lab.getGraphic().setStyle(pcaGraph.getStyle(iconColor, iconSVGShape, iconSize/2));
                                            break;
                                        }
                                    }
                                }
                            }


                        }
                    }else {
                        // set icons and colors for all subjects of this group
                        for(Subject s: project.getPcGraphSubjectsList().get(project.getCurrentTabIndex())){
                            if(s.getPcs() != null && Arrays.asList(s.getPcs()).contains(xValue) && Arrays.asList(s.getPcs()).contains(yValue)){
                                s.setColor(iconColor);
                                s.setIcon(iconSVGShape);
                                s.setIconSize(iconSize);
                                data.getNode().lookup(".chart-symbol").setStyle(pcaGraph.getStyle(iconColor, iconSVGShape, iconSize));
                                break;
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
        // disable ok button if no radio button is selected
        if(!hideRadioBtnClicked || !topRadioBtnClicked || !seriesRadioBtnClicked || !clearRadioBtnClicked){
            btnOK.setDisable(true);
        }
        chart = MainController.getPcaChart();
    }

    public void enableOK() {
        btnOK.setDisable(false);
    }

    public void setPCAGraph(PCAGraph pcaGraph) {
        this.pcaGraph = pcaGraph;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setClickedPoint(String xValue, String yValue) {
        this.xValueOfClickedPoint = xValue;
        this.yValueOfClickedPoint = yValue;
    }

    public void setPhenotypeComboBox(ObservableList<String> phenoGroups) {
        phenotypeComboBox.setItems(phenoGroups);
    }

    public void setPhenotypeGroup(String phenotypeGroup) {
        this.phenotypeGroup = phenotypeGroup;
    }

    /**
     * return icon svg shape for every selected icon type
     * @param iconTypeValue
     * @return
     */
    public String getSVGShape(String iconTypeValue) {
        project.getIconTypes().forEach((key, value) -> {
            if (value.equals(iconTypeValue)) {
                iconSVGShape = (String) key;
            }
        });
        return iconSVGShape;
    }

    public void setChosenIconDisplay(String style) { chosenIconDisplay.setStyle(style); }

    /**
     * default icon shape
     * @param icon
     */
    public void setIconSVGShape(String icon) {
        // set svg shape of clicked subject
        this.iconSVGShape = icon;
        // then set the type of its svg shape
        this.iconType = (String) project.getIconTypes().get(icon);
    }

}
