package org.h3abionet.genesis.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.PCAGraph;

import java.util.Arrays;

public class DataPointMouseEvent implements EventHandler<MouseEvent> {

    private XYChart.Data<Number, Number> data;
    private XYChart<Number, Number> chart;

    public DataPointMouseEvent(XYChart.Data<Number, Number> data, XYChart<Number, Number> chart) {
        this.data = data;
        this.chart = chart;
    }

    @Override
    public void handle(MouseEvent mouseEvent) {

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("view/IndividualDetails.fxml"));
                Parent parent = (Parent) fxmlLoader.load();
                Stage dialogStage = new Stage();
                dialogStage.setScene(new Scene(parent));
                dialogStage.setResizable(false);

                PCAIndividualDetailsController individualDetailsController = fxmlLoader.getController();
                individualDetailsController.setDataPointMouseEvent(this);
                String xValue = data.getXValue().toString();
                String yValue = data.getYValue().toString();
                String xAxisLabel = chart.getXAxis().getLabel();
                String yAxisLabel = chart.getYAxis().getLabel();
                individualDetailsController.setPcaLabel(xAxisLabel + ": " + xValue + "\n" + yAxisLabel + ": " + yValue);
                // get pheno data using x & y co-ordinates
                for(String [] s: PCAGraph.getPcasWithPhenoList() ){
                    // if an array in pcasWithPhenoList has both x & y
                    if(Arrays.asList(s).contains(xValue) && Arrays.asList(s).contains(yValue)){
                        // get pheno data: [MKK, AFR, pc1, pc2, pc3, ..., FID IID]
                        ObservableList<String> phenos = FXCollections.<String>observableArrayList(s[s.length-1], s[0], s[1]);
                        individualDetailsController.setPhenoLabel(phenos);
                        break;
                    }
                }
                individualDetailsController.setIconDisplay(data.getNode().getStyle());
                dialogStage.showAndWait();

            } catch (Exception ex) {
                ;
            }

    }
}
