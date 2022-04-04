package org.h3abionet.genesis.controller;

import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import jfxtras.labs.util.event.MouseControlUtil;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;

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
 * This is the main controller
 *
 * @author scott
 */
public class MainController implements Initializable{
    // interface (view) variables
    @FXML
    private TabPane tabPane;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Button admixtureBtn;
    @FXML
    private Button pcaBtn;
    @FXML
    private Button newProjBtn;
    @FXML
    private Button settingsBtn;
    @FXML
    private Button downloadBtn;
    @FXML
    private Button individualBtn;
    @FXML
    private Button searchBtn;
    @FXML
    private Button drawingBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button saveProjBtn;
    @FXML
    private Button importProjBtn;
    @FXML
    private Button helpBtn;

    // drawing tools
    @FXML
    private AnchorPane drawingAnchorPane; // can set visibility
    @FXML
    private Button lineTool;
    @FXML
    private Button circleTool;
    @FXML
    private Button arrowTool;
    @FXML
    private Button textTool;
    @FXML
    private Button rectangleTool;

    // drawing tool variables
    private boolean drawingAnchorPaneVisibility;
    Shape pivot;
    private ButtonType cancelButton;
    private ButtonType deleteBtn;
    private ButtonType doneBtn;

    // pca variables
    private static Tab pcaChartTab;
    private static int tabCount = -1;
    private static int currentTabIndex; // changed by clicking on tabs
    private ScatterChart<Number, Number> pcaChart;

    // admixture variables
    /**
     * this stores incoming list of admixture charts to be rendered in a row
     * e.g. [chart1, chart2, chart3, chart4, ... ] every chart represents a
     * population group e.g. MKK, ASW, etc.
     */
    private ArrayList<StackedBarChart<String, Number>> listOfAdmixtureCharts; // current admixture charts (1 graph)
    private List<ArrayList<StackedBarChart<String, Number>>> allAdmixtureCharts = new ArrayList<>(); // all graphs
    private Tab admixtureTab;
    private GridPane gridPane; // gridpane for keeping list of admixture charts
    private static int rowPointer = 0; // points to a new row for every new admix charts or K value
    private static VBox admixVbox; // has only 2 nodes : chart title & gridPane
    private static ScrollPane scrollPane; // its content = admixVbox
    private static AnchorPane admixPane;
    private double VBOX_MARGIN = 50;
    private static double defaultAdmixPlotWidth = 1200; // default width
    private static Text chartHeading; // default heading;
    private HiddenIndividualsController hiddenIndividualsController;
    private PCADataInputController pcaDataInputController;
    private ProjectDetailsController projectDetailsController;
    private ImportProjectController importProjectController;
    private AdmixtureDataInputController admixDataInputCtlr;
    private boolean isAdmixCreationSuccessful;
    private PCAGraph pcaGraph;
    private AdmixtureGraph admixtureGraph;
    private Project project;
    private ArrayList<ScatterChart> pcaChartsList = new ArrayList<>();
//    private ArrayList<ScatterChart> pcaChartsList = new ArrayList<>();
    private PCAGraphEventsHandler pc;

    double orgSceneX, orgSceneY;
    private boolean projectIsImported;

    @FXML
    private void newProject(ActionEvent event) throws IOException {
        // remove background image
        setTabPaneStyle();
        try {
            // load data input scene
            FXMLLoader projLoader = new FXMLLoader(Genesis.class.getResource("view/ProjDialogEntry.fxml"));
            Parent projParent = projLoader.load();
            projectDetailsController = projLoader.getController();
            projectDetailsController.setMainController(this);
            Stage dialogStage = new Stage();
            dialogStage.setScene(new Scene(projParent));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

            if (project.isFamCreated() & project.isPhenoCreated()) {
                // if both fam and phenotype file are correct, launch project
                disableImportProjBtn(true);
                disableNewProjBtn(true);
                disablePcaBtn(false);
                disableAdmixtureBtn(false);
                disableControlBtns(false);
            } else if (project.isPhenoFileProvided() && project.isFamCreated() == false) {
                // if only pheno file is provided, dont allow admixture
                disableImportProjBtn(true);
                disableNewProjBtn(true);
                disablePcaBtn(false);
                disableAdmixtureBtn(true);
                disableControlBtns(false);
            }
            else {
//                 otherwise don't launch the project
                disableImportProjBtn(false);
                disableNewProjBtn(false);
                disablePcaBtn(true);
                disableAdmixtureBtn(true);
                disableControlBtns(true);
                disableSettingsBtn(true);
            }
        }catch (Exception ex){
            ;
        }
    }

    public void setAdmixCreationSuccessful(boolean isAdmixCreationSuccessful) {
        this.isAdmixCreationSuccessful = isAdmixCreationSuccessful;
    }

    public void setPcaGraph(PCAGraph pcaGraph) {
        this.pcaGraph = pcaGraph;
    }

    public void setAdmixtureGraph(AdmixtureGraph admixtureGraph) {
        this.admixtureGraph = admixtureGraph;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * remove the genesis logo from the tabPane
     */
    public void setTabPaneStyle(){
        tabPane.setStyle("-fx-background-image: null;-fx-background-color: white;");
    }

    @FXML
    @SuppressWarnings("empty-statement")
    private void newPCA(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("view/PCADataInput.fxml"));
        Parent parent = (Parent) fxmlLoader.load();
        pcaDataInputController = fxmlLoader.getController();
        pcaDataInputController.setMainController(this);
        pcaDataInputController.setProject(project);
        pcaDataInputController.enableOK();
        if(pcaGraph!=null){
            pcaDataInputController.setPcaGraph(pcaGraph);
            pcaDataInputController.setButtons();
        }
        Stage dialogStage = new Stage();
        dialogStage.setScene(new Scene(parent));
        dialogStage.setResizable(false);

        dialogStage.showAndWait();

        try {
            if(pcaDataInputController.isFirstPcaSuccessful()){
                setPCAChart(pcaGraph.getPcaChart());
                // disable the pca button after first import
                disablePcaBtn(false);
                disableImportProjBtn(true);
                disableNewProjBtn(true);
                // enable other buttons
                disableControlBtns(false);

            }else{
                ;
            }
        } catch (NullPointerException e) {
            Genesis.throwErrorException("Oops, there was an error!");
        }
    }

