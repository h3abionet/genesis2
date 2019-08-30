/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.h3abionet.genesis.model;

import org.h3abionet.genesis.controller.IndividualDetailsController;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.controller.Open0Controller;
import org.h3abionet.genesis.controller.ProjectDetailsController;

/**
 *
 * @author Henry
 */
public class PCAProject {
    
    Fam fam;
    Pheno pheno;
    
    ProjectDetailsController projectDetailsController;
    
    HashMap<String, String[]> pcaValues;
    HashMap<String, String[]> map;
    List<String[]> listOfows;
    String pca_cols[];
    List<String> eigvals;  
    String pcaName;
    private XYChart.Series<Number, Number> series;

    public PCAProject(String pcaName) throws FileNotFoundException, IOException {
        projectDetailsController =new ProjectDetailsController();
        setPCA(pcaName);
        
    }
    
    public void setPCA(String pcaName ) throws FileNotFoundException, IOException {
        pcaValues = new HashMap<>();
        eigvals = new ArrayList<>();
        BufferedReader r = openFile(pcaName);
        String line = r.readLine();
        String fields [] = line.split("\\s+");
        int    num_pcas = fields.length-2;
        System.out.println("We have "+num_pcas+" PCAs");
        pca_cols = new String[num_pcas];
        for (int i=0; i<num_pcas; i++ )
             pca_cols[i] = "PCA"+Integer.toString(i+1);
        eigvals.addAll(Arrays.asList(Arrays.copyOfRange(fields,2,fields.length)));
        System.out.println(eigvals);
        line = r.readLine();
        while (line !=null) {                       
            fields  = line.split("\\s+");
            String key = fields[1];
            pcaValues.put(key, Arrays.copyOfRange(fields,2,fields.length-1));          
            line = r.readLine();
        }
        
    }
    
    public Map<String, List<String[]>> getGroups(){
        fam = new Fam();
        pheno = new Pheno();
        List<String[]> combinedValues = mergePhenoPCA();
        
        Map<String, List<String[]>> resultSet = combinedValues.stream()
                .collect(Collectors.groupingBy(array -> array[0],
                        Collectors.mapping(e -> Arrays.copyOfRange(e, 1, e.length),
                                Collectors.toList())));
        return resultSet;
    }
    
    public List<String[]> mergePhenoPCA(){     
        map = new HashMap<>();
        listOfows = new ArrayList<>();
        map.putAll(pcaValues);

        pheno.getPheno().forEach((key , value) -> {
        //Get the value for key in map.
        String[] list = map.get(key);
        if (list != null) {
            //Merge two list together
            String [] rows = combine(value, list);
            map.put(key , rows);
        }
        else {
            //Do nothing to remove nulls
            ;
        }
        });  
        
        map.entrySet().forEach(entry->{
            ArrayList<String> entries = new ArrayList<>(Arrays.asList(entry.getValue()));
            entries.add(entry.getKey());
            String[] individualRows = entries.toArray(new String[entries.size()]);
            listOfows.add(individualRows);
        });
    return listOfows;
    }

   public ObservableList<String> getPca_cols() {
      ObservableList<String> pca_list = FXCollections.observableArrayList();
      pca_list.addAll(Arrays.asList(pca_cols));
        return pca_list;
    }
    
   public void getGraph() throws IOException{
       
        FXMLLoader  loader =  new FXMLLoader(Genesis.class.getResource("view/Main.fxml"));
        Parent root = (Parent)loader.load();
        Open0Controller open0Controller = loader.getController();
        
        NumberAxis xAxis = new NumberAxis();
        xAxis.setSide(Side.BOTTOM);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setSide(Side.LEFT);
        ScatterChart<Number,Number> sc = new ScatterChart<Number,Number>(xAxis,yAxis);
        // setup chart
        xAxis.setLabel("PCA1");
        yAxis.setLabel("PCA2");
        sc.setTitle(projectDetailsController.getProjectName());
        
//        getGroups().forEach((k,v) -> {
//            for (String[] v1 : v) {
//                System.out.println(k+" "+v1[pheno.getNum_phenos()-1] +" "+ v1[pheno.getNum_phenos()]);
//            }
//        });
//        
        getGroups().forEach((k,v) -> {
            series = new XYChart.Series<Number, Number>();
            series.setName(k);
            for (String[] v1 : v) {
                series.getData().add(new XYChart.Data(Float.parseFloat(v1[pheno.getNum_phenos()-1]),Float.parseFloat(v1[pheno.getNum_phenos()])));  
            }
            sc.getData().add(series);
        });
        
        for (final XYChart.Data<Number, Number> data : series.getData()){
                data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
                @Override
                public void handle(MouseEvent event) {
                    FXMLLoader  loader =  new FXMLLoader(Genesis.class.getResource("view/IndividualDetails.fxml"));
                    try {
                        Parent root = (Parent)loader.load();
                        IndividualDetailsController individualDetailsController = loader.getController();
                        individualDetailsController.getPhenoDetails().setText("X: "+data.getXValue()+"\nY: "+data.getYValue());
                        Tooltip.install(data.getNode(), new Tooltip("X: "+data.getXValue()+"\nY: "+data.getYValue()));    
                    
                    } catch (IOException ex) {
                        Logger.getLogger(PCAProject.class.getName()).log(Level.SEVERE, null, ex);
                    }
                        
                    }
            
                });
            
        }
        
        open0Controller.getPcaAnchorPane().getChildren().add(sc);
        Stage dialogStage = new Stage();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Genesis.class.getResource("css/scatterchart.css").toExternalForm());
        dialogStage.setScene(scene);
        dialogStage.show();
  
    }


    public static String[] combine(String[] a, String[] b){
        int length = a.length + b.length;
        String[] result = new String[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
    
   public BufferedReader openFile(String name) throws FileNotFoundException  {

	InputStreamReader is = new InputStreamReader(new FileInputStream(name));
	BufferedReader       dinp  = new BufferedReader(is);
	return dinp;
   }
}
