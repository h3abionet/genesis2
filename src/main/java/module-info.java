module org.h3abionet.genesis {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop; // to bring in awt
    requires javafx.swing; // javafx.embed.swing
    requires javafx.base;
    requires javafx.graphics;
    requires org.apache.pdfbox;
    requires javafx.web;
//    requires javafx.platformutil;
    // added for runtime binding
    opens org.h3abionet.genesis.controller to javafx.fxml;

    exports org.h3abionet.genesis;

}
