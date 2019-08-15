/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.PCAProject;

/**
 * FXML Controller class
 *
 * @author Henry
 */
public class PCADataInputController implements Initializable {
    
    
//    @FXML private Open0Controller open0Controller;
    
    private Stage dialogStage;
    
    private PCAProject project;

    private boolean okClicked = false;

    private String  pheno_fname_s="", pca_fname_s="";
      
    @FXML
    private TextField pca_proj_name;

    @FXML
    private Button pca_pheno_fname;

    @FXML
    private Button pca_evec_fname;

    @FXML
    private ComboBox<String> pcaComboButton1;

    @FXML
    private ComboBox<String> pcaComboButton2;

    @FXML
    private Button entryOKButton;

    @FXML
    private Button entryCancelButton;
    
    public void setPcaDialogStage() throws IOException{
    FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("view/PCADataInput.fxml"));
    Parent root1 = (Parent) fxmlLoader.load();
    dialogStage = new Stage();
    dialogStage.initModality(Modality.APPLICATION_MODAL);
    dialogStage.setTitle("Genesis 2.0");
    dialogStage.setScene(new Scene(root1));
    dialogStage.show();
    }                                                                                                        
    
    public boolean isOkClicked() {
        return okClicked;
    }
    
    private File getFile(String which) {
       File wanted;
       FileChooser fileChooser = new FileChooser();
       fileChooser.setTitle(which);
       wanted = fileChooser.showOpenDialog(dialogStage); 
       return wanted;
        
    }
    
    @FXML
    void handlePcaProjectName(ActionEvent event) {
        if (pca_proj_name.getText().length()> 0)  
          entryOKButton.setDisable(false);
    }
    
    @FXML
    void handlePcaPhenoFname(ActionEvent event) {
      File phen = getFile("Choose FAM file");  
      pheno_fname_s = phen.getAbsolutePath();
      pca_pheno_fname.setText(phen.getName());
      pca_pheno_fname.setStyle("-fx-text-fill: green");
    }

    @FXML
    void handlePcaEvecFname(ActionEvent event) throws IOException {
      File pca = getFile("Choose PCA file");  
      pca_fname_s = pca.getAbsolutePath();
      pca_evec_fname.setText(pca.getName());
      pca_evec_fname.setStyle("-fx-text-fill: green");   
      project = new PCAProject(pca_proj_name.getText(), pheno_fname_s, pca_fname_s);
      pcaComboButton1.setItems(project.getPca_cols());
      pcaComboButton2.setItems(project.getPca_cols());
    }

    @FXML
    void handlePcaEntryOK(ActionEvent event) throws IOException {
        
        okClicked = true;
        Genesis.getPrimaryStage().close();
        
        FXMLLoader  loader =  new FXMLLoader(Genesis.class.getResource("view/Main.fxml"));
        Parent root = (Parent)loader.load();
        Open0Controller open0Controller = loader.getController();
        
        ObservableList<List<String>> list = FXCollections.observableArrayList(project.plotPoints());
        open0Controller.getlistViewTableTab().setItems(list);

        NumberAxis xAxis = new NumberAxis();
        xAxis.setSide(Side.BOTTOM);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setSide(Side.LEFT);
        
        ScatterChart<Number,Number> sc = new ScatterChart<Number,Number>(xAxis,yAxis);
        
        // setup chart
        xAxis.setLabel("PCA1");
        yAxis.setLabel("PCA2");
        sc.setTitle(project.getPca_proj_name());

        
        XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
        series.setName("ALL");
        for (int i = 0; i < list.size(); i++) { 
            series.getData().add(new XYChart.Data<Number, Number>(Float.parseFloat(list.get(i).get(2)),Float.parseFloat(list.get(i).get(3)))); 
        }

        sc.getData().add(series);
        open0Controller.getAnchorPane().getChildren().add(sc);
        
        
        final double SCALE_DELTA = 1.1;
        final AnchorPane zoomPane = new AnchorPane();

        zoomPane.getChildren().add(sc);
        
        open0Controller.getAnchorPane().getChildren().setAll(zoomPane);
        
        zoomPane.setOnScroll(new EventHandler<ScrollEvent>() {
        @Override public void handle(ScrollEvent event) {
        event.consume();

        if (event.getDeltaY() == 0) {
        return;
        }
//
        double scaleFactor =
        (event.getDeltaY() > 0)
        ? SCALE_DELTA
        : 1/SCALE_DELTA;

        zoomPane.setScaleX(zoomPane.getScaleX() * scaleFactor);
        zoomPane.setScaleY(zoomPane.getScaleY() * scaleFactor);
        }
        });
   
        dialogStage = new Stage();
//        dialogStage.initOwner(Genesis.getPrimaryStage());
//        System.out.println(Genesis.getPrimaryStage());
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Genesis.class.getResource("css/scatterchart.css").toExternalForm());
        dialogStage.setScene(scene);
        dialogStage.show();
        
        closeStage(event);
    }
    
    
    @FXML
    void handlePcaEntryCancel(ActionEvent event) {
        System.out.println("Cancel");
        closeStage(event);
    }

    public void closeStage(ActionEvent event){
    Node source = (Node)event.getSource();
    Stage stage = (Stage) source.getScene().getWindow();
    stage.close();
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
