/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.PCAGraph;
import org.h3abionet.genesis.model.Project;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author Henry
 */
public class PCADataInputController implements Initializable {

    private PCAGraph pcaGraph;
    private MainController mainController;

    private String pcaFilePath = "";
    private String pcaFileName = "";

    private String pcaComboButton1Value = "";
    private String pcaComboButton2Value = "";
    private boolean firstPcaSuccessful = false;

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
    private Project project;

    public void setButtons(){
        pcaEvecFileBtn.setText(project.getPcaFileName());
        pcaEvecFileBtn.setStyle("-fx-text-fill: #06587F");
        pcaComboButton1.setItems(pcaGraph.getPcaColumnLabels());
        pcaComboButton2.setItems(pcaGraph.getPcaColumnLabels());

        // show current PCAs being displayed - user changes them
        pcaComboButton1.setValue(pcaGraph.getPcaColumnLabels().get(0));
        pcaComboButton2.setValue(pcaGraph.getPcaColumnLabels().get(1));

        // set the default values of the pca combo box buttons
        pcaComboButton1Value = pcaGraph.getPcaColumnLabels().get(0);
        pcaComboButton2Value = pcaGraph.getPcaColumnLabels().get(1);
    }

    /**
     * Read the pcaFile file using the PCA
     *
     * @param event
     */
    @FXML
    private void handlePCAEvecFileBtn(ActionEvent event) throws IOException {
        File pcaFile = getFile("Choose PCA file");
        try{
            pcaFilePath = pcaFile.getAbsolutePath();
            pcaFileName = pcaFile.getName();
            project.setPcaFileName(pcaFileName);
            pcaEvecFileBtn.setText(project.getPcaFileName());
            pcaEvecFileBtn.setStyle("-fx-text-fill: #06587F");

            pcaGraph = new PCAGraph(pcaFilePath);
            pcaGraph.setMainController(mainController);
            mainController.setPcaGraph(pcaGraph); // set pca graph in the main controller

            pcaComboButton1.setItems(pcaGraph.getPcaColumnLabels());
            pcaComboButton2.setItems(pcaGraph.getPcaColumnLabels());

            // set default pcas
            pcaComboButton1.setValue(pcaGraph.getPcaColumnLabels().get(0)); // set to PCA1
            pcaComboButton2.setValue(pcaGraph.getPcaColumnLabels().get(1)); // set to PCA2

            entryOKButton.setDisable(false);

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
                // extract pca column index from the combo selection e.g "PCA 1" to 1
                int pcaX = Integer.parseInt(pcaComboButton1Value.substring(4)); // e.g  1
                int pcaY = Integer.parseInt(pcaComboButton2Value.substring(4)); // e.g  2
                // create the chart
                pcaGraph.createGraph(pcaX, pcaY);
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
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("pca file", "*.eigenvec", "*.evec");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle(which);

        if(Genesis.getPreviouslyOpenedPath()==null){
            ;
        }else{
            File validator = new File(Genesis.getPreviouslyOpenedPath());
            if (validator.exists() && validator.isDirectory())
                fileChooser.setInitialDirectory(validator);
        }

        wanted = fileChooser.showOpenDialog(new Stage());

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

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public boolean isFirstPcaSuccessful() {
        return firstPcaSuccessful;
    }

    void enableOK() {
        entryOKButton.setDisable(false);
    }

    public void setPcaGraph(PCAGraph pcaGraph) {
        this.pcaGraph = pcaGraph;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        entryOKButton.setDisable(true);
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
