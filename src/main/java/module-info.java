module org.h3abionet.genesis {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop; // to bring in awt
    requires javafx.swing; // javafx.embed.swing
//    requires javafx.webEmpty; //  javafx.scene.web
    requires javafx.base;
    requires javafx.graphics;
    requires jfxtras.labs;
    requires org.apache.pdfbox;
    requires javafx.web;
    // added for runtime binding
    opens org.h3abionet.genesis.controller to javafx.fxml;
    exports org.h3abionet.genesis;
    // put in to try to fix launch crash; stops crash at Main.fxml line 16:
    // Caused by: java.lang.IllegalAccessException: class javafx.fxml.FXMLLoader$ValueElement (in module javafx.fxml) cannot access class org.h3abionet.genesis.controller.MainController (in module org.h3abionet.genesis) because module org.h3abionet.genesis does not export org.h3abionet.genesis.controller to module javafx.fxml
    exports org.h3abionet.genesis.controller;
}
