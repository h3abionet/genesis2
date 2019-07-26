package genesisprototype.controller;


import genesisprototype.GenesisPrototype;
import genesisprototype.model.Project;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

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

/**
 *
 * @author scott
 */
public class Open0Controller implements Initializable {
    
     
    @FXML
    private MenuBar menuBar;
    
    @FXML
    private MenuItem newpca;
    
    @FXML 
    private AnchorPane  projectAnchor;
    
    private Project project;
    
    private VBox projectVBox(ProjectDetailsController control) {
       String proj_name  =  control.getProjectName();
       String fam_name   =  control.getFam();
       String pheno_name =  control.getPheno();
       VBox v = new VBox();
       HBox h = new HBox(10);
       Label lab = new Label();
       lab.setText("Project: "+proj_name);
       Label fam = new Label();
       fam.setText(" FAM file: "+fam_name);
       Label pheno = new Label();
       if (pheno_name.length()==0) pheno_name="None";
       pheno.setText(" Pheno file: "+pheno_name);
       h.getChildren().addAll(lab, fam, pheno);
       h.setPadding(new Insets(10,10,10,10));
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
        System.out.println("Switching to dialog"+file.getCanonicalPath());
        project.addPCA(file);
        
    }
    
    
     

    
    @FXML
    private void handleKeyInput(final InputEvent event) {
       if (event instanceof KeyEvent) {
        final KeyEvent keyEvent = (KeyEvent) event;
        if (keyEvent.isControlDown() && (keyEvent.getKeyCode() == KeyEvent.VK_H)){
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
