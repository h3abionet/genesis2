package org.h3abionet.genesis.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.PCAGraphEventsHandler;
import org.h3abionet.genesis.model.LabelOptions;
import org.h3abionet.genesis.model.CircleOptions;
import jfxtras.labs.util.event.MouseControlUtil;
import org.h3abionet.genesis.model.AdmixtureGraphEventsHandler;
import org.h3abionet.genesis.model.Arrow;
import org.h3abionet.genesis.model.Project;
import org.h3abionet.genesis.model.RectangleOptions;

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
 * @author scott 
 */
public class MainController implements Initializable {
    
    // interface (view) variables
    @FXML
    private TabPane tabPane;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Button newadmixture;
    @FXML
    private Button newpca;
    @FXML
    private Button newproject;
    @FXML
    private Button threeDButton;
    @FXML
    private Button dataButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button downloadButton;
    @FXML
    private Button individualButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button drawingButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button fileButton;
    @FXML
    private Button helpButton;
    
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
    Circle pivot;
    
    // pca variables
    private static Tab pcaChartTab;
    private static int tabCount = 0;
    private static int pcaChartIndex;
    private static ScatterChart<Number, Number> pcaChart;
    private static ArrayList<ScatterChart> pcaChartsList = new ArrayList<>();
    
    // admixture variables
    private ArrayList<StackedBarChart<String, Number>> listOfAdmixtureCharts;
    private Tab admixtureTab;
    private GridPane gridPane; // gridpane for keeping list of admixture charts
    private int rowIndex = 0; // gridpane rowIndex index

    @FXML
    private void newProject(ActionEvent event) throws IOException {
        Genesis.loadFxmlView("view/ProjDialogEntry.fxml");
        
    }

    @FXML
    @SuppressWarnings("empty-statement")
    private void newPCA(ActionEvent event) throws IOException {
        Genesis.loadFxmlView("view/PCADataInput.fxml");
        try {
            setPCAChart(PCADataInputController.pcaChart);

        } catch (NullPointerException e) {
            Genesis.throwErrorException("Oops, there was an error!");
        }
    }
    
    @FXML
    @SuppressWarnings("empty-statement")
    private void newAdmixture(ActionEvent event) throws IOException {
        Genesis.loadFxmlView("view/AdmixtureDataInput.fxml");
        try {
            setAdmixtureChart(AdmixtureDataInputController.listOfStackedBarCharts);

        } catch (NullPointerException e) {
            Genesis.throwErrorException("Oops, there was an error!");
        }
    }

    /**
     * loads the stored data when the data button is pressed
     * It uses the launchPCADataInputView method in PCADataInputController to show the stage
     * @param event
     * @throws IOException 
     */
    @FXML
    @SuppressWarnings("empty-statement")
    private void loadData(ActionEvent event) throws IOException {
        PCADataInputController.launchPCADataInputView();        
        try {
            setPCAChart(PCADataInputController.pcaChart);
        } catch (Exception e) {
            ;
        }

    }
    
