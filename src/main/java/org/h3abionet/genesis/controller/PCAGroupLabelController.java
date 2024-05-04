package org.h3abionet.genesis.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.h3abionet.genesis.Genesis;
import org.h3abionet.genesis.model.PCAGraph;
import org.h3abionet.genesis.model.Project;

import java.net.URL;
import java.util.ResourceBundle;

public class PCAGroupLabelController implements Initializable {

    @FXML
    private CheckBox hideGroupCheckbox;

    @FXML
    private TextField groupNameLbl;

    @FXML
    private ComboBox<String> legendPosition;

    @FXML
    private Button cancelBtn;

    @FXML
    private Button doneBtn;

    @FXML
    private Spinner<Double> legendFontSizeSpinner;

    @FXML
    private ComboBox<String> legendFontComboBox;

    @FXML
    private ColorPicker legendFontColorPicker;

    // default axis chosen values -- changed by event handlers
    private String legendFont = "Helvetica";
    private Color legendFontColor = Color.BLACK;
    private String legendFontPosture = "REGULAR";
    private double legendFontSize = 13;

    private String oldGroupName;
    private Project proj;
    private Label label;
    private ScatterChart<Number, Number> chart;
    PCAGraph pcaGraph;

    @FXML
    void cancelHandler(ActionEvent event) {
        Genesis.closeOpenStage(event);
    }

    @FXML
    void doneHandler(ActionEvent event) {
        // String oldGroupName, String newGroupName, StackPane pane, Lbe
        String newGroupName = groupNameLbl.getText();
        
        // FIXME -- made this no longer editable as it breaks if the new
        // name is the same as another group name
        if (!oldGroupName.equals(newGroupName)) { // is name changed?
            // rename the groups on the legend
            pcaGraph.renameLegendGroupNames(oldGroupName, newGroupName);

            // rename group name in the project
            proj.renameGroupName(oldGroupName, newGroupName);
        }

        if(hideGroupCheckbox.isSelected()==true){
            pcaGraph.hideGroup(oldGroupName);

            // add group name to project
            if(!proj.getHiddenGroups().contains(newGroupName)){
                proj.getHiddenGroups().add(newGroupName);
            }
        }

        // change legend position
        chart.lookup(".chart").setStyle("-fx-legend-side: "+legendPosition.getValue()+";");

        // style the legend item
        legendFont = legendFontComboBox.getValue();
        legendFontSize = legendFontSizeSpinner.getValue();
        legendFontColor = legendFontColorPicker.getValue();
        
        pcaGraph.styleLegendItems(legendFont, legendFontSize, 
                // substring removes the leading "0x" from the hex representation
                legendFontColor.toString().substring(2));

        Genesis.closeOpenStage(event);

    }

    public void setGroupNameLbl(String groupName) {
        this.oldGroupName = groupName;
        groupNameLbl.setText(groupName);
    }

    public void setComboBox() {
        legendPosition.setItems(FXCollections.observableArrayList(new String[]{"Right", "Bottom"}));
        legendPosition.setValue("Right");
    }

    public void setProj(Project proj) {
        this.proj = proj;
    }

    public void setGroupNameNode(Label label) {
        this.label = label;
    }

    public void setChart(ScatterChart<Number, Number> sc) {
        this.chart = sc;
    }

    public void setPCAGraph(PCAGraph pcaGraph) {
        this.pcaGraph = pcaGraph;
    }

    private void setSpinner(Spinner sp, double startValue, double endValue, double defaultValue) {
        SpinnerValueFactory<Double> spValues = new SpinnerValueFactory.DoubleSpinnerValueFactory(startValue, endValue);
        sp.setEditable(true);
        sp.setValueFactory(spValues);
        sp.getValueFactory().setValue(defaultValue);
        TextFormatter textFormatter = new TextFormatter(spValues.getConverter(),
                spValues.getValue());
        sp.getEditor().setTextFormatter(textFormatter);
        spValues.valueProperty().bindBidirectional(textFormatter.valueProperty());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        legendFontColorPicker.setValue(Color.BLACK);
        setSpinner(legendFontSizeSpinner, 5, 25, 13);
        legendFontComboBox.setItems(FXCollections.observableArrayList(Font.getFamilies()));
    }
}
