package org.h3abionet.genesis.model;

import javafx.scene.paint.Color;

public class Annotation {

    String name;
    int radius;
    int width;
    int height;
    int archWidth;
    int archHeight;
    int strokeWidth;
    Color strokeColor;
    Color fillColor;
    int length;
    int startX;
    int startY;
    int endX;
    int endY;
    int centerX;
    int centerY;
    String fill;
    String Stroke;
    int rotation;

    public String getName() {
        return name;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public void setCenterX(int centerX) {
        this.centerX = centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public void setCenterY(int centerY) {
        this.centerY = centerY;
    }

    public String getFill() {
        return fill;
    }

    public void setFill(String fill) {
        this.fill = fill;
    }

    public String getStroke() {
        return Stroke;
    }

    public void setStroke(String stroke) {
        Stroke = stroke;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getArchWidth() {
        return archWidth;
    }

    public void setArchWidth(int archWidth) {
        this.archWidth = archWidth;
    }

    public int getArchHeight() {
        return archHeight;
    }

    public void setArchHeight(int archHeight) {
        this.archHeight = archHeight;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public Color getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getEndX() {
        return endX;
    }

    public void setEndX(int endX) {
        this.endX = endX;
    }

    public int getEndY() {
        return endY;
    }

    public void setEndY(int endY) {
        this.endY = endY;
    }
}
