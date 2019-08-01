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

import java.util.HashMap;


/**
 *
 * @author scott
 */
public class PCGraph extends Graph {
    
    protected int pcs [] ; // which PCs used
    protected int pc_labels [];
    protected Object icons [];

    /**
     * For every phenotype column (key), we have a list of  phenotypes/pops
     * in order -- initially in the order of the file
     */
    protected HashMap<String,String[]> pop_order [];

    public PCGraph() {
    }

   
    
    public static Graph makeGraph(Project p, String label, String pc_file, String phe_file) {
       PCGraph g;
       g = new PCGraph();
       g.project = p;
       g.label = label;
       return g;      
    }
    

}