    @FXML
    @SuppressWarnings("empty-statement")
    private void newAdmixture(ActionEvent event) throws IOException {

        if (AdmixtureSettingsController.isAdmixVertical()) {
            Genesis.throwInformationException("First change the graph to a horizontal linear layout");
        } else {
            FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("view/AdmixtureDataInput.fxml"));
            Parent parent = fxmlLoader.load();
            admixDataInputCtlr = fxmlLoader.getController();
            admixDataInputCtlr.setMainController(this);
            admixDataInputCtlr.setProject(project); // the proj
            Stage dialogStage = new Stage();
            dialogStage.setScene(new Scene(parent));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

            if (isAdmixCreationSuccessful) { // was data imported correctly

                if (AdmixtureSettingsController.isAdmixRotated()) {
                    setAdmixtureChart(admixtureGraph.getListOfStackedBarCharts());
                    admixVbox.setMaxHeight(Double.MAX_VALUE); // restore vGrow property
                } else {
                    setAdmixtureChart(admixtureGraph.getListOfStackedBarCharts());
                }

                // disable new project, import project and data buttons
                disableImportProjBtn(true);
                disableNewProjBtn(true);

                // enable other buttons
                disableControlBtns(false);

            } else {
                ; // if import was wrong, do nothing
            }
        }
    }

    /**
     * Uses PCAGraphEventsHandler model class
     *
     * @param pcaChart
     */
    public void setPCAChart(ScatterChart<Number, Number> pcaChart) {
        this.pcaChart = pcaChart;
        try {
            // pc acquires the pcaChart for additional features
            pc = new PCAGraphEventsHandler(pcaChart);

            // get axis labels
            String xAxisLabel = pcaChart.getXAxis().getLabel();
            String yAxisLabel = pcaChart.getYAxis().getLabel();
            String x = xAxisLabel.substring(4);
            String y = yAxisLabel.substring(4);

            // create new tab for the pca chart
            ++tabCount;
            pcaChartTab = new Tab();

            // tab name e.g PCA 1 & 2 ans space with close icon
            pcaChartTab.setText("PCA " + x + " & " + y+"    ");
            pcaChartTab.setClosable(true);
            pcaChartTab.setId("tab "+ tabCount);

            // add the pca chart container to the tab
            pcaChartTab.setContent(pc.addGraph());

            tabPane.getTabs().add(pcaChartTab);

            tabPaneClickEvent(); // set pcaChart index to selected tab number

            pcaChartsList.add(pcaChart); // add new chart to a list

            closeTab(pcaChartTab); // on closing the tab

        } catch (Exception e) {
            ;
        }
    }

    /**
     *
     */
    public void tabPaneClickEvent(){
        tabPane.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> ov, Number oldValue, Number newValue) -> {
            currentTabIndex = (int) newValue;
            project.setCurrentTabIndex(currentTabIndex);
        });
    }

    /**
     * close very tab
     * @param tab
     */
    private void closeTab(Tab tab) {
        tab.setOnCloseRequest((Event t) -> {
            String action =  Genesis.confirmAction("Are you sure?");
            // if yes button is clicked
            if(action.equals("yesBtnPressed")){
                // if only one tab is displayed, close the program if the it is not a help tab
                if(tabPane.getTabs().size()==1){
                    if(tab.getId().contains("help")){ // remove help tab
                        helpBtn.setDisable(false);
                        tab.getTabPane().getTabs().remove(tab);
                    }else {
                        // ask the user to save the project or close the program
                        saveProject(new ActionEvent());
                        Platform.exit(); // close program
                    }
                }else{ // if more than 2 tabs are displayed, remove the selected tab

                    // remove admixture tab
                    if(tab.getId().contains("admix")){ // admixture tab
                        tab.getTabPane().getTabs().remove(admixtureTab);
                        gridPane.getChildren().clear();
                        allAdmixtureCharts.clear();
                        project.getImportedKs().clear();
                    }

                    // remove pca tab
                    if(tab.getId().contains("tab")){ // pca tab
                        tab.getTabPane().getTabs().remove(tab); // remove the tab
                        pcaChartsList.remove(currentTabIndex);
                        project.getSubjectsList().remove(currentTabIndex);
                    }

                    // remove help tab
                    if(tab.getId().contains("help")){ // remove help tab
                        helpBtn.setDisable(false);
                        tab.getTabPane().getTabs().remove(tab);
                    }
                }
            }else { // No Btn is clicked
                t.consume(); // do nothing
            }
        });
    }

    /**
     * This method sets the admixture pcaChart.
     */
    public void setAdmixtureChart(ArrayList<StackedBarChart<String, Number>> admixCharts) {
        listOfAdmixtureCharts = admixCharts; // get multiple charts
        allAdmixtureCharts.add(listOfAdmixtureCharts);
        int sumOfIndividuals = project.getNumOfIndividuals();

        // check if the first rowPointer has nodes. If not, define column constraints.
        if (gridPane.contains(1, 0)) {
            ;
        } else {
            //  K value column
            ColumnConstraints kValueColumn = new ColumnConstraints();
            kValueColumn.setHgrow(Priority.NEVER);
            gridPane.getColumnConstraints().add(kValueColumn);

            // define column constraints based on number of indivs per admix chart
            for (StackedBarChart<String, Number> s : listOfAdmixtureCharts) {
                ColumnConstraints cc = new ColumnConstraints();
                int numOfIndividuals = s.getData().get(0).getData().size();
                double columnSize = (double) numOfIndividuals / (double) sumOfIndividuals * 100.0;
                cc.setPercentWidth(columnSize);
                cc.setHgrow(Priority.NEVER);
                gridPane.getColumnConstraints().add(cc);
            }
        }

        try {
            // if first chart, add gridpane to index 1 of vbox else reset index 1 with new gridpane
            if (rowPointer == 0) {
                admixPane.getChildren().add(admixtureGraph.getGridPane(gridPane, rowPointer));
                AnchorPane name = (AnchorPane) admixVbox.getChildren().get(1);
                name.getChildren().add(admixPane);
                scrollPane.setContent(admixVbox);
            } else {
                admixPane.getChildren().set(0, admixtureGraph.getGridPane(gridPane, rowPointer));
                AnchorPane name = (AnchorPane) admixVbox.getChildren().get(1);
                name.getChildren().set(0, admixPane);
                name.setStyle("-fx-background-color: transparent;");
                scrollPane.setContent(admixVbox);
            }

            admixtureTab.setContent(scrollPane);
            admixtureTab.getContent().autosize();

            //  if it is first k, add new tab to the tabpane
            if(project.getImportedKs().size()==1 || project.isProjIsImported()){
                tabPane.getTabs().add(admixtureTab);
            }else{ // else first remove the existing admixture tab
                ObservableList<Tab> tabs = tabPane.getTabs();
                for(int t=0; t<tabs.size(); t++){
                    if(tabs.get(t).getId().equals("admix")){
                        tabs.set(t,admixtureTab);
                        break;
                    }
                }
            }

            closeTab(admixtureTab); //  on closing the admixture tab
            rowPointer++; // create another rowPointer index

        } catch (Exception e) {
            ; // do nothing
        }

        tabPaneClickEvent();
    }

    @FXML
    private void settingsSelector(ActionEvent event) throws IOException {

        // get selected tab
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();

        try{
            if (selectedTab.getId().contains("tab")) {
                // show pca settings
                FXMLLoader loader = new FXMLLoader(Genesis.class.getResource("view/PCASettings.fxml"));
                Parent root = loader.load();
                PCASettingsController pcSettingsCtlr = loader.getController();
                pcSettingsCtlr.setScatterChart(pcaChartsList.get(currentTabIndex));
                pcSettingsCtlr.setPCAGraphLayout(project.getPCAGraphLayouts().get(currentTabIndex));
                pcSettingsCtlr.setControls();
                pcSettingsCtlr.setMainController(this);
                disableSettingsBtn(true);
                Stage dialogStage = new Stage();
                dialogStage.setScene(new Scene(root));
                dialogStage.setResizable(false);
                dialogStage.showAndWait();
            }
            else if(selectedTab.getId().contains("admix")){
                // show admixture settings
                FXMLLoader loader = new FXMLLoader(Genesis.class.getResource("view/AdmixtureSettings.fxml"));
                Parent root = loader.load();
                AdmixtureSettingsController admixSettingsCtlr =  loader.getController();
                admixSettingsCtlr.setAdmixtureGraph(admixtureGraph);
                admixSettingsCtlr.setMainController(this);
                admixSettingsCtlr.setProject(project);
                admixSettingsCtlr.setControls();
                disableSettingsBtn(true);
                Stage dialogStage = new Stage();
                dialogStage.setScene(new Scene(root));
                dialogStage.setResizable(false);
                dialogStage.showAndWait();
            }
        }catch(Exception e){
            //TODO disable setting button if no chart
            Genesis.throwInformationException("No chart to format");
        }
    }

    /**
     * Saves the pcaChart in the right format
     */
    @FXML
    @SuppressWarnings("empty-statement")
    public void saveChart() throws Exception {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();

        try {
            // load pca setting
            if (selectedTab.getId().contains("tab")) {
                PCAGraphEventsHandler pc = new PCAGraphEventsHandler(pcaChartsList.get(currentTabIndex));
                pc.saveChart();
            }

            // load admixture setting
            if (selectedTab.getId().contains("admix")){
                AdmixtureGraphEventsHandler admixGraphEventHandler = new AdmixtureGraphEventsHandler();
                admixGraphEventHandler.saveChart(admixVbox);
            }

        } catch (Exception e) {
            Genesis.throwInformationException("No chart to save");
        }
    }

    @FXML
    public void showHiddenIndividuals() throws IOException {
        FXMLLoader loader = new FXMLLoader(Genesis.class.getResource("view/HiddenIndividuals.fxml"));
        Parent parent = (Parent) loader.load();
        hiddenIndividualsController = loader.getController();
        hiddenIndividualsController.setMainController(this);
        hiddenIndividualsController.setPcaGraph(pcaGraph);
        hiddenIndividualsController.setAdmixtureGraph(admixtureGraph);

        // get all hidden points for the current graph
        hiddenIndividualsController.setHiddenIndividualCombo(project.getHiddenPoints());
        hiddenIndividualsController.setHiddenGroupComboCombo(project.getHiddenGroups());

        // is current graph admixture
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        if (selectedTab.getId().contains("admix")){
            hiddenIndividualsController.setCurrentGraphType("admixture");
        }
        // is current graph pca?
        if (selectedTab.getId().contains("tab")) {
            hiddenIndividualsController.setCurrentGraphType("pca");
        }

        // show stage
        Stage dialogStage = new Stage();
        dialogStage.setScene(new Scene(parent));
        dialogStage.setResizable(false);

        dialogStage.showAndWait();
    }

    @FXML
    public void searchIndividual(){
        boolean idFound = false;
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search for individual here");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter FID or IID here:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            String id = result.get();
            for(Subject s: project.getSubjectsList()){
                if(s.getFid().equals(id) || s.getIid().equals(id)){
                    idFound = true; // id is found
                    ScatterChart<Number, Number> graph = pcaChartsList.get(currentTabIndex);
                        for(XYChart.Series<Number, Number> series : graph.getData()){
                            for (XYChart.Data<Number, Number> data : series.getData()){
                                String xPoint = data.getXValue().toString();
                                String yPoint = data.getYValue().toString();
                                if(s.getPcs() != null && Arrays.asList(s.getPcs()).contains(xPoint) && Arrays.asList(s.getPcs()).contains(yPoint)){
                                    ScaleTransition scaleTransition = new ScaleTransition();
                                    scaleTransition.setDuration(Duration.millis(1000));
                                    scaleTransition.setNode(data.getNode());
                                    scaleTransition.setByY(1.5);// Y direction movement
                                    scaleTransition.setByX(1.5);// X direction movement
                                    scaleTransition.setCycleCount(4);// Set cycle count rotation 4
                                    scaleTransition.setAutoReverse(true);// auto reverse activation
                                    scaleTransition.play();// applying rotate transition on circle
                                    break;
                                }
                            }
                        }
                }
            }
        }

        if(idFound==false){
            Genesis.throwInformationException("Individual Not Found");
        }
    }

    /**
     * return PCA based on their index in the list
     *
     * @return PCA pcaChart
     */
    public ScatterChart<Number, Number> getPcaChart() {
        return pcaChartsList.get(currentTabIndex);
    }

    /**
     * get list of all displayed pc graphs
     * @return
     */
    public ArrayList<ScatterChart> getPcaChartsList() {
        return pcaChartsList;
    }

    /*
     * @return list of admix charts
     */
    public ArrayList<StackedBarChart<String, Number>> getListOfAdmixtureCharts() {
        return listOfAdmixtureCharts;
    }

    /**
     * provide access to the grid pane being displayed
     *
     * @return
     */
    public GridPane getGridPane() {
        return gridPane;
    }

    @FXML
    private void drawingTool(ActionEvent event) {
            pivot = new Circle(0, 0, 8);
            pivot.setTranslateX(50);
            pivot.setTranslateY(50);
            drawingAnchorPaneVisibility = !drawingAnchorPaneVisibility;
            drawingAnchorPane.setVisible(drawingAnchorPaneVisibility);
    }

    @FXML
    private void addLine(ActionEvent event) {
        try {
            // create a line
            Line line = new Line(0, 150, 200, 150);
            line.setStrokeWidth(2);
            line.setStroke(Color.BLACK);

            // add line properties
            Annotation lineAnnotation = new Annotation();
            lineAnnotation.setName("line");
            lineAnnotation.setStrokeWidth(2);
            lineAnnotation.setStrokeColor(Color.BLACK);
            lineAnnotation.setStartX(line.getStartX());
            lineAnnotation.setStartY(line.getStartY());
            lineAnnotation.setEndX(line.getEndX());
            lineAnnotation.setEndY(line.getEndY());

            updateAnnotationsLists(lineAnnotation); // store the properties
            addShapeToChart(line, currentTabIndex); // add to chart
            addLineEvents(line, lineAnnotation); // add click and mouse events
        }catch(Exception e){
            Genesis.throwInformationException("First add the Chart");
        }
    }

    /**
     * recreate the line when importing the project
     * @param l
     * @param chartIndex
     */
    public void recreateLine(Annotation l, int chartIndex, String chartType){
        Line line = new Line(l.getStartX(), l.getStartY(), l.getEndX(), l.getEndY());
        line.setStrokeWidth(l.getStrokeWidth());

        line.setLayoutX(l.getLayoutX());
        line.setLayoutY(l.getLayoutY());

        Rotate rotate = new Rotate(l.getRotation(),l.getEndX(), line.getEndY());
        line.getTransforms().add(rotate);

        if(l.getStrokeColor().equals("000000") || l.getStrokeColor().equals("ff")){
            line.setStroke(Color.BLACK);
        }else{
            line.setStroke(Color.web(l.getStrokeColor()));
        }

        // add the line to the graph
        addImportedShape(line, chartIndex, chartType);
        // add click events
        addLineEvents(line, l);
    }

    private void addLineEvents(Line line, Annotation lineAnnotation){
        line.setOnMousePressed((t) -> {
            orgSceneX = t.getSceneX();
            orgSceneY = t.getSceneY();

            Line l = (Line) (t.getSource());
            l.toFront();
        });

        line.setOnMouseDragged((t) -> {
            double offsetX = t.getSceneX() - orgSceneX;
            double offsetY = t.getSceneY() - orgSceneY;

            Line l = (Line) (t.getSource());

            line.setTranslateX(l.getTranslateX() + offsetX);
            line.setTranslateY(l.getTranslateY() + offsetY);

            orgSceneX = t.getSceneX();
            orgSceneY = t.getSceneY();
        });

        line.setOnMouseClicked((MouseEvent e) -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                // use class circle options -- accepts chosen circle as a parameter
                LineOptions lineOptions = new LineOptions(line, lineAnnotation);
                lineOptions.setProject(project);
                lineOptions.setSelectedTab(tabPane.getSelectionModel().getSelectedItem());
                lineOptions.setMainController(this);
                // modify the chosen circle
                lineOptions.modifyArrow();
            }
        });

        line.setOnMouseReleased(mouseEvent -> {
            // set annotations
            lineAnnotation.setStartX(line.getStartX());
            lineAnnotation.setStartY(line.getStartY());
            lineAnnotation.setEndX(line.getEndX());
            lineAnnotation.setEndY(line.getEndY());
            lineAnnotation.setLayoutX(line.getBoundsInParent().getMinX()+20);
            lineAnnotation.setLayoutY(line.getBoundsInParent().getMinY()-129);
        });
    }

    @FXML
    private void addArrow(ActionEvent event) {
        try{
            Arrow arrow = new Arrow();
            arrow.setStartX(200);
            arrow.setStartY(200);
            arrow.setEndX(400);
            arrow.setEndY(200);

            Annotation arrowAnnotation = new Annotation();
            arrowAnnotation.setName("arrow");
            arrowAnnotation.setStartX(200);
            arrowAnnotation.setStartY(200);
            arrowAnnotation.setEndX(400);
            arrowAnnotation.setEndY(200);

            MouseControlUtil.makeDraggable(arrow);

            updateAnnotationsLists(arrowAnnotation); // store the properties
            addArrowToChart(arrow, currentTabIndex); // add to chart
            addArrowEvents(arrow, arrowAnnotation);
        }catch(Exception e){
            Genesis.throwInformationException("First add the Chart");
        }
    }

    public void recreateArrow(Annotation a, int chartIndex, String chartType){
        Arrow arrow = new Arrow();
        arrow.setStartX(a.getStartX());
        arrow.setStartY(a.getStartY());
        arrow.setEndX(a.getEndX());
        arrow.setEndY(a.getEndY());
        arrow.setLayoutX(a.getLayoutX());
        arrow.setLayoutY(a.getLayoutY());

        MouseControlUtil.makeDraggable(arrow);

//        if(a.getStrokeColor().equals("000000") || a.getStrokeColor().equals("ff")){
//            arrow.setStroke(Color.web("000000"));
//        }else{
//            arrow.setStroke(Color.web(a.getStrokeColor()));
//        }
        addImportedArrow(arrow, chartIndex, chartType); // add to chart
        addArrowEvents(arrow, a);
    }

    private void addArrowEvents(Arrow arrow, Annotation arrowAnnotation){
        arrow.setOnMouseEntered(e -> {
            arrow.getScene().setCursor(Cursor.HAND);
            arrow.setEffect(new DropShadow(20, Color.BLUE));
        });

        arrow.setOnMouseExited(e -> {
            arrow.getScene().setCursor(Cursor.DEFAULT);
            arrow.setEffect(null);
        });

        arrow.setOnMousePressed((t) -> {
            orgSceneX = t.getSceneX();
            orgSceneY = t.getSceneY();

            Arrow l = (Arrow) (t.getSource());
            l.toFront();
        });

        arrow.setOnMouseDragged((t) -> {
            double offsetX = t.getSceneX() - orgSceneX;
            double offsetY = t.getSceneY() - orgSceneY;
            Arrow l = (Arrow) (t.getSource());
            arrow.setTranslateX(l.getTranslateX() + offsetX);
            arrow.setTranslateY(l.getTranslateY() + offsetY);
            orgSceneX = t.getSceneX();
            orgSceneY = t.getSceneY();
        });

        arrow.setOnMouseClicked((MouseEvent e) -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                // use class circle options -- accepts chosen circle as a parameter
                ArrowOptions arrowOptions = new ArrowOptions(arrow, arrowAnnotation);
                arrowOptions.setProject(project);
                arrowOptions.selectedTab(tabPane.getSelectionModel().getSelectedItem());
                arrowOptions.setMainController(this);
                // modify the chosen circle
                arrowOptions.modifyArrow();
            }
        });

        arrow.setOnMouseReleased(mouseEvent -> {
            // set annotations
            arrowAnnotation.setLayoutX(arrow.getBoundsInParent().getCenterX());
            arrowAnnotation.setLayoutY(arrow.getBoundsInParent().getCenterY());
        });
    }

    @FXML
    private void addCircle(ActionEvent event) {
        try{
            Circle circle = new Circle();
            circle.setCenterX(200);
            circle.setCenterY(200);
            circle.setRadius(100);
            circle.setFill(Color.TRANSPARENT);
            circle.setStroke(Color.BLACK);
            circle.setStrokeWidth(1);
//            System.out.println("layout "+circle.getLayoutX()+" "+circle.getLayoutY());

            Annotation circleAnnotation = new Annotation();
            circleAnnotation.setName("circle");
            circleAnnotation.setCenterX(200);
            circleAnnotation.setCenterY(200);
            circleAnnotation.setRadius(100);
            circleAnnotation.setStrokeWidth(1);
            circleAnnotation.setStrokeColor(Color.BLACK); // black

            updateAnnotationsLists(circleAnnotation);// add the circle to the list of annotations
            MouseControlUtil.makeDraggable(circle); // add drag event
            addShapeToChart(circle, currentTabIndex);
            addCircleEvents(circle, circleAnnotation);
        }catch(Exception e){
            Genesis.throwInformationException("First add the Chart");
        }
    }

    private void addCircleEvents(Circle circle, Annotation circleAnn){

        circle.setOnMouseClicked((MouseEvent e) -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                circleAnn.setCenterX(e.getSceneX());
                circleAnn.setCenterY(e.getSceneY());
                // use class circle options -- accepts chosen circle as a parameter
                CircleOptions circleOptions = new CircleOptions(circle, circleAnn);
                circleOptions.setProject(project);
                circleOptions.setSelectedTab(tabPane.getSelectionModel().getSelectedItem());
                circleOptions.setMainController(this);
                // modify the chosen circle
                circleOptions.modifyCircle();
            }
        });

        circle.setOnMouseReleased(mouseEvent -> {
            circleAnn.setLayoutX(circle.getBoundsInParent().getCenterX());
            circleAnn.setLayoutY(circle.getBoundsInParent().getCenterY());
        });
    }

    public void recreateCircle(Annotation circleAnn, int chartIndex, String chartType){
        Circle circle = new Circle();
//        circle.setCenterX(circleAnn.getCenterX());
//        circle.setCenterY(circleAnn.getCenterY());
        circle.setRadius(circleAnn.getRadius());
        circle.setFill(Color.TRANSPARENT);
        circle.setStrokeWidth(circleAnn.getStrokeWidth());
        circle.setLayoutX(circleAnn.getLayoutX());
        circle.setLayoutY(circleAnn.getLayoutY());

        if(circleAnn.getStrokeColor().equals("000000") || circleAnn.getStrokeColor().equals("ff")){
            circle.setStroke(Color.web("000000"));
        }else{
            circle.setStroke(Color.web(circleAnn.getStrokeColor()));
        }

        MouseControlUtil.makeDraggable(circle);
        addImportedShape(circle, chartIndex,chartType);
        addCircleEvents(circle, circleAnn);

//        circle.relocate(circleAnn.getCenterX(), circleAnn.getCenterY());
    }

    @FXML
    private void addText(ActionEvent event) {
        try{
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Text");
            dialog.setHeaderText(null);
            dialog.setGraphic(null);
            dialog.setContentText("Text:");
            Optional<String> result = dialog.showAndWait();

            // set text default color and position on the pcaChart
            Text text = new Text(300, 50, result.get());
            text.setFill(Color.BLACK);
            text.setFont(Font.font("helvetica", FontWeight.NORMAL, FontPosture.REGULAR, 14));

            Annotation textAnnotation = new Annotation();
            textAnnotation.setName("text");
            textAnnotation.setStartX(300);
            textAnnotation.setStartY(50);
            textAnnotation.setText(result.get());
            textAnnotation.setFill(Color.BLACK);
            textAnnotation.setFontFamily("helvetica");
            textAnnotation.setFontSize(14);
            textAnnotation.setFontWeight("NORMAL");


            // can drag text
            MouseControlUtil.makeDraggable(text);
            updateAnnotationsLists(textAnnotation);
            addShapeToChart(text, currentTabIndex);
            addTextEvents(text, textAnnotation);
        }catch(Exception e){
            Genesis.throwInformationException("First add the Chart");
        }
    }

    public void recreateText(Annotation textAnn, int chartIndex, String chartType){
        Text text = new Text();
        text.setText(textAnn.getText());
//        text.setX(textAnn.getStartX());
//        text.setY(textAnn.getStartY());
        String family = textAnn.getFontFamily();
        int size = textAnn.getFontSize();
        String weight = textAnn.getFontWeight();
        text.setX(textAnn.getLayoutX());
        text.setY(textAnn.getLayoutY());

        if(weight.equals("EXTRA_BOLD")){
            text.setFont(Font.font(family, FontWeight.EXTRA_BOLD, FontPosture.REGULAR, size));
        }else {
            text.setFont(Font.font(family, FontWeight.NORMAL, FontPosture.REGULAR, size));
        }

        if(textAnn.getFill().equals("000000") || textAnn.getFill().equals("ff")){
            text.setFill(Color.web("000000"));
        }else{
            text.setFill(Color.web(textAnn.getFill()));
        }

        MouseControlUtil.makeDraggable(text);
        addImportedShape(text, chartIndex, chartType);
        addTextEvents(text, textAnn);
    }

    private void addTextEvents(Text text, Annotation textAnnotation){
        // add mouse event to text for editing options
        text.setOnMouseClicked((MouseEvent e) -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                // use class label options -- accepts chosen label as a parameter
                LabelOptions labelOptions = new LabelOptions(text, textAnnotation);
                labelOptions.setProject(project);
                labelOptions.setSelectedTab(tabPane.getSelectionModel().getSelectedItem());
                labelOptions.setMainController(this);
                // modify the chosen label
                labelOptions.modifyLabel();
            }
        });

        text.setOnMouseReleased(mouseEvent -> {
            // set annotations
            textAnnotation.setLayoutX(text.getBoundsInParent().getCenterX()-20);//error margin
            textAnnotation.setLayoutY(text.getBoundsInParent().getCenterY());
        });

    }


    @FXML
    private void addRectangle(ActionEvent event) {
        try{
            Rectangle rec = new Rectangle(100, 100, 200, 100);
            rec.setFill(Color.TRANSPARENT);
            rec.setStroke(Color.BLACK);
            rec.setStrokeWidth(2);

            Annotation rectangleAnnotation = new Annotation();
            rectangleAnnotation.setName("rectangle");
            rectangleAnnotation.setStartX(100);
            rectangleAnnotation.setStartY(100);
            rectangleAnnotation.setWidth(200);
            rectangleAnnotation.setHeight(100);
            rectangleAnnotation.setStrokeColor(Color.BLACK);
            rectangleAnnotation.setStrokeWidth(2);
            rectangleAnnotation.setArcHeight(0);
            rectangleAnnotation.setArcWidth(0);

            MouseControlUtil.makeDraggable(rec);
            updateAnnotationsLists(rectangleAnnotation);// add the circle to the list of annotations
            addShapeToChart(rec, currentTabIndex);
            addRectangleEvents(rec, rectangleAnnotation);
        }catch(Exception e){
            Genesis.throwInformationException("First add the Chart");
        }
    }

    public void recreateRectangle(Annotation recAn, int chartIndex, String chartType){
        Rectangle rec = new Rectangle();
//        rec.setX(recAn.getStartX());
//        rec.setY(recAn.getStartY());
        rec.setWidth(recAn.getWidth());
        rec.setHeight(recAn.getHeight());
        rec.setStrokeWidth(recAn.getStrokeWidth());

        rec.setArcHeight(recAn.getArcHeight());
        rec.setArcWidth(recAn.getArcWidth());
        rec.setFill(Color.TRANSPARENT);

//        rec.setX(recAn.getLayoutX());
//        rec.setY(recAn.getLayoutY());
        rec.relocate(recAn.getLayoutX(),recAn.getLayoutY());

        if(recAn.getStrokeColor()!=null){
            if(recAn.getStrokeColor().equals("000000") || recAn.getStrokeColor().equals("ff")){
                rec.setStroke(Color.web("000000"));
            }else{
                rec.setStroke(Color.web(recAn.getStrokeColor()));
            }
        }else{
            ;
        }
        MouseControlUtil.makeDraggable(rec);
        addImportedShape(rec, chartIndex, chartType);
        addRectangleEvents(rec, recAn);
    }

    private void addRectangleEvents(Rectangle rec, Annotation rectangleAnnotation){
        rec.setOnMouseClicked((MouseEvent e) -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                // use class rectangle options -- accepts chosen rectangle as a parameter
                RectangleOptions rectangleOptions = new RectangleOptions(rec, rectangleAnnotation);
                rectangleOptions.setProject(project);
                rectangleOptions.setSelectedTab(tabPane.getSelectionModel().getSelectedItem());
                rectangleOptions.setMainController(this);
                // modify the chosen rectangle
                rectangleOptions.modifyRectangle();
            }
        });

        rec.setOnMouseReleased(mouseEvent -> {
            // set annotations
            rectangleAnnotation.setWidth(rec.getWidth());
            rectangleAnnotation.setHeight(rec.getHeight());
            rectangleAnnotation.setArcHeight(rec.getArcHeight());
            rectangleAnnotation.setArcWidth(rec.getArcWidth());
            rectangleAnnotation.setStrokeWidth(rec.getStrokeWidth());
            rectangleAnnotation.setLayoutX(rec.getBoundsInParent().getMinX()+20); // 20 error margin
            rectangleAnnotation.setLayoutY(rec.getBoundsInParent().getMinY()+20);
        });
    }

    public void addShapeToChart(Shape shape, int chartIndex){
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        // if admix pane
        if(selectedTab.getId().contains("admix")){
            admixPane.getChildren().add(getGroup(shape));
        }

        // if pca tab - add annotation
        if(selectedTab.getId().contains("tab")){
            String[] s = selectedTab.getId().split(" "); // [pca, 0] or [pca, 11]
            int tabIndex = Integer.valueOf(s[1]);

            Pane p = (Pane) pcaChartsList.get(tabIndex).getChildrenUnmodifiable().get(1);
            p.getChildren().add(getGroup(shape));
        }
    }

    public void addArrowToChart(Arrow arrow, int chartIndex){
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        // if admix pane
        if(selectedTab.getId().contains("admix")){
            admixPane.getChildren().add(getArrowGroup(arrow));
        }

        // if pca tab - add annotation
        if(selectedTab.getId().contains("tab")){
            String[] s = selectedTab.getId().split(" "); // [pca, 0] or [pca, 11]
            int tabIndex = Integer.valueOf(s[1]);

            Pane p = (Pane) pcaChartsList.get(tabIndex).getChildrenUnmodifiable().get(1);
            p.getChildren().add(getArrowGroup(arrow));
        }
    }

    public void addImportedShape(Shape shape, int chartIndex, String chartType){
        // if admix pane
        if(chartType.equals("admixture")){
            admixPane.getChildren().add(getGroup(shape));
        }

        // if pca tab - add annotation
        if(chartType.equals("pca")){
            Pane p = (Pane) pcaChartsList.get(chartIndex).getChildrenUnmodifiable().get(1);
            p.getChildren().add(getGroup(shape));
        }
    }

    public void addImportedArrow(Arrow arrow, int chartIndex, String chartType){
        // if admix pane
        if(chartType.equals("admixture")){
            admixPane.getChildren().add(getArrowGroup(arrow));
        }

        // if pca tab - add annotation
        if(chartType.equals("pca")){
            Pane p = (Pane) pcaChartsList.get(chartIndex).getChildrenUnmodifiable().get(1);
            p.getChildren().add(getArrowGroup(arrow));
        }
    }

    private Group getGroup(Shape shape){
        Group gr = new Group();

        // change cursor on hovering the shape
        shape.setOnMouseEntered(e -> {
            shape.getScene().setCursor(Cursor.HAND);
            shape.setEffect(new DropShadow(20, Color.BLUE));
        });

        shape.setOnMouseExited(e -> {
            shape.getScene().setCursor(Cursor.DEFAULT);
            shape.setEffect(null);
        });

        gr.getChildren().addAll(shape);
        return gr;
    }

    private Group getArrowGroup(Arrow arrow){
        Group gr = new Group();

        // change cursor on hovering the shape
        arrow.setOnMouseEntered(e -> {
            arrow.getScene().setCursor(Cursor.HAND);
            arrow.setEffect(new DropShadow(20, Color.BLUE));
        });

        arrow.setOnMouseExited(e -> {
            arrow.getScene().setCursor(Cursor.DEFAULT);
            arrow.setEffect(null);
        });

        gr.getChildren().addAll(arrow);
        return gr;
    }

    private void updateAnnotationsLists(Annotation annotationType){
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();

        if (selectedTab.getId().contains("admix")) {
            project.getAdmixtureAnnotationsList().add(annotationType);
        }

        // if pca tab - add annotation
        if (selectedTab.getId().contains("tab")) {
            String[] s = selectedTab.getId().split(" "); // [pca, 0] or [pca, 11]
            int tabIndex = Integer.valueOf(s[1]);
            project.getPcGraphAnnotationsList().get(tabIndex).add(annotationType);
        }
    }


    public Dialog getDialog(GridPane gridPane){
        Dialog dialog = new Dialog<>();
        dialog.setTitle("Select properties");
        dialog.setHeaderText(null);
        dialog.setResizable(false);

        dialog.getDialogPane().setContent(gridPane);

        cancelButton = new ButtonType("Cancel");
        deleteBtn = new ButtonType("Delete");
        doneBtn = new ButtonType("Done");

        dialog.getDialogPane().getButtonTypes().setAll(cancelButton, deleteBtn, doneBtn);
        return dialog;
    }

    public ButtonType getButtonType(String btnName){
        ButtonType btn = null;
        switch (btnName) {
            case "Delete":
                btn = deleteBtn;
                break;
            case "Cancel":
                btn = cancelButton;
                break;
            case "Done":
                btn = doneBtn;
                break;
        }
        return btn;
    }

    /**
     * used to point to a particular row under modification also used as charts
     * index
     * @return
     */
    public static int getRowPointer() {
        return rowPointer;
    }

    static void setRowPointer(int i) {
        rowPointer = i;
    }

    // get pane for rotation
    public static AnchorPane getAdmixPane() {
        return admixPane;
    }

    public static VBox getAdmixVbox() {
        return admixVbox;
    }
    /**
     * access this heading for text formatting
     * @return
     */
    public static Text getChartHeading() {
        return chartHeading;
    }

    public static double getDefaultAdmixPlotWidth() {
        return defaultAdmixPlotWidth;
    }

    public List<ArrayList<StackedBarChart<String, Number>>> getAllAdmixtureCharts() {
        return allAdmixtureCharts;
    }

    @FXML
    private void importProject(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(Genesis.class.getResource("view/ImportProject.fxml"));
        Parent p = loader.load();
        importProjectController = loader.getController();
        importProjectController.setMainController(this);
        importProjectController.disableDoneBtn(true);
        Stage dialogStage = new Stage();
        dialogStage.setScene(new Scene(p));
        dialogStage.setResizable(false);

        dialogStage.showAndWait();
    }

    @FXML
    void saveProject(ActionEvent event){
        try{
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Project");
            FileChooser.ExtensionFilter genFilter = new FileChooser.ExtensionFilter("Genesis File", "*.g2f");
            fileChooser.getExtensionFilters().addAll(genFilter);
            fileChooser.setInitialFileName(project.getProjectName()+".g2f");
            Stage stage = new Stage();
            File projFile = fileChooser.showSaveDialog(stage);

            if(projFile != null){
                FileOutputStream fileOut =  new FileOutputStream(projFile);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(project);
                out.close();
                out.close();
                fileOut.close();
            }
        }catch(IOException e){;}
    }

    @FXML
    private void help(ActionEvent event) {
//            Tab tab = setHelpTab(Genesis.class.getResource("help/home.html").toExternalForm());
//            tabPane.getTabs().add(tab);
//            closeTab(tab);
        Genesis.throwInformationException("Sorry. The documentation/help is temporary unavailable");
            helpBtn.setDisable(true);
    }
 
    /**
     * help tab with documentation
     * @param url
     * @return
     */
    private Tab setHelpTab(String url){
        WebView webView = new WebView();
        webView.getEngine().load(url);
        Tab helpTab = new Tab("Help");
        helpTab.setId("help");
        helpTab.setContent(webView);
        return helpTab;
    }

    @FXML
    @SuppressWarnings("empty-statement")
    private void closeProgram(ActionEvent event) {
        // call the confirmation method
        String result = Genesis.confirmAction("Are you sure you want to close this program?");
        if (result.equals("yesBtnPressed")) {
            Platform.exit();
        } else {
            ; //do nothing
        }
    }

    public void disableNewProjBtn(boolean b){
        newProjBtn.setDisable(b);
    }

    public void disablePcaBtn(boolean b){
        pcaBtn.setDisable(b);
    }

     public void disableAdmixtureBtn(boolean b){
        admixtureBtn.setDisable(b);
     }

     public void disableSettingsBtn(boolean b){
        settingsBtn.setDisable(b);
     }

     public void disableDownloadBtn(boolean b){
        downloadBtn.setDisable(b);
     }

     public void disableDrawingBtn(boolean b){
        drawingBtn.setDisable(b);
     }

     public void disableIndividualBtn(boolean b){
        individualBtn.setDisable(b);
     }

     public void disableSearchBtn(boolean b){
        searchBtn.setDisable(b);
     }

     public void disableSaveBtn(boolean b){
        saveProjBtn.setDisable(b);
     }

     public void disableImportProjBtn(boolean b){
        importProjBtn.setDisable(b);
     }

    public Project getProject() {
        return project;
    }

    public void disableControlBtns(boolean enable){
        disableSaveBtn(enable);
        disableSettingsBtn(enable);
        disableDrawingBtn(enable);
        disableIndividualBtn(enable);
        disableSearchBtn(enable);
        disableSaveBtn(enable);
        disableDownloadBtn(enable);
    }

    @Override
    public void initialize(java.net.URL arg0, ResourceBundle arg1) {
        // disable control buttons
        disablePcaBtn(true);
        disableAdmixtureBtn(true);
        disableSettingsBtn(true);
        disableDownloadBtn(true);
        disableDrawingBtn(true);
        disableIndividualBtn(true);
        disableSearchBtn(true);
        disableSaveBtn(true);

        // set the admixture tab
        admixtureTab = new Tab();
        admixtureTab.setText("Admixture Plot");
        admixtureTab.setId("admix");
        admixtureTab.setStyle("-fx-background-color:  #06587F;");

        drawingAnchorPaneVisibility = false;
        drawingAnchorPane.setVisible(drawingAnchorPaneVisibility);

        //VBox to keep the admix plot and heading
        admixVbox = new VBox(10);
        admixVbox.setPrefWidth(defaultAdmixPlotWidth + VBOX_MARGIN); // TODO - change these hard coded values
        admixVbox.setStyle("-fx-background-color: white;");

        // add pane for the title
        chartHeading = new Text("Admixture plot");
        StackPane titlePane = new StackPane(chartHeading);
        titlePane.setAlignment(Pos.CENTER);
        titlePane.setPrefWidth(Double.MAX_VALUE);

        AnchorPane pane = new AnchorPane();
        pane.setStyle("-fx-background-color: white;");
        admixVbox.getChildren().addAll(titlePane, pane);

        // gridpane section for admixture plots
        gridPane = new GridPane();
        gridPane.setStyle("-fx-background-color: white;");
        gridPane.setCache(true);
        gridPane.setCacheHint(CacheHint.SPEED);
        gridPane.setHgap(0); //horizontal gap
        gridPane.setVgap(0); //vertical gap
//        gridPane.setGridLinesVisible(false);
        gridPane.setMinWidth(defaultAdmixPlotWidth); // TODO - change these hard coded values
        gridPane.setMaxWidth(defaultAdmixPlotWidth); // increase this value to increase the thickness of subjects

        AnchorPane.setRightAnchor(gridPane, 40.0);

        admixPane = new AnchorPane();
        admixPane.setStyle("-fx-background-color: white; -fx-border-color: white;");

        // set scrollpane that keeps admix charts
        scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: red; -fx-border-color: white;");
    }

    public void setProjIsImported(boolean b) {
        this.projectIsImported = b;
    }

    public boolean isProjectIsImported() {
        return projectIsImported;
    }
}
