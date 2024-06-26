/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.controller.MainController;
import org.h3abionet.genesis.controller.PCAGroupLabelController;
import org.h3abionet.genesis.controller.PCAIndividualDetailsController;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 *
 * @author Henry
 */
public class PCAGraph extends Graph implements Serializable {

    private static final long serialVersionUID = 2L;

    private String[] pcaColumnLabels; // store pca column name: PCA 1, PCA 2, ...
    private final List<String> eigenValues; // store eigen values
    private transient XYChart.Series<Number, Number> series; // FIXME: why transient?
    private transient BufferedReader bufferReader;
    private transient ScatterChart<Number, Number> pcaChart;
    private String line;
    private HashMap groupColors;
    private HashMap groupIcons;
    private transient MainController mainController;
    // group name -> with all associated graphs for different values of k
    // changed to transient but this one that never gets data added to it
    // on file load; backed up in project.hiddenGroups and saved from there
    private transient HashMap<String, ArrayList<XYChart.Series<Number, Number>>> hiddenPCAGroups = new HashMap<>();
    private int labelClickCounter;
    private transient Label firstGroupLabel, secondGroupLabel;
    private Project project;

    /**
     *
     * @param pcaFilePath - PCA file absolute path
     * @throws FileNotFoundException
     * @throws IOException
     */
    public PCAGraph(String pcaFilePath, Project project) throws FileNotFoundException, IOException {
        this.project = project;
        eigenValues = new ArrayList<>();
        // read data
        readGraphData(pcaFilePath);

        // set pc graph in project
        project.setPcaGraph(this);
    }

    /**
     *
     * @param pcaFilePath This is the PCA file absolute path
     * @throws FileNotFoundException
     * @throws IOException
     */
    @Override
    protected final void readGraphData(String pcaFilePath) throws FileNotFoundException, IOException {
        bufferReader = Genesis.openFile(pcaFilePath);
        line = bufferReader.readLine();
        String fields[] = line.trim().split("\\s+");

        // check if file has eigen values - search for "eig" in first string
        if (fields[0].contains("eig")) {
            eigenValues.addAll(Arrays.asList(Arrays.copyOfRange(fields, 1, fields.length)));
            line = bufferReader.readLine(); // read next line
            fields = line.trim().split("\\s+");
        }

        // check if ids are seperated by colons:
        if (fields[0].contains(":")) {
            // check if last column is a control column
            if (fields[fields.length - 1].contains("C") || fields[fields.length - 1].contains("c")) {
                // set columns
                setPcaColumnLabels(fields, 2);

                // read all the remaining lines
                while (line != null) {
                    fields = line.trim().split("\\s+");
                    String[] ids = fields[0].split(":");
                    String fid = ids[0];
                    String iid = ids[1];
                    String pcs[] = Arrays.copyOfRange(fields, 1, fields.length - 1);

                    // set pcs for every subject
                    for(Subject s: project.getSubjectsList()){
                        if(s.getFid().equals(fid) && s.getIid().equals(iid)){
                            s.setPcs(pcs);
                        }
                    }
                    line = bufferReader.readLine();
                }

            } else {
                // if the file doesnot contain the control column
                // remove only the id column
                setPcaColumnLabels(fields, 1);

                while (line != null) {
                    fields = line.trim().split("\\s+");
                    String[] ids = fields[0].split(":");
                    String fid = ids[0];
                    String iid = ids[1];
                    String pcs[] = Arrays.copyOfRange(fields, 1, fields.length);

                    // set pcs for every subject
                    for(Subject s: project.getSubjectsList()){
                        if(s.getFid().equals(fid) && s.getIid().equals(iid)){
                            s.setPcs(pcs);
                        }
                    }

                    line = bufferReader.readLine();
                }
            }

        }

        if (!fields[0].contains(":")) {
            if (fields[fields.length - 1].contains("C") || fields[fields.length - 1].contains("c")) {
                // remove first and second id, and the control column
                setPcaColumnLabels(fields, 3);

                while (line != null) {
                    fields = line.trim().split("\\s+");
                    String fid = fields[0];
                    String iid = fields[1];
                    String[] pcs = Arrays.copyOfRange(fields, 2, fields.length - 1);

                    // set pcs for every subject
                    for(Subject s: project.getSubjectsList()){
                        if(s.getFid().equals(fid) && s.getIid().equals(iid)){
                            s.setPcs(pcs);
                        }
                    }

                    // read next line
                    line = bufferReader.readLine();
                }

            } else {
                // remove only the 2 id columns
                setPcaColumnLabels(fields, 2);
                while (line != null) {
                    fields = line.trim().split("\\s+");
                    String fid = fields[0];
                    String iid = fields[1];
                    String pcs[] = Arrays.copyOfRange(fields, 2, fields.length);

                    // set pcs for every subject
                    for(Subject s: project.getSubjectsList()){
                        // note: some ids in pheno are not in the evec file
                        if(s.getFid().equals(fid) && s.getIid().equals(iid)){
                            s.setPcs(pcs);
                        }
                    }
                    line = bufferReader.readLine();
                }
            }

        }

    }