    /**
     * Uses PCAGraphEventsHandler model class 
     * @param pcaChart
     */
    private void setPCAChart(ScatterChart<Number, Number> pcaChart) {
        this.pcaChart = pcaChart;
        try {
            // pc acquires the pcaChart for additional features
            PCAGraphEventsHandler pc = new PCAGraphEventsHandler(pcaChart);
            
            // get axis labels
            String xAxisLabel = pcaChart.getXAxis().getLabel();
            String yAxisLabel = pcaChart.getYAxis().getLabel();
            String x = xAxisLabel.substring(4, xAxisLabel.length());
            String y = yAxisLabel.substring(4, yAxisLabel.length());
            
            // create new tab for the pca chart 
            tabCount++;
            pcaChartTab = new Tab();
            pcaChartTab.setText("PCA " + x + " & " + y); // tab name e.g PCA 1 & 2
            pcaChartTab.setClosable(true);
            pcaChartTab.setId("tab" + tabCount);

            // add the pca chart container to the tab
            pcaChartTab.setContent(pc.addGraph());
            tabPane.getTabs().add(pcaChartTab);

            // set pcaChart index to selected tab number 
            tabPane.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> ov, Number oldValue, Number newValue) -> {
                pcaChartIndex = (int) newValue;
                System.out.println(pcaChartIndex);

            });
            pcaChartsList.add(pcaChart);

        } catch (Exception e) {
            ;
        }
    }
    
    /**
     * This method sets the admixture pcaChart.
     * @param admixtureDataInputController used to access the pcaChart
     */
    @SuppressWarnings("empty-statement")
    private void setAdmixtureChart(ArrayList<StackedBarChart<String, Number>> admixCharts){
        listOfAdmixtureCharts = admixCharts; // get multiple charts
        
        int sumOfIndividuals = Project.numOfIndividuals;
            
        // check if the first rowIndex has nodes. If not, define column constraints.
        if(gridPane.contains(1,0)){
            ;
        }else{
            //  K value column
            ColumnConstraints kValueColumn = new ColumnConstraints();
            kValueColumn.setHgrow(Priority.NEVER);
            gridPane.getColumnConstraints().add(kValueColumn);

            // define column constraints based on number of indivs per admix chart
            for(StackedBarChart<String, Number> s: listOfAdmixtureCharts){
                ColumnConstraints cc = new ColumnConstraints();
                int numOfIndividuals = s.getData().get(0).getData().size();
                double columnSize = (double)numOfIndividuals/(double)sumOfIndividuals * 100.0;
                cc.setPercentWidth(columnSize);
                cc.setHgrow(Priority.NEVER);
                gridPane.getColumnConstraints().add(cc);
            }
        }

        try {
            // use this class for additional pcaChart features: event handlers
            AdmixtureGraphEventsHandler admixtureChart;
            admixtureChart = new AdmixtureGraphEventsHandler(listOfAdmixtureCharts, gridPane, rowIndex);

            ScrollPane scrollPane = new ScrollPane(admixtureChart.getGridPane());
            scrollPane.setFitToWidth(true);

            admixtureTab.setContent(scrollPane);
            admixtureTab.getContent().autosize();
            tabPane.getTabs().add(admixtureTab);
            
            // create another rowIndex index
            rowIndex++;
            
        } catch (Exception e) {
            ; // do nothing
        }
    
    }
    

    @FXML
    private void settingsSelector(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("view/FontSelector.fxml"));
            FXMLLoader fxmlLoaderAdmixture = new FXMLLoader(Genesis.class.getResource("view/AdmixtureSettings.fxml"));
            Stage iconStage = new Stage();
            iconStage.initOwner(settingsButton.getScene().getWindow());
            
            
            if(tabPane.getChildrenUnmodifiable().contains(pcaChart)){
                iconStage.setScene(new Scene((Parent) fxmlLoader.load()));
                iconStage.setResizable(false);
                iconStage.showAndWait();
            
            }else{
                iconStage.setScene(new Scene((Parent) fxmlLoaderAdmixture.load()));
                iconStage.setResizable(false);
                iconStage.showAndWait();
            
            }
            
        } catch (IOException e) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Sorry, there is no chart to format");

            alert.showAndWait();
        }

    }

    /**
     * Saves the pcaChart in the right format
     */
    @FXML
    @SuppressWarnings("empty-statement")
    public void saveChart() {
        PCAGraphEventsHandler pc = new PCAGraphEventsHandler(pcaChartsList.get(pcaChartIndex));
        pc.saveChart();

    }
    
    @FXML
    public void showHidenIndividuals() throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("view/HiddenIndividuals.fxml"));
        Parent parent = (Parent) fxmlLoader.load();
        Stage dialogStage = new Stage();
        dialogStage.setScene(new Scene(parent));
        dialogStage.setResizable(false);

        HiddenIndividualsController hidden = fxmlLoader.getController();
        for(Node n: hidden.getItems()){
            n.setOnMouseClicked(e ->{
                n.setVisible(true);
            });
        }        
        dialogStage.showAndWait();   
    }

    /**
     * 
     * return PCA based on their index in the list
     * @return PCA pcaChart
     */
    public static ScatterChart<Number, Number> getPcaChart() {
        return pcaChartsList.get(pcaChartIndex);
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

        Pane p = (Pane) pcaChartsList.get(pcaChartIndex).getChildrenUnmodifiable().get(1);
        Region r = (Region) p.getChildren().get(0);
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
        p.getChildren().add(gr);
        
        p.setOnMouseClicked(evt -> {
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
            if (e.getButton() == MouseButton.SECONDARY)
            {
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
            if (e.getButton() == MouseButton.SECONDARY)
            {
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
            if (e.getButton() == MouseButton.SECONDARY)
            {
                // use class rectangle options -- accepts chosen rectangle as a paremeter
                RectangleOptions rectangleOptions = new RectangleOptions(rec);
                // modify the chosen rectangle
                rectangleOptions.modifyRectangle();
            }
        });
    }

    private void addShapeToChart(Shape shape) {
        Pane p = (Pane) pcaChartsList.get(pcaChartIndex).getChildrenUnmodifiable().get(1);
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

    @FXML
    private void help(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog("Help");
        dialog.setTitle("Help");
        dialog.setHeaderText("Hi, let's help you");
        dialog.setContentText("What are looking for?");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> System.out.println("Your keywords: " + name));
    }

    @FXML
    @SuppressWarnings("empty-statement")
    private void closeProgram(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setContentText("Are you sure you want to close this program?");

        ButtonType yesBtn = new ButtonType("YES");
        ButtonType cancelBtn = new ButtonType("NO");
        alert.getButtonTypes().setAll(yesBtn, cancelBtn);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == yesBtn) {
            Platform.exit();
        } else {
            ; //do nothing
        }

    }

    @Override
    public void initialize(java.net.URL arg0, ResourceBundle arg1) {
        // set the admixture tab
        admixtureTab = new Tab();
        admixtureTab.setText("Admixture Plot");
        admixtureTab.setId("admix");
        
        drawingAnchorPaneVisibility = false;
        drawingAnchorPane.setVisible(drawingAnchorPaneVisibility);
        
        // gridpane section for admixture plots
        gridPane = new GridPane();
        gridPane.setHgap(0); //horizontal gap
        gridPane.setVgap(0); //vertical gap
        gridPane.setGridLinesVisible(false);
        gridPane.setMinWidth(600);
        gridPane.setMaxWidth(600);
        gridPane.maxWidthProperty().bind(tabPane.widthProperty()); // extend pcaChart to the size of the window
        gridPane.maxHeightProperty().bind(tabPane.heightProperty());

    }

}
