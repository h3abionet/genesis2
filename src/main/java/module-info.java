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
    exports org.h3abionet.genesis;
    exports org.h3abionet.genesis.controller;
}