    /***
     * list of column labels is used by the user to select which pcs to be viewed
     * @param fields
     * @param unwantedCols
     */
    private void setPcaColumnLabels(String fields[], int unwantedCols) {
        // remove first id and control column
        int num_of_pcs = fields.length - unwantedCols; // get number of pcs
        pcaColumnLabels = new String[num_of_pcs];
        for (int i = 0; i < num_of_pcs; i++) {
            // store every pca: [PCA 1, PCA 2, ...]
            pcaColumnLabels[i] = "PCA " + (i + 1);
        }
    }

    /**
     *
     * @return
     */
    public ObservableList<String> getPcaColumnLabels() {
        ObservableList<String> pcLabelsList = FXCollections.observableArrayList();
        pcLabelsList.addAll(Arrays.asList(pcaColumnLabels));
        return pcLabelsList;
    }

    /**
     * set project if reading it as a saved object
     * @param project
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     *
     * @return List of eigen values
     */
    public List<String> getEigenValues() {
        return eigenValues;
    }

    /**
     * recreate pca graphs when reading a saved project
     * @param pcaX
     * @param pcaY
     * @param graphIndex
     * @return
     */
    public ScatterChart<Number, Number> recreatePcaGraph(int pcaX, int pcaY, int graphIndex) throws IOException {
        // get index of pcs which will be used to extract associated values in the pcs[]
        int xPcaIndex = pcaX - 1; // position in the pcs array
        int yPcaIndex =  pcaY - 1; // position in the pcs array

        // retrieve subjects in the graphIndex position
        PCAGraphLayout pcaGraphLayout = project.getPCAGraphLayouts().get(graphIndex);
        ArrayList<Subject> subjectsList = project.getPcGraphSubjectsList().get(graphIndex);

        ScatterChart<Number, Number> sc = recreateScatterChart(pcaGraphLayout); // create a chart
        pcaGraphLayout.setGraphProperties(sc);

        // create series
        createSeries(xPcaIndex, yPcaIndex, subjectsList, sc);

        // set color, icon, click event and legend for this chart
        for(int i=0; i<sc.getData().size(); i++) {
            setSeriesColorsAndIcons(sc, i, subjectsList);
            setSubjectMouseEvent(sc, i);
            // set legend
            setLegend(sc, i, project.getPcaGroupColors(), project.getPcaGroupIcons());
        }
        return sc;
    }

    /**
     *
     * @param pcaX e.g. 1
     * @param pcaY e.g. 2
     * @return
     * @throws IOException
     */
    @Override
    public void createGraph(int pcaX, int pcaY) throws IOException {

        int xPcaIndex = pcaX-1; // position in the pcs array
        int yPcaIndex = pcaY-1; // position in the pcs array

        // store the pc column numbers for every chart created
        project.getSelectedPCs().add(new int[]{pcaX, pcaY}); // [[1,2], [3,4], ...]

        ScatterChart<Number, Number> sc = createScatterChart(pcaX, pcaY);

        ArrayList<Subject> subjects = (ArrayList<Subject>) project.getSubjectsList().clone();

        // add subjects to the list of all subjects
        project.getPcGraphSubjectsList().add(subjects);

        // add an empty list of annotation list
        ArrayList<Annotation> annotationList = new ArrayList<>();
        project.getPcGraphAnnotationsList().add(annotationList);

        // get a copy of default colors and add them to a list for this particular graph
        groupColors = project.getPcaGroupColors();

        // get a copy of default icons and add them to a list for this particular graph
        groupIcons = project.getPcaGroupIcons();

        // create series
        createSeries(xPcaIndex, yPcaIndex, subjects, sc);

        // set colors and icons
        for(int i=0; i<sc.getData().size(); i++) {
            setSeriesColorsAndIcons(sc, i, subjects);
            // set mouse event and tooltip
            setSubjectMouseEvent(sc, i);

            // set the legend
            setLegend(sc, i, groupColors, groupIcons);
        }
        pcaChart =  sc;
    }

    /***
     * create a scatter chart and define its labels
     * @param pcaX
     * @param pcaY
     * @return
     */
    private ScatterChart<Number, Number> createScatterChart(int pcaX, int pcaY){
        String xAxisLabel = "PCA "+pcaX;
        String yAxisLabel = "PCA "+pcaY;

        PCAGraphLayout pcaGraphLayout = new PCAGraphLayout();
        pcaGraphLayout.setxAxisLabel(xAxisLabel);
        pcaGraphLayout.setyAxisLabel(yAxisLabel);

        NumberAxis xAxis = new NumberAxis();
        xAxis.setSide(Side.BOTTOM);
        xAxis.setLabel(xAxisLabel);  // set its label
        xAxis.setMinorTickVisible(false);
        
        NumberAxis yAxis = new NumberAxis();
        yAxis.setSide(Side.LEFT); 
        yAxis.setLabel(yAxisLabel);  // set its label
        yAxis.setMinorTickVisible(false);


        ScatterChart<Number, Number> sc = new ScatterChart<>(xAxis, yAxis);
        String title = xAxisLabel + " Vs " + yAxisLabel + " Chart";
        sc.setTitle(title); // set as default value
        pcaGraphLayout.setGraphTitle(title);

        project.getPCAGraphLayouts().add(pcaGraphLayout); // keep this graph's layout
        
        return sc;
    }

