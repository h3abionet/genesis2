/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.model;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.controller.AdmixtureIndividualDetailsController;
import org.h3abionet.genesis.controller.AdmixtureOptionsController;
import org.h3abionet.genesis.controller.MainController;
import org.h3abionet.genesis.controller.PopulationGroupLabelController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * @author henry
 */
public class AdmixtureGraph extends Graph implements Serializable {

    private static final long serialVersionUID = 2L;

    private String[] ancestryLabels;
    public int currentNumOfAncestries = 0; // number of series
    private final double CHART_HEIGHT = 100; // default height for every chart
    private String defaultHeading = "Admixture plot";
    IconsAndColors iconsAndColors = new IconsAndColors();

    // admixture plot colors -- add more depending on the number of ancestries
    private String[] hexCodes = iconsAndColors.getListOfColors();
    public ArrayList<String> ancestryColors = new ArrayList<>(Arrays.asList(hexCodes));

//    public ArrayList<String> ancestryOrder = new ArrayList<>();  // to order colour

    private transient ArrayList<StackedBarChart<String, Number>> listOfStackedBarCharts;
    private int numOfAncestries;
    private int rowIndexOfClickedAdmixChart;
    private String clickedId;
    private transient AdmixtureOptionsController admixtureOptionsController;
    private double defaultAngleOfChartNames = 90;

    public static void setChartIndex(int chartIndex) {
        AdmixtureGraph.chartIndex = chartIndex;
    }

    private static int chartIndex = 0;
    private transient MainController mainController;
    private ArrayList<String> iidDetails;
    // group name -> with all associated graphs for different values of k
    private  static HashMap<String, ArrayList<StackedBarChart<String, Number>>> hiddenAdmixGraphs = new HashMap<>();

    private static int labelClickCounter = 0;
    private transient StackPane firstGroupLabel, secondGroupLabel;
    private transient GridPane gridPane;
    private Node firstChart, secondChart;
    private static int kClickCounter = 0;
    private transient static StackPane firstKLabel, secondKLabel;
    private static int clickedColIndex;
    boolean correctAdmixFile = Boolean.parseBoolean(null);
    private Project project;

    //    private ArrayList<String> orderOfAdmixGraphs = (ArrayList<String>) project.getGroupNames(); // store graph names here
    private HashMap<String, ColumnConstraints> constraintsHashMap = new HashMap<>();

    /**
     * Constructor
     *
     * @param admixtureFilePath
     * @throws IOException
     */
    public AdmixtureGraph(String admixtureFilePath, Project project) throws IOException {
        this.project = project;
        readGraphData(admixtureFilePath);
//
//        for (String l: ancestryLabels)
//            ancestryOrder.add(l);
        project.setAdmixtureGraph(this);
    }

    @Override
    public void readGraphData(String admixtureFilePath) throws IOException {
        BufferedReader r = Genesis.openFile(admixtureFilePath);
        String line = r.readLine();
        String fields[] = line.split("\\s+");

        // check if file contains strings
        for(String f: fields){
            try {
                Float floatVal = Float.valueOf(f).floatValue();
                correctAdmixFile = true;
            }catch (Exception e){
                correctAdmixFile = false;
            }
        }

        // check first value: float or id ?
        if(correctAdmixFile) {
            setNumOfAncestries(fields.length);
            project.setImportedKs(numOfAncestries);
            setAncestryLabels(numOfAncestries);

            // store colors in project once. Reuse stored colors on imported projects
//            if(!project.isProjIsImported()) {
//                ArrayList colors = new ArrayList(); // number of colors for this chart
//                for (int c = 0; c < numOfAncestries; c++) {
//                    colors.add(ancestryColors.get(c));
//                }
//                project.getAdmixtureAncestryColor().add(colors);
//            }

            if(chartIndex==project.getAdmixtureAncestryColor().size()){
                ArrayList colors = new ArrayList(); // number of colors for this chart
                for (int c = 0; c < numOfAncestries; c++) {
                    colors.add(ancestryColors.get(c));
                }
                project.getAdmixtureAncestryColor().add(colors);
            }

            for (Subject sub : project.getSubjectsList()) {
                fields = line.split("\\s+");
                ArrayList<String> qValues = new ArrayList<>(Arrays.asList(fields));
                String iid = sub.getIid();
                qValues.add(0, iid); // add qs
                String qs[] = qValues.toArray(new String[0]);
                sub.setQs(qs);
                line = r.readLine();
            }
        }else {
            Genesis.throwInformationException("Imported file contains strings");
        }
    }

