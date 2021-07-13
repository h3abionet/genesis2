package org.h3abionet.genesis.controller;

import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import jfxtras.labs.util.event.MouseControlUtil;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.AdmixtureGraph;
import org.h3abionet.genesis.model.PCAGraph;
import org.h3abionet.genesis.model.Project;
import org.h3abionet.genesis.model.Subject;

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
public class MainController implements Initializable {
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
    private Button threeDBtn;
    @FXML
    private Button dataBtn;
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

    // pca variables
    private static Tab pcaChartTab;
    private static int tabCount = 0;
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
    private AdmixtureGraphEventsHandler admixGraphEventHandler;
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

    @FXML
    private void newProject(ActionEvent event) throws IOException {
        // remove background image
        setTabPaneStyle();

        // load data input scene
        FXMLLoader projLoader = new FXMLLoader(Genesis.class.getResource("view/ProjDialogEntry.fxml"));
        Parent projParent = projLoader.load();
        projectDetailsController =  projLoader.getController();
        projectDetailsController.setMainController(this);
        Stage dialogStage = new Stage();
        dialogStage.setScene(new Scene(projParent));
        dialogStage.setResizable(false);
        dialogStage.showAndWait();

        if(project.isFamCreated() & project.isPhenoCreated()){
            // if both fam and phenotype file are correct, launch project
            disableImportProjBtn(true);
            disableNewProjBtn(true);
            disablePcaBtn(false);
            disableAdmixtureBtn(false);
            disableControlBtns(false);
            disableDataBtn(false);
        }else if(project.isFamCreated() && project.isPhenoFileProvided()==false) {
            // if only the fam file is provided and is correct, launch project
            disableImportProjBtn(true);
            disableNewProjBtn(true);
            disablePcaBtn(false);
            disableAdmixtureBtn(false);
            disableControlBtns(false);
            disableDataBtn(false);
        }else {
            // otherwise don't launch the project
            disableImportProjBtn(false);
            disableNewProjBtn(false);
            disablePcaBtn(true);
            disableAdmixtureBtn(true);
            disableControlBtns(true);
            disableSettingsBtn(true);
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
        tabPane.setStyle("-fx-background-image: null");
    }

    @FXML
    @SuppressWarnings("empty-statement")
    private void newPCA(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("view/PCADataInput.fxml"));
        Parent parent = (Parent) fxmlLoader.load();
        pcaDataInputController = fxmlLoader.getController();
        pcaDataInputController.setMainController(this);
        Stage dialogStage = new Stage();
        dialogStage.setScene(new Scene(parent));
        dialogStage.setResizable(false);

        dialogStage.showAndWait();

        try {
            if(pcaDataInputController.isFirstPcaSuccessful()){
                setPCAChart(pcaGraph.getPcaChart());
                // disable the pca button after first import
                disablePcaBtn(true);
                disableImportProjBtn(true);
                disableNewProjBtn(true);
                disableDataBtn(false);
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
                disableDataBtn(true);
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
     * loads the stored data when the data button is pressed It uses the
     * launchPCADataInputView method in PCADataInputController to show the stage
     *
     * @param event
     * @throws IOException
     */
    @FXML
    @SuppressWarnings("empty-statement")
    private void loadData(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("view/PCADataInput.fxml"));
        Parent root = fxmlLoader.load();
        PCADataInputController controller = fxmlLoader.getController();
        controller.enableOK();
        controller.setPcaGraph(pcaGraph);
        controller.setButtons();
        Stage dialogStage = new Stage();
        dialogStage.setScene(new Scene(root));
        dialogStage.setResizable(false);
        dialogStage.showAndWait();

        try {
            setPCAChart(pcaGraph.getPcaChart());
        } catch (Exception e) {
            ;
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
            PCAGraphEventsHandler pc = new PCAGraphEventsHandler(pcaChart);

            // get axis labels
            String xAxisLabel = pcaChart.getXAxis().getLabel();
            String yAxisLabel = pcaChart.getYAxis().getLabel();
            String x = xAxisLabel.substring(4);
            String y = yAxisLabel.substring(4);

            // create new tab for the pca chart
            tabCount++;
            pcaChartTab = new Tab();

            // tab name e.g PCA 1 & 2 ans space with close icon
            pcaChartTab.setText("PCA " + x + " & " + y+"    ");
            pcaChartTab.setClosable(true);
            pcaChartTab.setId("tab " + tabCount);

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
                scrollPane.setContent(admixVbox);
            }

            admixtureTab.setContent(scrollPane);
            admixtureTab.getContent().autosize();

            //  if it is first k, add new tab to the tabpane
            if(project.getImportedKs().size()==1 || project.isProjIsImported()){
                tabPane.getTabs().add(admixtureTab);
            }else{ // else first remove the existing admixture tab
                for(int t=0; t<tabPane.getTabs().size(); t++){
                    if(tabPane.getTabs().get(t).getId().equals("admix")){
                        tabPane.getTabs().remove(tabPane.getTabs().get(t));
                        tabPane.getTabs().add(admixtureTab);
                    }
                }
            }

            closeTab(admixtureTab); //  on closing the admixture tab

            // create another rowPointer index
            rowPointer++;

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
    public void saveChart() {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();

        try {
            // load pca setting
            if (selectedTab.getId().contains("tab")) {
                PCAGraphEventsHandler pc = new PCAGraphEventsHandler(pcaChartsList.get(currentTabIndex));
                pc.saveChart();
            }
            // load admixture setting
            if (selectedTab.getId().contains("admix")){
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
        Line line = new Line(0, 150, 200, 150);
        line.setStrokeWidth(2);
        line.setStroke(Color.web("000000"));

        addShapeToChart(line);

        MouseControlUtil.makeDraggable(line);

        // set on mouse drag
        line.setOnMouseMoved((MouseEvent evnt) -> {
            double mouseDeltaX = evnt.getSceneX() - pivot.getTranslateX();
            double mouseDeltaY = evnt.getSceneY() - pivot.getTranslateY();
            double radAngle = Math.atan2(mouseDeltaY, mouseDeltaX);
            double[] res = rotateLine(pivot, radAngle - Math.toRadians(lineCurrentAngle(line)), line.getEndX(), line.getEndY());

            line.setEndX(res[0]);
            line.setEndY(res[1]);

        });
    }

    @FXML
    private void addArrow(ActionEvent event) {
        Arrow arrow = new Arrow();
        arrow.setStartX(200);
        arrow.setStartY(200);
        arrow.setEndX(400);
        arrow.setEndY(200);

        MouseControlUtil.makeDraggable(arrow);

        Pane pane = (Pane) pcaChartsList.get(currentTabIndex).getChildrenUnmodifiable().get(1);
        Region r = (Region) pane.getChildren().get(0);
        Group gr = new Group();

        arrow.setOnMouseEntered(e -> {
            arrow.getScene().setCursor(Cursor.HAND);
            arrow.setEffect(new DropShadow(20, Color.BLUE));
        });

        arrow.setOnMouseExited(e -> {
            arrow.getScene().setCursor(Cursor.DEFAULT);
            arrow.setEffect(null);
        });

        gr.getChildren().addAll(arrow);
        pane.getChildren().add(gr);

        pane.setOnMouseClicked(evt -> {
            switch (evt.getButton()) {
                case PRIMARY:
                    // set pos of end with arrow head
                    arrow.setEndX(evt.getX());
                    arrow.setEndY(evt.getY());
                    break;
                case SECONDARY:
                    // set pos of end without arrow head
                    arrow.setStartX(evt.getX());
                    arrow.setStartY(evt.getY());
                    break;
            }
        });

    }

    @FXML
    private void addCircle(ActionEvent event) {
        Circle circle = new Circle();
        circle.setCenterX(200);
        circle.setCenterY(200);
        circle.setRadius(100);
        circle.setFill(Color.TRANSPARENT);
        circle.setStroke(Color.BLACK);

        MouseControlUtil.makeDraggable(circle);
        addShapeToChart(circle);

        circle.setOnMouseClicked((MouseEvent e) -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                // use class circle options -- accepts chosen circle as a paremeter
                CircleOptions circleOptions = new CircleOptions(circle);
                // modify the chosen circle
                circleOptions.modifyCircle();
            }
        });

    }

    @FXML
    private void addText(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Text");
        dialog.setHeaderText(null);
        dialog.setGraphic(null);
        dialog.setContentText("Text:");

        Optional<String> result = dialog.showAndWait();
        // set text default color and position on the pcaChart
        Text text = new Text(300, 50, result.get());
        text.setFill(Color.BLACK);

        // can drag text
        MouseControlUtil.makeDraggable(text);
        // add text to pcaChart
        addShapeToChart(text);
        // add mouse event to text for editing options
        text.setOnMouseClicked((MouseEvent e) -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                // use class label options -- accepts chosen label as a paremeter
                LabelOptions labelOptions = new LabelOptions(text);
                // modify the chosen label
                labelOptions.modifyLabel();
            }
        });

    }

    @FXML
    private void addRectangle(ActionEvent event) {
        Rectangle rec = new Rectangle(100, 100, 200, 100);
        rec.setFill(Color.TRANSPARENT);
        rec.setStroke(Color.BLACK);

        MouseControlUtil.makeDraggable(rec);
        addShapeToChart(rec);

        rec.setOnMouseClicked((MouseEvent e) -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                // use class rectangle options -- accepts chosen rectangle as a paremeter
                RectangleOptions rectangleOptions = new RectangleOptions(rec);
                // modify the chosen rectangle
                rectangleOptions.modifyRectangle();
            }
        });
    }

    public void addShapeToChart(Shape shape) {
        Pane p = (Pane) pcaChartsList.get(currentTabIndex).getChildrenUnmodifiable().get(1);
        Region r = (Region) p.getChildren().get(0);
        Group gr = new Group();

        shape.setOnMouseEntered(e -> {
            shape.getScene().setCursor(Cursor.HAND);
            shape.setEffect(new DropShadow(20, Color.BLUE));
        });

        shape.setOnMouseExited(e -> {
            shape.getScene().setCursor(Cursor.DEFAULT);
            shape.setEffect(null);
        });

        gr.getChildren().addAll(shape);
        p.getChildren().add(gr);
    }

    private double lineCurrentAngle(Line line) {
        return Math.toDegrees(Math.atan2(line.getEndY() - pivot.getTranslateY(), line.getEndX() - pivot.getTranslateX()));
    }

    private double[] rotateLine(Shape pivot, double radAngle, double endX, double endY) {
        double x, y;
        x = Math.cos(radAngle) * (endX - pivot.getTranslateX()) - Math.sin(radAngle) * (endY - pivot.getTranslateY()) + pivot.getTranslateX();
        y = Math.sin(radAngle) * (endX - pivot.getTranslateX()) + Math.cos(radAngle) * (endY - pivot.getTranslateY()) + pivot.getTranslateY();
        return new double[]{x, y};
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
                fileOut.close();
            }
        }catch(IOException e){;}
    }

    @FXML
    private void help(ActionEvent event) {
                    Tab tab = setHelpTab(Genesis.class.getResource("help/home.html").toExternalForm());
                    tabPane.getTabs().add(tab);
                    closeTab(tab);
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

     public  void disableDataBtn(boolean b){
        dataBtn.setDisable(b);
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

     public void disable3DBtn(boolean b){
        threeDBtn.setDisable(b);
     }

     public void disableSaveBtn(boolean b){
        saveProjBtn.setDisable(b);
     }

     public void disableImportProjBtn(boolean b){
        importProjBtn.setDisable(b);
     }

    public void disableControlBtns(boolean enable){
        disableSaveBtn(enable);
        disableSettingsBtn(false);
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
        disableDataBtn(true);
        disableDownloadBtn(true);
        disableDrawingBtn(true);
        disableIndividualBtn(true);
        disableSearchBtn(true);
        disable3DBtn(true);
        disableSaveBtn(true);

        // set the admixture tab
        admixtureTab = new Tab();
        admixtureTab.setText("Admixture Plot");
        admixtureTab.setId("admix");

        drawingAnchorPaneVisibility = false;
        drawingAnchorPane.setVisible(drawingAnchorPaneVisibility);

        //VBox to keep the admix plot and heading
        admixVbox = new VBox(10);
        admixVbox.setPrefWidth(defaultAdmixPlotWidth + VBOX_MARGIN); // TODO - change these hard coded values

        // add pane for the title
        chartHeading = new Text("Admixture plot");
        StackPane titlePane = new StackPane(chartHeading);
        titlePane.setAlignment(Pos.CENTER);
        titlePane.setPrefWidth(Double.MAX_VALUE);

        AnchorPane pane = new AnchorPane();
//        pane.setStyle("-fx-border-color: red; -fx-border-width: 1;");

        admixVbox.getChildren().addAll(titlePane, pane);
//        admixVbox.setStyle("-fx-border-color: pink; -fx-background-color: white; -fx-border-width: 5px");

        // gridpane section for admixture plots
        gridPane = new GridPane();
        gridPane.setHgap(0); //horizontal gap
        gridPane.setVgap(0); //vertical gap
        gridPane.setGridLinesVisible(false);
        gridPane.setMinWidth(defaultAdmixPlotWidth); // TODO - change these hard coded values
        gridPane.setMaxWidth(defaultAdmixPlotWidth); // increase this value to increase the thickness of subjects
        AnchorPane.setRightAnchor(gridPane, 40.0);

        admixPane = new AnchorPane();
//        admixPane.setStyle("-fx-border-color: green; -fx-border-width: 3px 3px 3px 3px");

        // set scrollpane that keeps admix charts
        scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
//        scrollPane.setStyle("-fx-border-color: purple; -fx-border-width: 2px");
    }

}
