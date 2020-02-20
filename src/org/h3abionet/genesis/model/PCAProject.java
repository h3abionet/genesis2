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

    /**
     *
     */
    Fam fam;

    /**
     *
     */
    Pheno pheno;

    /**
     *
     */
    ProjectDetailsController projectDetailsController;

    /**
     *
     */
    HashMap<String, String[]> pcaValues;

    /**
     *
     */
    HashMap<String, String[]> map;

    /**
     *
     */
    List<String[]> listOfrows;

    /**
     *
     */
    String pca_cols[];

    /**
     *
     */
    List<String> eigvals;

    /**
     *
     */
    String pcaName;

    /**
     *
     */
    private XYChart.Series<Number, Number> series;

    /**
     *
     * @param pcaName This is the pca file absolute path
     * @throws FileNotFoundException
     * @throws IOException
     */
    public PCAProject(String pcaName) throws FileNotFoundException, IOException {
        projectDetailsController = new ProjectDetailsController();
        setPCA(pcaName);

    }

    /**
     *
     * @param pcaName This is the pca file absolute path
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void setPCA(String pcaName) throws FileNotFoundException, IOException {
        pcaValues = new HashMap<>();
        eigvals = new ArrayList<>();
        BufferedReader r = openFile(pcaName);
        String line = r.readLine();
        String fields[] = line.split("\\s+");
        int num_pcas = fields.length - 2;
        // System.out.println("We have " + num_pcas + " PCAs");
        pca_cols = new String[num_pcas];
        for (int i = 0; i < num_pcas; i++) {
            pca_cols[i] = "PCA " + Integer.toString(i + 1);
        }
        eigvals.addAll(Arrays.asList(Arrays.copyOfRange(fields, 2, fields.length)));
        // System.out.println(eigvals);
        line = r.readLine();
        while (line != null) {
            fields = line.split("\\s+");
            String key = fields[1];
            pcaValues.put(key, Arrays.copyOfRange(fields, 2, fields.length - 1));
            line = r.readLine();
        }

    }

    /**
     *
     * @return
     */
    public Map<String, List<String[]>> getGroups() {
        fam = new Fam();
        pheno = new Pheno();
        List<String[]> combinedValues = mergePhenoPCA();

        Map<String, List<String[]>> resultSet = combinedValues.stream()
                .collect(Collectors.groupingBy(array -> array[0],
                        Collectors.mapping(e -> Arrays.copyOfRange(e, 1, e.length),
                                Collectors.toList())));
        return resultSet;
    }

    /**
     *
     * @return
     */
    public List<String[]> mergePhenoPCA() {
        map = new HashMap<>();
        listOfrows = new ArrayList<>();
        map.putAll(pcaValues);

        pheno.getPheno().forEach((key, value) -> {
            //Get the value for key in map.
            String[] list = map.get(key);
            if (list != null) {
                //Merge two list together
                String[] rows = combine(value, list);
                map.put(key, rows);
            } else {
                //Do nothing to remove nulls
                ;
            }
        });

        map.entrySet().forEach(entry -> {
            ArrayList<String> entries = new ArrayList<>(Arrays.asList(entry.getValue()));
            entries.add(entry.getKey());
            String[] individualRows = entries.toArray(new String[entries.size()]);
            listOfrows.add(individualRows);
        });
        return listOfrows;
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

        getGroups().forEach((k, v) -> {
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
