package org.h3abionet.genesis.model;

import javafx.scene.chart.ScatterChart;

import java.io.Serializable;

public class PCAGraphLayout implements Serializable {

    private static final long serialVersionUID = 2L;

    // axes
    private String axesFont = "Helvetica";
    private String axesFontStyle = "NORMAL";
    private String axesFontColor = "000000";
    private String axesPosture = "REGULAR";
    private double axesFontSize = 13;

    // heading styles
    private String headingFont = "Helvetica";
    private String headingFontStyle = "NORMAL";
    private String headingFontColor = "000000";
    private String headingPosture = "REGULAR";
    private double headingFontSize = 13;

    private String graphTitle;
    private String xAxisLabel;
    private String yAxisLabel;

    private boolean showAxes = true;
    private boolean showBorders = false;
    private boolean showAxisLabels = true;
    private boolean showGrid = true;
    private boolean showAxisMarks = true;

    public String getAxesFont() {
        return axesFont;
    }

    public void setAxesFont(String axesFont) {
        this.axesFont = axesFont;
    }

    public String getAxesFontStyle() {
        return axesFontStyle;
    }

    public void setAxesFontStyle(String axesFontStyle) {
        this.axesFontStyle = axesFontStyle;
    }

    public String getAxesFontColor() {
        return axesFontColor;
    }

    public void setAxesFontColor(String axesFontColor) {
        this.axesFontColor = axesFontColor;
    }

    public String getAxesPosture() {
        return axesPosture;
    }

    public void setAxesPosture(String axesPosture) {
        this.axesPosture = axesPosture;
    }

    public double getAxesFontSize() {
        return axesFontSize;
    }

    public void setAxesFontSize(double axesFontSize) {
        this.axesFontSize = axesFontSize;
    }

    public String getHeadingFont() {
        return headingFont;
    }

    public void setHeadingFont(String headingFont) {
        this.headingFont = headingFont;
    }

    public String getHeadingFontStyle() {
        return headingFontStyle;
    }

    public void setHeadingFontStyle(String headingFontStyle) {
        this.headingFontStyle = headingFontStyle;
    }

    public String getHeadingFontColor() {
        return headingFontColor;
    }

    public void setHeadingFontColor(String headingFontColor) {
        this.headingFontColor = headingFontColor;
    }

    public String getHeadingPosture() {
        return headingPosture;
    }

    public void setHeadingPosture(String headingPosture) {
        this.headingPosture = headingPosture;
    }

    public double getHeadingFontSize() {
        return headingFontSize;
    }

    public void setHeadingFontSize(double headingFontSize) {
        this.headingFontSize = headingFontSize;
    }

    //    public String getFont() {
//        return font;
//    }
//
//    public void setFont(String font) {
//        this.font = font;
//    }
//
//    public String getFontStyle() {
//        return fontStyle;
//    }
//
//    public void setFontStyle(String fontStyle) {
//        this.fontStyle = fontStyle;
//    }
//
//    public String getFontColor() {
//        return fontColor;
//    }
//
//    public void setFontColor(String fontColor) {
//        this.fontColor = fontColor;
//    }
//
//    public String getFontPosture() {
//        return fontPosture;
//    }
//
//    public void setFontPosture(String fontPosture) {
//        this.fontPosture = fontPosture;
//    }
//
//    public double getFontSize() {
//        return fontSize;
//    }
//
//    public void setFontSize(double fontSize) {
//        this.fontSize = fontSize;
//    }

    public String getGraphTitle() {
        return graphTitle;
    }

    public void setGraphTitle(String graphTitle) {
        this.graphTitle = graphTitle;
    }

    public String getxAxisLabel() {
        return xAxisLabel;
    }

    public void setxAxisLabel(String xAxisLabel) {
        this.xAxisLabel = xAxisLabel;
    }

    public String getyAxisLabel() {
        return yAxisLabel;
    }

    public void setyAxisLabel(String yAxisLabel) {
        this.yAxisLabel = yAxisLabel;
    }

    public boolean isShowAxes() {
        return showAxes;
    }

    public void setShowAxes(boolean showAxes) {
        this.showAxes = showAxes;
    }

    public boolean isShowBorders() {
        return showBorders;
    }

    public void setShowBorders(boolean showBorders) {
        this.showBorders = showBorders;
    }

    public boolean isShowAxisLabels() {
        return showAxisLabels;
    }

    public void setShowAxisLabels(boolean showAxisLabels) {
        this.showAxisLabels = showAxisLabels;
    }

    public boolean isShowGrid() {
        return showGrid;
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }

    public boolean isShowAxisMarks() {
        return showAxisMarks;
    }

    public void setShowAxisMarks(boolean showAxisMarks) {
        this.showAxisMarks = showAxisMarks;
    }

    public void setGraphProperties(ScatterChart<Number, Number> sc) {
        // set x-axis
        sc.getXAxis().lookup(".axis-label").setStyle("-fx-fill: #"+axesFontColor+";"+
                "-fx-font-size: "+axesFontSize+"pt;"+
                "-fx-font-weight: "+axesFontStyle+";"+
                "-fx-font-family: \"" +axesFont+"\";"+
                "-fx-text-fill: #"+axesFontColor+";" );

        // set x-axis
        sc.getYAxis().lookup(".axis-label").setStyle("-fx-fill: #"+axesFontColor+";"+
                "-fx-font-size: "+axesFontSize+"pt;"+
                "-fx-font-weight: "+axesFontStyle+";"+
                "-fx-font-family: \"" +axesFont+"\";"+
                "-fx-text-fill: #"+axesFontColor+";" );

        // set title
        sc.lookup(".chart-title").setStyle("-fx-fill: #"+headingFontColor+";"+
                "-fx-font-size: "+headingFontSize+"pt;"+
                "-fx-font-weight: "+headingFontStyle+";"+
                "-fx-font-family: \"" +headingFont+"\";"+
                "-fx-text-fill: #"+headingFontColor+";");


        if(!showAxes){
            sc.lookup(".chart-vertical-zero-line").setStyle("-fx-stroke: transparent;");
            sc.lookup(".chart-horizontal-zero-line").setStyle("-fx-stroke: transparent;");
        }

        if(!showAxisLabels){
            sc.getXAxis().lookup(".axis-label").setVisible(false);
            sc.getYAxis().lookup(".axis-label").setVisible(false);
        }

        if(!showAxisMarks){
            sc.getXAxis().setTickLabelsVisible(false);
            sc.getYAxis().setTickLabelsVisible(false);
            sc.getXAxis().setTickMarkVisible(false);
            sc.getYAxis().setTickMarkVisible(false);
        }

        // these are hard coded values -- should be changed
        if(showBorders){
            sc.lookup(".chart-plot-background").setStyle("-fx-border-color: #918f8e;"+
                    "-fx-border-style: solid;"+
                    "-fx-border-width: 2px;"+
                    "-fx-border-insets: -2px;");
        }

        if(!showGrid){
            sc.lookup(".chart-vertical-grid-lines").setStyle(
                    "-fx-stroke: transparent;"
            );

            sc.lookup(".chart-horizontal-grid-lines").setStyle(
                    "-fx-stroke: transparent;"
            );
        }

    }
}
