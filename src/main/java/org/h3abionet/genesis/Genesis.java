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
import javafx.scene.Group;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.h3abionet.genesis.controller.MainController;

/**
 * This is the main class of the program
 * @author scott
 * Code for start allowing rescaling from Jason Winnebeck
 *   https://gillius.org/blog/2013/02/javafx-window-scaling-on-resize.html
 * copyright © 1998-2020 by Jason Winnebeck
 */
public class Genesis extends Application {

    private static String previouslyOpenedPath;
    private static Stage mainStage;

    @Override
    public void start( Stage stage ) throws Exception {
        mainStage = stage;
        shutdownProgram(stage);

        String mainfile = "view/Main.fxml", // view/
                cssfile="css/pca.css"; // css/

        FXMLLoader loader = new FXMLLoader( getClass().getResource( mainfile ) );
	AnchorPane contentRootRegion = (AnchorPane) loader.load();

	//Set a default "standard" or "100%" resolution
	double origW = 1000;
	double origH = 700;

	//If the Region containing the GUI does not already have a preferred width and height, set it.
	//But, if it does, we can use that setting as the "standard" resolution.
	if ( contentRootRegion.getPrefWidth() == Region.USE_COMPUTED_SIZE ) {
            System.out.println("using preset width="+origW);
        } else {
		origW = contentRootRegion.getPrefWidth();
        }

	if ( contentRootRegion.getPrefHeight() == Region.USE_COMPUTED_SIZE )
		contentRootRegion.setPrefHeight( origH );
	else
		origH = contentRootRegion.getPrefHeight();
        
        double aspectRatio = origW/origH;

	//Wrap the resizable content in a non-resizable container (Group)
	Group group = new Group( contentRootRegion );
	//Place the Group in a StackPane, which will keep it centered
	StackPane rootPane = new StackPane();
	rootPane.getChildren().add( group );

	stage.setTitle( "Genesis 2" );
	//Create the scene initally at the "100%" size
	Scene scene = new Scene( rootPane, origW, origH );
        scene.getStylesheets().add(Genesis.class.getResource(cssfile).toExternalForm());

        stage.addEventFilter(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (event.getCode() == KeyCode.F) {
                boolean newValue = !stage.isFullScreen();
                stage.setAlwaysOnTop(newValue);
                stage.setFullScreenExitKeyCombination(new KeyCodeCombination(KeyCode.E));
                stage.setFullScreen(newValue);
            }
        });

        
        //Bind the scene's width and height to the scaling parameters on the group
	group.scaleXProperty().bind( scene.widthProperty().divide( origW ) );
	group.scaleYProperty().bind( scene.heightProperty().divide( origH ) );


        stage.setMaximized(false);
        stage.setFullScreen(false);

	//Set the scene to the window (stage) and show it
	stage.setScene( scene );
	stage.show();
}
    
    public void start_old(Stage stage) throws Exception {
        mainStage = stage;
        shutdownProgram(stage);
        
        String mainfile = "view/Main.fxml", // view/
                cssfile="css/pca.css"; // css/

        Parent root = FXMLLoader.load(getClass().getResource(mainfile));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Genesis.class.getResource(cssfile).toExternalForm());
        
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

    public static Stage getMainStage() {
        return mainStage;
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
