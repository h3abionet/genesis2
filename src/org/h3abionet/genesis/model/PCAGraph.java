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

    private String[] pcaColumnLabels; // store pca column name: PCA 1, PCA 2, ...
    private final List<String> eigenValues; // store eigen values
    private XYChart.Series<Number, Number> series;
    private BufferedReader bufferReader;
    private String line;
    private ScatterChart<Number, Number> pcaChart;
    private ArrayList<Subject> graphDetailsList;

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
                    for(Subject s: project.getSubjectArrayList()){
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
                    for(Subject s: project.getSubjectArrayList()){
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
                    for(Subject s: project.getSubjectArrayList()){
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
                    for(Subject s: project.getSubjectArrayList()){
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
    public ObservableList<String> getPCAcolumns() {
        ObservableList<String> pca_list = FXCollections.observableArrayList();
        pca_list.addAll(Arrays.asList(pcaColumnLabels));
        return pca_list;
    }

    /**
     *
     * @return List of eigen values
     */
    public List<String> getEigenValues() {
        return eigenValues;
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
        String xAxisPca = x; // PCA 1
        String yAxisPca = y; // PCA 2

        int xPcaNumber = Integer.parseInt(xAxisPca.substring(4, xAxisPca.length())); //1
        int yPcaNumber = Integer.parseInt(yAxisPca.substring(4, yAxisPca.length())); //2

        NumberAxis xAxis = new NumberAxis();
        xAxis.setSide(Side.BOTTOM);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setSide(Side.LEFT);

        ScatterChart<Number, Number> sc = new ScatterChart<>(xAxis, yAxis);

        // setup chart
        xAxis.setLabel(xAxisPca);
        yAxis.setLabel(yAxisPca);
        sc.setTitle(xAxisPca + " Vs " + yAxisPca + " Chart"); // set as default value

        // clone subject list
        graphDetailsList = (ArrayList)project.getSubjectArrayList().clone();
        // add clone to graph list
        project.getPcGraphList().add(graphDetailsList);

        // create series
        for (int i= 0; i<project.getGroupNames().size(); i++){
            series = new XYChart.Series<>();

            String serieName = project.getGroupNames().get(i);
            series.setName(serieName);

            for (Subject s: graphDetailsList){
                if(project.getPhenoColumnNumber() == 3){ // read 4th column in pheno file
                    if(s.getPhenotypeA().equals(serieName) && s.getPcs() != null){
                        series.getData().add(new XYChart.Data(Float.parseFloat(s.getPcs()[xPcaNumber-1]), Float.parseFloat(s.getPcs()[yPcaNumber-1])));
                    }
                }else {
                    if(s.getPhenotypeB().equals(serieName) && s.getPcs() != null){ // read 3rd column in pheno file
                        series.getData().add(new XYChart.Data(Float.parseFloat(s.getPcs()[xPcaNumber-1]), Float.parseFloat(s.getPcs()[yPcaNumber-1])));
                    }
                }
            }
            // do not add empty series (pheno groups with no data) to the chart
            if(series.getData().size()>0){
                sc.getData().add(series);
            }
        }

        // set colors and icons
        for(int i=0; i<sc.getData().size(); i++) {
            XYChart.Series<Number, Number> serie = sc.getData().get(i);
            Set<Node> nodes = sc.lookupAll(".series" + i);

            if(project.getPhenoColumnNumber() == 3){
                for(Subject s : graphDetailsList){
                    if(s.getPhenotypeA().equals(serie.getName())){
                        for (Node n : nodes) {
                            n.setStyle(getStyle(s.getColor(), s.getIcon(), 5));
                        }
                        break;
                    }
                }
            }

            // set mouse event and tooltip
            for(XYChart.Data<Number, Number> data : sc.getData().get(i).getData()){
                setTooltip(data); // set tool tip

                data.getNode().setOnMouseClicked(e -> {
                    try {
                        // set the event
                        setMouseEvent(data, sc);
                    } catch (Exception ex) {
                        ;
                    }
                });
            }

            // set the legend
            for (Node n : sc.getChildrenUnmodifiable()) {
                if (n.getClass().toString().equals("class com.sun.javafx.charts.Legend")) {
                    TilePane tn = (TilePane) n;
                    ObservableList<Node> children = tn.getChildren();

                    Label lab = (Label) children.get(i).lookup(".chart-legend-item");

                    // get color and shape of the group for this lab
                    String bgColor = (String) project.getGroupColors().get(lab.getText());
                    String shape = (String) project.getGroupIcons().get(lab.getText());

                    // set graphics
                    lab.getGraphic().setStyle(getStyle(bgColor, shape, 5));

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

        }

        // sort legend items
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
        pcaChart =  sc;
    }

    public ScatterChart<Number, Number> getPcaChart() {
        return pcaChart;
    }

    /*
     * This method is not used by pca
     */
    @Override
    public ArrayList<StackedBarChart<String, Number>> createGraph() {
        return null;
    }
    @Override
    protected void setPopulationGroups() {}

    public String getStyle(String color, String icon, int padding){
        String s = "-fx-background-color: "+color+", white;"
                + "-fx-shape: \""+icon+"\";"
                + "-fx-background-insets: 0, 2;"
                + "-fx-background-radius: 5px;"
                + "-fx-padding: "+padding+"px;";
        return s;
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
        for(Subject s: project.getPcGraphList().get(project.getCurrentTabIndex())){
            if(s.getPcs() != null && Arrays.asList(s.getPcs()).contains(xValue) && Arrays.asList(s.getPcs()).contains(yValue)){
                s.setHiddenXValue(Float.parseFloat(xValue));
                s.setHiddenYValue(Float.parseFloat(yValue));

                // if the arraylist of ids for current pc graph exists
                if(project.getCurrentTabIndex()>=0 && project.getCurrentTabIndex()<project.getHiddenPoints().size()){
                    project.getHiddenPoints().get(project.getCurrentTabIndex()).add(s.getFid()+" "+s.getIid());
                }else {
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

        Float x = null, y = null;
        String groupName = null;

        for(Subject s: project.getPcGraphList().get(project.getCurrentTabIndex())){
            if(s.getFid().equals(fid) && s.getIid().equals(iid) && project.getPhenoColumnNumber()==3){
                x = s.getHiddenXValue();
                y = s.getHiddenYValue();
                groupName = s.getPhenotypeA();
                s.setHidden(false); // activate visibility of a point
                break;
            }
            if(s.getFid().equals(fid) && s.getIid().equals(iid) && project.getPhenoColumnNumber()==4){
                x = s.getHiddenXValue();
                y = s.getHiddenYValue();
                groupName = s.getPhenotypeA();
                s.setHidden(false);
                break;
            }
        }

        for(XYChart.Series<Number, Number> s: chart.getData()){
            if(s.getName().equals(groupName)){

                XYChart.Data<Number, Number> data = new XYChart.Data(x, y);
                s.getData().add(data); // add point to graph

                // get group properties
                String color = (String) project.getGroupColors().get(groupName);
                String iconType = (String) project.getGroupIcons().get(groupName);

                // set the style of icon
                data.getNode().setStyle(getStyle(color, iconType, 5));

                data.getNode().setOnMouseClicked(e ->{
                    try {
                        setMouseEvent(data, chart);
                    } catch (Exception ex) {
                        ;
                    }
                });
                break;
            }
        }
    }

    /**
     *
     * @param data
     * @param chart
     * @throws IOException
     */
    public void setMouseEvent(XYChart.Data<Number, Number> data, ScatterChart<Number, Number> chart) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("view/IndividualDetails.fxml"));
            Parent parent = (Parent) fxmlLoader.load();
            Stage dialogStage = new Stage();
            dialogStage.setScene(new Scene(parent));
            dialogStage.setResizable(false);

            PCAIndividualDetailsController individualDetailsController = fxmlLoader.getController();
            individualDetailsController.setPCAGraph(this);

            // set values of clicked pca point
            String xValue = String.valueOf(data.getXValue());
            String yValue = String.valueOf(data.getYValue());
            individualDetailsController.setClickedPoint(xValue, yValue);

            // display details of the point
            String xAxisLabel = chart.getXAxis().getLabel();
            String yAxisLabel = chart.getYAxis().getLabel();
            individualDetailsController.setPcaLabel(xAxisLabel + ": " + xValue + "\n" + yAxisLabel + ": " + yValue);

            for(Subject s: graphDetailsList){
                if(s.getPcs()!=null && Arrays.asList(s.getPcs()).contains(xValue) && Arrays.asList(s.getPcs()).contains(yValue)){
                    ObservableList<String> phenos = FXCollections.<String>observableArrayList(s.getFid(), s.getIid(), s.getPhenotypeA(), s.getPhenotypeB());
                    individualDetailsController.setPhenoLabel(phenos);
                    break;
                }
            }

            // display icon for clicked point
            individualDetailsController.setIconDisplay(data.getNode().getStyle());

            // set values of groupName combo box
            individualDetailsController.setGroupName(FXCollections.observableArrayList(project.getGroupNames()));
            dialogStage.showAndWait();

        } catch (Exception ex) {
            ;
        }
    }

    /**
     *
     * @param data
     */
    private void setTooltip(XYChart.Data<Number, Number> data){
        // manage tooltip delay
        Tooltip.install(data.getNode(), new Tooltip(data.getXValue() + "\n" + data.getYValue()));
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
}
