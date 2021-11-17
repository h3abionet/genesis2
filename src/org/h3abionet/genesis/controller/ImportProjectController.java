package org.h3abionet.genesis.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.AdmixtureGraph;
import org.h3abionet.genesis.model.PCAGraph;
import org.h3abionet.genesis.model.Project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class ImportProjectController {

    @FXML
    private Button importProjBtn;

    @FXML
    private Button cancelBtn;

    @FXML
    private Button doneBtn;

    private MainController mainController;
    private String importedProjFile;
    File projFile;
    Project proj = null;

    @FXML
    private void importProject(ActionEvent event) {
        projFile = getFile("Choose project file");
        if(projFile != null){
            importedProjFile = projFile.getAbsolutePath();
            importProjBtn.setText(projFile.getName());
            importProjBtn.setStyle("-fx-text-fill: #06587F");
            doneBtn.setDisable(false);
        }else{
            Genesis.throwInformationException("No project selected");
        }
    }

    @FXML
    private void Cancel(ActionEvent event) {
        Genesis.closeOpenStage(event);
    }

    @FXML
    void Done(ActionEvent event) throws IOException, ClassNotFoundException, InterruptedException {
        try {
            FileInputStream fileIn = new FileInputStream(projFile);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            proj = (Project) in.readObject();
            mainController.setProject(proj);
            in.close();
            fileIn.close();
            proj.setProjIsImported(true);
            readGraphs();
            mainController.disableNewProjBtn(true);
            mainController.disableControlBtns(false);
            mainController.disablePcaBtn(false);
            mainController.disableAdmixtureBtn(false);
            mainController.disableDataBtn(false);

        } catch (IOException i) {
            Genesis.throwErrorException("Failed to import the project");
        } catch (ClassNotFoundException c) {
            Genesis.throwErrorException("Project class not found");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Genesis.closeOpenStage(event);
    }

    /**
     *
     * @param which This is the title of the dialog box
     * @return File object
     */
    private File getFile(String which) {
        File wanted;
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Genesis file", "*.g2f");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle(which);
        Stage stage = new Stage();
        wanted = fileChooser.showOpenDialog(stage);
        return wanted;
    }

    public void setMainController(MainController mainCtrl) {
        this.mainController = mainCtrl;
    }

    public void disableDoneBtn(boolean b) {
        doneBtn.setDisable(b);
    }

    public void readGraphs() throws IOException, InterruptedException {

        if(proj.getPcaGraph()!=null) {

            // call saved pcaGraph object from projects
            PCAGraph pcaGraph = proj.getPcaGraph();
            pcaGraph.setProject(proj);
            pcaGraph.setMainController(mainController);
            mainController.setPcaGraph(pcaGraph);

            // remove genesis logo from every tabPane
            mainController.setTabPaneStyle();

            // get arrayList of subjects for every pc graph
            for (int listIndex = 0; listIndex < proj.getPcGraphSubjectsList().size(); listIndex++) {
                // get list of selected pc columns for every pc graph
                int pcIndex[] = proj.getSelectedPCs().get(listIndex); // [[1,2], [4,10], ...]
                int firstPC = pcIndex[0]; // e.g 1 - x column index
                int secondPC = pcIndex[1]; // e,g 2 - y column index

                // set every pca graph on a new tab. recreateGraph - returns a graph given the x,y pc columns
                mainController.setPCAChart(pcaGraph.recreatePcaGraph(firstPC, secondPC, listIndex));
            }
        }

        if(proj.getAdmixtureGraph()!=null) {

            for (int i = 0; i < proj.getImportedKs().size(); i++) {
                //  call saved admixture object from projects
                AdmixtureGraph admixtureGraph = proj.getAdmixtureGraph();
                admixtureGraph.setProject(proj);
                admixtureGraph.setChartIndex(i);
                admixtureGraph.setMainController(mainController);
                mainController.setAdmixtureGraph(admixtureGraph);

                // remove genesis logo from every tabPane
                mainController.setTabPaneStyle();

                int kValue = proj.getImportedKs().get(i);

                admixtureGraph.setNumOfAncestries(kValue);

                admixtureGraph.setAncestryLabels(kValue);

                admixtureGraph.createAdmixGraph();
                mainController.setAdmixtureChart(admixtureGraph.getListOfStackedBarCharts());

            }
        }

    }

}