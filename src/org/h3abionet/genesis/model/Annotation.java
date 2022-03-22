package org.h3abionet.genesis.model;

import javafx.scene.paint.Color;

import java.io.Serializable;

public class Annotation implements Serializable {

    private static final long serialVersionUID = 2L;

    String name;
    double radius;
    double width;
    double height;
    double arcWidth;
    double arcHeight;
    double strokeWidth;
    String strokeColor;
    String fillColor;
    double length;
    double startX;
    double startY;
    double endX;
    double endY;
    double centerX;
    double centerY;
    String fill;
    double rotation;
    String text;
    private int fontSize;
    private String fontFamily;
    private String fontWeight;


    public String getName() {
        return name;
    }

    public double getCenterX() {
        return centerX;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public void setCenterX(double centerX) {
        this.centerX = centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    public void setCenterY(double centerY) {
        this.centerY = centerY;
    }

    public String getFill() {
        return fill;
    }

    public void setFill(Color color) {
        this.fill =  String.format( "#%02X%02X%02X",
                (int)( color.getRed() * 255 ),
                (int)( color.getGreen() * 255 ),
                (int)( color.getBlue() * 255 ) );
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getArcWidth() {
        return arcWidth;
    }

    public double getArcHeight() {
        return arcHeight;
    }

    public double getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(double strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public String getStrokeColor() {
        return strokeColor;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getStartX() {
        return startX;
    }

    public void setStartX(double startX) {
        this.startX = startX;
    }

    public double getStartY() {
        return startY;
    }

    public void setStartY(double startY) {
        this.startY = startY;
    }

    public double getEndX() {
        return endX;
    }

    public void setEndX(double endX) {
        this.endX = endX;
    }

    public double getEndY() {
        return endY;
    }

    public void setEndY(double endY) {
        this.endY = endY;
    }

    public void setArcHeight(double archHeight) {
        this.arcHeight = archHeight;
    }

    public void setArcWidth(double archWidth) {
        this.arcWidth = archWidth;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setStrokeColor(Color color )
    {
        this.strokeColor =  String.format( "#%02X%02X%02X",
                (int)( color.getRed() * 255 ),
                (int)( color.getGreen() * 255 ),
                (int)( color.getBlue() * 255 ) );
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getFontSize() {
        return fontSize;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontWeight(String fontWeight) {
        this.fontWeight = fontWeight;
    }

    public String getFontWeight() {
        return fontWeight;
    }
}