    public boolean isCorrectAdmixFile() {
        return correctAdmixFile;
    }

    public void setNumOfAncestries(int numOfAncestries) {
        this.numOfAncestries = numOfAncestries;
    }

    public void setAncestryLabels(int numOfAncestries) {

        ancestryLabels = new String[numOfAncestries];
        for (int i = 0; i < numOfAncestries; i++) {
            ancestryLabels[i] = "Ancestry " + (i + 1);
        }
    }

    /**
     * create multiple plots for every phenotype
     *
     * @return
     */
    @Override
    public void createAdmixGraph() {
        // charts = [chart1, chart2, chart3, ...]
        ArrayList<StackedBarChart<String, Number>> charts = new ArrayList<>(); // store stacked charts for every group

        // remove groups with no individuals - subjects
        Iterator<String> iter = project.getGroupNames().iterator();
        while(iter.hasNext()) {
            String group = iter.next();
            ArrayList<Subject> thisGroup = project.getSubjectGroups().get(group);
            if(thisGroup.size()==0) {
                iter.remove(); // Removes the 'current' group
            }
        }

        project.getGroupNames().parallelStream().forEachOrdered(
                groupName -> {
//
//                }
//        );

//        for (String groupName : project.getGroupNames()) {

            XYChart.Series<String, Number> ancestry;
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis(0, 1, 0.1);

            //xAxis.setAutoRanging(false);
            xAxis.setTickMarkVisible(false);
            yAxis.setTickMarkVisible(false);

            xAxis.setTickLabelsVisible(false);
            yAxis.setTickLabelsVisible(false);

            xAxis.setPrefSize(0, 0);
            yAxis.setPrefSize(0, 0);

            yAxis.setAutoRanging(false);
            yAxis.setMinorTickVisible(false);

            StackedBarChart<String, Number> populationGroupChart = new StackedBarChart<>(xAxis, yAxis);

            // collect all values belong to this population groups
            // [{iid, v1, v2}, {iid, v1, v2}, ...]
            List<String []> listOfQValues = new ArrayList<>();

            ArrayList<Subject> thisGroup = project.getSubjectGroups().get(groupName);

            thisGroup.parallelStream().forEachOrdered(sub -> {
                int sizeOfQValuesList = sub.getqValuesList().size();
                if (sizeOfQValuesList != 0 && sub.isHidden() == false) {
                    String [] ls = sub.getqValuesList().get(chartIndex);
                    listOfQValues.add(ls); //{iid, v1, v2}
                }
            });


//            for (Subject sub : project.getSubjectsList()) {
//                int sizeOfQValuesList = sub.getqValuesList().size();
//                if (project.isPhenoFileProvided()) {
//                    if (sub.getPhenos()[project.getPhenoColumnNumber() - 1].equals(groupName) && sizeOfQValuesList != 0 && sub.isHidden() == false) {
//                        ArrayList<String> ls = sub.getqValuesList().get(chartIndex);
//                        listOfQValues.add(ls); //{iid, v1, v2}
//                    }
//                }
//            }


            // create data series for every ancestry
            int numOfAncentries = ancestryLabels.length;
            for (int i = 0; i < numOfAncentries; i++) {
                ancestry = new XYChart.Series<>();
                ancestry.setName(ancestryLabels[i]);

                Collection<XYChart.Data<String,Number>> individuals = new ArrayList<>(listOfQValues.size());
                for (String [] qValues : listOfQValues) { // get individual values
                    individuals.add(new XYChart.Data<>(qValues[0], Float.parseFloat(qValues[1 + i]))); //{iid, v1, v2}
                }
                ancestry.getData().addAll(individuals);

//                for (String [] qValues : listOfQValues) { // get individual values
//                    ancestry.getData().add(new XYChart.Data<>(qValues[0], Float.parseFloat(qValues[1 + i]))); //{iid, v1, v2, v3, v4}
//                }
                populationGroupChart.getData().add(ancestry); // add values to chart
            }

            setAncestryColors(populationGroupChart, ancestryColors); // set ancestry colors

            // update current num of ancestries
            // used to label Ks (K=1,2,3,..) and create ancestry options buttons
            currentNumOfAncestries = populationGroupChart.getData().size();

            // define populationGroupChart size
            populationGroupChart.getXAxis().setLabel(groupName);
            populationGroupChart.getXAxis().setVisible(false);
            populationGroupChart.setPrefHeight(CHART_HEIGHT);
            populationGroupChart.setMinHeight(CHART_HEIGHT);
            populationGroupChart.setLegendVisible(false);
//            populationGroupChart.setAlternativeRowFillVisible(false);
//            populationGroupChart.setAlternativeColumnFillVisible(false);
//            populationGroupChart.setHorizontalGridLinesVisible(false);
//            populationGroupChart.setVerticalGridLinesVisible(false);
            populationGroupChart.setCategoryGap(-1);
//            populationGroupChart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");

            // set the css stylesheet
            populationGroupChart.getStylesheets().add(Genesis.class.getResource("css/admixture.css").toExternalForm());

            // remove all legend items
            for (Node n : populationGroupChart.getChildrenUnmodifiable()) {
                if (n.getClass().toString().equals("class com.sun.javafx.charts.Legend")) {
                    TilePane tn = (TilePane) n;
                    ObservableList<Node> children = tn.getChildren();
                    tn.getChildren().remove(0, children.size()); // remove all items in range 0 to size
                    break;
                }
            }

            // only add charts with data
            if (populationGroupChart.getData().get(0).getData().size() > 0) {
                showIndividualDetails(populationGroupChart);
                charts.add(populationGroupChart);
            }
    }
        );
        listOfStackedBarCharts = charts;
        chartIndex += 1;
    }

