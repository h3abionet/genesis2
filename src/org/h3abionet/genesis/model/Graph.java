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

import javafx.scene.chart.StackedBarChart;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author scott
 */
public abstract class Graph {

    public List<String> groupNames;
    public Project project;
    public String label;
    public String caption;
    protected int margin[];
    protected int size[];
    protected Map<String, List<String []>> populationGroups; // based on the phenoColumnNumber
    
    protected abstract void readGraphData(String filePath) throws FileNotFoundException, IOException;
    protected abstract void setPopulationGroups();
    abstract ArrayList<StackedBarChart<String, Number>> createGraph(); // used by admixture
    abstract void createGraph(int pcaX, int pcaY) throws IOException; // used by pca

    public Graph() {
        project = Project.getProject();
    }

    // TODO - expand method for saving charts
    public void saveChart(){;}

}