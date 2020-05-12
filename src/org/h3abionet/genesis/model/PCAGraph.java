/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import org.h3abionet.genesis.Genesis;

/**
 *
 * @author Henry
 */
public class PCAGraph extends Graph {

    private final HashMap<String, String[]> pcaValues; // store pca values with associated ids
    private final List<String[]> pcasWithPhenoList; // rows in pca (evec) file
    private String[] pcaColumnLabels; // store pca column name: PCA 1, PCA 2, ...
    private final List<String> eigenValues; // store eigen values
    private XYChart.Series<Number, Number> group;

    /**
     *
     * @param pcaFilePath - PCA file absolute path
     * @throws FileNotFoundException
     * @throws IOException
     */
    public PCAGraph(String pcaFilePath) throws FileNotFoundException, IOException {
        pcasWithPhenoList = new ArrayList<>();
        pcaValues = new HashMap<>();
        eigenValues = new ArrayList<>();
        readGraphData(pcaFilePath);
        setPopulationGroups();

    }

    /**
     *
     * @param pcaFilePath This is the PCA file absolute path
     * @throws FileNotFoundException
     * @throws IOException
     */
    @Override
    protected final void readGraphData(String pcaFilePath) throws FileNotFoundException, IOException {
        BufferedReader r = Genesis.openFile(pcaFilePath);
        String line = r.readLine();
        String fields[] = line.split("\\s+");

        int num_pcas = fields.length - 2; // get number of pcas
        pcaColumnLabels = new String[num_pcas];
        for (int i = 0; i < num_pcas; i++) {
            pcaColumnLabels[i] = "PCA " + Integer.toString(i + 1); // store every pca: [PCA 1, PCA 2, ...]
        }

        // store eigen values
        eigenValues.addAll(Arrays.asList(Arrays.copyOfRange(fields, 2, fields.length)));

        line = r.readLine(); // read next line
        while (line != null) {
            fields = line.split("\\s+");
            String key = fields[1];
            pcaValues.put(key, Arrays.copyOfRange(fields, 2, fields.length - 1)); // store keys and values in a hashmap
            line = r.readLine();
        }

//        pcaValues.entrySet().forEach(entry->{
//            System.out.println(entry.getKey() + " " + entry.getValue());  
//        });
    }

    /**
     * Group individuals by chosen column - default is column 3 in the phenotype
     * file
     *
     * @return
     */
    @Override
    protected final void setPopulationGroups() {
        List<String[]> combinedValues = mergePhenoWithPCA(); // ArrayList of array from mergePhenoWithPCA method

        populationGroups = combinedValues.stream()
                .collect(Collectors.groupingBy(array -> array[Project.phenoColumnNumber - 3], // groupby [YRI, EXM, LWK, ...] - third col in pheno file
                        Collectors.mapping(e -> Arrays.copyOfRange(e, 1, e.length),
                                Collectors.toList())));

    }

    /**
     * merge 2 hash maps using keys: 1- pheno hashmap; 2- pca hashmap
     *
     * @return List of arrays: [YRI, AFR, -0.0266, 0.0318, ..., NA19178:NA19178]
     */
    private List<String[]> mergePhenoWithPCA() {
        HashMap<String, String[]> combinerMap = new HashMap<>();
        combinerMap.putAll(pcaValues);
        Project.pcaPhenoData.forEach((key, value_of_pheno) -> {
            // Get the value for key in combinerMap of pcaValues --- returns a list of pcas.
            String[] list_of_pcas = combinerMap.get(key);
            if (list_of_pcas != null) {
                // Merge two list together: 
                String[] pcas_with_phenos = combine(value_of_pheno, list_of_pcas);
                combinerMap.put(key, pcas_with_phenos); // new combinerMap [key1 -> [pheno and pca values], key2 -> [pheno and pca values], ...]
            } else {
                // Do nothing to remove nulls
                ;
            }
        });

        combinerMap.entrySet().forEach(entry -> {
            ArrayList<String> entries = new ArrayList<>(Arrays.asList(entry.getValue())); // [YRI, AFR, -0.0266, 0.0318, ...]
            entries.add(entry.getKey());  // [YRI, AFR, -0.0266, 0.0318, ..., id]

            String[] individualRows = entries.toArray(new String[entries.size()]);
            pcasWithPhenoList.add(individualRows);
        });

//         checked merged results
//        for (int i = 0; i < pcasWithPhenoList.size(); i++){
//            System.out.println(Arrays.asList(pcasWithPhenoList.get(i)));
//        }
        return pcasWithPhenoList; // [YRI, AFR, -0.0266, 0.0318, ..., NA19178:NA19178]
    }

    /**
     *
     * @return
     */
    public ObservableList<String> getPCAcolumns() {
        ObservableList<String> pca_list = FXCollections.observableArrayList();
        pca_list.addAll(Arrays.asList(pcaColumnLabels));
        return pca_list;
    }
    
     /**
     *
     * @return List of eigen values
     */
    public List<String> getEigenValues() {
        return eigenValues;
    }


    /**
     *
     * @param x e.g. PCA 1 -> only consider the integer (1)
     * @param y e.g. PCA 2 -> only consider the integer (2)
     * @return
     * @throws IOException
     */
    @Override
    public ScatterChart<Number, Number> createGraph(String x, String y) throws IOException {
        String xAxisPca = x;
        String yAxisPca = y;

        int xPcaNumber = Integer.parseInt(xAxisPca.substring(4, xAxisPca.length()));
        int yPcaNumber = Integer.parseInt(yAxisPca.substring(4, yAxisPca.length()));

        NumberAxis xAxis = new NumberAxis();
        xAxis.setSide(Side.BOTTOM);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setSide(Side.LEFT);
        ScatterChart<Number, Number> sc = new ScatterChart<>(xAxis, yAxis);

        // setup chart
        xAxis.setLabel(xAxisPca);
        yAxis.setLabel(yAxisPca);
        sc.setTitle(xAxisPca + " Vs " + yAxisPca + " Chart"); // set as default value

        populationGroups.forEach((k, v) -> { // get groups
            group = new XYChart.Series<>();
            group.setName(k);

            for (String[] v1 : v) {
                group.getData().add(new XYChart.Data(Float.parseFloat(v1[xPcaNumber]), Float.parseFloat(v1[yPcaNumber])));
            }
            sc.getData().add(group);
        });

        return sc;
    }
    
    /**
     *
     * @param a
     * @param b
     * @return
     */
    private String[] combine(String[] a, String[] b) {
        int length = a.length + b.length;
        String[] result = new String[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    /*
    * This method is not used by pca
     */
    @Override
    public ArrayList<StackedBarChart<String, Number>> createGraph() {
        return null;
    }
;
}
