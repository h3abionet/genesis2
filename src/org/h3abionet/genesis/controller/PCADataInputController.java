/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.Fam;
import org.h3abionet.genesis.model.PCAProject;
import org.h3abionet.genesis.model.Pheno;
import org.h3abionet.genesis.model.Project;

/**
 * FXML Controller class
 *
 * @author Henry
 */
public class PCADataInputController implements Initializable {
    
    private Stage dialogStage;
    private static PCADataInputController controller;
    PCAProject pCAproject;
    private static PCAProject pCAproject2;
    Project project;
    Fam fam;
    Pheno pheno;

    private boolean okClicked = false;
    private static String pca_fname_s = "";
    private static String pca_file = "";
    
    private Open0Controller open0Controller;
    private static String pcaComboButton1Value = "";
    private static String pcaComboButton2Value = "";
    private ScatterChart<Number, Number> chart;

    @FXML
    private AnchorPane pcaInputAnchorpane;

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

    public Button getPca_evec_fname() {
        return pca_evec_fname;
    }

    public ComboBox<String> getPcaComboButton1() {
        return pcaComboButton1;
    }

    public ComboBox<String> getPcaComboButton2() {
        return pcaComboButton2;
    }

    public void setChart(ScatterChart<Number, Number> chart) {
        this.chart = chart;
    }
   
    public void setOpen0Controller(Open0Controller open0Controller) {
        this.open0Controller = open0Controller;
    }

    public ScatterChart<Number, Number> getChart() {
        return chart;
    }

    public static PCADataInputController getController() {
        return controller;
    }

    public void setPcaDialogStage() throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("view/PCADataInput.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        controller = (PCADataInputController)fxmlLoader.getController();
        dialogStage = new Stage();
        dialogStage.setScene(new Scene(root));
        dialogStage.setResizable(false);
        
        controller.pca_evec_fname.setText(pca_file);
        controller.pca_evec_fname.setStyle("-fx-text-fill: green");
        pCAproject2 = new PCAProject(pca_fname_s);
        controller.pcaComboButton1.setItems(pCAproject2.getPca_cols());
        controller.pcaComboButton2.setItems(pCAproject2.getPca_cols());

        dialogStage.showAndWait();
        

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
    private void handlePcaEvecFname(ActionEvent event) throws IOException {
        File pca = getFile("Choose PCA file");
        pca_fname_s = pca.getAbsolutePath();
        pca_file = pca.getName();
        pca_evec_fname.setText(pca_file);
        pca_evec_fname.setStyle("-fx-text-fill: green");
        pCAproject = new PCAProject(pca_fname_s);
        pcaComboButton1.setItems(pCAproject.getPca_cols());
        pcaComboButton2.setItems(pCAproject.getPca_cols());

    }

    @FXML
    private void pcaComboButtonOneChanged(ActionEvent event) {
        pcaComboButton1Value = pcaComboButton1.getValue();
    }

    @FXML
    private void pcaComboButtonTwoChanged(ActionEvent event) {
        pcaComboButton2Value = pcaComboButton2.getValue();
    }

    @FXML
    private void handlePcaEntryOK(ActionEvent event) throws IOException {
        okClicked = true;

        if(!pcaComboButton1Value.equals("") && !pcaComboButton2Value.equals("")){
            if(pCAproject != null){
                setChart(pCAproject.getGraph(pcaComboButton1Value, pcaComboButton2Value));
            }else{
                setChart(pCAproject2.getGraph(pcaComboButton1Value, pcaComboButton2Value));
            }     
        closeStage(event);
        }else{
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Please select the PCAs to plot");
            alert.showAndWait();
        }
    }

    @FXML
    void handlePcaEntryCancel(ActionEvent event) {
        System.out.println("Cancel");
        closeStage(event);
    }

    public void closeStage(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

}
