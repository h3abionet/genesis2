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
package org.h3abionet.genesis;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * This is the main class of the program
 * @author scott
 */
public class Genesis extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        
        shutdownProgram(stage);
        
        Parent root = FXMLLoader.load(getClass().getResource("view/Main.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Genesis.class.getResource("css/pca.css").toExternalForm());

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.H) {
                System.out.println("Please help");
            }
        });
        
        // Press F to enter full screen mode or E to exit full screen mode
        stage.addEventFilter(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (event.getCode() == KeyCode.F) {
                boolean newValue = !stage.isFullScreen();
                stage.setAlwaysOnTop(newValue);
                stage.setFullScreenExitKeyCombination(new KeyCodeCombination(KeyCode.E));
                stage.setFullScreen(newValue);
            }
        });
        
        stage.setScene(scene);
        stage.show();
    }
    
    /**
     * This displays a dialog for closing the program
     * @param mainStage The stage to be closed
     */
    private void shutdownProgram(Stage mainStage) {
        Alert alert = new Alert(Alert.AlertType.NONE, "Do you want to close the program?", ButtonType.YES, ButtonType.NO);
        mainStage.setOnCloseRequest(evt -> {
            if (alert.showAndWait().get() == ButtonType.YES){
            mainStage.close();
            }else{
            evt.consume();
            }
        });
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
