/*
 * Copyright 2019 University of the Witwatersrand, Johannesburg on behalf of the Pan-African Bioinformatics Network for H3Africa.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.h3abionet.genesis.controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.h3abionet.genesis.model.Project;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;


/**
 * @author Scott Hazelhurst
 */
public class Open0Controller implements Initializable {


    @FXML
    MenuBar menuBar;

    @FXML
    MenuItem newpca;

    @FXML
    AnchorPane projectAnchor;

    private Project project;

    private VBox projectVBox(ProjectDetailsController control) {
        String proj_name = control.getProjectName();
        String fam_name = control.getFam();
        String pheno_name = control.getPheno();
        VBox v = new VBox();
        HBox h = new HBox(10);
        Label lab = new Label();
        lab.setText("Project: " + proj_name);
        Label fam = new Label();
        fam.setText(" FAM file: " + fam_name);
        Label pheno = new Label();
        if (pheno_name.length() == 0) pheno_name = "None";
        pheno.setText(" Pheno file: " + pheno_name);
        h.getChildren().addAll(lab, fam, pheno);
        h.setPadding(new Insets(10, 10, 10, 10));
        v.getChildren().add(h);
        return v;
    }

    @FXML
    private void newProject(ActionEvent event) throws IOException {
        ProjectDetailsController controller;
        Stage dialogStage = new Stage();
        dialogStage.setTitle("New Project details");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        Window primaryStage = menuBar.getScene().getWindow();
        dialogStage.initOwner(primaryStage);
        controller = new ProjectDetailsController("view/ProjDialogEntry.fxml");
        Scene scene = new Scene(controller);
        dialogStage.setScene(scene);
        controller.setDialogStage(dialogStage);
        dialogStage.showAndWait();
        newpca.setDisable(false);
        projectAnchor.setVisible(true);
        projectAnchor.getChildren().add(projectVBox(controller));
        project = controller.getProject();

    }

    @FXML
    private void newPCA(ActionEvent event) throws IOException {
        Window primaryStage = menuBar.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(primaryStage);
        project.addPCA(file);

    }


    @FXML
    private void handleKeyInput(final InputEvent event) {
        if (event instanceof KeyEvent) {
            final KeyEvent keyEvent = (KeyEvent) event;
            if (keyEvent.isControlDown() && (keyEvent.getKeyCode() == KeyEvent.VK_H)) {
                provideAboutFunctionality();
            }
        }
    }


    private void provideAboutFunctionality() {
        System.out.println("I need help!");
    }

    @Override
    public void initialize(java.net.URL arg0, ResourceBundle arg1) {
        menuBar.setFocusTraversable(true);

    }

}
