package org.h3abionet.genesis.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.PCGraph;
import org.h3abionet.genesis.model.LabelOptions;
import org.h3abionet.genesis.model.CircleOptions;
import jfxtras.labs.util.event.MouseControlUtil;
import org.h3abionet.genesis.model.Arrow;
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
    Circle pivot;
    Line line;

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
    
    // other variables
    private ProjectDetailsController projectDetailsController;
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

    public void setChart(PCADataInputController controller) {
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
    private void fontSelector(ActionEvent event) {
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
    public void saveChart() {
        PCGraph pc = new PCGraph(chartList.get(chartIndex));
        pc.saveChart();

    }
    
    @FXML
    public void hidenIndividuals() throws IOException{
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
     * This function returns the chart when called by the individual details
     * controller
     *
     * @return
     */
    public static ScatterChart<Number, Number> getChart() {
        return chart;
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
        Rotate rotate = new Rotate();

        line = new Line(0, 150, 200, 150);
        line.setStrokeWidth(2);
        line.setStroke(Color.web("000000"));

        addShapeToChart(line);

        MouseControlUtil.makeDraggable(line);

        // set on mouse drag
        line.setOnMouseMoved((MouseEvent evnt) -> {
            double mouseDeltaX = evnt.getSceneX() - pivot.getTranslateX();
            double mouseDeltaY = evnt.getSceneY() - pivot.getTranslateY();
            double radAngle = Math.atan2(mouseDeltaY, mouseDeltaX);
            double[] res = rotateLine(pivot, radAngle - Math.toRadians(lineCurrentAngle()), line.getEndX(), line.getEndY());

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

        Pane p = (Pane) chart.getChildrenUnmodifiable().get(1);
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
        // set text default color and position on the chart
        Text text = new Text(300, 50, result.get());
        text.setFill(Color.BLACK);
        
        // can drag text
        MouseControlUtil.makeDraggable(text);
        // add text to chart
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
        Pane p = (Pane) chart.getChildrenUnmodifiable().get(1);
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
    
    private double lineCurrentAngle() {
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
        pCADataInputController = new PCADataInputController();
        pCADataInputController.setOpen0Controller(this);
        projectDetailsController = new ProjectDetailsController();

        drawingAnchorPaneVisibility = false;
        drawingAnchorPane.setVisible(drawingAnchorPaneVisibility);

    }

}
