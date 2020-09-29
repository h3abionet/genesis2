module genesis2 {
    exports  org.h3abionet.genesis;
    exports org.h3abionet.genesis.controller ;
    opens org.h3abionet.genesis to javafx.fxml;
    opens org.h3abionet.genesis.controller to javafx.fxml;
    requires fontawesomefx;
    requires javafx.swt;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;
    requires javafx.swing;
    requires javafx.web;
    requires commons.logging;
    requires jcommon;
    requires OpenViewerFX;
    requires org.apache.pdfbox;
}