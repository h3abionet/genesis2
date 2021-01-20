package org.h3abionet.genesis.model;

public class Subject implements java.io.Serializable{
    // phenotype fields
    private String fid;
    private String iid;
    private String phenotypeA;
    private String phenotypeB;

    // fam fields
    private String pat;
    private String mat;
    private String sex; // 1=male; 2=female; other=unknown
    private String phen;

    // other properties
    private String color;
    private String icon;
    private boolean hidden;
    private String[] pcs;
    private Float hiddenXValue;
    private Float hiddenYValue;

    // constructor for pheno file - mostly provided
    public Subject(String fid, String iid, String phenotypeA, String phenotypeB, String color, String icon, boolean isHidden) {
        this.fid = fid;
        this.iid = iid;
        this.phenotypeA = phenotypeA;
        this.phenotypeB = phenotypeB;
        this.color = color;
        this.icon = icon;
        this.hidden = isHidden;
    }

    // if fam file is provided, set the following fields
    public void setPat(String pat) {
        this.pat = pat;
    }

    public void setMat(String mat) {
        this.mat = mat;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setPhen(String phen) {
        this.phen = phen;
    }

    // getters for properties
    public String getFid() {
        return fid;
    }

    public String getIid() {
        return iid;
    }

    public String getPhenotypeA() {
        return phenotypeA;
    }

    public String getPhenotypeB() {
        return phenotypeB;
    }

    public String getColor() {
        return color;
    }

    public String getIcon() {
        return icon;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
    public boolean isHidden() {
        return hidden;
    }

    public Float getHiddenXValue() {
        return hiddenXValue;
    }

    public void setHiddenXValue(Float hiddenXValue) {
        this.hiddenXValue = hiddenXValue;
    }

    public Float getHiddenYValue() {
        return hiddenYValue;
    }

    public void setHiddenYValue(Float hiddenYValue) {
        this.hiddenYValue = hiddenYValue;
    }

    public String[] getPcs() {
        return pcs;
    }

    public void setPcs(String[] pcs) {
        this.pcs = pcs;
    }

}
