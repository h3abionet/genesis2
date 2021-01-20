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
import java.io.BufferedReader;

import org.h3abionet.genesis.model.Graph;
import org.h3abionet.genesis.model.Project;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.h3abionet.genesis.Genesis;

/**
 * This class loads a data entry window for the project name, fam file and phenotype file
 * @author henry
 */
public class ProjectDetailsController implements Initializable{

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
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
    @FXML
    private ComboBox<String> colWithPhenoComboBox;

    private Project project;
    private MainController mainController;
    private static String fam_fname_s = ""; // fam file absolute path
    private static String pheno_fname_s = ""; // phenotype file absolute path
    private static String proj_name_s = ""; // project name

    @FXML
    private void handleFamFname() {
        File famFile = getFile("Choose FAM file");
        if(famFile != null){
            fam_fname_s = famFile.getAbsolutePath();
            fam_fname.setText(famFile.getName());
            fam_fname.setStyle("-fx-text-fill: #06587F");
        }
    }

    @FXML
    private void handlePhenoFname() throws IOException {
        File phen = getFile("Choose Pheno file");
        if(phen != null){
            pheno_fname_s = phen.getAbsolutePath();
            pheno_fname.setText(phen.getName());
            pheno_fname.setStyle("-fx-text-fill: #06587F");

            // read first line of the pheno file to create column names
            BufferedReader brTest = new BufferedReader(new FileReader(pheno_fname_s));
            String brText = brTest .readLine();
            String[] strArray = brText.split("\\s+");

            // create column names
            String [] phenoColNames = new String[strArray.length];
            for(int i = 0; i< strArray.length; i++){
                int phenoColNumber = i+1;
                phenoColNames[i] = "Column "+phenoColNumber;
            }

            // display seletion for a column with phenotype
            colWithPhenoComboBox.setItems(FXCollections.observableArrayList(phenoColNames));
            colWithPhenoComboBox.setValue("Column 3"); // default
            entryOKButton.setDisable(false); // disable the ok button if the pheno is provided
        }
    }

    /**
     * Called when the user clicks OK.
     * Reads the fam file and phenotype file using the project model classes
     * Displays a dialog box if no file was provided
     * @param event
     * @throws java.io.IOException
     */
    @FXML
    private void handlePcaEntryOK(ActionEvent event) throws IOException {
        proj_name_s = proj_name.getText();
        String colWithPhenoValue = colWithPhenoComboBox.getValue(); // get combox string value e.g. Column 1

        if (!proj_name_s.matches(".*\\w.*")) { //check alphanumerics in the title
            proj_name_s = "Project"; // set project name - this will be changed at runtime (can be a combination of file names)
        }

        //check if files have been provided (can be only one or both), else display an alert message.
        int phenoColumnNumber = Integer.parseInt(colWithPhenoValue.substring(7, colWithPhenoValue.length()));

        if (fam_fname_s.length() != 0 && pheno_fname_s.length() != 0) {
            project = new Project(proj_name_s, fam_fname_s, pheno_fname_s, phenoColumnNumber);
            mainController.setProject(project);

        } else if (pheno_fname_s.length() != 0) {
            project = new Project(proj_name_s, pheno_fname_s, phenoColumnNumber);
            mainController.setProject(project);

        } else {
            Genesis.throwInformationException("No files provided");
        }
        Genesis.closeOpenStage(event);
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handlePcaEntryCancel(ActionEvent event) {
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
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("project files", "*.phe", "*.fam");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle(which);
        Stage stage = new Stage();
        wanted = fileChooser.showOpenDialog(stage);
        return wanted;

    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        entryOKButton.setDisable(true); // disable OK button

    }

}
