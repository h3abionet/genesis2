/*
 * Copyright 2019 University of the Witwatersrand, Johannesburg on behalf of the Pan-African Bioinformatics Network for H3Africa.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.h3abionet.genesis.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;

/**
 *
 * @author scott
 */
public abstract class Graph {

    public List<String> groupNames;
    private Project project;
    HashMap groupColors = new HashMap();
    HashMap groupIcons =  new HashMap();
    private HashMap iconsHashmap;
    public String label;
    public String caption;
    protected int margin[];
    protected int size[];
    protected Map<String, List<String []>> populationGroups; // based on the phenoColumnNumber
    
    protected abstract void readGraphData(String filePath) throws FileNotFoundException, IOException;
    protected abstract void setPopulationGroups();
    abstract ArrayList<StackedBarChart<String, Number>> createGraph(); // used by admixture
    abstract ScatterChart<Number, Number> createGraph(String PCA1, String PCA2) throws IOException; // used by pca

    private String[] colors = new String[]{"#800000", "#000080", "#808000", "#FFFF00", "#860061", "#ff8000", "#008000", "#800080", "#004C4C", "#ff00ff"};
    private String[] icons = new String[]{"M 0.0 10.0 L 3.0 3.0 L 10.0 0.0 L 3.0 -3.0 L 0.0 -10.0 L -3.0 -3.0 L -10.0 0.0 L -3.0 3.0 Z",
            "M0 -3.5 v7 l 4 -3.5z",
            "M5,0 L10,9 L5,18 L0,9 Z",
            "M2,0 L5,4 L8,0 L10,0 L10,2 L6,5 L10,8 L10,10 L8,10 L5,6 L2,10 L0,10 L0,8 L4,5 L0,2 L0,0 Z",
            "M 20.0 20.0  v24.0 h 10.0  v-24   Z",
            "M0,4 L2,4 L4,8 L7,0 L9,0 L4,11 Z",
            "M 2 2 L 6 2 L 4 6 z",
            "M 10 10 H 90 V 90 H 10 L 10 10",
            "M0 -3.5 v7 l 4 -3.5z", // repeated
            "M5,0 L10,9 L5,18 L0,9 Z", // repeated
            "M2,0 L5,4 L8,0 L10,0 L10,2 L6,5 L10,8 L10,10 L8,10 L5,6 L2,10 L0,10 L0,8 L4,5 L0,2 L0,0 Z" // repeated
    };

    public Graph() {
        project = Project.getProject();
        groupNames = project.getGroupNames();

        // set colors and icons for every phenotype
        for (int i = 0; i < groupNames.size(); i++){
            groupColors.put(groupNames.get(i),colors[i]);  // mkk -> #800000
            groupIcons.put(groupNames.get(i),icons[i]); // mkk -> "M0 -3.5 v7 l 4 -3.5z"
        }

        // name the shapes
        iconsHashmap = new HashMap<String, String>();
        iconsHashmap.put("M 0.0 10.0 L 3.0 3.0 L 10.0 0.0 L 3.0 -3.0 L 0.0 -10.0 L -3.0 -3.0 L -10.0 0.0 L -3.0 3.0 Z", "star");
        iconsHashmap.put("M0 -3.5 v7 l 4 -3.5z", "arrow");
        iconsHashmap.put("M5,0 L10,9 L5,18 L0,9 Z", "kite");
        iconsHashmap.put("M2,0 L5,4 L8,0 L10,0 L10,2 L6,5 L10,8 L10,10 L8,10 L5,6 L2,10 L0,10 L0,8 L4,5 L0,2 L0,0 Z", "cross");
        iconsHashmap.put("M 20.0 20.0  v24.0 h 10.0  v-24   Z", "rectangle");
        iconsHashmap.put("M0,4 L2,4 L4,8 L7,0 L9,0 L4,11 Z", "tick");
        iconsHashmap.put("M 2 2 L 6 2 L 4 6 z", "triangle");
        iconsHashmap.put("M 10 10 H 90 V 90 H 10 L 10 10", "square");

    }

    public HashMap getGroupColors() {
        return groupColors;
    }

    public HashMap getGroupIcons() {
        return groupIcons;
    }

    public HashMap getIconsHashmap() {
        return iconsHashmap;
    }

    // TODO - expand method for saving charts
    public void saveChart(){;}

    public String[] combine(String[] a, String[] b) {
        int length = a.length + b.length;
        String[] result = new String[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

}