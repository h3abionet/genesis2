package org.h3abionet.genesis.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.h3abionet.genesis.Genesis;
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
        }else{
            Genesis.throwInformationException("No project selected");
        }

    }

    @FXML
    private void Cancel(ActionEvent event) {
        Genesis.closeOpenStage(event);
    }

    @FXML
    void Done(ActionEvent event) {
        try {
            FileInputStream fileIn = new FileInputStream(projFile);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            proj = (Project) in.readObject();
            in.close();
            fileIn.close();
            System.out.println("Project successfully imported");
        } catch (IOException i) {
            Genesis.throwErrorException("Failed to import the project");
        } catch (ClassNotFoundException c) {
            Genesis.throwErrorException("Project class not found");
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
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("project files", "*.gen");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle(which);
        Stage stage = new Stage();
        wanted = fileChooser.showOpenDialog(stage);
        return wanted;
    }
}