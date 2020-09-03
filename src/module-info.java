/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

module Genesis {
    exports  org.h3abionet.genesis;
    exports org.h3abionet.genesis.controller ;
    opens org.h3abionet.genesis to javafx.fxml;
    opens org.h3abionet.genesis.controller to javafx.fxml;
    requires javafx.swt;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;
    requires javafx.swing;
    requires javafx.web;
    requires fontawesomefx;
    requires jfxtras.controls;
    requires jfxtras.labs;
    requires pdfbox.app;
    requires jfxtras.common;
    requires jfxtras.fxml;
}
