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
    double pivotX;
    double pivotY;
    double centerX;
    double centerY;
    String fill;
    double rotation = 0;
    String text;
    private int fontSize;
    private String fontFamily;
    private String fontWeight;
    double layoutX;
    double layoutY;
    double translateX;
    double translateY;
    
    public Annotation (Annotation original) {
        if (original != null) {
            name = original.name;
            radius = original.radius;
            width = original.width;
            height = original.height;
            arcWidth = original.arcWidth;
            arcHeight = original.arcHeight;
            strokeWidth = original.strokeWidth;
            strokeColor = original.strokeColor;
            fillColor = original.fillColor;
            length = original.length;
            startX = original.startX;
            startY = original.startY;
            endX = original.endX;
            endY = original.endY;
            pivotX = original.pivotX;
            pivotY = original.pivotY;
            centerX = original.centerX;
            centerY = original.centerY;
            fill = original.fill;
            rotation = original.rotation;
            text = original.text;
            fontSize = original.fontSize;
            fontFamily = original.fontFamily;
            fontWeight = original.fontWeight;
            layoutX = original.layoutX;
            layoutY = original.layoutY;
        }
    }
    
    // default for no initial values
    public Annotation () {
    
    }

    public void debugPrint () {
        System.out.print("start x = "+getStartX()+" start y = "+ getStartY() +
                "end x = " + getEndX() + "end y =" + getEndY());
    }
    
    public void debugPrintTranslate () {
        System.out.print("translate x = "+getTranslateX()+" y = "+ getTranslateY());
    }


    public String getName() {
        return name;
    }

    public double getCenterX() {
        return centerX;
    }

    public double getRotation() {
        return rotation;
    }
    
    public double getPivotX () {
        return pivotX;
    }
    
    public double getPivotY () {
        return pivotY;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public void setPivotX (double pivotX) {
        this.pivotX = pivotX;
    }
    
    public void setPivotY (double pivotY) {
        this.pivotY = pivotY;
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

    public double getLayoutX() {
        return layoutX;
    }

    public void setLayoutX(double layoutX) {
        this.layoutX = layoutX;
    }

    public double getLayoutY() {
        return layoutY;
    }

    public void setLayoutY(double layoutY) {
        this.layoutY = layoutY;
    }
    
    public void setTranslateX (double newTranslateX) {
        translateX = newTranslateX;
    }

    public double getTranslateX () {
        return translateX;
    }
    
    public void setTranslateY (double newTranslateY) {
        translateY = newTranslateY;
    }
    
    public double getTranslateY () {
        return translateY;
    }
}
