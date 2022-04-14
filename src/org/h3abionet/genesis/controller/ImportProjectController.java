package org.h3abionet.genesis.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.*;

import java.io.*;
import java.util.ArrayList;

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
            mainController.setProjIsImported(true);
            mainController.disableNewProjBtn(true);
            mainController.disableControlBtns(false);
            mainController.disablePcaBtn(false);
            mainController.disableAdmixtureBtn(false);

        } catch (IOException i) {
            Genesis.throwErrorException(i.toString());
        } catch (ClassNotFoundException c) {
            Genesis.throwErrorException(c.toString());
        } catch (InterruptedException e) {
            Genesis.throwErrorException(e.toString());
//            e.printStackTrace();
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

            // add annotations
            for (int annoIndex = 0; annoIndex < proj.getPcGraphAnnotationsList().size(); annoIndex++) {
                ArrayList<Annotation> annotations = proj.getPcGraphAnnotationsList().get(annoIndex);
                for(Annotation an : annotations ){
                    switch(an.getName()) {
                        case "line":
                            mainController.recreateLine(an,annoIndex,"pca");
                            break;
                        case "circle":
                            mainController.recreateCircle(an,annoIndex,"pca");
                            break;
                        case "arrow":
                            mainController.recreateArrow(an,annoIndex,"pca");
                        case "rectangle":
                            mainController.recreateRectangle(an,annoIndex,"pca");
                            break;
                        case "text":
                            mainController.recreateText(an,annoIndex,"pca");
                            break;
                        default:
                            return; // nothing
                    }
                }
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

            // add annotations
            for(Annotation an : proj.getAdmixtureAnnotationsList()){
                switch(an.getName()) {
                    case "line":
                        mainController.recreateLine(an,0,"admixture");
                        break;
                    case "circle":
                        mainController.recreateCircle(an,0,"admixture");
                        break;
                    case "arrow":
                        mainController.recreateArrow(an,0,"admixture");
                        break;
                    case "rectangle":
                        mainController.recreateRectangle(an,0,"admixture");
                        break;
                    case "text":
                        mainController.recreateText(an,0,"admixture");
                        break;
                    default:
                        return; // nothing
                }
            }
        }
    }

}