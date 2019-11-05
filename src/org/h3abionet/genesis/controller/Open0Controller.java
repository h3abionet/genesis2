package org.h3abionet.genesis.controller;

import org.h3abionet.genesis.model.Project;
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
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.PCGraph;
import jfxtras.labs.util.event.MouseControlUtil;
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
 *
 * @author scott This is the main controller
 */
public class Open0Controller implements Initializable {

    @FXML
    private FontSelectorController fontSelectorController;
    @FXML
    private PCADataInputController pCADataInputController;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab admixtureTab;
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
    
    // drawiwing tools
    @FXML
    private AnchorPane drawingAnchorPane;
    
    private boolean drawingAnchorPaneVisibility;

    @FXML
    private Button penTool;

    @FXML
    private Button circleTool;

    @FXML
    private Button arrowTool;
    
    @FXML
    private Button textTool;

    @FXML
    private Slider sliderTool;

    @FXML
    private ColorPicker colorPickerTool;
    
    // other variables
    private ProjectDetailsController projectDetailsController;
    private Project project;
    private static Tab pcaTab;
    private static int tabCount = 0;
    private int chartIndex;
    private static ScatterChart<Number, Number> chart;
    ArrayList<ScatterChart> chartList = new ArrayList<>();

    @FXML
    private void newProject(ActionEvent event) throws IOException {
        projectDetailsController.loadProjDialogEntry();

    }

    @FXML
    @SuppressWarnings("empty-statement")
    private void newPCA(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(Genesis.class.getResource("view/PCADataInput.fxml"));
        Stage stage = new Stage();
        stage.initOwner(newpca.getScene().getWindow());
        stage.setScene(new Scene((Parent) loader.load()));
        stage.setResizable(false);
        stage.showAndWait();

        PCADataInputController controller = loader.getController();

        try {
            setChart(controller);

        } catch (NullPointerException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setContentText("Oops, there was an error!");
            alert.showAndWait();
        }

    }

    @FXML
    @SuppressWarnings("empty-statement")
    private void loadData(ActionEvent event) throws IOException {
        pCADataInputController.setPcaDialogStage();
        PCADataInputController controller = PCADataInputController.getController();
        
        try {
            setChart(controller);
            
        } catch (Exception e) {
           ;
        }

    }
    
    public void setChart(PCADataInputController controller){
        chart = controller.getChart();
        try {
            PCGraph pc = new PCGraph(chart);
            
            String xAxisLabel = chart.getXAxis().getLabel();
            String yAxisLabel = chart.getYAxis().getLabel();
            String x = xAxisLabel.substring(4, xAxisLabel.length());
            String y = yAxisLabel.substring(4, yAxisLabel.length());
            
            tabCount++;
            pcaTab = new Tab();
            pcaTab.setText("PCA " + x + " & " + y);
            pcaTab.setClosable(true);
            pcaTab.setId("tab" + tabCount);
            
            // add the container to the tab
            pcaTab.setContent(pc.addGraph());
            tabPane.getTabs().add(pcaTab);
            
            // set chart index to selected tab number 
            tabPane.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> ov, Number oldValue, Number newValue) -> {
                chartIndex = (int) newValue;
                System.out.println(chartIndex);

            }); 
            chartList.add(chart);
            
        } catch (Exception e) {
           ;
        }
    }

    @FXML
    private void fontSelector(ActionEvent event){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("view/FontSelector.fxml"));
            Stage iconStage = new Stage();
            iconStage.initOwner(settingsButton.getScene().getWindow());
            iconStage.setScene(new Scene((Parent) fxmlLoader.load()));
            iconStage.setResizable(false);
            iconStage.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Sorry, there is no chart to format");

            alert.showAndWait();
        }

    }

    /**
     * Saves the chart in the right format
     */
    @FXML
    @SuppressWarnings("empty-statement")
    public void saveChart(){
        PCGraph pc = new PCGraph(chartList.get(chartIndex));
        pc.saveChart();
        
    }

    /**
     * This function returns the chart
     * when called by the individual details controller
     * @return
     */
    public static ScatterChart<Number, Number> getChart() {
        return chart;
    }
    
    @FXML
    private void drawingTool(ActionEvent event){
        drawingAnchorPaneVisibility = !drawingAnchorPaneVisibility;
        drawingAnchorPane.setVisible(drawingAnchorPaneVisibility);

    }
    
    @FXML
    private void handDrawingButton(ActionEvent event){
       
       Pane p = (Pane) chart.getChildrenUnmodifiable().get(1);
       Region r = (Region) p.getChildren().get(0);
       Group gr = new Group();
       
       Line line = new Line(0, 150, 200,150);   
       line.setStrokeWidth(2); 
       line.setStroke(Color.web("000000"));
       MouseControlUtil.makeDraggable(line);

       gr.getChildren().addAll(line);
       p.getChildren().add(gr);
    
    }
    
    @FXML
    private void drawArrow(ActionEvent event){}
    
    @FXML
    private void drawCircle(ActionEvent event){
        Pane p = (Pane) chart.getChildrenUnmodifiable().get(1);
        Region r = (Region) p.getChildren().get(0);
        Group gr = new Group();
       
        Circle circle = new Circle();  
        circle.setCenterX(200);  
        circle.setCenterY(200);  
        circle.setRadius(100);  
        circle.setFill(Color.TRANSPARENT);
        circle.setStroke(Color.BLACK);
        MouseControlUtil.makeDraggable(circle);
        
        gr.getChildren().addAll(circle);
        p.getChildren().add(gr);
    
    }
    
    @FXML
    private void addText(ActionEvent event){
        Pane p = (Pane) chart.getChildrenUnmodifiable().get(1);
        Region r = (Region) p.getChildren().get(0);
        Group gr = new Group();
        
        Text text = new Text("Point");  
        text.setFill(Color.web("fabbff"));
        MouseControlUtil.makeDraggable(text);
        
        gr.getChildren().addAll(text);
        p.getChildren().add(gr);
    
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
        pCADataInputController = new PCADataInputController();
        pCADataInputController.setOpen0Controller(this);
        projectDetailsController = new ProjectDetailsController();

        drawingAnchorPaneVisibility = false;
        drawingAnchorPane.setVisible(drawingAnchorPaneVisibility);
        
        
    }
    
}