    private ScatterChart<Number, Number> recreateScatterChart(PCAGraphLayout pcaGraphLayout){
        NumberAxis xAxis = new NumberAxis();
        xAxis.setSide(Side.BOTTOM);
        xAxis.setLabel(pcaGraphLayout.getxAxisLabel());  // set its label
        xAxis.setMinorTickVisible(pcaGraphLayout.isShowAxisMarks());

        NumberAxis yAxis = new NumberAxis();
        yAxis.setSide(Side.LEFT);
        yAxis.setLabel(pcaGraphLayout.getyAxisLabel());  // set its label
        yAxis.setMinorTickVisible(pcaGraphLayout.isShowAxisMarks());

        ScatterChart<Number, Number> sc = new ScatterChart<>(xAxis, yAxis);
        sc.setTitle(pcaGraphLayout.getGraphTitle()); // set as default value
        return sc;

    }

    /**
     * create series (phenotype groups)
     * @param xPcaIndex
     * @param yPcaIndex
     * @param subjectsList
     * @param sc
     */
    private void createSeries(int xPcaIndex, int yPcaIndex, ArrayList<Subject> subjectsList, ScatterChart<Number, Number> sc) {
        if(project.isPhenoFileProvided()){
            for (int i= 0; i<project.getGroupNames().size(); i++){
                series = new XYChart.Series<>();

                String serieName = project.getGroupNames().get(i);
                series.setName(serieName);

                for (Subject s: subjectsList){
                    setSeriesData(xPcaIndex, yPcaIndex, serieName, s);
                }
                // do not add empty series (pheno groups with no data) to the chart
                if(series.getData().size()>0){
                    sc.getData().add(series);
                }
            }
        }else { // only fam file
            series = new XYChart.Series<>();
            series.setName(project.getGroupNames().get(0));
            for (Subject s: subjectsList){
                setSeriesData(xPcaIndex, yPcaIndex, project.getGroupNames().get(0), s);
            }
            sc.getData().add(series);
        }
    }

    /**
     * set data for every series
     * @param xPcaIndex
     * @param yPcaIndex
     * @param serieName
     * @param s
     */
    private void setSeriesData(int xPcaIndex, int yPcaIndex, String serieName, Subject s) {
        if(project.isPhenoFileProvided()){
            if(s.getPhenos()[project.getPhenoColumnNumber()-1].equals(serieName) && s.getPcs() != null && s.isHidden()==false){
                series.getData().add(new XYChart.Data(Float.parseFloat(s.getPcs()[xPcaIndex]), Float.parseFloat(s.getPcs()[yPcaIndex])));
            }
        }else{ // if only fam file is provided
            if(s.getPcs() != null && s.isHidden()==false){
                series.getData().add(new XYChart.Data(Float.parseFloat(s.getPcs()[xPcaIndex]), Float.parseFloat(s.getPcs()[yPcaIndex])));

            }
        }
    }

    /**
     * set the color and icon for every fa
     * @param sc
     * @param serieIndex
     */
    private void setSeriesColorsAndIcons(ScatterChart<Number, Number> sc, int serieIndex, ArrayList<Subject> sub){
        XYChart.Series<Number, Number> serie = sc.getData().get(serieIndex);
        Set<Node> nodes = sc.lookupAll(".series" + serieIndex);

        if (project.isPhenoFileProvided()) {
            for(Subject s : sub) {
                if (s.getPhenos()[project.getPhenoColumnNumber() - 1].equals(serie.getName())) {
                    for (Node n : nodes) {
                        n.setStyle(getStyle(s.getColor(), s.getIcon()));
                        scaleIconSize(n, s.getIconSize());
                    }
                    break;
                }
            }
        }else{
            for(Subject s : sub) {
                for (Node n : nodes) {
                    n.setStyle(getStyle(s.getColor(), s.getIcon()));
                    scaleIconSize(n,s.getIconSize());
                }
                break;
            }
        }
    }

