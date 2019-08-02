package org.h3abionet.genesis.model;

/**
 *
 * @author A Salmi
 */
public class PCASubject {
    // PC values
    private double[] values;
    private String name;
    // Show/hide individual
    private boolean visible;
    // individual's group
    private PCAGroup group;

    public PCASubject(String name, double[] values) {
        this.values = values;
        this.name = name;
        visible = true;
    }

    // setters and getters

    public double[] getValues() {
        return values;
    }

    public void setValues(double[] values) {
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public PCAGroup getGroup() {
        return group;
    }

    public void setGroup(PCAGroup group) {
        this.group = group;
    }
}
