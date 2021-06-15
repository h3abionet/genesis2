/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
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
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.Project;

import java.io.IOException;
import java.net.URL;
import java.util.*;

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
    private ArrayList<StackedBarChart<String, Number>> currChart;

    // clicked admixture chart
    private StackedBarChart<String, Number> admixChart;

    // To store all ancestor HBoxes which will be added the leftVBox
    private ArrayList<HBox> listOfAncenstorHBox;

    // borders for vbox and hbox containers
    private final String cssLayout = "-fx-border-color: #d9d9d9; -fx-border-width: 1;";
    
    private Stage optionsStage;
    private Scene sceneForPopulationGroupBtn;
    private int rowIndexOfClickedAdmixChart;
    private Project project;
    private MainController mainController;
    private AdmixtureGraphEventsHandler admixtureGraphEventsHandler;
    private GridPane gridPane;

    @FXML
    private void deleteGraph(ActionEvent event) {
        // remove all nodes in clicked row
        String result = Genesis.confirmAction("Are you sure you want to delete this plot?");
        if (result.equals("yesBtnPressed")) {
            gridPane.getChildren().removeIf(
                    node -> GridPane.getRowIndex(node) == rowIndexOfClickedAdmixChart);
            
        // remove a list of all charts in that index
        mainController.getAllAdmixtureCharts().remove(rowIndexOfClickedAdmixChart);
        MainController.setRowPointer(MainController.getRowPointer()-1);

        // close stage
        Genesis.closeOpenStage(event);
        } else {
            Genesis.closeOpenStage(event);
        }
    }

    @FXML
    private void colourLikePrevious(ActionEvent event){
        int K = currChart.get(0).getData().size();
        ArrayList<String> ancestryOrder = new ArrayList<String>();
        ArrayList<String> currAncestries =  new ArrayList<>();
        boolean used [] = new boolean [K];
        ArrayList<String> curColourCodes = new ArrayList<>();

        Stage stage = (Stage) previousGraphColourBtn.getScene().getWindow();
        stage.close();

        if (rowIndexOfClickedAdmixChart == 0 ) return; // There isn't a previous chart
        ArrayList<StackedBarChart<String,Number>> prev =  mainController.getAllAdmixtureCharts().get(rowIndexOfClickedAdmixChart-1);

        int Kp = prev.get(0).getData().size();
        int ancestry_match[][]  = new int [Kp][2];

        // work out which series in the prevous match the current
        getMatch(currChart, prev, ancestry_match);
        // get the hex colour codes that we currently use
        getCurrentColours(K, currAncestries, curColourCodes);
        //  Recolour appropriately and get the correct order for the series (currAncestries)
        matchToPrevious(Kp, ancestry_match, used, prev, currAncestries, curColourCodes);
        // there may be series not used
        handleUnmatchedColours(K, used, curColourCodes, currAncestries);
        Comparator<XYChart.Series<String, Number>> mycomp
                = (s1, s2)
                -> currAncestries.indexOf(s1.getName()) - currAncestries.indexOf(s2.getName());
        for (StackedBarChart<String,Number> segment : currChart)
            segment.setData(segment.getData().sorted(mycomp));
    }

    private void handleUnmatchedColours(int K, boolean[] used, ArrayList<String> curColourCodes, ArrayList<String> currAncestries) {
        for (int i=0; i<K; i++) {  // There may be at least one colour in the previous chart not used
            if (!used[i]) {
                String style = curColourCodes.remove(0);
                currAncestries.add(currChart.get(0).getData().get(i).getName());
                for (StackedBarChart<String,Number> currSeg: currChart )  {
                    XYChart.Series<String,Number> series = currSeg.getData().get(i);
                    for (XYChart.Data<String,Number> item : series.getData())
                        item.getNode().setStyle(style);
                }
            }
        }
    }

    private void matchToPrevious(int Kp, int[][] ancestry_match, boolean[] used, ArrayList<StackedBarChart<String, Number>> prev, ArrayList<String> currAncestries, ArrayList<String> curColourCodes) {
        for (int i=0; i<Kp; i++) {
            if (ancestry_match[i][1]>0) {// There was a match
                int curr_ind = ancestry_match[i][0];
                if (used[curr_ind]) continue; // due to odd colouring we already have this
                used[curr_ind]=true;
                String curr_anc = currChart.get(0).getData().get(curr_ind).getName();  // name of ancestry
                XYChart.Series<String, Number> other_hue = prev.get(0).getData().get(i);
                String style = other_hue.getData().get(0).getNode().lookup(".default-color"+i+".chart-bar").getStyle();
                currAncestries.add(curr_anc);
                for (StackedBarChart<String,Number> currSeg: currChart )  {
                    XYChart.Series<String,Number> series = currSeg.getData().get(curr_ind);
                    for (XYChart.Data<String,Number> item : series.getData())
                        item.getNode().setStyle(style);
                }
                curColourCodes.remove(style); // We've used this colour
            }
        }
    }

    private void getCurrentColours(int K, ArrayList<String> currAncestries, ArrayList<String> curColourCodes) {
        for (int i=0; i<K; i++)  {
            XYChart.Series<String, Number> firstSegI = currChart.get(0).getData().get(i);
            currAncestries.add(firstSegI.getName());
            curColourCodes.add(firstSegI.getData().get(0).getNode().lookup(".default-color"+i+".chart-bar").getStyle());
        }
    }

    private void getMatch(ArrayList<StackedBarChart<String, Number>> currChart, ArrayList<StackedBarChart<String, Number>> prev,
                          int[][] colour_match) {
        // For every ancestry in the previous chart find the best match in the currer one
        int K = currChart.get(0).getData().size();
        XYChart.Series<String,Number> c, p;
        float curr_usage [], prev_usage [];

        // go through each group
        for (int i=0; i<currChart.size(); i++) {
            ObservableList<XYChart.Series<String, Number>> currChartSeg = currChart.get(i).getData();
            curr_usage = getColourUsage(currChartSeg);
            prev_usage = getColourUsage(prev.get(i).getData());
            for(int colour=0; colour<K; colour++) {
                int curr_size = currChartSeg.get(0).getData().size();
                for(int other_colour=0; other_colour<prev_usage.length; other_colour++) {
                    if (((curr_usage[colour]>=0.5) && (prev_usage[other_colour]>=0.5)) ||
                            ((curr_usage[colour]>=0.4) && (prev_usage[other_colour]>=0.4))) {
                        if (curr_size>colour_match[other_colour][1]) {
                            colour_match[other_colour][0] = colour;
                            colour_match[other_colour][1] = curr_size;
                        }
                    }
                }
            }
        }
    }

    private float[] getColourUsage(ObservableList<XYChart.Series<String, Number>> data) {
        float usage [] = new float[data.size()];
        int i=0;
        for (XYChart.Series<String,Number> series : data) {
            for (XYChart.Data<String, Number> x : series.getData()) {
                usage[i]=usage[i]+x.getYValue().floatValue();
            }
            usage[i]=usage[i]/series.getData().size();
            i=i+1;
        }
        return usage;
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
            currChart.forEach((s) -> {
                sortToFamOrder(s);
            });
        });

        // dominant sort button
        Button dominantColourBtn = new Button("Dominant colour");
        dominantColourBtn.setOnMouseClicked((MouseEvent devt) -> {
            for (StackedBarChart<String, Number> stackedBarChart : currChart) {
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
            Rectangle ancestorColorDisplay = new Rectangle(25, 25);
            ancestorColorDisplay.setArcWidth(5);
            ancestorColorDisplay.setArcHeight(5);

            String defaultColor = ".default-color" + i + ".chart-bar";
            // get the default color of every ancestor (using the default-color0,1,2 class) and fill it in the rectangle
            StackPane node = (StackPane) admixChart.getData().get(i).getData().get(i).getNode().lookup(defaultColor);
            Background bg = node.getBackground();
            Paint paint = bg.getFills().get(0).getFill();
            ancestorColorDisplay.setFill(paint);
            
            // show hand cursor for user to click and change the color
            ancestorColorDisplay.hoverProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        ancestorColorDisplay.setCursor(Cursor.HAND);
                    }
            });

            int ancestorIndex = i;
            ancestorColorDisplay.setOnMouseClicked((MouseEvent colorEvt) -> {
                try {
                    // change the color of ancestries
                    FXMLLoader colorLoader = new FXMLLoader(Genesis.class.getResource("view/AncestryColor.fxml"));
                    Parent parent = (Parent) colorLoader.load();
                    Stage colorStage = new Stage();
                    colorStage.setScene(new Scene(parent));
                    colorStage.setResizable(false);
                    AncestryColorController acc = colorLoader.getController();
                    acc.setAncestryColor(ancestorColorDisplay.getFill());
                    acc.setAncestryIndex(ancestorIndex);
                    acc.setGridPane(mainController.getGridPane());
                    acc.setRowIndexOfClickedAdmixChart(rowIndexOfClickedAdmixChart);
                    acc.setListOfAdmixtureCharts(mainController.getAllAdmixtureCharts().get(rowIndexOfClickedAdmixChart));
                    acc.setAllAdmixtureCharts(mainController.getAllAdmixtureCharts());
                    colorStage.showAndWait();
                    
                    if (acc.isColorSelected()) {
                        ancestorColorDisplay.setFill(acc.getChosenPaint());
                    }
                } catch (IOException ex) {
                    Genesis.throwInformationException("Failed! Try Again!");
                }
            });
            
            //  set ancestor name -> used to name buttons and as a label for color options window
            String ancestorName = admixChart.getData().get(i).getName();
            
            // create ancestor button
            Button ancenstorNameBtn = new Button("Change " + ancestorName);

            // load stage for shifting up or down the ancestries
            ancenstorNameBtn.setOnMouseClicked((MouseEvent anacestorEvent) -> {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("view/ShiftAncestry.fxml"));
                    Parent parent = (Parent) fxmlLoader.load();
                    Stage dialogStage = new Stage();
                    dialogStage.setScene(new Scene(parent));
                    dialogStage.setResizable(false);

                    ShiftAncestryController sac = fxmlLoader.getController();
                    sac.setAncestorNumberLabel(ancestorName); // set the ancestry name
                    sac.setNumOfAncestry(admixChart.getData().size());
                    sac.setListOfAdmixtureCharts(mainController.getAllAdmixtureCharts().get(rowIndexOfClickedAdmixChart));
                    sac.setRowIndexOfClickedAdmixChart(rowIndexOfClickedAdmixChart);
                    sac.setGridPane(mainController.getGridPane());
//                    sac.setMainController(mainController);
                    dialogStage.showAndWait();

                } catch (IOException e) {
                }

            });

            // create a sort individuals button
            Button colorSortBtn = new Button("Sort Ancestry " + ancestorName.substring(ancestorName.length() - 1));
            colorSortBtn.setOnMouseClicked((MouseEvent devt) -> {
                for (StackedBarChart<String, Number> stackedBarChart : currChart) {
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
//        List<String> fam_order_iids = AdmixtureGraph.famOrder.get(s.getId());
        List<String> fam_order_iids = project.getFamOrder().get(s.getId());

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
        
//        // get row index of clicked chart - starts from 0
//        rowIndexOfClickedAdmixChart = AdmixtureGraphEventsHandler.getRowIndexOfClickedAdmixChart();
//
//        // get list of charts in this position of row index
//        currChart = mainController.getAllAdmixtureCharts().get(rowIndexOfClickedAdmixChart);

        listOfAncenstorHBox = new ArrayList<>();
        
        optionsStage = new Stage();

        // TODO - change this item list based on the position of the graph
        orderCombobox.getItems().addAll("Shift Graph Up", "Shift Graph to Top",
                "Shift Graph Down", "Shift Graph to Bottom");
    }

    public void setProject(Project project) {
        this.project = project;
    }

//    public void setMainController(MainController mainController) {
//        this.mainController = mainController;
//    }
//
//    public void setAdmixtureGraphEventsHandler(AdmixtureGraphEventsHandler admixtureGraphEventsHandler) {
//        this.admixtureGraphEventsHandler = admixtureGraphEventsHandler;
//    }

    public void setCurrChart(ArrayList<StackedBarChart<String, Number>> currChart) {
        this.currChart = currChart;
    }

    public void setRowIndexOfClickedAdmixChart(int rowIndexOfClickedAdmixChart) {
        this.rowIndexOfClickedAdmixChart = rowIndexOfClickedAdmixChart;
    }

    public void setGridPane(GridPane gridPane) {
        this.gridPane = gridPane;
    }
}