    /**
     * add tool tip and click event on every pca graph data point
     * @param sc
     * @param serieIndex
     */
    private void setSubjectMouseEvent(ScatterChart<Number, Number> sc, int serieIndex) {
        for(XYChart.Data<Number, Number> data : sc.getData().get(serieIndex).getData()){

            // display tooltip to show x and y pc values of every subject/individual
            Tooltip.install(data.getNode(), new Tooltip(data.getXValue() + "\n" + data.getYValue()));
            data.getNode().setOnMouseClicked(e -> {
                try {
                    // launch individual details window
                    showIndividualDetails(data, sc);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    /**
     * set Legend Items and add left/right click events on every legend item
     * @param sc
     * @param serieIndex
     */
    public void setLegend(ScatterChart<Number, Number> sc, int serieIndex,  HashMap iconColors, HashMap iconShapes){

        // sort legend items
        sortLegendItems(sc);

        for (Node n : sc.getChildrenUnmodifiable()) {
            if (n.getClass().toString().equals("class com.sun.javafx.charts.Legend")) {
                TilePane tn = (TilePane) n;
                ObservableList<Node> children = tn.getChildren();
                tn.setCursor(Cursor.CROSSHAIR);

                // add hover property
                tn.hoverProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        tn.setStyle("-fx-background-color: #b3e5fc");
                    }else {
                        tn.setStyle(null);
                    }
                });

                Label lab = (Label) children.get(serieIndex).lookup(".chart-legend-item");

                // get color and shape of the group for this lab
//                String groupName = project.getOrderOfLegendItems().get(serieIndex); //lab.getText()
//                String iconColor = (String) iconColors.get(lab.getText());
//                String iconShape = (String) iconShapes.get(lab.getText());

                String iconColor = null;
                String iconShape = null;

                if(project.isProjIsImported()){
                    try {
                        iconColor = (String) iconColors.get(project.getOrderOfLegendItems().get(serieIndex));
                        iconShape = (String) iconShapes.get(project.getOrderOfLegendItems().get(serieIndex));
                        lab.getGraphic().setStyle(getStyle(iconColor, iconShape, project.getDefaultIconSize()));
                    }catch(Exception e){
                        iconColor = (String) iconColors.get(lab.getText());
                        iconShape = (String) iconShapes.get(lab.getText());
                        lab.getGraphic().setStyle(getStyle(iconColor, iconShape, project.getDefaultIconSize()));
                    }
                }else{
                    iconColor = (String) iconColors.get(lab.getText());
                    iconShape = (String) iconShapes.get(lab.getText());
                    lab.getGraphic().setStyle(getStyle(iconColor, iconShape, project.getDefaultIconSize()));
                }

                // divide legend icon size by 2 - otherwise it will be twice bigger than the icons of the graph
                // set the legend icons (graphics)
                lab.getGraphic().setStyle(getStyle(iconColor, iconShape, project.getDefaultIconSize()));

                // legend mouse click events for left and right click
                for (XYChart.Series<Number, Number> s : sc.getData()) {
                    if (s.getName().equals(lab.getText())) {
                        lab.setOnMouseClicked(me -> {
                            // Toggle group (phenotype) visibility on left click
                            if (me.getButton() == MouseButton.PRIMARY) {
                                chartGroupNameClicked(me.getSource(), sc);
                            }else{ // right click
                                try {
                                    FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("view/PCAGroupLabel.fxml"));
                                    Parent parent = (Parent) fxmlLoader.load();
                                    Stage dialogStage = new Stage();
                                    dialogStage.setScene(new Scene(parent));
                                    dialogStage.setResizable(false);

                                    // show subject details when clicked
                                    PCAGroupLabelController pglc = fxmlLoader.getController();
                                    pglc.setComboBox();
                                    pglc.setGroupNameLbl(lab.getText());
                                    pglc.setProj(project);
                                    pglc.setGroupNameNode(lab);
                                    pglc.setChart(sc);
                                    pglc.setPCAGraph(this);
                                    dialogStage.initModality(Modality.APPLICATION_MODAL);
                                    dialogStage.showAndWait();

                                }catch (IOException e){
                                    ;
                                }
                            }
                        });
                        break;
                    }
                }
            }

        }
    }

    /**
     * sort legend items alphabetically
     * @param sc
     */
    private void sortLegendItems(ScatterChart<Number, Number> sc){
        for (Node n : sc.getChildrenUnmodifiable()) {
            if (n.getClass().toString().equals("class com.sun.javafx.charts.Legend")) {
                TilePane tn = (TilePane) n;
                ObservableList<Node> children = tn.getChildren();
                ObservableList<Label> labels = FXCollections.observableArrayList();

                for(int i=0;i<children.size();i++){
                    labels.add((Label)children.get(i).lookup(".chart-legend-item"));
                }

                // arrange the items as organised before
                if(project.isProjIsImported()) {
                    labels.sort(Comparator.comparing(v -> project.getOrderOfLegendItems().indexOf(v.getText())));
                }else{
                    // sort alphabetically
                    Collections.sort(labels, new LabelComparator());
                }

                tn.getChildren().setAll(labels);
            }
        }
    }

    public void showHiddenGroup(String group) {
        ArrayList<XYChart.Series<Number, Number>> seriesArrayList = hiddenPCAGroups.get(group);
        int sizeOfHiddenGroups = seriesArrayList.size();
        int numOfCharts = mainController.getPcaChartsList().size();
        if(sizeOfHiddenGroups==numOfCharts){

            // all pca charts
            for (int i=0; i<mainController.getPcaChartsList().size();i++){

                ScatterChart<Number, Number> chart = mainController.getPcaChartsList().get(i);

                chart.getData().add(seriesArrayList.get(i));

                // set color, icon, click event and legend for this chart
                for(int k=0; k<chart.getData().size(); k++) {
                    // set legend
                    setLegend(chart, k, project.getPcaGroupColors(), project.getPcaGroupIcons());
                }
            }
        }

        // remove group from the hidden list of all graphs in the project
        project.getHiddenGroups().remove(group);

    }

    private void chartGroupNameClicked(Object source, ScatterChart<Number, Number> sc) {
//        Label clickNext = new Label("Click where to");

        if (!(source instanceof Label)) {
            return;
        }
        Label lbl = (Label) source;

        if (labelClickCounter == 0) {
            firstGroupLabel = lbl;
//            clickNext.setStyle("-fx-text-fill: red");

            for (Node n : sc.getChildrenUnmodifiable()) {
                if (n.getClass().toString().equals("class com.sun.javafx.charts.Legend")) {
                    TilePane tn = (TilePane) n;
                    ObservableList<Node> children = tn.getChildren(); // get legend items
                    tn.setCursor(Cursor.HAND);
//                    children.add(clickNext);
                    break;
                }
            }

        } else {
            secondGroupLabel = lbl;
            // swap
            groupNameSwap();

            // remove the next label
            for (Node n : sc.getChildrenUnmodifiable()) {
                if (n.getClass().toString().equals("class com.sun.javafx.charts.Legend")) {
                    TilePane tn = (TilePane) n;
                    ObservableList<Node> children = tn.getChildren(); // get legend items
                    tn.setCursor(Cursor.CROSSHAIR);
//                    children.remove(children.size() - 1);
                    break;
                }
            }
        }
        labelClickCounter = ++labelClickCounter % 2;  // change values between 0 1
    }

    private void groupNameSwap() {

        for(int g=0; g<mainController.getPcaChartsList().size(); g++) {

            ScatterChart<Number, Number> sc = mainController.getPcaChartsList().get(g);
            int firstIndex = 0;
            int secondIndex = 0;
            for (Node n : sc.getChildrenUnmodifiable()) {
                if (n.getClass().toString().equals("class com.sun.javafx.charts.Legend")) {
                    TilePane tn = (TilePane) n;
                    firstIndex = tn.getChildren().indexOf(firstGroupLabel);
                    secondIndex = tn.getChildren().indexOf(secondGroupLabel);
                    break;
                }
            }

            // shift down
            if(firstIndex<secondIndex) {
                try {
                    for (int graph = 0; graph < mainController.getPcaChartsList().size(); graph++) {
                        ObservableList<Node> children = getLegend(graph);
                        for (int j = firstIndex; j <= secondIndex; j++) { // starting
                            Node first = children.get(j);
                            Node second = children.get(j + 1);
                            if ((first instanceof Label)) {
                                if (j < secondIndex) { // limit
                                    children.set(j, new Label("Cur")); // current position
                                    children.set(j + 1, new Label("Cur")); // next position
                                    children.set(j + 1, first);
                                    children.set(j, second);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    return;
                }
            }

            // shift up
            if(firstIndex>secondIndex){
                try{
                for(int graph=0; graph<mainController.getPcaChartsList().size(); graph++) {
                    ObservableList<Node> children = getLegend(graph);
                    for (int j = firstIndex; j > secondIndex; j--) { // starting
                        Node first = children.get(j);
                        Node second = children.get(j - 1);
                        if ((first instanceof Label)) {
                            if (j > secondIndex) { // limit
                                children.set(j, new Text("Cur")); // current position
                                children.set(j - 1, new Text("Cur")); // next position
                                children.set(j - 1, first);
                                children.set(j, second);
                            }
                        }
                    }
                }
                }catch (Exception e){
                    ;
                }
            }

            // swap items in the legend list
            project.getOrderOfLegendItems().clear();
            for(Node node: getLegend(0)){
                if(node instanceof Label){
                    project.getOrderOfLegendItems().add(((Label) node).getText());
                }
            }
        }
    }

    private ObservableList<Node> getLegend(int graphIndex){
        ObservableList<Node> children = null;
            ScatterChart<Number, Number> chart = mainController.getPcaChartsList().get(graphIndex);
            for (Node n : chart.getChildrenUnmodifiable()) {
                if (n.getClass().toString().equals("class com.sun.javafx.charts.Legend")) {
                    TilePane tn = (TilePane) n;
                    children = tn.getChildren();
                }
            }
        return children;
    }

    public void renameLegendGroupNames(String oldGroupName, String newGroupName){
        // rename group in all charts
        for(int i=0; i<mainController.getPcaChartsList().size(); i++){ // every chart

            ScatterChart<Number, Number> sc = mainController.getPcaChartsList().get(i);

            for (Node n : sc.getChildrenUnmodifiable()) {
                if (n.getClass().toString().equals("class com.sun.javafx.charts.Legend")) {
                    TilePane tn = (TilePane) n;
                    ObservableList<Node> children = tn.getChildren(); // get legend items

                    for (int j=0;j<sc.getData().size();j++){ // get every serie
                        Label lab = (Label) children.get(j).lookup(".chart-legend-item");
                        if(lab.getText().equals(oldGroupName)){
                            // change the node with group name on the chart
                            lab.setText(newGroupName);
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     * remove group from charts
     * @param oldGroupName
     */
    public void hideGroup(String oldGroupName) {
        hideGroup(oldGroupName, false);
    }
    
    public void hideGroup(String oldGroupName, boolean reload) {

        ArrayList<XYChart.Series<Number, Number>> hiddenGroups = new ArrayList<>(); // for various Ks

        // all pca charts
        for (int i=0; i<mainController.getPcaChartsList().size();i++){
            ScatterChart<Number, Number> chart = mainController.getPcaChartsList().get(i);

            for(int j=0;j<chart.getData().size();j++){
                XYChart.Series<Number, Number> series = chart.getData().get(j);

                if (series.getName().equals(oldGroupName)) {

            if (!reload)
                project.getHiddenGroups().add(series.getName()); // save this series
                    hiddenGroups.add(series); // save this series
                    chart.getData().remove(j); // remove it from the chart

                    // set color, icon, click event and legend for this chart
                    for(int k=0; k<chart.getData().size(); k++) {
                        // set legend
                        setLegend(chart, k, project.getPcaGroupColors(), project.getPcaGroupIcons());
                    }
                    break;
                }
            }
        }

        if (hiddenPCAGroups == null)
            hiddenPCAGroups = new HashMap<>();
        hiddenPCAGroups.put(oldGroupName, hiddenGroups);
    }

    /**
     * Sort legend items
     */
    private static class LabelComparator implements Comparator<Label> {
        @Override
        public int compare(Label o1, Label o2) {
            String s1 = o1.getText();
            String s2 = o2.getText();
            return s1.compareTo(s2);
        }
    }

    /**
     *
     * @param series
     */
    public void hideIndividual(XYChart.Series<Number, Number> series, String ids[]){
        // add this individual to a list of hidden individuals
        
        //FIXME: is this what breaks save and load? Put back and now works...
        
        project.getHiddenPoints().add(ids[0]+" "+ids[1]); // ids of the individual

        // for every displayed graph, hide this individual
        for(int i=0;i< mainController.getPcaChartsList().size();i++){

            ScatterChart<Number, Number> graph = mainController.getPcaChartsList().get(i);

            // get pc labels of the graph
            int pcCols[] = project.getSelectedPCs().get(i); // e.g [1, 2] for pca 1 vs pca 2 graph

            // get the correct serie to remove the individual from
            for (XYChart.Series<Number, Number> sss : graph.getData()){
                if(sss.getName().equals(series.getName())){

                    // get individual with these ids
                    for(Subject s: project.getPcGraphSubjectsList().get(i)){
                        if(s.getPcs() != null && s.getFid().equals(ids[0]) && s.getIid().equals(ids[1])){
                            s.setHidden(true);
                            // get x and y values in the position of the pc columns
                            Float xValue = Float.parseFloat(s.getPcs()[pcCols[0]-1]); // reduce position by 1
                            Float yValue = Float.parseFloat(s.getPcs()[pcCols[1]-1]);

                            // if the data point has same x y value, remove it from the series
                            for(XYChart.Data<Number, Number> d: sss.getData()){
                                if(d.getXValue().equals(xValue) && d.getYValue().equals(yValue)){
                                    sss.getData().remove(d);
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     *
     * @param ids
     */
    public void showIndividual(String ids[]){
        String fid = ids[0];
        String iid = ids[1];
        String fid_iid = fid+" "+iid;

        // retrieve subject properties
        Float hiddenXValue = null, hiddenYValue = null;
        String groupName = null, iconColor = null, iconsvg  = null;
        int iconSize = 0;

        // remove this individual from the list of hidden ids
        project.getHiddenPoints().remove(fid_iid);

        for(int i=0;i< mainController.getPcaChartsList().size();i++){

            // get pc labels of the graph
            int pcCols[] = project.getSelectedPCs().get(i); // e.g [1, 2] for pca 1 vs pca 2 graph

            // get properties of this individual / subject
            for(Subject s: project.getPcGraphSubjectsList().get(i)){
                if(s.getPcs()!=null && s.getFid().equals(fid) && s.getIid().equals(iid)){
                    hiddenXValue = Float.parseFloat(s.getPcs()[pcCols[0]-1]);
                    hiddenYValue =  Float.parseFloat(s.getPcs()[pcCols[1]-1]);
                    iconSize = s.getIconSize();
                    groupName = s.getPhenos()[project.getPhenoColumnNumber()-1];
                    iconColor = s.getColor();
                    iconsvg = s.getIcon();
                    s.setHidden(false); // activate visibility of a point
                    break;
                }
            }

            ScatterChart<Number, Number> graph = mainController.getPcaChartsList().get(i);

            for(XYChart.Series<Number, Number> s: graph.getData()){
                if(s.getName().equals(groupName)){
                    XYChart.Data<Number, Number> data = new XYChart.Data(hiddenXValue, hiddenYValue);
                    s.getData().add(data); // add point to graph

                    // set the style of icon
                    data.getNode().setStyle(getStyle(iconColor, iconsvg));
                    scaleIconSize(data.getNode(), iconSize);

                    data.getNode().setOnMouseClicked(e ->{
                        try {
                            showIndividualDetails(data, graph);
                        } catch (Exception ex) {
                            ;
                        }
                    });
                    break;
                }
            }
        }
    }

    /**
     * show every individuals/subject details when clicked
     * @param data
     * @param chart
     * @throws IOException
     */
    public void showIndividualDetails(XYChart.Data<Number, Number> data, ScatterChart<Number, Number> chart) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("view/IndividualDetails.fxml"));
            Parent parent = fxmlLoader.load();
            Stage dialogStage = new Stage();

            dialogStage.setScene(new Scene(parent));
            dialogStage.setResizable(false);
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            PCAIndividualDetailsController individualDetailsController = fxmlLoader.getController();
            individualDetailsController.disableOK();
            individualDetailsController.setPCAGraph(this);
            individualDetailsController.setProject(project);
            individualDetailsController.setChart(mainController.getPcaChart());

            // set values of clicked pca point
            String xValue = String.valueOf(data.getXValue());
            String yValue = String.valueOf(data.getYValue());
            individualDetailsController.setClickedPoint(xValue, yValue);

            // display details of the point
            String xAxisLabel = chart.getXAxis().getLabel();
            String yAxisLabel = chart.getYAxis().getLabel();
            individualDetailsController.setPcaLabel(xAxisLabel + ": " + xValue + "\n" + yAxisLabel + ": " + yValue);

            // list of current pca subjects
            ArrayList<Subject> subjects = project.getPcGraphSubjectsList().get(project.getCurrentTabIndex());
            for(Subject s: subjects){
                ObservableList<String> phenoDetails;
                if(s.getPcs()!=null && Arrays.asList(s.getPcs()).contains(xValue) && Arrays.asList(s.getPcs()).contains(yValue)){
                    if(project.isPhenoFileProvided()){
                        phenoDetails = FXCollections.<String>observableArrayList(s.getPhenos());
                        phenoDetails.add(s.getSex());
                        individualDetailsController.setPhenotypeGroup(phenoDetails.get(project.getPhenoColumnNumber()-1)); // MKK, LWK if column is 3 (index 2)
                    }else {
                        phenoDetails = FXCollections.observableArrayList(new String[]{s.getFid(), s.getIid(), s.getSex()});
                        individualDetailsController.setPhenotypeGroup(project.getGroupNames().get(0));
                    }
//
                    individualDetailsController.setPhenoListView(phenoDetails);
                    individualDetailsController.setIconColor(s.getColor());
                    individualDetailsController.setIconSVGShape(s.getIcon());
                    individualDetailsController.setIconSize(s.getIconSize());
                    individualDetailsController.setIdsOfClickedPoint(new String[]{s.getFid(), s.getFid()});
                    break;
                }
            }

//            // display icon for clicked point
            individualDetailsController.setIconDisplay(data.getNode().getStyle());

            dialogStage.showAndWait();

        } catch (Exception ex) {;}
    }

    /**
     * modify the series/phenotype group
     * @param serieName
     * @param iconColor
     * @param iconSVGShape
     * @param iconSize
     */
    public void changeSeriesProperties(String serieName, String iconColor, String iconSVGShape, int iconSize){
        // change for every subject
        for(int g=0; g<mainController.getPcaChartsList().size(); g++) {
            for (Subject s : project.getPcGraphSubjectsList().get(g)) {
                if (project.isPhenoFileProvided()) {
                    if (s.getPhenos()[project.getPhenoColumnNumber() - 1].equals(serieName)) {
                        s.setColor(iconColor);
                        s.setIcon(iconSVGShape);
                        s.setIconSize(iconSize);
                    }
                } else {
                    s.setColor(iconColor);
                    s.setIcon(iconSVGShape);
                    s.setIconSize(iconSize);
                }
            }
        }

        // change the color and icon of this phenotype category
        project.getPcaGroupColors().put(serieName, iconColor);
        project.getPcaGroupIcons().put(serieName, iconSVGShape);

        for(int g=0; g<mainController.getPcaChartsList().size(); g++){
            ScatterChart<Number, Number> pcgraph = mainController.getPcaChartsList().get(g);
            for(int serieNum=0;serieNum<pcgraph.getData().size();serieNum++){
                if(pcgraph.getData().get(serieNum).getName().equals(serieName)){
                    for (XYChart.Data<Number, Number> dt : pcgraph.getData().get(serieNum).getData()) {
                        scaleIconSize(dt.getNode(), iconSize);
                        dt.getNode().lookup(".chart-symbol").setStyle(getStyle(iconColor,iconSVGShape));
                    }
                }
            }
            for(XYChart.Series<Number, Number> series: pcgraph.getData()){
                if(series.getName().equals(serieName)){
                    // change population group color and icon on the chart
                    for (XYChart.Data<Number, Number> dt : series.getData()) {
                        String style = getStyle(iconColor, iconSVGShape, iconSize);
                        dt.getNode().lookup(".chart-symbol").setStyle(style);
                    }

                    // change population group color and icon the legend - dont change the size of icons on the legend
                    for (Node n : pcgraph.getChildrenUnmodifiable()) {
                        if (n.getClass().toString().equals("class com.sun.javafx.charts.Legend")) {
                            TilePane tn = (TilePane) n;
                            ObservableList<Node> children = tn.getChildren();
                            for(int i=0;i<children.size();i++){
                                Label lab = (Label) children.get(i).lookup(".chart-legend-item");
                                if(lab.getText().equals(serieName)){
                                    // divide legend icon size by 2 - otherwise it will be twice bigger than the icons of the graph
                                    lab.getGraphic().setStyle(getLegendStyle(iconColor, iconSVGShape));
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * change properties of clicked individual
     * @param data
     * @param xValue
     * @param yValue
     * @param iconColor
     * @param iconSVGShape
     * @param iconSize
     */
    public void changeSubjectProperties(XYChart.Data<Number, Number> data, String xValue, String yValue, String iconColor, String iconSVGShape, int iconSize){
        for(Subject s: project.getPcGraphSubjectsList().get(project.getCurrentTabIndex())){
            if(s.getPcs() != null && Arrays.asList(s.getPcs()).contains(xValue) && Arrays.asList(s.getPcs()).contains(yValue)){
                //change properties
                s.setColor(iconColor);
                s.setIcon(iconSVGShape);
                s.setIconSize(iconSize);

                // show changes
                data.getNode().lookup(".chart-symbol").setStyle(getStyle(iconColor, iconSVGShape));
                scaleIconSize(data.getNode(), iconSize);
                break;
            }
        }
    }

    /**
     * reset subject properties
     * @param data
     * @param xValue
     * @param yValue
     */
    public void resetSubjectProperties(XYChart.Data<Number, Number> data, String xValue, String yValue) {
        for(Subject s: project.getPcGraphSubjectsList().get(project.getCurrentTabIndex())){
            if(s.getPcs() != null && Arrays.asList(s.getPcs()).contains(xValue) && Arrays.asList(s.getPcs()).contains(yValue)){

                HashMap groupColors = project.getPcaGroupColors();
                HashMap groupIcon = project.getPcaGroupIcons();

                // get the color and icon of the chart in this position
                String color = (String) groupColors.get(s.getPhenos()[project.getPhenoColumnNumber()-1]);
                String icon = (String) groupIcon.get(s.getPhenos()[project.getPhenoColumnNumber()-1]);

                // reset the subject properties
                s.setIcon(icon);
                s.setColor(color);

                // set the style to default
                data.getNode().lookup(".chart-symbol").setStyle(getStyle(color, icon));
                scaleIconSize(data.getNode(), s.getIconSize());
                break;
            }
        }
    }

    public void  styleLegendItems(String font, double size, String color){
        for(int g=0; g<mainController.getPcaChartsList().size(); g++) {
            for(Node node: getLegend(g)){
                if(node instanceof Label){
                    Label lbl = ((Label) node);
                    lbl.setStyle("-fx-text-fill: #"+color+";"+
                            "-fx-font-size: "+size+"pt;"+
                            "-fx-font-family: \"" + font + "\";");
                }
            }
        }
    }

    /**
     * return a string of style
     * @param color
     * @param icon
     * @return
     */
    public String getStyle(String color, String icon){
        String s = "-fx-shape:\""+icon+"\";"
//                + "-fx-background-insets: 0, 2;"
//                +"-fx-background-size: "+iconSize+"px;"
//                + "-fx-padding: "+iconSize+"px;"
//                + "-fx-background-radius: "+iconSize+"px;"
                + "-fx-background-color: "+color+","+color+";";
        return s;
    }

    public String getStyle(String color, String icon, int iconSize){
        String s = "-fx-shape:\""+icon+"\";"
//                + "-fx-background-insets: 0, 2;"
//                +"-fx-background-size: "+iconSize+"px;"
//                + "-fx-padding: "+iconSize+"px;"
                + "-fx-background-radius: "+iconSize+"px;"
                + "-fx-background-color: "+color+","+color+";";
        return s;
    }

    public String getLegendStyle(String color, String icon){
        String s = "-fx-shape:\""+icon+"\";"
                + "-fx-background-color:"+color+","+color+";";
        return s;
    }

    private void scaleIconSize(Node n, double iconSize){
        double scaleValue = Double.parseDouble(String.valueOf(iconSize))/10;
        n.setScaleX(scaleValue);
        n.setScaleY(scaleValue);
    }


    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * get created graph - called by the setPCAGraph method in main controller
     * @return
     */
    public ScatterChart<Number, Number> getPcaChart() {
        return pcaChart;
    }

    @Override
    public void createAdmixGraph() {}

}
