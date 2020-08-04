/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.AdmixtureGraph;

/**
 * FXML Controller class
 *
 * @author henry
 */
public class AdmixtureOptionsController implements Initializable {

    @FXML
    private Button creatLabelBtn;

    @FXML
    private Button populationGroupOptionsBtn;

    @FXML
    private Button previousGraphColourBtn;

    @FXML
    private Button nextGraphColourBtn;

    @FXML
    private ComboBox<String> orderCombobox;

    @FXML
    private Button deleteGraphBtn;

    @FXML
    private Button cancelBtn;

    // list of admixture charts
    private ArrayList<StackedBarChart<String, Number>> listOfCharts;

    // clicked admixture chart
    private StackedBarChart<String, Number> admixChart;

    // To store all ancestor HBoxes which will be added the leftVBox
    private ArrayList<HBox> listOfAncenstorHBox;

    // number of series;
    private final int numOfAncestries = AdmixtureGraph.currentNumOfAncestries;

    // borders for vbox and hbox containers
    private final String cssLayout = "-fx-border-color: #d9d9d9; -fx-border-width: 1;";
    
    private Stage optionsStage;
    private Scene sceneForPopulationGroupBtn;

    @FXML
    private void deleteGraph(ActionEvent event) {

    }

    @FXML
    private void loadPopulationGroupOptions(ActionEvent event) {
        Stage stage = (Stage) populationGroupOptionsBtn.getScene().getWindow();
        stage.close();
        
        VBox optionsContainerVBox = new VBox(5);
        optionsContainerVBox.setPadding(new Insets(10));

        HBox populationOptionsHBox = new HBox(2);
        populationOptionsHBox.setStyle(cssLayout);

        // create the right vbox to keep the fam order and dominant order buttons
        VBox rightVBox = setVbox(new Label("Sort within populations algorithmically"));
        rightVBox.setStyle(cssLayout);

        // fam sort button
        Button famOrderBtn = new Button("Fam order");
        famOrderBtn.setOnMouseClicked((MouseEvent famOrderEvent) -> {
            listOfCharts.forEach((s) -> {
                sortToFamOrder(s);
            });
        });

        // dominant sort button
        Button dominantColourBtn = new Button("Dominant colour");
        dominantColourBtn.setOnMouseClicked((MouseEvent devt) -> {
            for (StackedBarChart<String, Number> stackedBarChart : listOfCharts) {
                // store totals of yvalue elements in every serie                                            
                ArrayList<Double> sumList = new ArrayList<>();

                // get sum of all yvalues in every serie / ancestry
                for (int s = 0; s < stackedBarChart.getData().size(); s++) {
                    double sum = 0;
                    for (int n = 0; n < stackedBarChart.getData().get(s).getData().size(); n++) {
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

        VBox leftVBox = setVbox(new Label("Change and order colours"));

        // for every ancestor, get its color, create a button with its name and create a sort button
        for (int i = admixChart.getData().size()-1; i >= 0; i--) {

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
//                    ancestorOptionsController.setAncestryPosition(listOfAncenstorHBox.indexOf(ancenstorHBox));

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
            int serieNum = i + 1;
            Button colorSortBtn = new Button("Sort Ancestry " + ancestorName.substring(ancestorName.length() - 1));
            colorSortBtn.setOnMouseClicked((MouseEvent devt) -> {
                for (StackedBarChart<String, Number> stackedBarChart : listOfCharts) {
                    // get last character on a btn and use it use it as the index of the ancestry
                    String ancestryNumber = colorSortBtn.getText().substring(colorSortBtn.getText().length() - 1);
                    // sort
                    sortChartByColor(stackedBarChart, Integer.valueOf(ancestryNumber) - 1);
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
        doneBtn.setOnMouseClicked((MouseEvent dominantEvent) -> {
            optionsStage.close();
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

    }

    @FXML
    private void cancelHandler(ActionEvent event) {
        Genesis.closeOpenStage(event);
    }

    public void setAdmixChart(StackedBarChart<String, Number> admixChart) {
        this.admixChart = admixChart;
    }

    private VBox setVbox(Node node) {
        VBox vb = new VBox(node);
        vb.setPadding(new Insets(10));
        vb.setSpacing(5);
        vb.setStyle(cssLayout);
        return vb;
    }

    /**
     * sort individuals according to fam order
     *
     * @param s
     */
    private void sortToFamOrder(StackedBarChart<String, Number> s) {
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
     *
     * @param stackedBarChart
     * @param ancestryNumber
     */
    private void sortChartByColor(StackedBarChart<String, Number> stackedBarChart, int ancestryNumber) {

        CategoryAxis xAxis = (CategoryAxis) stackedBarChart.getXAxis();

        XYChart.Series<String, Number> chosenAncestry = stackedBarChart.getData().get(ancestryNumber);
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        listOfCharts = MainController.getAllAdmixtureCharts().get(AdmixtureGraphEventsHandler.getRowIndexOfClickedAdmixChart());

        listOfAncenstorHBox = new ArrayList<>();
        
        optionsStage = new Stage();

        // TODO - change this item list based on the position of the graph
        orderCombobox.getItems().addAll("Shift Graph Up", "Shift Graph to Top",
                "Shift Graph Down", "Shift Graph to Bottom");
    }

}
