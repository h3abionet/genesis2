package org.h3abionet.genesis.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.PCAGraph;
import org.h3abionet.genesis.model.Project;

import java.io.*;

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
    void Done(ActionEvent event) throws IOException, ClassNotFoundException {
//        try {
            FileInputStream fileIn = new FileInputStream(projFile);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            proj = (Project) in.readObject();
            mainController.setProject(proj);
            in.close();
            fileIn.close();
            readGraphs();

//        } catch (IOException i) {
//            Genesis.throwErrorException("Failed to import the project");
//        } catch (ClassNotFoundException c) {
//            Genesis.throwErrorException("Project class not found");
//        }
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
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Genesis file", "*.ggf");
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

    public void readGraphs() throws IOException {
        // call saved pcaGraph object from projects
        PCAGraph pcaGraph = proj.getPcaGraph();
        // set project
        pcaGraph.setProject(proj);

        // remove genesis logo from every tabPane
        mainController.setTabPaneStyle();

        // get arrayList of subjects for every pc graph
        for(int subjectsListPosition=0; subjectsListPosition<proj.getPcGraphSubjectsList().size(); subjectsListPosition++){

            // get list of selected pc columns for every pc graph
            String pcs[] = proj.getSelectedPCs().get(subjectsListPosition).split("\\s+"); // ["1 2", "4 10", ...]
            String firstPC = pcs[0]; // x column index
            String secondPC = pcs[1]; // y column index

            // set every pca graph on a new tab. recreateGraph - returns a graph given the x,y pc columns
            mainController.setPCAChart(pcaGraph.recreatePcaGraph(firstPC, secondPC, subjectsListPosition));
        }

    }

}