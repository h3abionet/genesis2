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
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import org.h3abionet.genesis.Genesis;
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
    private transient XYChart.Series<Number, Number> series;
    private transient BufferedReader bufferReader;
    private transient ScatterChart<Number, Number> pcaChart;
    private String line;
    private ArrayList<Subject> subjects;
    private HashMap groupColors;
    private HashMap groupIcons;

    /**
     *
     * @param pcaFilePath - PCA file absolute path
     * @throws FileNotFoundException
     * @throws IOException
     */
    public PCAGraph(String pcaFilePath) throws FileNotFoundException, IOException {
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
                    for(Subject s: project.getPcGraphSubjects()){
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
                    for(Subject s: project.getPcGraphSubjects()){
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
                    for(Subject s: project.getPcGraphSubjects()){
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
                    for(Subject s: project.getPcGraphSubjects()){
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
            pcaColumnLabels[i] = "PCA " + Integer.toString(i + 1);
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
     * @param x
     * @param y
     * @param graphIndex
     * @return
     */
    public ScatterChart<Number, Number> recreatePcaGraph(String x, String y, int graphIndex){

        String xAxisLabel = "PCA "+x;
        String yAxisLabel = "PCA "+y;

        int xPcaIndex = Integer.parseInt(x) - 1; // position in the pcs array
        int yPcaIndex =  Integer.parseInt(y) - 1; // position in the pcs array

        ScatterChart<Number, Number> sc = createScatterChart(xAxisLabel, yAxisLabel);

        // create series
        for (int i= 0; i<project.getGroupNames().size(); i++){
            series = new XYChart.Series<>();

            String serieName = project.getGroupNames().get(i);
            series.setName(serieName);

            for (Subject s: project.getPcGraphSubjectsList().get(graphIndex)){
                setSeriesData(xPcaIndex, yPcaIndex, serieName, s);
            }
            // do not add empty series (pheno groups with no data) to the chart
            if(series.getData().size()>0){
                sc.getData().add(series);
            }
        }

        for(int i=0; i<sc.getData().size(); i++) {
            setSerieColorsAndIcons(sc, i);
            setSubjectMouseEvent(sc, i);
            setLegend(sc, i);
        }

        return sc;
    }

    /**
     *
     * @param x e.g. PCA 1 -> only consider the integer (1)
     * @param y e.g. PCA 2 -> only consider the integer (2)
     * @return
     * @throws IOException
     */
    @Override
    public void createGraph(String x, String y) throws IOException {
        String xAxisPca = x; // xAxisLabel e.g PCA 1
        String yAxisPca = y; // yAxisLabel e.g PCA 2

        int xPcaNumber = Integer.parseInt(xAxisPca.substring(4, xAxisPca.length())); //1
        int xPcaIndex = xPcaNumber-1; // position in the pcs array
        
        int yPcaNumber = Integer.parseInt(yAxisPca.substring(4, yAxisPca.length())); //2
        int yPcaIndex = yPcaNumber-1; // position in the pcs array

        // store the pc column numbers for every chart created
        project.getSelectedPCs().add(xPcaNumber+" "+yPcaNumber); // "1 2"

        ScatterChart<Number, Number> sc = createScatterChart(xAxisPca, yAxisPca);

        // create a new subjects list for every pc graph
        subjects = (ArrayList)project.getPcGraphSubjects().clone();

        // add subjects to the list of all subjects
        project.getPcGraphSubjectsList().add(subjects);

        // get a copy of default colors and add them to a list for this particular graph
        groupColors = (HashMap) project.getGroupColors().clone();
        project.getListOfGraphsGroupColors().add(groupColors);

        // get a copy of default icons and add them to a list for this particular graph
        groupIcons = (HashMap) project.getGroupIcons().clone();
        project.getListOfGraphsGroupIcons().add(groupIcons);

        // create series
        for (int i= 0; i<project.getGroupNames().size(); i++){
            series = new XYChart.Series<>();

            String serieName = project.getGroupNames().get(i);
            series.setName(serieName);

            for (Subject s: subjects){
                setSeriesData(xPcaIndex, yPcaIndex, serieName, s);
            }
            // do not add empty series (pheno groups with no data) to the chart
            if(series.getData().size()>0){
                sc.getData().add(series);
            }
        }

        // set colors and icons
        for(int i=0; i<sc.getData().size(); i++) {
             setSerieColorsAndIcons(sc, i);
            // set mouse event and tooltip
            setSubjectMouseEvent(sc, i);
            // set the legend
            setLegend(sc, i);
        }

        pcaChart =  sc;
    }

    /***
     * create a scatter chart and define its labels
     * @param xAxisLabel
     * @param yAxisLabel
     * @return
     */
    private ScatterChart<Number, Number> createScatterChart(String xAxisLabel, String yAxisLabel){
        NumberAxis xAxis = new NumberAxis();
        xAxis.setSide(Side.BOTTOM);
        xAxis.setLabel(xAxisLabel);  // set its label
        
        NumberAxis yAxis = new NumberAxis();
        yAxis.setSide(Side.LEFT); 
        yAxis.setLabel(yAxisLabel);  // set its label

        ScatterChart<Number, Number> sc = new ScatterChart<>(xAxis, yAxis);
        sc.setTitle(yAxisLabel + " Vs " + yAxisLabel + " Chart"); // set as default value
        
        return sc;
    }

    /**
     * set data for every series
     * @param xPcaIndex
     * @param yPcaIndex
     * @param serieName
     * @param s
     */
    private void setSeriesData(int xPcaIndex, int yPcaIndex, String serieName, Subject s) {
        if(project.getPhenoColumnNumber() == 3){ // get the 3rd column in subject pheno details
            if(s.getPhenotypeA().equals(serieName) && s.getPcs() != null){
                series.getData().add(new XYChart.Data(Float.parseFloat(s.getPcs()[xPcaIndex]), Float.parseFloat(s.getPcs()[yPcaIndex])));
            }
        }else {
            if(s.getPhenotypeB().equals(serieName) && s.getPcs() != null){ // get 4th column in subject pheno details
                series.getData().add(new XYChart.Data(Float.parseFloat(s.getPcs()[xPcaIndex]), Float.parseFloat(s.getPcs()[yPcaIndex])));
            }
        }
    }

    /**
     * set the color and icon for every fa
     * @param sc
     * @param serieIndex
     */
    private void setSerieColorsAndIcons(ScatterChart<Number, Number> sc, int serieIndex){
        XYChart.Series<Number, Number> serie = sc.getData().get(serieIndex);
        Set<Node> nodes = sc.lookupAll(".series" + serieIndex);

        if(project.getPhenoColumnNumber() == 3){ // get phenotype A
            for(Subject s : subjects){
                if(s.getPhenotypeA().equals(serie.getName())){
                    for (Node n : nodes) {
                        n.setStyle(getStyle(s.getColor(), s.getIcon(), s.getIconSize()));
                    }
                    break;
                }
            }
        }

        if(project.getPhenoColumnNumber() == 4){  // get phenotype B
            for(Subject s : subjects){
                if(s.getPhenotypeB().equals(serie.getName())){
                    for (Node n : nodes) {
                        n.setStyle(getStyle(s.getColor(), s.getIcon(), s.getIconSize()));
                    }
                    break;
                }
            }
        }

    }

    /**
     * add tool tip and click event on every pca graph data point
     * @param sc
     * @param serieIndex
     */
    private void setSubjectMouseEvent(ScatterChart<Number, Number> sc, int serieIndex){
        for(XYChart.Data<Number, Number> data : sc.getData().get(serieIndex).getData()){

            // display tooltip to show x and y pc values of every subject/individual
            Tooltip.install(data.getNode(), new Tooltip(data.getXValue() + "\n" + data.getYValue()));

            data.getNode().setOnMouseClicked(e -> {
                try {
                    // launch individual details window
                    showIndividualDetails(data, sc);
                } catch (Exception ex) {
                    ;
                }
            });
        }
    }

    /**
     * set Legend Items and add left/right click events on every legend item
     * @param sc
     * @param serieIndex
     */
    private void setLegend(ScatterChart<Number, Number> sc, int serieIndex){
        for (Node n : sc.getChildrenUnmodifiable()) {
            if (n.getClass().toString().equals("class com.sun.javafx.charts.Legend")) {
                TilePane tn = (TilePane) n;
                ObservableList<Node> children = tn.getChildren();

                Label lab = (Label) children.get(serieIndex).lookup(".chart-legend-item");

                // get color and shape of the group for this lab
                String bgColor = (String) groupColors.get(lab.getText());
                String shape = (String) groupIcons.get(lab.getText());

                // divide legend icon size by 2 - otherwise it will be twice bigger than the icons of the graph
                // set the legend icons (graphics)
                lab.getGraphic().setStyle(getStyle(bgColor, shape, project.getDefaultIconSize()/2));

                // legend mouse click events for left and right click
                for (XYChart.Series<Number, Number> s : sc.getData()) {
                    if (s.getName().equals(lab.getText())) {
                        lab.setCursor(Cursor.HAND); // Hint user that legend symbol is clickable
                        lab.setOnMouseClicked(me -> {
                            // Toggle group (phenotype) visibility on left click
                            if (me.getButton() == MouseButton.PRIMARY) {
                                for (XYChart.Data<Number, Number> d : s.getData()) {
                                    if (d.getNode() != null) {
                                        d.getNode().setVisible(!d.getNode().isVisible()); // Toggle visibility of every node in the series
                                    }
                                }
                            }else{ // right click
                                // show dialog for legend position and hiding phenotype
                                List<String> choices = new ArrayList<>();
                                choices.add("bottom");
                                choices.add("right");

                                ChoiceDialog<String> dialog = new ChoiceDialog<>("right", choices);
                                dialog.setTitle("Legend");
                                dialog.setHeaderText("Select legend position");
                                dialog.setContentText("Position:");

                                Optional<String> result = dialog.showAndWait();
                                result.ifPresent(position -> sc.lookup(".chart").setStyle("-fx-legend-side: " + position + ";"));

                            }
                        });
                        break;
                    }
                }


            }

        }

        // sort legend items
        sortLegendItems(sc);

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
                Collections.sort(labels, new LabelComparator());
                tn.getChildren().setAll(labels);
            }
        }
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
     * @param data
     */
    public void hideIndividual(XYChart.Series<Number, Number> series, XYChart.Data<Number, Number> data){
        //remove the point
        series.getData().remove(data);

        // get its coordinates
        String xValue = data.getXValue().toString();
        String yValue = data.getYValue().toString();

        // get the chart index
        for(Subject s: project.getPcGraphSubjectsList().get(project.getCurrentTabIndex())){
            if(s.getPcs() != null && Arrays.asList(s.getPcs()).contains(xValue) && Arrays.asList(s.getPcs()).contains(yValue)){
                s.setHiddenXValue(Float.parseFloat(xValue));
                s.setHiddenYValue(Float.parseFloat(yValue));

                // if the arraylist of ids for current pc graph exists
                if(project.getCurrentTabIndex()>=0 && project.getCurrentTabIndex()<project.getHiddenPoints().size()){
                    project.getHiddenPoints().get(project.getCurrentTabIndex()).add(s.getFid()+" "+s.getIid());
                }else {
                    // add a new list to store hidden ids of a graph in the position of the current tab index
                    ArrayList<String> hiddenIndvList = new ArrayList<>();
                    hiddenIndvList.add(s.getFid()+" "+s.getIid());
                    project.getHiddenPoints().add(hiddenIndvList);
                }
                s.setHidden(true);
                break;
            }
        }
    }

    /**
     *
     * @param chart
     * @param ids
     */
    public void unhideIndividual(ScatterChart<Number, Number> chart, String ids[]){
        String fid = ids[0];
        String iid = ids[1];

        // retrieve subject properties
        Float hiddenXValue = null, hiddenYValue = null;
        String groupName = null, iconColor = null, iconsvg  = null;
        int iconSize = 0;

        for(Subject s: project.getPcGraphSubjectsList().get(project.getCurrentTabIndex())){
            if(s.getFid().equals(fid) && s.getIid().equals(iid) && project.getPhenoColumnNumber()==3){
                hiddenXValue = s.getHiddenXValue();
                hiddenYValue = s.getHiddenYValue();
                iconSize = s.getIconSize();
                groupName = s.getPhenotypeA();
                iconColor = s.getColor();
                iconsvg = s.getIcon();
                s.setHidden(false); // activate visibility of a point
                break;
            }
            
            if(s.getFid().equals(fid) && s.getIid().equals(iid) && project.getPhenoColumnNumber()==4){
                hiddenXValue = s.getHiddenXValue();
                hiddenYValue = s.getHiddenYValue();
                iconSize = s.getIconSize();
                groupName = s.getPhenotypeB();
                iconColor = s.getColor();
                iconsvg = s.getIcon();
                s.setHidden(false);
                break;
            }
        }

        for(XYChart.Series<Number, Number> s: chart.getData()){
            if(s.getName().equals(groupName)){
                XYChart.Data<Number, Number> data = new XYChart.Data(hiddenXValue, hiddenYValue);
                s.getData().add(data); // add point to graph

                // set the style of icon
                data.getNode().setStyle(getStyle(iconColor, iconsvg, iconSize));

                data.getNode().setOnMouseClicked(e ->{
                    try {
                        showIndividualDetails(data, chart);
                    } catch (Exception ex) {
                        ;
                    }
                });
                break;
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
            Parent parent = (Parent) fxmlLoader.load();
            Stage dialogStage = new Stage();
            dialogStage.setScene(new Scene(parent));
            dialogStage.setResizable(false);

            PCAIndividualDetailsController individualDetailsController = fxmlLoader.getController();
            individualDetailsController.setPCAGraph(this);
            individualDetailsController.setProject(project);

            // set values of clicked pca point
            String xValue = String.valueOf(data.getXValue());
            String yValue = String.valueOf(data.getYValue());
            individualDetailsController.setClickedPoint(xValue, yValue);

            // display details of the point
            String xAxisLabel = chart.getXAxis().getLabel();
            String yAxisLabel = chart.getYAxis().getLabel();
            individualDetailsController.setPcaLabel(xAxisLabel + ": " + xValue + "\n" + yAxisLabel + ": " + yValue);

            for(Subject s: subjects){
                if(s.getPcs()!=null && Arrays.asList(s.getPcs()).contains(xValue) && Arrays.asList(s.getPcs()).contains(yValue)){
                    ObservableList<String> phenoDetails = FXCollections.<String>observableArrayList(s.getFid(), s.getIid(), s.getPhenotypeA(), s.getPhenotypeB());
                    individualDetailsController.setPhenoListView(phenoDetails);
                    individualDetailsController.setPhenotypeGroup(phenoDetails.get(project.getPhenoColumnNumber()-1)); // MKK, LWK if column is 3 (index 2)
                    individualDetailsController.setIconColor(s.getColor());
                    individualDetailsController.setIconSVGShape(s.getIcon());
                    individualDetailsController.setIconSize(s.getIconSize());
                    break;
                }
            }

            // display icon for clicked point
            individualDetailsController.setIconDisplay(data.getNode().getStyle());

            // set values of groupName combo box
            ObservableList<String> uniqueGroups = FXCollections.observableArrayList();
            for (XYChart.Series<Number, Number> s: chart.getData()){
                uniqueGroups.add(s.getName());
            }
            individualDetailsController.setPhenotypeComboBox(FXCollections.observableArrayList(uniqueGroups));
            dialogStage.showAndWait();

        } catch (Exception ex) {
            ;
        }
    }

    /**
     * return a string of style
     * @param color
     * @param icon
     * @param iconSize
     * @return
     */
    public String getStyle(String color, String icon, int iconSize){
        String s = "-fx-background-color: "+color+", white;"
                + "-fx-shape: \""+icon+"\";"
                + "-fx-background-insets: 0, 2;"
                + "-fx-background-radius:"+iconSize+"px;"
                + "-fx-padding: "+iconSize+"px;"
                + "-fx-pref-width: "+iconSize+"px;"
                + "-fx-pref-height: "+iconSize+"px;";
        return s;
    }

    /**
     * get created graph - called by the setPCAGraph method in main controller
     * @return
     */
    public ScatterChart<Number, Number> getPcaChart() {
        return pcaChart;
    }

    @Override
    public ArrayList<StackedBarChart<String, Number>> createGraph() {
        return null;
    }
    @Override
    protected void setPopulationGroups() {}

}
