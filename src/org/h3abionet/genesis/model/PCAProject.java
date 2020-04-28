/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import org.h3abionet.genesis.controller.ProjectDetailsController;

/**
 *
 * @author Henry
 */
public class PCAProject {

    Fam fam;
    Pheno pheno;
    ProjectDetailsController projectDetailsController;
    HashMap<String, String[]> pcaValues; // store pca values with associated ids
    HashMap<String, String[]> map; // used to merge pcaValues with phenoFileMap values
    List<String[]> listOfRows; // rows in pca (evec) file
    String pca_cols[]; // store pca column name: PCA 1, PCA 2, ...
    List<String> eigvals; // store eigen values
    private XYChart.Series<Number, Number> series;

    /**
     *
     * @param pcaFilePath This is the pca file absolute path
     * @throws FileNotFoundException
     * @throws IOException
     */
    public PCAProject(String pcaFilePath) throws FileNotFoundException, IOException {
        fam = new Fam();
        pheno = new Pheno();
        projectDetailsController = new ProjectDetailsController();
        map = new HashMap<>();
        listOfRows = new ArrayList<>();
        pcaValues = new HashMap<>();
        eigvals = new ArrayList<>();
        
        setPCA(pcaFilePath);

    }

    /**
     *
     * @param pcaFilePath This is the pca file absolute path
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void setPCA(String pcaFilePath) throws FileNotFoundException, IOException {
        BufferedReader r = openFile(pcaFilePath);
        String line = r.readLine();
        String fields[] = line.split("\\s+");
        
        int num_pcas = fields.length - 2; // get number of pcas
        pca_cols = new String[num_pcas];
        for (int i = 0; i < num_pcas; i++) {
            pca_cols[i] = "PCA " + Integer.toString(i + 1); // store every pca: [PCA 1, PCA 2, ...]
        }
        
        // store eigen values
        eigvals.addAll(Arrays.asList(Arrays.copyOfRange(fields, 2, fields.length))); 
        
        line = r.readLine(); // read next line
        while (line != null) {
            fields = line.split("\\s+");
            String key = fields[1];
            pcaValues.put(key, Arrays.copyOfRange(fields, 2, fields.length - 1)); // store keys and values in a hashmap
            line = r.readLine();
        }

    }

    /**
     * Group ArrayList of pcas according to pheno
     * @return
     */
    public Map<String, List<String[]>> getGroups() {
        List<String[]> combinedValues = mergePhenoPCA(); // ArrayList of array from mergePhenoPCA method

        Map<String, List<String[]>> resultSet = combinedValues.stream()
                .collect(Collectors.groupingBy(array -> array[0], // groupby [YRI, EXM, LWK, ...] - third col in pheno file
                        Collectors.mapping(e -> Arrays.copyOfRange(e, 1, e.length),
                                Collectors.toList())));
        return resultSet;
    }

    /**
     * merge 2 hash maps using keys: 1- pheno  hashmap; 2- pca hashmap
     * @return An arraylist of array: [YRI, AFR, -0.0266, 0.0318, ..., NA19178:NA19178]
     */
    public List<String[]> mergePhenoPCA() {
        map.putAll(pcaValues);

        pheno.getPcaPhenoHashMap().forEach((key, value_of_pheno) -> {
            // Get the value for key in map of pcaValues --- returns a list of pcas.
            String[] list_of_pcas = map.get(key);
            if (list_of_pcas != null) {
                // Merge two list together: 
                String[] rows = combine(value_of_pheno, list_of_pcas);
                map.put(key, rows); // new map [key1 -> [pheno and pca values], key2 -> [pheno and pca values], ...]
            } else {
                // Do nothing to remove nulls
                ;
            }
        });

        map.entrySet().forEach(entry -> {
            ArrayList<String> entries = new ArrayList<>(Arrays.asList(entry.getValue())); // [YRI, AFR, -0.0266, 0.0318, ...]
            entries.add(entry.getKey());  // [YRI, AFR, -0.0266, 0.0318, ..., id]
            
            String[] individualRows = entries.toArray(new String[entries.size()]);
            listOfRows.add(individualRows);
        });
        
        // checked merged results
        for (int i = 0; i < listOfRows.size(); i++){
            System.out.println(Arrays.asList(listOfRows.get(i)));
        }

        return listOfRows; // [YRI, AFR, -0.0266, 0.0318, ..., NA19178:NA19178]
    }

    /**
     *
     * @return
     */
    public ObservableList<String> getPca_cols() {
        ObservableList<String> pca_list = FXCollections.observableArrayList();
        pca_list.addAll(Arrays.asList(pca_cols));
        return pca_list;
    }

    /**
     *
     * @param x
     * @param y
     * @return
     * @throws IOException
     */
    public ScatterChart<Number, Number> getGraph(String x, String y) throws IOException {
        String xAxisPca  = x;
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
        sc.setTitle(projectDetailsController.getProjectName());

        getGroups().forEach((k, v) -> { // get groups
            series = new XYChart.Series<>();
            series.setName(k);
            
            for(String[] v1 : v) {
                series.getData().add(new XYChart.Data(Float.parseFloat(v1[xPcaNumber]), Float.parseFloat(v1[yPcaNumber])));
            }
            sc.getData().add(series);
        });

        return sc;
    }

    /**
     *
     * @param a
     * @param b
     * @return
     */
    public static String[] combine(String[] a, String[] b) {
        int length = a.length + b.length;
        String[] result = new String[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    /**
     *
     * @param name
     * @return
     * @throws FileNotFoundException
     */
    public BufferedReader openFile(String name) throws FileNotFoundException {

        InputStreamReader is = new InputStreamReader(new FileInputStream(name));
        BufferedReader dinp = new BufferedReader(is);
        return dinp;
    }
}
