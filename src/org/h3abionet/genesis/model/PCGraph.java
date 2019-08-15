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
    
    protected int curr_pcs [] ; // which PCs currently being shown
    protected int pc_labels [];
    protected Object icons [];
   

    protected float pcs[][]; // for each person the PCs they have
    protected bool  ind_visible [] ; //  is person i visible
    protected Icon  ind_icon []; // defaut null -- means use group icon

 
    protected HashMap<String,Icon>  group_icon[];
    // group_icon[3] -- hash map that gives the icons for  the groups in col 3 of phenotype file
    // group_icon[3].get("YRI") -- icon that is used for YRI group in column 3 of phenotupe file

    protected String group_order_by_posn[][]; 
    // group_order_by_posn[3] -- order of groups in the key for groups in column 3
    // e.g. group_order_by_posn[3][5]   -- name of the group at position 5 for column 3

    //OR
    protected HashMap<String,int> group_order_byName[];
    // group_order_by_name[3].get("SWT") -- in which position should SWT for column 3


    /**
     * For every phenotype column (key), we have a list of  phenotypes/pops
     * in order -- initially in the order of the file
     */
    protected HashMap<String,String[]> pop_order [];

    public PCGraph() {
        curr_pcs = new int [2];
	curr_pcs[0]=1; 
        curr_pcs[1]=2;
    }

    
    public static Graph makeGraph(Project p, String label, String pc_file, String phe_file) {
       PCGraph g;
       g = new PCGraph();
       g.project = p;
       g.label = label;
       return g;      
    }
    

}
