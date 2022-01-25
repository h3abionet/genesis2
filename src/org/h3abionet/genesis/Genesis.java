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

import java.io.*;
import java.util.Optional;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * This is the main class of the program
 * @author scott
 */
public class Genesis extends Application {

    private static String previouslyOpenedPath;

    @Override
    public void start(Stage stage) throws Exception {
                
        shutdownProgram(stage);
        
        Parent root = FXMLLoader.load(getClass().getResource("view/Main.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Genesis.class.getResource("css/pca.css").toExternalForm());
        
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.H) {
                System.out.println("Please Help");
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
        
        stage.setWidth(1000);
        stage.setHeight(700);
        stage.setMaximized(false);
        stage.setFullScreen(false);
        
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
     * Close the open stage
     * @param event
     */
    public static void closeOpenStage(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
    
    /**
     * user show choose yes or no to confirm action
     * @param contentText
     * @return 
     */
    public static String confirmAction(String contentText){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setContentText(contentText);

        ButtonType yesBtn = new ButtonType("YES");
        ButtonType noBtn = new ButtonType("NO");
        alert.getButtonTypes().setAll(yesBtn, noBtn);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == yesBtn) {
            return "yesBtnPressed";
        } else {
            return "noBtnPressed";
        }        
    }
    
     /**
     *
     * @param name
     * @return
     * @throws FileNotFoundException
     */
    public static BufferedReader openFile(String name) throws FileNotFoundException {
        InputStreamReader is = new InputStreamReader(new FileInputStream(name));
        BufferedReader dinp = new BufferedReader(is);
        return dinp;
    }
    
    /* load view
     * @param fxmlLink -  link for the window to be loaded
     * @throws FileNotFoundException
     */
    public static void loadFxmlView(String fxmlLink) throws IOException{
        FXMLLoader loader = new FXMLLoader(Genesis.class.getResource(fxmlLink));
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene((Parent) loader.load()));
        stage.setResizable(false);
        stage.showAndWait();
    }
    
    // throw error exception dialog box
    public static void throwErrorException(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // throw information dialog box
    public static void throwInformationException(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // throw information dialog box
    public static void throwInformationException(Exception ex){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText("Look, an Exception Dialog");
        alert.setContentText("Could not find file blabla.txt!");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    public static String getPreviouslyOpenedPath() {
        return previouslyOpenedPath;
    }

    public static void setPreviouslyOpenedPath(String previouslyOpenedPath) {
        Genesis.previouslyOpenedPath = previouslyOpenedPath;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