    private void showIndividualDetails(StackedBarChart<String, Number> admixChart) {
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
                            dialogStage.initModality(Modality.APPLICATION_MODAL);

                            // show subject details when clicked
                            AdmixtureIndividualDetailsController admixIndivDetailsCtrler = fxmlLoader.getController();
                            admixIndivDetailsCtrler.setProject(project);
                            admixIndivDetailsCtrler.setAdmixtureGraph(this);
                            admixIndivDetailsCtrler.setAdmixtureChart(admixChart);
                            admixIndivDetailsCtrler.setClickedId(item.getXValue());
                            setClickedId(item.getXValue()); // set id of the clicked individual on the graph

                            for (Subject sub : project.getSubjectsList()) {
                                if (sub.getIid().equals(item.getXValue())) {
//                                    List<String> list = sub.getqValuesList().get(rowIndexOfClickedAdmixChart).subList(1,
//                                            sub.getqValuesList().get(rowIndexOfClickedAdmixChart).size());

                                    String [] list = Arrays.copyOfRange(sub.getqValuesList().get(rowIndexOfClickedAdmixChart), 1,
                                            sub.getqValuesList().get(rowIndexOfClickedAdmixChart).length);

                                    iidDetails = new ArrayList<>(Arrays.asList(sub.getPhenos()));
                                    iidDetails.add(sub.getSex());
                                    admixIndivDetailsCtrler.setValuesList(Arrays.asList(list)); // get Y value
                                    break;
                                }
                            }
                            admixIndivDetailsCtrler.setPhenoList(iidDetails);
                            dialogStage.showAndWait();
                        } catch (IOException ex) {
                            ;
                        }
                    }
                });
            });
        });
    }

    /**
     * hide selected group
     * @param groupName
     */
    public void hideGroup(String groupName){ // groupName = a stacked bar chart

//        orderOfAdmixGraphs.remove(groupName); //  remove group from order

        ArrayList<StackedBarChart<String, Number>> groupCharts = new ArrayList<>(); // for various Ks
        ArrayList<Node> deleteNodes = new ArrayList<>();
        GridPane gridPane = mainController.getGridPane();
        Integer chosenGraphColIndex = null;

        for (int i = 0; i < mainController.getAllAdmixtureCharts().size(); i++) {
            for (StackedBarChart<String, Number> admixGraph : mainController.getAllAdmixtureCharts().get(i)) {
                if(admixGraph.getId().equals(groupName)){
                    // remove graph from the list of all graphs
                    mainController.getAllAdmixtureCharts().get(i).remove(admixGraph);

                    // add graph to list of hidden graphs for this particular group
                    groupCharts.add(admixGraph);

//                    chosenGraphColIndex = clickedColIndex;
                    break;
                }
            }
        }

        // get nodes to delete and change column index for existing nodes
        for (Node child : gridPane.getChildren()) {
            // get index from child
            int currentColIndex = GridPane.getColumnIndex(child);
            // add this node to the list of nodes to be deleted
            if(currentColIndex==clickedColIndex){
                deleteNodes.add(child);
            }
            if (currentColIndex > clickedColIndex) {
                // decrement cols for cols after the deleted col
                GridPane.setColumnIndex(child, currentColIndex - 1);
            }
        }

        // remove deleted nodes from the gridpane
        gridPane.getChildren().removeAll(deleteNodes);

        // get column constraints
        ColumnConstraints cc =  gridPane.getColumnConstraints().get(clickedColIndex);
        cc.setPercentWidth(0);
        cc.setHgrow(Priority.NEVER);
        gridPane.getColumnConstraints().remove(cc);

        constraintsHashMap.put(groupName, cc); //  store this constraint

        // add group name to project
        if(!project.getHiddenGroups().contains(groupName)){
            project.getHiddenGroups().add(groupName);
        }

        // keep all graphs for this group here - for various K values = [CEU -> [g1, g2, g3, g4]] where 1-4 are k values
        hiddenAdmixGraphs.put(groupName, groupCharts);
    }

    /**
     * show hidden group
     * @param group
     */
    public void showHiddenGroup(String group) {

        GridPane gridPane = mainController.getGridPane();

        // remove group from the project
        ArrayList<StackedBarChart<String, Number>> hiddenGroupGraphs =  hiddenAdmixGraphs.get(group);

        // for every hidden group, get all its graphs and add them back to the right K-value list of all project graphs
        for(int h=0; h< hiddenGroupGraphs.size();h++){
            int size0fChart = mainController.getAllAdmixtureCharts().get(h).get(0).getData().size();
            for(StackedBarChart<String, Number> hGraph: hiddenGroupGraphs){
                if(hGraph.getData().size()==size0fChart){
                    mainController.getAllAdmixtureCharts().get(h).add(hGraph);
                    break;
                }
            }
        }

        // get total number of individuals
        int sumOfIndividuals = project.getNumOfIndividuals();
        int numOfIndividuals = hiddenGroupGraphs.get(0).getData().get(0).getData().size(); // size of of one graph
        double columnSize = (double) numOfIndividuals / (double) sumOfIndividuals * 100.0;

        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(columnSize);
        cc.setHgrow(Priority.NEVER);
        gridPane.getColumnConstraints().add(cc);

        int numOfRows = gridPane.getRowCount();
        int numOfCols = gridPane.getColumnCount();
        int rowIndex = 0;

        ObservableList<Node> children = gridPane.getChildren();

        // get order of rows and k values - then use them to put back the graphs whose number of series,
        // is equal to the k value (in the order of the k values)
        ArrayList<Integer> kValueOrder = new ArrayList<>();

        while (rowIndex < numOfRows) {
            for (Node node : children) {
                if(gridPane.getRowIndex(node) == rowIndex && gridPane.getColumnIndex(node) == 0) {
                    StackPane pane  = (StackPane) node;
                    Text txt = (Text) pane.getChildren().get(0);
                    kValueOrder.add(Integer.valueOf(txt.getText().substring(txt.getText().length() - 1)));
                    break;
                }
            }
            rowIndex++;
        }

        int rowCounter = 0;
        setChartGroupName(numOfCols-1, numOfRows-1, group);

        while (rowCounter < numOfRows-1) { // 1 is the row with the group name
            // loop through ks
            for (int kValue : kValueOrder) {
                for (StackedBarChart<String, Number> graph : hiddenGroupGraphs) {
                    if (graph.getData().size() == kValue) {
                        int row = kValueOrder.indexOf(kValue);
                        GridPane.setRowIndex(graph, row);
                        GridPane.setColumnIndex(graph, numOfCols-1);
                        gridPane.getChildren().add(graph);
                    }
                }
            }
            rowCounter++;
        }

//        gridPane.setStyle("-fx-border-color: green; -fx-border-width: 3px 3px 3px 3px");

        // remove group from the hidden list of all graphs in the project
        project.getHiddenGroups().remove(group);

        // define Group names for individuals
    }

    /**
     * set id for clicked individual
     * @param clickedId
     */
    private void setClickedId(String clickedId) {
        this.clickedId = clickedId;
    }

    /**
     * un hide hidden individuals
     * @param ids
     */
    public void showIndividual(String[] ids){
        String iid = ids[1];
        String groupName = null;
        ArrayList<String[]> qValues = null;

        // set subject group and remove individual
        for (Subject s : project.getSubjectsList()) {
            if (s.getIid().equals(iid)) {
                s.setHidden(false); // set hidden to false
                groupName = s.getPhenos()[project.getPhenoColumnNumber() - 1];
                // remove this individual to a list of hidden individuals
                project.getHiddenPoints().remove(s.getFid()+" "+s.getIid()); // fid+" "+iid
                qValues = s.getqValuesList();
                break;
            }
        }

        for (int i = 0; i < mainController.getAllAdmixtureCharts().size(); i++) {
            // get one admix plot: // plot1 -> {[chart 1,chart 2,..]}
            for (StackedBarChart<String, Number> admixGraph : mainController.getAllAdmixtureCharts().get(i)) {
                if(admixGraph.getId().equals(groupName)){ // right graph or group for the individual
                    CategoryAxis xAxis = (CategoryAxis) admixGraph.getXAxis();
                    xAxis.getCategories().add(iid); //TODO add subject to right position if graph was sorted (track order of series)

                    for (int s=0; s<admixGraph.getData().size();s++){
                        XYChart.Series<String, Number> serie = admixGraph.getData().get(s);
                        Float yValue = Float.parseFloat(qValues.get(i)[s+1]);
                        serie.getData().add(new XYChart.Data<>(iid, yValue));
                        //TODO add this data node to a list of all graphs
                    }
                }
                //TODO add admix colors for every subject [if k = 3, sub.setAdmixColor([b, w, y])] - same as qVaules
                setAncestryColors(admixGraph, ancestryColors);
                // add click event handler
                showIndividualDetails(admixGraph);
            }
        }
    }

    /**
     * hide individual in all plots
     */
    public void hideIndividual() {
        String groupName = null;

        // loop through charts to remove all individual { plot1 -> {[chart 1,chart 2,..]}, plot2 -> {[chart 1,chart 2,..]},...}
        for (int i = 0; i < mainController.getAllAdmixtureCharts().size(); i++) { // plot1 -> {[chart 1,chart 2,..]}
            // get one admix plot: // plot1 -> {[chart 1,chart 2,..]}
            for (StackedBarChart<String, Number> admixGraph : mainController.getAllAdmixtureCharts().get(i)) { // chart1
                for (XYChart.Series<String, Number> ancestry : admixGraph.getData()) { // series
                    for (XYChart.Data<String, Number> individual : ancestry.getData()) {
                        String iid = individual.getXValue(); // individual id

                        if (iid.equals(clickedId)) {

                            // get individual with this iid and set to hide
                            for (Subject s : project.getSubjectsList()) {
                                if (s.getIid().equals(iid)) {
                                    s.setHidden(true); // set hidden
                                    groupName = s.getPhenos()[project.getPhenoColumnNumber() - 1];

                                    // add this individual to a list of hidden individuals
                                    project.getHiddenPoints().add(s.getFid()+" "+s.getIid()); // fid+" "+iid
                                    break;
                                }
                            }

                            // get categories and remove the iid
                            CategoryAxis xAxis = (CategoryAxis) admixGraph.getXAxis();
                            ObservableList<String> iids = xAxis.getCategories();
                            int index = iids.indexOf(iid);
                            xAxis.getCategories().remove(index);
                            xAxis.setCategories(iids);

                            for (XYChart.Series<String, Number> serie : admixGraph.getData()) {
                                ancestry.getData().remove(individual);
                            }
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

    public ArrayList<StackedBarChart<String, Number>> getListOfStackedBarCharts() {
        return listOfStackedBarCharts;
    }

    /**
     * Set colors
     */
    private void setAncestryColors(StackedBarChart<String, Number> stackedBarChart, ArrayList<String> admixColors) {
        int numOfSeries = stackedBarChart.getData().size();// series are ancenstries
        for (int i = 0; i < numOfSeries; i++) {
            int ancestryIndex = i;
//            String ancestryColor = admixColors.get(i);
//            String[] colors = (String[]) project.getAdmixtureAncestryColor().get("K="+numOfSeries);
            ArrayList ancestryColorList = project.getAdmixtureAncestryColor().get(chartIndex);
            String ancestryColor = (String) ancestryColorList.get(i);
            stackedBarChart.getData().get(i).getData().forEach((bar) -> {
                    bar.getNode().lookupAll(".default-color" + ancestryIndex + ".chart-bar")
                            .forEach(n -> n.setStyle("-fx-bar-fill: " + ancestryColor + ";-fx-background-color:"+ancestryColor+"; -fx-border-color: " + "transparent" + ";-fx-opacity: 100;"));
            });
        }
    }

    public double getCHART_HEIGHT() {
        return CHART_HEIGHT;
    }

    public int getCurrentNumOfAncestries() {
        return currentNumOfAncestries;
    }

    public String getDefaultHeading() {
        return defaultHeading;
    }

    /**
     * set project if reading it as a saved object
     *
     * @param project
     */
    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public void createGraph(int pcaX, int pcaY) {
        ;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setRowIndexOfClickedAdmixChart(int rowIndexOfClickedAdmixChart) {
        this.rowIndexOfClickedAdmixChart = rowIndexOfClickedAdmixChart;
    }

    /**
     *
     */
    @SuppressWarnings("empty-statement")
    public GridPane getGridPane(GridPane gridPane, int rowPointer) {
        this.gridPane = gridPane;
        try {
            Text kValue = new Text("K = " + numOfAncestries);
            StackPane kValuePane = new StackPane(kValue);
            kValuePane.setId(String.valueOf(kValue));
            kValuePane.setAlignment(Pos.CENTER);
            kValuePane.setMargin(kValue, new Insets(5));

            kValuePane.hoverProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    kValuePane.setCursor(Cursor.HAND);
                }
            });

            // add event to the label
            kValuePane.setOnMouseClicked((MouseEvent e) -> {
                MouseButton btn = e.getButton();
                if (btn == MouseButton.PRIMARY) {
                    kValueClicked(e.getSource());
                }else if(btn == MouseButton.SECONDARY){
                    ;
                }
            });

            gridPane.add(kValuePane, 0, rowPointer); // add K value.

        int colIndex = 1;

        for (StackedBarChart<String, Number> admixChart : listOfStackedBarCharts) {
                // set the chart size
                admixChart.setMinWidth(Double.MIN_VALUE);
                admixChart.setMaxWidth(Double.MAX_VALUE);

                // set the margins of the chart
                GridPane.setMargin(admixChart, new Insets(0, 0.5, 0, 0)); // TODO remove the chart content margins on axes

                // remove last rowPointer - population group labels
                // remove previous group labels - cell will be replaced with new graph
                ObservableList<Node> children = gridPane.getChildren();
                for(Node node : children) {
                    if(node instanceof StackPane && gridPane.getRowIndex(node) == rowPointer && gridPane.getColumnIndex(node) == colIndex) {
                        StackPane stackPane = (StackPane)node; // use what you want to remove
                        gridPane.getChildren().remove(stackPane);
                        break;
                    }
                }

                // remove the x-axis label from the stacked bar chart
                String xLabel = admixChart.getXAxis().getLabel();
                admixChart.setId(xLabel); // set chart id
                admixChart.getXAxis().setLabel(null);

                // set the label at the bottom
                setChartGroupName(colIndex, rowPointer+1, xLabel);

                gridPane.add(admixChart, colIndex, rowPointer);
                gridPane.setStyle("-fx-background-color: transparent;");

            // right click mouse event handler
                admixChart.setOnMouseClicked((MouseEvent event) -> {
                    MouseButton button = event.getButton();
                    if (button == MouseButton.SECONDARY) {
                        // set the rowIndex of the clicked chart
                        rowIndexOfClickedAdmixChart = GridPane.getRowIndex(admixChart);
                        setRowIndexOfClickedAdmixChart(rowIndexOfClickedAdmixChart);
                        try {
                            FXMLLoader loader = new FXMLLoader(Genesis.class.getResource("view/AdmixtureOptions.fxml"));
                            Parent p = (Parent) loader.load();
                            Stage dialogStage = new Stage();
                            dialogStage.setScene(new Scene(p));
                            dialogStage.setResizable(false);
                            AdmixtureOptionsController aop = loader.getController();
                            setAdmixtureIndividualsCtler(aop);
                            aop.setRowIndexOfClickedAdmixChart(rowIndexOfClickedAdmixChart);
                            aop.setCurrChart(mainController.getAllAdmixtureCharts().get(rowIndexOfClickedAdmixChart));
                            aop.setGridPane(mainController.getGridPane());
                            aop.setMainController(mainController);
                            aop.setProject(project);
                            aop.setAdmixChart(admixChart);
                            aop.setNumOfRows(gridPane.getRowCount());
                            aop.downwardShiftMovement();
                            aop.upwardShiftMovement();
                            dialogStage.initModality(Modality.APPLICATION_MODAL);
                            dialogStage.show();
                        } catch (IOException ex) {
                            Genesis.throwErrorException("Failed to load plot format options");
                        }
                    }
                });

                // increment the column index
                colIndex++;
            }
        } catch (Exception e) {
            ;
//            Genesis.throwErrorException("Sorry. Try Again"); //do nothing
        }

        return gridPane;
    }

    private void setAdmixtureIndividualsCtler(AdmixtureOptionsController aop) {
        this.admixtureOptionsController = aop;
    }

    private void setChartGroupName(int colIndex, int rowIndex, String groupName){
        Text chartGroupName = new Text(groupName);
        chartGroupName.setRotate(defaultAngleOfChartNames); // default angle of the chart names
        StackPane pane = new StackPane(chartGroupName);
        pane.setAlignment(Pos.CENTER);
        pane.setMargin(chartGroupName, new Insets(0));
//        pane.setPadding(new Insets(15,0,0,0));
        String paneCssStyle = "-fx-padding: 15px; -fx-border-style: solid solid none solid ; -fx-border-color: black; -fx-border-width: 1px"; //TODO check if not redundant
        pane.setStyle(paneCssStyle);

        pane.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                pane.setCursor(Cursor.HAND);
                pane.setStyle(paneCssStyle+"; -fx-background-color: #e1f3f7;");
            } else {
                pane.setStyle(paneCssStyle+"; -fx-background-color: transparent;");
            }
        });

        // add group label to last row
        GridPane.setColumnIndex(pane, colIndex);
        GridPane.setRowIndex(pane, rowIndex);
        gridPane.getChildren().add(pane);

        // add event to the label
        pane.setOnMouseClicked(event ->
        {
            if (event.getButton() == MouseButton.SECONDARY)
            {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("view/PopulationGroupLabel.fxml"));
                    Parent parent = (Parent) fxmlLoader.load();
                    Stage dialogStage = new Stage();
                    dialogStage.setScene(new Scene(parent));
                    dialogStage.setResizable(false);
                    PopulationGroupLabelController pglc = fxmlLoader.getController();
                    clickedColIndex = GridPane.getColumnIndex(pane);
                    pglc.setGroupNameLbl(groupName);
                    pglc.setAdmixtureGraph(this);
                    pglc.setProj(project);
                    pglc.setStackedPane(pane);
                    dialogStage.initModality(Modality.APPLICATION_MODAL);
                    dialogStage.showAndWait();
                } catch (IOException e) {
                }
            }else {
                chartGroupNameClicked(event.getSource());
            }
        });
    }

    /**
     * identify clicked population group labels
     * @param source
     */
    private void chartGroupNameClicked(Object source) {
        Label clickWhereTo = new Label("Click where to");
        clickWhereTo.setStyle("-fx-text-fill:red");

        if (!(source instanceof StackPane)) {
            return;
        }
        StackPane lbl = (StackPane) source;

        if (labelClickCounter == 0) {
            firstGroupLabel = lbl;
            MainController.getAdmixVbox().getChildren().add(clickWhereTo);
            MainController.getAdmixVbox().setAlignment(Pos.CENTER);
        } else {
            secondGroupLabel = lbl;
            // if the same label is clicked, do not swap
            if(GridPane.getColumnIndex(firstGroupLabel) != GridPane.getColumnIndex(secondGroupLabel)){
//                groupNameSwap();
                moveColumnGroups();
                MainController.getAdmixVbox().getChildren().remove(MainController.getAdmixVbox().getChildren().size()-1);
            }else {
                MainController.getAdmixVbox().getChildren().remove(MainController.getAdmixVbox().getChildren().size()-1);
            }
        }
        labelClickCounter = ++labelClickCounter % 2;  // changes values between 0 1
    }

    /**
     * groupNameSwap population group labels
     */
    private void moveColumnGroups() {
        ObservableList<Node> children = gridPane.getChildren();

        // column and row index for clicked labels
        int firstRow = GridPane.getRowIndex(firstGroupLabel);
        int firstCol = GridPane.getColumnIndex(firstGroupLabel);
        int secondRow = GridPane.getRowIndex(secondGroupLabel);
        int secondCol = GridPane.getColumnIndex(secondGroupLabel);

        // swap the population labels
        if (firstCol < secondCol) {
            for (int j = firstCol; j < secondCol; j++) {
                for (Node node : children) {
                    // get first chart
                    if (GridPane.getColumnIndex(node) == j && GridPane.getRowIndex(node) == firstRow) {
                        if (node instanceof StackPane) {
                            firstGroupLabel = (StackPane) node;
                        }
                    }
                    // get second chart
                    if (GridPane.getColumnIndex(node) == j + 1 && GridPane.getRowIndex(node) == firstRow) {
                        if (node instanceof StackPane) {
                            secondGroupLabel = (StackPane) node;
                        }
                    }
                }

                // groupNameSwap their column constraints
                ArrayList<String> list = project.getGroupNames();

                Collections.swap(gridPane.getColumnConstraints(), j, j+1);
                Collections.swap(list, j-1, j);

                // remove group existing labels
                gridPane.getChildren().removeAll(firstGroupLabel, secondGroupLabel);

                // groupNameSwap population group nodes
                if (firstCol != secondCol) {
                    gridPane.add(firstGroupLabel, j + 1, secondRow);
                    gridPane.add(secondGroupLabel, j, firstRow);
                }
            }
            int rowIndex = 0;
            while (rowIndex < firstRow) {
            for (int j = firstCol; j < secondCol; j++) {
                // get the charts to swap
                for (Node node : children) {
                    // get first chart
                    if (GridPane.getColumnIndex(node) == j && GridPane.getRowIndex(node) == rowIndex) {
                        firstChart = node;
                    }
                    // get second chart
                    if (GridPane.getColumnIndex(node) == j + 1 && GridPane.getRowIndex(node) == rowIndex) {
                        secondChart = node;
                    }
                }

                if (firstChart != null && secondChart != null) {
                    // remove nodes
                    gridPane.getChildren().removeAll(firstChart, secondChart);
                    // groupNameSwap nodes
                    if (firstCol != secondCol) {
                        gridPane.add(firstChart, j+1, rowIndex);
                        gridPane.add(secondChart, j, rowIndex);
                    }
                } else {
                    ;
                }
            }
            // reset nodes
            firstChart = null;
            secondChart = null;
            rowIndex++;
        }
        }

        if(firstCol>secondCol){
            for (int j = firstCol; j > secondCol; j--) {
                for (Node node : children) {
                    // get first chart
                    if (GridPane.getColumnIndex(node) == j && GridPane.getRowIndex(node) == firstRow) {
                        if (node instanceof StackPane) {
                            firstGroupLabel = (StackPane) node;
                        }
                    }
                    // get second chart
                    if (GridPane.getColumnIndex(node) == j - 1 && GridPane.getRowIndex(node) == firstRow) {
                        if (node instanceof StackPane) {
                            secondGroupLabel = (StackPane) node;
                        }
                    }
                }

                // groupNameSwap their column constraints
                Collections.swap(gridPane.getColumnConstraints(), j, j-1);

                // remove group existing labels
                gridPane.getChildren().removeAll(firstGroupLabel, secondGroupLabel);

                // groupNameSwap population group nodes
                if (firstCol != secondCol) {
                    gridPane.add(firstGroupLabel, j - 1, secondRow);
                    gridPane.add(secondGroupLabel, j, firstRow);
                }
            }

            int rowIndex = 0;
            while (rowIndex < firstRow) {
                for (int j = firstCol; j > secondCol; j--) {
                    // get the charts to swap
                    for (Node node : children) {
                        // get first chart
                        if (GridPane.getColumnIndex(node) == j && GridPane.getRowIndex(node) == rowIndex) {
                            firstChart = node;
                        }
                        // get second chart
                        if (GridPane.getColumnIndex(node) == j - 1 && GridPane.getRowIndex(node) == rowIndex) {
                            secondChart = node;
                        }
                    }

                    if (firstChart != null && secondChart != null) {
                        // remove nodes
                        gridPane.getChildren().removeAll(firstChart, secondChart);
                        // groupNameSwap nodes
                        if (firstCol != secondCol) {
                            gridPane.add(firstChart, j-1, rowIndex);
                            gridPane.add(secondChart, j, rowIndex);
                        }
                    } else {
                        ;
                    }
                }
                // reset nodes
                firstChart = null;
                secondChart = null;
                rowIndex++;
            }
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
        int secondRow = GridPane.getRowIndex(secondKLabel);
        int secondCol = GridPane.getColumnIndex(secondKLabel);

        // swap the plot lists
        Collections.swap(mainController.getAllAdmixtureCharts(), firstRow, secondRow);

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
}
