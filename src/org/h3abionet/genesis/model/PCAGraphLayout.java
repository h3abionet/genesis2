package org.h3abionet.genesis.model;

import javafx.scene.chart.ScatterChart;

import java.io.Serializable;

public class PCAGraphLayout implements Serializable {

    private static final long serialVersionUID = 2L;

    private String font = "System";
    private String fontStyle = "NORMAL";
    private String fontColor = "000000";
    private String fontPosture = "REGULAR";
    private int fontSize = 12;

    private String graphTitle;
    private String xAxisLabel;
    private String yAxisLabel;

    private boolean showAxes = true;
    private boolean showBorders = false;
    private boolean showAxisLabels = true;
    private boolean showGrid = true;
    private boolean showAxisMarks = true;

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public String getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String getFontPosture() {
        return fontPosture;
    }

    public void setFontPosture(String fontPosture) {
        this.fontPosture = fontPosture;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

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
        sc.getXAxis().lookup(".axis-label").setStyle("-fx-fill: #"+fontColor+";"+
                "-fx-font-size: "+fontSize+"pt;"+
                "-fx-font-weight: "+fontStyle+";"+
                "-fx-font-family: \"" +font+"\";"+
                "-fx-text-fill: #"+fontColor+";" );

        // set x-axis
        sc.getYAxis().lookup(".axis-label").setStyle("-fx-fill: #"+fontColor+";"+
                "-fx-font-size: "+fontSize+"pt;"+
                "-fx-font-weight: "+fontStyle+";"+
                "-fx-font-family: \"" +font+"\";"+
                "-fx-text-fill: #"+fontColor+";" );

        // set title
        sc.lookup(".chart-title").setStyle("-fx-fill: #"+fontColor+";"+
                "-fx-font-size: "+fontSize+"pt;"+
                "-fx-font-weight: "+fontStyle+";"+
                "-fx-font-family: \"" +font+"\";"+
                "-fx-text-fill: #"+fontColor+";");


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
