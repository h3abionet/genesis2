package org.h3abionet.genesis.model;

import java.util.ArrayList;

public class Subject implements java.io.Serializable{

    private static final long serialVersionUID = 2L;

    // phenotype fields
    private String fid;
    private String iid;

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
    private String[] phenos = null;
    private int iconSize;
    private ArrayList<String[]> qValuesList = new ArrayList<>(); // {{1,2}, {1,2, 3}, ...}

    /**
     * constructor for pheno file - mostly provided
     * for every subject, set their default color, icon, visibility, icon size and associated phenotype details
     * @param fid
     * @param iid
     * @param color
     * @param icon
     * @param iconSize
     * @param isHidden
     */
    public Subject(String fid, String iid, String pat, String mat, String sex, String phen, String color, String icon, int iconSize, boolean isHidden) {
        this.fid = fid;
        this.iid = iid;
        this.pat = pat;
        this.mat = mat;
        this.sex = sex;
        this.phen = phen;
        this.color = color;
        this.icon = icon;
        this.hidden = isHidden;
        this.iconSize = iconSize;
    }

    public void setQs(String [] qValues ){
        qValuesList.add(qValues);
    }

    public ArrayList<String[]> getqValuesList() {
        return qValuesList;
    }

    public void setPhenos(String[] phenos) {
        this.phenos = phenos;
    }

    public String[] getPhenos() {
        return phenos;
    }

    public String getSex() {
        if(sex!=null){
            if(sex.equals("1")){
                return "Male";
            }else if(sex.equals("2")){
                return "Female";
            }else{
                return "Unkown Sex";
            }
        }else{
            return "Unknown Sex";
        }
    }

    // getters for subject properties
    public String getFid() {
        return fid;
    }

    public String getIid() {
        return iid;
    }

    public String getColor() {
        return color;
    }

    public String getIcon() {
        return icon;
    }

    // called when default subject color/icon is being changed
    public void setColor(String color) {
        this.color = color;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    // hide or show subject methods
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isHidden() {
        return hidden;
    }

    // return all pcs for the subject
    public String[] getPcs() {
        return pcs;
    }

    // set all pcs for the subject
    public void setPcs(String[] pcs) {
        this.pcs = pcs;
    }

    public int getIconSize() {
        return iconSize;
    }

    public void setIconSize(int iconSize) {
        this.iconSize = iconSize;
    }
}
