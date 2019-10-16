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
package org.h3abionet.genesis.controller;

/**
 *
 * @author scott
 */
import org.h3abionet.genesis.model.Project;
import java.io.File;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.Fam;
import org.h3abionet.genesis.model.Pheno;

public class ProjectDetailsController {

    private boolean okClicked = false;
    private static String fam_fname_s = "";
    private static String pheno_fname_s = "";
    private static String proj_name_s = "";
    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private AnchorPane newProjectEntry;
    @FXML
    private Button entryOKButton;
    @FXML
    private Button entryCancelButton;
    @FXML
    private Button fam_fname;
    @FXML
    private Button pheno_fname;
    @FXML
    private TextField proj_name;

    private final Stage dialogStage = new Stage();

    Project project;
    Fam fam;
    Pheno pheno;

    public ProjectDetailsController() {

    }

    /**
     * Sets the stage of this dialog. This function is called in the
     *
     * @throws java.io.IOException
     */
    public void loadProjDialogEntry() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("view/ProjDialogEntry.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        dialogStage.setTitle("Genesis 2.0");
        dialogStage.setScene(new Scene(root));
        dialogStage.setResizable(false);
        dialogStage.show();

    }

    /**
     * Returns true if the user clicked OK, false otherwise.
     *
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleFamFname() {
        entryOKButton.setDisable(false); //disable OK button
        File famFile = getFile("Choose FAM file");
        //if no famFile provided and pheno file, disable OK button, else get file names
        if (famFile == null && pheno_fname_s == "") { 
            System.out.println("no fam");
            entryOKButton.setDisable(true);
        } else {
            if(famFile != null){
            fam_fname_s = famFile.getAbsolutePath();
            fam_fname.setText(famFile.getName());
            fam_fname.setStyle("-fx-text-fill: green");
            }else{
            ;
            }
        }
    }

    @FXML
    private void handlePhenoFname() {
        entryOKButton.setDisable(false);
        File phen = getFile("Choose FAM file");
        if ((phen == null && fam_fname_s == "")) {
            System.out.println("no pheno");
            entryOKButton.setDisable(true);
        } else {
            if(phen != null){
            pheno_fname_s = phen.getAbsolutePath();
            pheno_fname.setText(phen.getName());
            pheno_fname.setStyle("-fx-text-fill: green");
            }else{
                ;
            }
        }
    }

    /**
     * Called when the user clicks ok.
     *
     * @param event
     * @throws java.io.IOException
     */
    @FXML
    private void handlePcaEntryOK(ActionEvent event) throws IOException {
        okClicked = true;
        proj_name_s = proj_name.getText();

        if (!proj_name_s.matches(".*\\w.*")) { //check alphanumerics in the title
            proj_name_s = "PCA chart"; //set project name/Title to default(PCA chart) if not provided.
        }
        //check if files have been provided (can be only one or both), else display an alert message.
        if (fam_fname_s.length() != 0 && pheno_fname_s.length() != 0) {
            fam = new Fam(fam_fname_s); //read the fam using Fam class
            pheno = new Pheno(pheno_fname_s); //read the pheno using the Pheno class
        } else if (pheno_fname_s.length() != 0) {
            pheno = new Pheno(pheno_fname_s);
        } else if (fam_fname_s.length() != 0) {
            fam = new Fam(fam_fname_s);
        } else {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("No files provided");
            alert.showAndWait();
        }

        closeStage(event); //closing the open stage

    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handlePcaEntryCancel(ActionEvent event) {
        closeStage(event);
    }
    
    /**
     * return project name or Tile to display on the chart
     * @return 
     */
    public String getProjectName() {
        return proj_name_s;
    }

    private File getFile(String which) {
        File wanted;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(which);
        wanted = fileChooser.showOpenDialog(dialogStage);
        return wanted;

    }
    
    /**
     * Close the open stage
     * @param event
     */
    public void closeStage(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

}
