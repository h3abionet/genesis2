package org.h3abionet.genesis.model;

import java.io.Serializable;

public class Annotation implements Serializable {

    private static final long serialVersionUID = 2L;

    String name;
    int radius;
    int width;
    int height;
    int archWidth;
    int archHeight;
    int strokeWidth;
    String strokeColor;
    String fillColor;
    int length;
    double startX;
    double startY;
    double endX;
    double endY;
    int centerX;
    int centerY;
    String fill;
    String Stroke;
    double rotation;

    public String getName() {
        return name;
    }

    public int getCenterX() {
        return centerX;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
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

    public String getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(String strokeColor) {
        this.strokeColor = strokeColor;
    }

    public String getFillColor() {
        return fillColor;
    }

    public void setFillColor(String fillColor) {
        this.fillColor = fillColor;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
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
}
