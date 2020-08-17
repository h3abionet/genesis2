/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import java.io.File;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.PCAGraph;

/**
 * FXML Controller class
 *
 * @author Henry
 */
public class PCADataInputController {

    private static Stage dialogStage;
    private static PCAGraph pcaGraph;

    private static String pcaFilePath = "";
    private static String pcaFileName = "";

    private static String pcaComboButton1Value = "";
    private static String pcaComboButton2Value = "";
    public static ScatterChart<Number, Number> pcaChart;
    public static boolean firstPcaSuccessful = false;

    @FXML
    private Button pcaEvecFileBtn;

    @FXML
    private ComboBox<String> pcaComboButton1;

    @FXML
    private ComboBox<String> pcaComboButton2;

    @FXML
    private Button entryOKButton;

    @FXML
    private Button entryCancelButton;

    /**
     * presents a dialog box with a stored PCA file
     *
     * @throws IOException
     */
    public static void launchPCADataInputView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("view/PCADataInput.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        PCADataInputController controller = (PCADataInputController) fxmlLoader.getController();
        dialogStage = new Stage();
        dialogStage.setScene(new Scene(root));
        dialogStage.setResizable(false);

        controller.pcaEvecFileBtn.setText(pcaFileName);
        controller.pcaEvecFileBtn.setStyle("-fx-text-fill: #06587F");
        controller.pcaComboButton1.setItems(pcaGraph.getPCAcolumns());
        controller.pcaComboButton2.setItems(pcaGraph.getPCAcolumns());

        // show current PCAs being displayed - user changes them
        controller.pcaComboButton1.setValue(pcaComboButton1Value);
        controller.pcaComboButton2.setValue(pcaComboButton2Value);

        dialogStage.showAndWait();

    }

    /**
     * Read the pcaFile file using the PCA
     *
     * @param event
     * @throws IOException
     */
    @FXML
    private void handlePCAEvecFileBtn(ActionEvent event) throws IOException {
        File pcaFile = getFile("Choose PCA file");
        try{
            pcaFilePath = pcaFile.getAbsolutePath();
            pcaFileName = pcaFile.getName();
            pcaEvecFileBtn.setText(pcaFileName);
            pcaEvecFileBtn.setStyle("-fx-text-fill: #06587F");
            pcaGraph = new PCAGraph(pcaFilePath);
            pcaComboButton1.setItems(pcaGraph.getPCAcolumns());
            pcaComboButton2.setItems(pcaGraph.getPCAcolumns());
        }catch(Exception e){
            Genesis.throwErrorException("No File Imported");
        }
    }

    @FXML
    private void setPcaComboButton1Value(ActionEvent event) {
        pcaComboButton1Value = pcaComboButton1.getValue();
    }

    @FXML
    private void setPcaComboButton2Value(ActionEvent event) {
        pcaComboButton2Value = pcaComboButton2.getValue();
    }

    /**
     * gets selected PCAs and sets the pcaChart to be rendered
     *
     * @param event
     * @throws IOException
     */
    @FXML
    private void handlePcaEntryOK(ActionEvent event) throws IOException {

        if (!pcaComboButton1Value.equals("") && !pcaComboButton2Value.equals("")) {
            if (pcaGraph != null) {
                // set the chart
                pcaChart = pcaGraph.createGraph(pcaComboButton1Value, pcaComboButton2Value);
                firstPcaSuccessful = true;
            }
            Genesis.closeOpenStage(event);

        } else {
            Genesis.throwInformationException("Please import the file or select the PCAs to plot");
            
        }
    }

    /**
     * read evec file with pcas
     * @param which
     * @return
     */
    private File getFile(String which) {
        File wanted;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(which);
        wanted = fileChooser.showOpenDialog(dialogStage);
        return wanted;

    }

    /**
     * Uses the closeStage method to close the stage when Cancel button is
     * pressed.
     *
     * @param event
     */
    @FXML
    void handlePcaEntryCancel(ActionEvent event) {
        Genesis.closeOpenStage(event);
    }

}
