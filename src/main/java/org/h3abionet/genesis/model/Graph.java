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

import java.io.IOException;
import java.io.Serializable;

/**
 *
 * @author scott
 */
public abstract class Graph implements Serializable {

//    public to project;
    protected abstract void readGraphData(String filePath) throws IOException;
    abstract void createAdmixGraph(); // used by admixture
    abstract void createGraph(int pcaX, int pcaY) throws IOException; // used by pca

//    public void setProject(Project project) {
//        this.project = project;
//    }

//    public Graph() { // set project
//        project = Project.getProject();
//    }
}