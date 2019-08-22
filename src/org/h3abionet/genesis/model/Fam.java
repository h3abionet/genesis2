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

/**
 * @author Scott Hazelhurst
 */


public class Fam {
    String fid, iid;
    int pat;
    int mat;
    int sex;
    String phe;

    /*
     * @param pat: ID of father
     * @param mat: ID of mother
     * @param sex: sex of individual
     * @param phe: phenotype
     */
    public Fam(String s) {
        String fields[] = s.split("\\s");
        fid = fields[0];
        iid = fields[1];
        pat = Integer.parseInt(fields[2]);
        mat = Integer.parseInt(fields[3]);
        sex = Integer.parseInt(fields[4]);
        phe = fields[5];
    }
}
