package org.h3abionet.genesis.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author A Salmi
 */
public class AdmixSubject {
    private String name;
    private List<Double> ratios;
    private String[] phenotypeData;

    public AdmixSubject(String[] ratios) {
        this.ratios = new ArrayList<>(ratios.length);
        for (String s : ratios) {
            this.ratios.add(Double.valueOf(s));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Double> getRatios() {
        return ratios;
    }

    public void setRatios(List<Double> ratios) {
        this.ratios = ratios;
    }

    public String[] getPhenotypeData() {
        return phenotypeData;
    }

    public void setPhenotypeData(String[] phenotypeData) {
        this.phenotypeData = phenotypeData;
    }
}
